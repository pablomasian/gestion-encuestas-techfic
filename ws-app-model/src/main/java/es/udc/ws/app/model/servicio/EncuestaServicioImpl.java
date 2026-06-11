package es.udc.ws.app.model.servicio;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import javax.sql.DataSource;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.encuesta.SqlEncuestaDao;
import es.udc.ws.app.model.encuesta.SqlEncuestaDaoFactory;
import es.udc.ws.app.model.respuesta.RespuestaEncuesta;
import es.udc.ws.app.model.respuesta.SqlRespuestaEncuestaDao;
import es.udc.ws.app.model.respuesta.SqlRespuestaEncuestaDaoFactory;
import es.udc.ws.app.model.servicio.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.servicio.exceptions.EncuestaFinalizadaException;
import es.udc.ws.app.model.util.ModelConstants;
import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;
import static es.udc.ws.app.model.util.ModelConstants.MAX_LENGTH;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.validation.PropertyValidator;



public class EncuestaServicioImpl implements EncuestaServicio {

    private final DataSource dataSource;
    private SqlEncuestaDao encuestaDao = null;
    private SqlRespuestaEncuestaDao respuestaDao = null;

    public EncuestaServicioImpl() {
        dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        encuestaDao = SqlEncuestaDaoFactory.getDao();
        respuestaDao = SqlRespuestaEncuestaDaoFactory.getDao();
    }

    private void validateEncuesta(Encuesta encuesta) throws InputValidationException {

        PropertyValidator.validateMandatoryString("pregunta", encuesta.getPregunta());
        PropertyValidator.validateLong("pregunta.length", (long) encuesta.getPregunta().length(), 1, MAX_LENGTH);
        
        if (encuesta.getFechaHoraFin() == null) {
            throw new InputValidationException("La fecha de finalización no puede ser nula");
        }
        if (encuesta.getFechaHoraFin().isBefore(LocalDateTime.now())) {
            throw new InputValidationException("La fecha de finalización debe ser futura");
        }
        if(encuesta.getPregunta()==null || encuesta.getPregunta().isBlank()) {
            throw new InputValidationException("La pregunta no puede estar vacía");
        }
        if(encuesta.getPregunta().length()>MAX_LENGTH) {
            throw new InputValidationException("La pregunta no puede tener más de " + MAX_LENGTH + " caracteres");
        }
    }


    //FUNC 1 Crear encuesta
    @Override
    public Encuesta crearEncuesta(Encuesta encuesta) throws InputValidationException {

        validateEncuesta(encuesta);
        encuesta.setFechaCreacion(LocalDateTime.now().withNano(0));
        encuesta.setRespuestasPositivas(0);
        encuesta.setRespuestasNegativas(0);
        encuesta.setCancelada(false);

        try (Connection connection = dataSource.getConnection()) {
              
            try {
                //preparar la conexion
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                //crear la encuesta
                Encuesta EncuestaCreada = encuestaDao.crear(connection, encuesta);

                //commit
                connection.commit();
                
                return EncuestaCreada;

            } catch (SQLException ex) {
                connection.rollback();
                throw new RuntimeException(ex);
            } catch (RuntimeException | Error ex) {
                connection.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }


    //FUNC 2: Buscar encuestas por palabra clave y estado
    @Override
    public List<Encuesta> buscarPorPalabraClave(String palabraClave, boolean soloNoFinalizadas) throws InputValidationException {

        // Accept null as empty string for convenience during searches
        if (palabraClave == null) {
            palabraClave = "";
        }

        try (Connection connection = dataSource.getConnection()) {
            return encuestaDao.buscarPorPalabraClave(soloNoFinalizadas, palabraClave);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //FUNC 3: Obtener encuesta por id con recuento de respuestas
    @Override
    public Encuesta buscar(Long encuestaId) throws InstanceNotFoundException, InputValidationException {
        if (encuestaId == null) {
            // Consider null id as not found per correction notes
            throw new InstanceNotFoundException(encuestaId, Encuesta.class.getName());
        }

        try (Connection connection = dataSource.getConnection()) {
            return encuestaDao.buscar(connection, encuestaId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //FUNC 4: Responder encuesta
    @Override
    public RespuestaEncuesta responderEncuesta(Long encuestaId, String email, boolean respuestaPositiva) throws InstanceNotFoundException, EncuestaCanceladaException, EncuestaFinalizadaException {
        
        try (Connection connection = dataSource.getConnection()) {

            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                // Buscar la encuesta
                Encuesta encuesta = encuestaDao.buscar(connection, encuestaId);

                if (encuesta.isCancelada()) {
                    throw new EncuestaCanceladaException("La encuesta con id=" + encuestaId + " está cancelada");
                }

                if (encuesta.getFechaHoraFin().isBefore(LocalDateTime.now())) {
                    throw new EncuestaFinalizadaException("La encuesta con id=" + encuestaId + " ya ha finalizado");
                }

                // Check if the user already answered this encuesta
                try {
                    RespuestaEncuesta existing = respuestaDao.buscarPorEncuestaYEmail(connection, encuestaId, email);

                    // Update existing response
                    boolean previousPos = existing.isRespuestaPositiva();

                    // Update timestamp and respuesta value
                    existing.setFechaRespuesta(LocalDateTime.now().withNano(0));
                    existing.setRespuestaPositiva(respuestaPositiva);
                    respuestaDao.actualizarRespuesta(connection, existing);

                    // Adjust counters only if the boolean changed
                    if (previousPos != respuestaPositiva) {
                        if (respuestaPositiva) {
                            encuesta.setRespuestasPositivas(encuesta.getRespuestasPositivas() + 1);
                            encuesta.setRespuestasNegativas(encuesta.getRespuestasNegativas() - 1);
                        } else {
                            encuesta.setRespuestasNegativas(encuesta.getRespuestasNegativas() + 1);
                            encuesta.setRespuestasPositivas(encuesta.getRespuestasPositivas() - 1);
                        }
                        encuestaDao.actualizar(connection, encuesta);
                    }

                    // Return the updated response read from DB to ensure consistency
                    RespuestaEncuesta updated = respuestaDao.buscar(connection, existing.getRespuestaId());
                    connection.commit();
                    return updated;

                } catch (es.udc.ws.util.exceptions.InstanceNotFoundException ex) {
                    // No existing response: create a new one
                    RespuestaEncuesta respuesta = new RespuestaEncuesta(encuestaId, email, respuestaPositiva);
                    RespuestaEncuesta createdRespuesta = respuestaDao.crearRespuesta(connection, respuesta);

                    // Update encuesta counters (service layer handles business logic)
                    if (respuestaPositiva) {
                        encuesta.setRespuestasPositivas(encuesta.getRespuestasPositivas() + 1);
                    } else {
                        encuesta.setRespuestasNegativas(encuesta.getRespuestasNegativas() + 1);
                    }
                    encuestaDao.actualizar(connection, encuesta);

                    connection.commit();
                    return createdRespuesta;
                }

            } catch (InstanceNotFoundException | EncuestaFinalizadaException | EncuestaCanceladaException e) {
                connection.commit();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // FUNC 5: Cancelar encuesta
    @Override
    public Encuesta cancelarEncuesta(Long encuestaId) throws InstanceNotFoundException, EncuestaCanceladaException, EncuestaFinalizadaException {
        
        try (Connection connection = dataSource.getConnection()) {

            try {

                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                Encuesta encuesta = encuestaDao.buscar(connection, encuestaId);

                if (encuesta.getFechaHoraFin().isBefore(LocalDateTime.now())) {
                    throw new EncuestaFinalizadaException("La encuesta con id=" + encuestaId + " ya ha finalizado");
                }

                if (encuesta.isCancelada()) {
                    throw new EncuestaCanceladaException("La encuesta con id=" + encuestaId + " ya está cancelada");
                }

                encuesta.setCancelada(true);
                encuestaDao.actualizar(connection, encuesta);

                connection.commit();

                return encuesta;

            } catch (InstanceNotFoundException | EncuestaFinalizadaException | EncuestaCanceladaException ex) {
                connection.commit();
                throw ex;
            } catch (SQLException ex) {
                connection.rollback();
                throw new RuntimeException(ex);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }


    // FUNC 6: Obtener respuestas de encuesta
    @Override
    public List<RespuestaEncuesta> obtenerRespuestasEncuesta(Long encuestaId, boolean soloPositivas) throws InstanceNotFoundException, InputValidationException {

        // Validaciones de entrada
        if (encuestaId == null) {
            throw new InputValidationException("El ID de la encuesta no puede ser nulo");
        }

        try (Connection connection = dataSource.getConnection()) {

            // Verificar que la encuesta existe
           encuestaDao.buscar(connection, encuestaId);

            //Obtener las respuestas delegando el filtrado a la capa DAO para que lo haga la BD
            List<RespuestaEncuesta> respuestas = respuestaDao.obtenerRespuestas(connection, encuestaId, soloPositivas);
            
            return respuestas;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

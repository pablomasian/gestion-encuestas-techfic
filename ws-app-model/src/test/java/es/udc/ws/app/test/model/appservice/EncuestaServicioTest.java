package es.udc.ws.app.test.model.appservice;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.encuesta.SqlEncuestaDao;
import es.udc.ws.app.model.encuesta.SqlEncuestaDaoFactory;
import es.udc.ws.app.model.respuesta.RespuestaEncuesta;
import es.udc.ws.app.model.respuesta.SqlRespuestaEncuestaDao;
import es.udc.ws.app.model.respuesta.SqlRespuestaEncuestaDaoFactory;
import es.udc.ws.app.model.servicio.EncuestaServicio;
import es.udc.ws.app.model.servicio.EncuestaServicioFactory;
import es.udc.ws.app.model.servicio.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.servicio.exceptions.EncuestaFinalizadaException;
import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;

public class EncuestaServicioTest {

    // Variables compartidas por todos los tests
    private final String USER_EMAIL = "ws-user@example.com";
    
    private static EncuestaServicio encuestaServicio = null;
    private static SqlEncuestaDao encuestaDao = null;
    private static SqlRespuestaEncuestaDao respuestaDao = null;

    // Se ejecuta una vez antes de todos los tests - configura el entorno
    @BeforeAll
    public static void init() {
        DataSource dataSource = new SimpleDataSource();
        DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);
        
        encuestaServicio = EncuestaServicioFactory.getService();
        encuestaDao = SqlEncuestaDaoFactory.getDao();
        respuestaDao = SqlRespuestaEncuestaDaoFactory.getDao();
    }

    // Método auxiliar: crea una encuesta válida de ejemplo
    private Encuesta getValidEncuesta() {
        return new Encuesta(null, "¿Estás satisfecho con el servicio?", 
                           LocalDateTime.now().plusDays(7).withNano(0), 
                           false, null, 0, 0);
    }

    // Método auxiliar: elimina una encuesta de la BD (limpieza después de tests)
    private void removeEncuesta(Long encuestaId) {
        try (Connection connection = DataSourceLocator.getDataSource(APP_DATA_SOURCE).getConnection()) {
            try {
                // Configurar transacción
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                
                encuestaDao.eliminar(connection, encuestaId);
                
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            } catch (InstanceNotFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //metodo auxiliar: crea una respuesta directamente en la BD (preparar datos de prueba)
    private RespuestaEncuesta createRespuesta(Long encuestaId, String email, boolean positiva) {
        try (Connection connection = DataSourceLocator.getDataSource(APP_DATA_SOURCE).getConnection()) {
            try {
                // Configurar transacción
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                
                RespuestaEncuesta respuesta = new RespuestaEncuesta(encuestaId, email, positiva);
                RespuestaEncuesta createdRespuesta = respuestaDao.crearRespuesta(connection, respuesta);

                // Update encuesta counters (DAO no longer updates counters)
                try {
                    Encuesta encuesta = encuestaDao.buscar(connection, encuestaId);
                    if (positiva) {
                        encuesta.setRespuestasPositivas(encuesta.getRespuestasPositivas() + 1);
                    } else {
                        encuesta.setRespuestasNegativas(encuesta.getRespuestasNegativas() + 1);
                    }
                    encuestaDao.actualizar(connection, encuesta);
                } catch (InstanceNotFoundException e) {
                    connection.rollback();
                    throw new RuntimeException(e);
                }

                connection.commit();
                return createdRespuesta;
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

    //metodo auxiliar: crea una encuesta usando el servicio
    private Encuesta createEncuesta(Encuesta encuesta) {
        try {
            return encuestaServicio.crearEncuesta(encuesta);
        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        }
    }

    //Metodo auxiliar: elimina una respuesta de la BD
    private void removeRespuesta(Long respuestaId) {
        try (Connection connection = DataSourceLocator.getDataSource(APP_DATA_SOURCE).getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                
                respuestaDao.eliminar(connection, respuestaId);
                
                connection.commit();
            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw new RuntimeException(e);
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

    // Metodo auxiliar: actualiza una encuesta directamente en la BD
    private void updateEncuesta(Encuesta encuesta) {
        try (Connection connection = DataSourceLocator.getDataSource(APP_DATA_SOURCE).getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                
                encuestaDao.actualizar(connection, encuesta);
                
                connection.commit();
            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw new RuntimeException(e);
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



    // TESTS FUNC-1: Crear Encuesta

    @Test
    public void testCrearEncuesta() throws InputValidationException {
        Encuesta encuesta = getValidEncuesta();
        Encuesta createdEncuesta = null;

        try {
            LocalDateTime beforeCreationDate = LocalDateTime.now().withNano(0);
            
            createdEncuesta = encuestaServicio.crearEncuesta(encuesta);
            
            LocalDateTime afterCreationDate = LocalDateTime.now().withNano(0);

            assertNotNull(createdEncuesta.getEncuestaId());
            assertEquals(encuesta.getPregunta(), createdEncuesta.getPregunta());
            assertEquals(encuesta.getFechaHoraFin(), createdEncuesta.getFechaHoraFin());
            assertFalse(createdEncuesta.isCancelada());
            assertEquals(0, createdEncuesta.getRespuestasPositivas());
            assertEquals(0, createdEncuesta.getRespuestasNegativas());
            assertTrue(createdEncuesta.getFechaCreacion().compareTo(beforeCreationDate) >= 0);
            assertTrue(createdEncuesta.getFechaCreacion().compareTo(afterCreationDate) <= 0);
        } finally {
            if (createdEncuesta != null) {
                removeEncuesta(createdEncuesta.getEncuestaId());
            }
        }
    }

    @Test
    public void testCrearEncuestaConPreguntaNula() {
        Encuesta encuesta = getValidEncuesta();
        encuesta.setPregunta(null);

        assertThrows(InputValidationException.class, () -> {
            encuestaServicio.crearEncuesta(encuesta);
        });
    }

    @Test
    public void testCrearEncuestaConPreguntaVacia() {
        Encuesta encuesta = getValidEncuesta();
        encuesta.setPregunta("");

        assertThrows(InputValidationException.class, () -> {
            encuestaServicio.crearEncuesta(encuesta);
        });
    }

    @Test
    public void testCrearEncuestaConFechaFinNula() {
        Encuesta encuesta = getValidEncuesta();
        encuesta.setFechaHoraFin(null);

        assertThrows(InputValidationException.class, () -> {
            encuestaServicio.crearEncuesta(encuesta);
        });
    }

    @Test
    public void testCrearEncuestaConFechaFinPasada() {
        Encuesta encuesta = getValidEncuesta();
        encuesta.setFechaHoraFin(LocalDateTime.now().minusDays(1));

        assertThrows(InputValidationException.class, () -> {
            encuestaServicio.crearEncuesta(encuesta);
        });
    }
    

    //TESTS FUNC-2: Buscar Encuesta por ID
    
    @Test
    public void testBuscarEncuestaPorId() throws InputValidationException, InstanceNotFoundException {
        Encuesta encuesta = createEncuesta(getValidEncuesta());

        try {
            Encuesta foundEncuesta = encuestaServicio.buscar(encuesta.getEncuestaId());

            assertNotNull(foundEncuesta);
            assertEquals(encuesta.getEncuestaId(), foundEncuesta.getEncuestaId());
            assertEquals(encuesta.getPregunta(), foundEncuesta.getPregunta());
            assertEquals(encuesta.getFechaHoraFin(), foundEncuesta.getFechaHoraFin());
            assertEquals(0, foundEncuesta.getRespuestasPositivas());
            assertEquals(0, foundEncuesta.getRespuestasNegativas());
        } finally {
            removeEncuesta(encuesta.getEncuestaId());
        }
    }

    @Test
    public void testBuscarEncuestaConRespuestas() throws InputValidationException, InstanceNotFoundException {
        Encuesta encuesta = createEncuesta(getValidEncuesta());

        try {
            // Crear respuestas
            createRespuesta(encuesta.getEncuestaId(), "user1@example.com", true);
            createRespuesta(encuesta.getEncuestaId(), "user2@example.com", true);
            createRespuesta(encuesta.getEncuestaId(), "user3@example.com", false);

            Encuesta foundEncuesta = encuestaServicio.buscar(encuesta.getEncuestaId());

            assertEquals(2, foundEncuesta.getRespuestasPositivas());
            assertEquals(1, foundEncuesta.getRespuestasNegativas());
        } finally {
            removeEncuesta(encuesta.getEncuestaId());
        }
    }

    @Test
    public void testBuscarEncuestaNoExistente() {
        assertThrows(InstanceNotFoundException.class, () -> {
            encuestaServicio.buscar(999999L);
        });
    }

    @Test
    public void testBuscarEncuestaConIdNulo() {
        // Per correction, buscar(null) should be treated as not found
        assertThrows(es.udc.ws.util.exceptions.InstanceNotFoundException.class, () -> {
            encuestaServicio.buscar(null);
        });
    }


    //TESTS FUNC-3: Buscar Encuestas por Palabra Clave
    
    @Test
    public void testBuscarPorPalabraClaveTodasLasEncuestas() throws InputValidationException {
        Encuesta e1 = new Encuesta(null, "¿Te gusta Java?", 
                                   LocalDateTime.now().plusDays(5).withNano(0), 
                                   false, null, 0, 0);
        Encuesta e2 = new Encuesta(null, "¿Prefieres Python?", 
                                   LocalDateTime.now().plusDays(10).withNano(0), 
                                   false, null, 0, 0);
        
        Encuesta created1 = null;
        Encuesta created2 = null;

        try {
            created1 = createEncuesta(e1);
            created2 = createEncuesta(e2);

            List<Encuesta> resultados = encuestaServicio.buscarPorPalabraClave("", false);

            assertTrue(resultados.size() >= 2);
        } finally {
            if (created1 != null) removeEncuesta(created1.getEncuestaId());
            if (created2 != null) removeEncuesta(created2.getEncuestaId());
        }
    }

    @Test
    public void testBuscarPorPalabraClaveEspecifica() throws InputValidationException {
        Encuesta e1 = new Encuesta(null, "¿Te gusta Java?", 
                                   LocalDateTime.now().plusDays(5).withNano(0), 
                                   false, null, 0, 0);
        Encuesta e2 = new Encuesta(null, "¿Prefieres Python?", 
                                   LocalDateTime.now().plusDays(10).withNano(0), 
                                   false, null, 0, 0);
        Encuesta e3 = new Encuesta(null, "¿JavaScript es mejor?", 
                                   LocalDateTime.now().plusDays(15).withNano(0), 
                                   false, null, 0, 0);
        
        Encuesta created1 = null;
        Encuesta created2 = null;
        Encuesta created3 = null;

        try {
            created1 = createEncuesta(e1);
            created2 = createEncuesta(e2);
            created3 = createEncuesta(e3);

            List<Encuesta> resultados = encuestaServicio.buscarPorPalabraClave("Java", false);

            assertEquals(2, resultados.size());
            assertTrue(resultados.stream().anyMatch(e -> e.getPregunta().contains("Java")));
            assertTrue(resultados.stream().anyMatch(e -> e.getPregunta().contains("JavaScript")));
        } finally {
            if (created1 != null) removeEncuesta(created1.getEncuestaId());
            if (created2 != null) removeEncuesta(created2.getEncuestaId());
            if (created3 != null) removeEncuesta(created3.getEncuestaId());
        }
    }

    @Test
    public void testBuscarPorPalabraClaveSoloNoFinalizadas() throws InputValidationException {
        Encuesta e1 = new Encuesta(null, "Encuesta futura", 
                                   LocalDateTime.now().plusDays(5).withNano(0), 
                                   false, null, 0, 0);
        Encuesta e2 = new Encuesta(null, "Encuesta pasada", 
                                   LocalDateTime.now().minusDays(1).withNano(0), 
                                   false, null, 0, 0);
        
        Encuesta created1 = null;
        Encuesta created2 = null;

        try {
            created1 = createEncuesta(e1);
            // La e2 no se puede crear porque la validación no permite fechas pasadas
            // Así que la creamos y luego la actualizamos
            created2 = new Encuesta(null, "Encuesta pasada", 
                                   LocalDateTime.now().plusDays(1).withNano(0), 
                                   false, null, 0, 0);
            created2 = createEncuesta(created2);
            created2.setFechaHoraFin(LocalDateTime.now().minusDays(1));
            updateEncuesta(created2);

            List<Encuesta> resultados = encuestaServicio.buscarPorPalabraClave("Encuesta", true);

            assertEquals(1, resultados.size());
            assertEquals(created1.getEncuestaId(), resultados.get(0).getEncuestaId());
        } finally {
            if (created1 != null) removeEncuesta(created1.getEncuestaId());
            if (created2 != null) removeEncuesta(created2.getEncuestaId());
        }
    }

    @Test
    public void testBuscarPorPalabraClaveConContadores() throws InputValidationException {
        Encuesta encuesta = createEncuesta(getValidEncuesta());

        try {
            // Crear respuestas
            createRespuesta(encuesta.getEncuestaId(), "user1@example.com", true);
            createRespuesta(encuesta.getEncuestaId(), "user2@example.com", false);

            List<Encuesta> resultados = encuestaServicio.buscarPorPalabraClave("satisfecho", false);

            assertEquals(1, resultados.size());
            Encuesta resultado = resultados.get(0);
            assertEquals(1, resultado.getRespuestasPositivas());
            assertEquals(1, resultado.getRespuestasNegativas());
        } finally {
            removeEncuesta(encuesta.getEncuestaId());
        }
    }

    @Test
    public void testBuscarPorPalabraClaveNoEncuentraNada() throws InputValidationException {
        List<Encuesta> resultados = encuestaServicio.buscarPorPalabraClave("palabraquenoexiste123456", false);
        
        assertTrue(resultados.isEmpty());
    }

    @Test
    public void testBuscarPorPalabraClaveExcluyeCanceladas() throws InputValidationException, InstanceNotFoundException, EncuestaCanceladaException, EncuestaFinalizadaException {
        Encuesta e1 = createEncuesta(getValidEncuesta());
        Encuesta e2 = new Encuesta(null, "¿Otra encuesta?", 
                                   LocalDateTime.now().plusDays(10).withNano(0), 
                                   false, null, 0, 0);
        Encuesta created2 = null;

        try {
            created2 = createEncuesta(e2);
            
            // Cancelar la primera
            encuestaServicio.cancelarEncuesta(e1.getEncuestaId());

            List<Encuesta> resultados = encuestaServicio.buscarPorPalabraClave("", false);

            // Ahora las canceladas SÍ deben aparecer en los resultados
            assertTrue(resultados.stream().anyMatch(e -> e.getEncuestaId().equals(e1.getEncuestaId()) && e.isCancelada()));
        } finally {
            removeEncuesta(e1.getEncuestaId());
            if (created2 != null) removeEncuesta(created2.getEncuestaId());
        }
    }

    @Test
    public void testBuscarPorPalabraClaveConPalabraNula() throws InputValidationException {
        // null keyword should be treated as empty string and not throw
        var resultados = encuestaServicio.buscarPorPalabraClave(null, false);
        assertNotNull(resultados);
    }
    

    //TEST FUNC-4: Responder Encuesta (ya existentes abajo)
    @Test
    public void testResponderEncuesta() throws InstanceNotFoundException, EncuestaCanceladaException, EncuestaFinalizadaException, InputValidationException {

        Encuesta encuesta = createEncuesta(getValidEncuesta());
        RespuestaEncuesta respuesta = null;

        try {

            LocalDateTime beforeResponseDate = LocalDateTime.now().withNano(0);

        respuesta = encuestaServicio.responderEncuesta(encuesta.getEncuestaId(), USER_EMAIL, true);

        LocalDateTime afterResponseDate = LocalDateTime.now().withNano(0);

        // Validate returned object
        assertEquals(USER_EMAIL, respuesta.getEmail());
        assertEquals(encuesta.getEncuestaId(), respuesta.getEncuestaId());
        assertTrue(respuesta.isRespuestaPositiva());
        assertTrue((respuesta.getFechaRespuesta().compareTo(beforeResponseDate) >= 0)
            && (respuesta.getFechaRespuesta().compareTo(afterResponseDate) <= 0));

    // Re-read the persisted response from the DB via service and validate
    final Long createdRespuestaId = respuesta.getRespuestaId();
    List<RespuestaEncuesta> respuestas = encuestaServicio.obtenerRespuestasEncuesta(encuesta.getEncuestaId(), false);
    RespuestaEncuesta persisted = respuestas.stream()
        .filter(r -> r.getRespuestaId().equals(createdRespuestaId))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Persisted response not found"));
    assertEquals(respuesta, persisted);

        // Validate encuesta counters updated
        Encuesta encuestaPersisted = encuestaServicio.buscar(encuesta.getEncuestaId());
        assertEquals(1, encuestaPersisted.getRespuestasPositivas());
        assertEquals(0, encuestaPersisted.getRespuestasNegativas());

        } finally {
            if (respuesta != null) {
                removeRespuesta(respuesta.getRespuestaId());
            }
            removeEncuesta(encuesta.getEncuestaId());
        }
    }

    @Test
    public void testResponderEncuestaCancelada() throws InstanceNotFoundException, EncuestaCanceladaException, EncuestaFinalizadaException {

        Encuesta encuesta = createEncuesta(getValidEncuesta());

        try {
            encuestaServicio.cancelarEncuesta(encuesta.getEncuestaId());

            assertThrows(EncuestaCanceladaException.class, () ->
                    encuestaServicio.responderEncuesta(encuesta.getEncuestaId(), USER_EMAIL, true)
            );

        } finally {
            removeEncuesta(encuesta.getEncuestaId());
        }
    }

    @Test
    public void testResponderEncuestaFinalizada() throws InstanceNotFoundException {

        Encuesta encuesta = createEncuesta(getValidEncuesta());

        try {
            encuesta.setFechaHoraFin(LocalDateTime.now().minusDays(1));
            updateEncuesta(encuesta);

            assertThrows(EncuestaFinalizadaException.class, () ->
                    encuestaServicio.responderEncuesta(encuesta.getEncuestaId(), USER_EMAIL, true)
            );

        } finally {
            removeEncuesta(encuesta.getEncuestaId());
        }
    }


    // TESTS FUNC-5: Cancelar Encuesta
    @Test
    public void testCancelarEncuesta() throws InstanceNotFoundException, EncuestaCanceladaException, EncuestaFinalizadaException, InputValidationException {

        Encuesta encuesta = createEncuesta(getValidEncuesta());

        try {

            encuestaServicio.cancelarEncuesta(encuesta.getEncuestaId());

            Encuesta cancelledEncuesta = encuestaServicio.buscar(encuesta.getEncuestaId());

            assertTrue(cancelledEncuesta.isCancelada());

        } finally {
            removeEncuesta(encuesta.getEncuestaId());
        }
    }

    @Test
    public void testCancelarEncuestaYaCancelada() throws InstanceNotFoundException, EncuestaCanceladaException, EncuestaFinalizadaException {

        Encuesta encuesta = createEncuesta(getValidEncuesta());

        try {
            encuestaServicio.cancelarEncuesta(encuesta.getEncuestaId());

            assertThrows(EncuestaCanceladaException.class, () ->
                    encuestaServicio.cancelarEncuesta(encuesta.getEncuestaId())
            );

        } finally {
            removeEncuesta(encuesta.getEncuestaId());
        }
    }

    @Test
    public void testCancelarEncuestaFinalizada() throws InstanceNotFoundException {

        Encuesta encuesta = createEncuesta(getValidEncuesta());

        try {
            encuesta.setFechaHoraFin(LocalDateTime.now().minusDays(1));
            updateEncuesta(encuesta);

            assertThrows(EncuestaFinalizadaException.class, () ->
                    encuestaServicio.cancelarEncuesta(encuesta.getEncuestaId())
            );

        } finally {
            removeEncuesta(encuesta.getEncuestaId());
        }
    }


    //TESTS FUNC-6: Obtener Respuestas

    @Test
    public void testObtenerTodasLasRespuestas() throws InputValidationException, InstanceNotFoundException {
        Encuesta encuesta = getValidEncuesta();
        Encuesta createdEncuesta = null;

        try {
            createdEncuesta = encuestaServicio.crearEncuesta(encuesta);
            Long encuestaId = createdEncuesta.getEncuestaId();

            // Crear respuestas de prueba
            createRespuesta(encuestaId, "user1@example.com", true);
            createRespuesta(encuestaId, "user2@example.com", false);
            createRespuesta(encuestaId, "user3@example.com", true);

            List<RespuestaEncuesta> respuestas = encuestaServicio.obtenerRespuestasEncuesta(encuestaId, false);

            assertEquals(3, respuestas.size());
        } finally {
            if (createdEncuesta != null) {
                removeEncuesta(createdEncuesta.getEncuestaId());
            }
        }
    }

    @Test
    public void testObtenerSoloRespuestasPositivas() throws InputValidationException, InstanceNotFoundException {
        Encuesta encuesta = getValidEncuesta();
        Encuesta createdEncuesta = null;

        try {
            createdEncuesta = encuestaServicio.crearEncuesta(encuesta);
            Long encuestaId = createdEncuesta.getEncuestaId();

            // Crear respuestas mixtas
            createRespuesta(encuestaId, "user1@example.com", true);
            createRespuesta(encuestaId, "user2@example.com", false);
            createRespuesta(encuestaId, "user3@example.com", true);
            createRespuesta(encuestaId, "user4@example.com", false);

            List<RespuestaEncuesta> respuestas = encuestaServicio.obtenerRespuestasEncuesta(encuestaId, true);

            assertEquals(2, respuestas.size());
            for (RespuestaEncuesta respuesta : respuestas) {
                assertTrue(respuesta.isRespuestaPositiva());
            }
        } finally {
            if (createdEncuesta != null) {
                removeEncuesta(createdEncuesta.getEncuestaId());
            }
        }
    }

    @Test
    public void testObtenerRespuestasEncuestaNoExistente() {
        assertThrows(InstanceNotFoundException.class, () -> {
            encuestaServicio.obtenerRespuestasEncuesta(-1L, false);
        });
    }

    @Test
    public void testObtenerRespuestasConIdNulo() {
        assertThrows(InputValidationException.class, () -> {
            encuestaServicio.obtenerRespuestasEncuesta(null, false);
        });
    }
}

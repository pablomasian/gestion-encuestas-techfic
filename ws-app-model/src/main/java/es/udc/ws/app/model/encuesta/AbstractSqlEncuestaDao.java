package es.udc.ws.app.model.encuesta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import es.udc.ws.app.model.util.ModelConstants;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;

public abstract class AbstractSqlEncuestaDao implements SqlEncuestaDao {

    protected AbstractSqlEncuestaDao() {
    }
    // Buscar encuesta por id
    @Override
    public Encuesta buscar(Connection connection, Long encuestaId) throws InstanceNotFoundException {
        
        String queryString = "SELECT pregunta, fechaHoraFin, cancelada, fechaCreacion, " +
                "respuestasPositivas, respuestasNegativas FROM Encuesta WHERE encuestaId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            preparedStatement.setLong(1, encuestaId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new InstanceNotFoundException(encuestaId, Encuesta.class.getName());
            }

            int i = 1;
            String pregunta = resultSet.getString(i++);
            Timestamp fechaHoraFinTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime fechaHoraFin = fechaHoraFinTimestamp.toLocalDateTime();
            boolean cancelada = resultSet.getBoolean(i++);
            Timestamp fechaCreacionTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime fechaCreacion = fechaCreacionTimestamp.toLocalDateTime();
            int respuestasPositivas = resultSet.getInt(i++);
            int respuestasNegativas = resultSet.getInt(i++);
            
            //devolver la encuesta
            return new Encuesta(encuestaId, pregunta, fechaHoraFin, cancelada, fechaCreacion,
                    respuestasPositivas, respuestasNegativas);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Buscar encuestas por palabra clave y filtrado por estado
    @Override
    public List<Encuesta> buscarPorPalabraClave(boolean soloNoFinalizadas, String palabraClave) {
        
        List<Encuesta> encuestas = new ArrayList<>();

        String queryString = "SELECT encuestaId, pregunta, fechaCreacion, fechaHoraFin, "
            + "respuestasPositivas, respuestasNegativas, cancelada "
            + "FROM Encuesta WHERE pregunta LIKE ?";

        if (soloNoFinalizadas) {
            queryString += " AND fechaHoraFin > ?";
        }

        queryString += " ORDER BY fechaCreacion DESC";

        try (Connection connection = DataSourceLocator.getDataSource(ModelConstants.APP_DATA_SOURCE).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            preparedStatement.setString(1, "%" + palabraClave + "%");

            if (soloNoFinalizadas) {
                preparedStatement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            }

            ResultSet resultSet = preparedStatement.executeQuery();

           
            while (resultSet.next()) {
                Long encuestaId = resultSet.getLong(1);
                String pregunta = resultSet.getString(2);
                LocalDateTime fechaCreacion = resultSet.getTimestamp(3).toLocalDateTime();
                LocalDateTime fechaHoraFin = resultSet.getTimestamp(4).toLocalDateTime();
                int respuestasPositivas = resultSet.getInt(5);
                int respuestasNegativas = resultSet.getInt(6);
                boolean cancelada = resultSet.getBoolean(7);

                //añadimos la encuesta a la lista
                encuestas.add(new Encuesta(encuestaId, pregunta, fechaHoraFin, cancelada, fechaCreacion,
                        respuestasPositivas, respuestasNegativas));
            }

            // Devolvemos lista (vacia si no encuentra nada)
            return encuestas;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void actualizar(Connection connection, Encuesta encuesta) throws InstanceNotFoundException {
        
        //  update editable fields
        String queryString = "UPDATE Encuesta SET pregunta = ?, fechaHoraFin = ?, cancelada = ?"
                + ", respuestasPositivas = ?, respuestasNegativas = ?"
                + " WHERE encuestaId = ?";

        try (PreparedStatement ps = connection.prepareStatement(queryString)) {
            int i = 1;
            ps.setString(i++, encuesta.getPregunta());
            ps.setTimestamp(i++, java.sql.Timestamp.valueOf(encuesta.getFechaHoraFin()));
            ps.setBoolean(i++, encuesta.isCancelada());
            ps.setInt(i++, encuesta.getRespuestasPositivas());
            ps.setInt(i++, encuesta.getRespuestasNegativas());
            ps.setLong(i++, encuesta.getEncuestaId());

            int updatedRows = ps.executeUpdate();

            if (updatedRows == 0) {
                throw new InstanceNotFoundException(encuesta.getEncuestaId(), Encuesta.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void eliminar(Connection connection, Long encuestaId) throws InstanceNotFoundException {
        /* Create queryString. */
        String queryString = "DELETE FROM Encuesta WHERE encuestaId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill preparedStatement. */
            int i = 1;
            preparedStatement.setLong(i++, encuestaId);

            /* Execute query. */
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(encuestaId, Encuesta.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void eliminarTodas(Connection connection) {
        /* Delete responses first to avoid FK issues, then encuestas */
        try (PreparedStatement psResp = connection.prepareStatement("DELETE FROM RespuestaEncuesta");
             PreparedStatement psEnc = connection.prepareStatement("DELETE FROM Encuesta")) {
            psResp.executeUpdate();
            psEnc.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
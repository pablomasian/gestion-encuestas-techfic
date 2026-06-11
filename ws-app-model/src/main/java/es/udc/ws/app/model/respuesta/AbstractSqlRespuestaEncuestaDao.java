package es.udc.ws.app.model.respuesta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

public abstract class AbstractSqlRespuestaEncuestaDao implements SqlRespuestaEncuestaDao {

    protected AbstractSqlRespuestaEncuestaDao() {
    }

    @Override
    public RespuestaEncuesta buscar(Connection connection, Long respuestaId) throws InstanceNotFoundException {
        
        String queryString = "SELECT encuestaId, email, respuesta, fechaRespuesta FROM RespuestaEncuesta WHERE respuestaId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            preparedStatement.setLong(1, respuestaId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new InstanceNotFoundException(respuestaId, RespuestaEncuesta.class.getName());
            }

            int i = 1;
            Long encuestaId = resultSet.getLong(i++);
            String email = resultSet.getString(i++);
            String respuestaEnum = resultSet.getString(i++);
            boolean respuestaPositiva = "POSITIVA".equals(respuestaEnum);
            java.sql.Timestamp fechaRespuestaTimestamp = resultSet.getTimestamp(i++);
            java.time.LocalDateTime fechaRespuesta = fechaRespuestaTimestamp.toLocalDateTime();

            RespuestaEncuesta respuesta = new RespuestaEncuesta(encuestaId, email, respuestaPositiva, fechaRespuesta);
            respuesta.setRespuestaId(respuestaId);
            return respuesta;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<RespuestaEncuesta> obtenerRespuestas(Connection connection, Long encuestaId, boolean soloPositivas) {

        List<RespuestaEncuesta> respuestas = new ArrayList<>();
        String queryString = "SELECT respuestaId, email, respuesta, fechaRespuesta FROM RespuestaEncuesta WHERE encuestaId = ?";

        if (soloPositivas) {
            queryString += " AND respuesta = 'POSITIVA'";
        }

        queryString += " ORDER BY fechaRespuesta ASC";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            preparedStatement.setLong(1, encuestaId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int i = 1;
                Long respuestaId = resultSet.getLong(i++);
                String email = resultSet.getString(i++);
                String respuestaEnum = resultSet.getString(i++);
                boolean respuestaPositiva = "POSITIVA".equals(respuestaEnum);
                java.sql.Timestamp fechaRespuestaTimestamp = resultSet.getTimestamp(i++);
                java.time.LocalDateTime fechaRespuesta = fechaRespuestaTimestamp.toLocalDateTime();

                RespuestaEncuesta respuesta = new RespuestaEncuesta(encuestaId, email, respuestaPositiva, fechaRespuesta);
                respuesta.setRespuestaId(respuestaId);
                respuestas.add(respuesta);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return respuestas;
    }


    @Override
    public void actualizarRespuesta(Connection connection, RespuestaEncuesta respuesta)
            throws InstanceNotFoundException {

                String queryString = "UPDATE RespuestaEncuesta SET email = ?, respuesta = ? WHERE respuestaId = ?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

                    int i = 1;
                    preparedStatement.setString(1, respuesta.getEmail());
                    preparedStatement.setString(2, respuesta.isRespuestaPositiva() ? "POSITIVA" : "NEGATIVA");
                    preparedStatement.setLong(3, respuesta.getRespuestaId());

                    int rowsUpdated = preparedStatement.executeUpdate();

                    if (rowsUpdated == 0) {
                        throw new InstanceNotFoundException(respuesta.getRespuestaId(), RespuestaEncuesta.class.getName());
                    }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
    }

    @Override
    public void eliminar(Connection connection, Long respuestaId) throws InstanceNotFoundException {
        String queryString = "DELETE FROM RespuestaEncuesta WHERE respuestaId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            int i = 1;
            preparedStatement.setLong(i++, respuestaId);

            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(respuestaId, RespuestaEncuesta.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public RespuestaEncuesta buscarPorEncuestaYEmail(Connection connection, Long encuestaId, String email) throws InstanceNotFoundException {
        String queryString = "SELECT respuestaId, respuesta, fechaRespuesta FROM RespuestaEncuesta WHERE encuestaId = ? AND email = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            int i = 1;
            preparedStatement.setLong(i++, encuestaId);
            preparedStatement.setString(i++, email);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new InstanceNotFoundException("(encuestaId,email): (" + encuestaId + "," + email + ")", RespuestaEncuesta.class.getName());
            }

            i = 1;
            Long respuestaId = resultSet.getLong(i++);
            String respuestaEnum = resultSet.getString(i++);
            boolean respuestaPositiva = "POSITIVA".equals(respuestaEnum);
            java.sql.Timestamp fechaRespuestaTimestamp = resultSet.getTimestamp(i++);
            java.time.LocalDateTime fechaRespuesta = fechaRespuestaTimestamp.toLocalDateTime();

            RespuestaEncuesta respuesta = new RespuestaEncuesta(encuestaId, email, respuestaPositiva, fechaRespuesta);
            respuesta.setRespuestaId(respuestaId);
            return respuesta;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void eliminarTodas(Connection connection) {
        String queryString = "DELETE FROM RespuestaEncuesta";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}

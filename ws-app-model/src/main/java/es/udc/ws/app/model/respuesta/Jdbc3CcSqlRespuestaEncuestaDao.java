package es.udc.ws.app.model.respuesta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import es.udc.ws.app.model.servicio.exceptions.DataAccessRuntimeException; //añadida por un problema de Heisenburg en los tests

public class Jdbc3CcSqlRespuestaEncuestaDao extends AbstractSqlRespuestaEncuestaDao {
    @Override
    public RespuestaEncuesta crearRespuesta(Connection connection, RespuestaEncuesta respuesta) {

        String queryString = "INSERT INTO RespuestaEncuesta (encuestaId, email, respuesta, fechaRespuesta) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                        queryString, Statement.RETURN_GENERATED_KEYS)) {
            // Compute a single timestamp to use both for insert and returned object
        final LocalDateTime fechaInsert =
            ((respuesta.getFechaRespuesta() != null) ? respuesta.getFechaRespuesta() : LocalDateTime.now()).withNano(0);

            int i = 1;
            preparedStatement.setLong(i++, respuesta.getEncuestaId());
            preparedStatement.setString(i++, respuesta.getEmail());
            preparedStatement.setString(i++, respuesta.isRespuestaPositiva() ? "POSITIVA" : "NEGATIVA");
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(fechaInsert));

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if(!resultSet.next()) {
                throw new SQLException(
                        "JDBC driver did not return generated key.");
            }

            Long respuestaId = resultSet.getLong(1);

            // NOTE: counters are updated at service level (business logic). DAO only persists the response.

            // Crear nuevo objeto con datos correctos (no mutamos el objeto de entrada y no re-leemos de BD)
            RespuestaEncuesta createdRespuesta = new RespuestaEncuesta(
                respuesta.getEncuestaId(),
                respuesta.getEmail(),
                respuesta.isRespuestaPositiva(),  // valor boolean del input, NO re-leído de la BD
                fechaInsert
            );
            createdRespuesta.setRespuestaId(respuestaId);
            return createdRespuesta;

        } catch (SQLException e) {
            throw new DataAccessRuntimeException(e);
        }

    }
}

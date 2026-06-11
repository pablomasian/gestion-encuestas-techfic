package es.udc.ws.app.model.encuesta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

public class Jdbc3CcSqlEncuestaDao extends AbstractSqlEncuestaDao {

    @Override
    public Encuesta crear(Connection connection, Encuesta encuesta){

        /* Create "queryString". */
        String queryString = "INSERT INTO Encuesta"
                + " (pregunta, fechaHoraFin, cancelada, fechaCreacion)"
                + " VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                        queryString, Statement.RETURN_GENERATED_KEYS)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setString(i++, encuesta.getPregunta());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(encuesta.getFechaHoraFin()));
            preparedStatement.setBoolean(i++, encuesta.isCancelada());

            /* Execute query. */
            preparedStatement.executeUpdate();

            /* Get generated identifier. */
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (!resultSet.next()) {
                throw new SQLException(
                        "JDBC driver did not return generated key.");
            }
            Long encuestaId = resultSet.getLong(1);
            
            /* Set the generated id. */
            encuesta.setEncuestaId(encuestaId);

            /* Get the creation date. */
            try (PreparedStatement ps2 = connection.prepareStatement(
                    "SELECT fechaCreacion FROM Encuesta WHERE encuestaId = ?")) {
                ps2.setLong(1, encuestaId);
                try (ResultSet rs2 = ps2.executeQuery()) {
                    if (rs2.next()) {
                        encuesta.setFechaCreacion(rs2.getTimestamp(1).toLocalDateTime());
                    }
                }
            }

            /* Return encuesta. */
            return encuesta;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}

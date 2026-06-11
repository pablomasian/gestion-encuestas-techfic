package es.udc.ws.app.test.model.appservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.servicio.EncuestaServicio;
import es.udc.ws.app.model.servicio.EncuestaServicioImpl;
import es.udc.ws.app.model.servicio.exceptions.EncuestaFinalizadaException;
import es.udc.ws.app.model.util.ModelConstants;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;

public class ExcepcionFinalizadaTest {

    @BeforeAll
    public static void init() throws Exception {
        DataSource ds = new SimpleDataSource();
        DataSourceLocator.addDataSource(ModelConstants.APP_DATA_SOURCE, ds);
    }

    @BeforeEach
    void cleanDatabase() { //limpia la bbdd antes de cada test
        try (Connection connection = DataSourceLocator.getDataSource(ModelConstants.APP_DATA_SOURCE).getConnection()) {
            connection.setAutoCommit(false);
            try (var ps1 = connection.prepareStatement("DELETE FROM RespuestaEncuesta");
                 var ps2 = connection.prepareStatement("DELETE FROM Encuesta")) {
                ps1.executeUpdate();
                ps2.executeUpdate(); 
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testResponderEncuestaFinalizadaLanza() {
        EncuestaServicio service = new EncuestaServicioImpl();
        Encuesta encuesta = new Encuesta("Finalizada (final test)", LocalDateTime.now().plusDays(1));
        
        try {
            Encuesta e = service.crearEncuesta(encuesta);

            // Forzamos en la BD que la fecha de fin sea pasada
            try (Connection connection = DataSourceLocator.getDataSource(ModelConstants.APP_DATA_SOURCE).getConnection()) {
                try (PreparedStatement ps = connection.prepareStatement("UPDATE Encuesta SET fechaHoraFin = ? WHERE encuestaId = ?")) {
                    ps.setTimestamp(1, java.sql.Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
                    ps.setLong(2, e.getEncuestaId());
                    ps.executeUpdate();
                }
            }

            assertThrows(EncuestaFinalizadaException.class, () ->
                service.responderEncuesta(e.getEncuestaId(), "final@x.com", true)
            );
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

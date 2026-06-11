package es.udc.ws.app.test.model.appservice;

import java.sql.Connection;
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
import es.udc.ws.app.model.servicio.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.util.ModelConstants;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;
import es.udc.ws.app.model.encuesta.SqlEncuestaDao;
import es.udc.ws.app.model.encuesta.SqlEncuestaDaoFactory;
import es.udc.ws.app.model.respuesta.SqlRespuestaEncuestaDao;
import es.udc.ws.app.model.respuesta.SqlRespuestaEncuestaDaoFactory;

public class ExcepcionCanceladaTest {

    private static SqlEncuestaDao encuestaDao;
    private static SqlRespuestaEncuestaDao respuestaDao;

    @BeforeAll
    public static void init() throws Exception {
        DataSource ds = new SimpleDataSource();
        DataSourceLocator.addDataSource(ModelConstants.APP_DATA_SOURCE, ds);
        // Ensure DAO factories available for cleanup
        encuestaDao = SqlEncuestaDaoFactory.getDao();
        respuestaDao = SqlRespuestaEncuestaDaoFactory.getDao();
    }

    @BeforeEach
    void cleanDatabase() {
        try (Connection connection = DataSourceLocator.getDataSource(ModelConstants.APP_DATA_SOURCE).getConnection()) {
            connection.setAutoCommit(false);
            try {
                // Use DAO bulk cleanup helpers to avoid raw SQL in tests
                respuestaDao.eliminarTodas(connection);
                encuestaDao.eliminarTodas(connection);
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
    void testResponderEncuestaCanceladaLanza() {
        EncuestaServicio service = new EncuestaServicioImpl();
        Encuesta encuesta = new Encuesta("Para cancelar (cancel test)", LocalDateTime.now().plusDays(2));
        
        try {
            Encuesta e = service.crearEncuesta(encuesta);
            service.cancelarEncuesta(e.getEncuestaId());

            assertThrows(EncuestaCanceladaException.class, () ->
                service.responderEncuesta(e.getEncuestaId(), "cancel@x.com", true)
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

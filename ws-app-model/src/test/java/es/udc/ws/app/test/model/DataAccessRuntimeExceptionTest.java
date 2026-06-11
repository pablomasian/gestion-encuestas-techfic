package es.udc.ws.app.test.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import es.udc.ws.app.model.respuesta.RespuestaEncuesta;
import es.udc.ws.app.model.respuesta.SqlRespuestaEncuestaDao;
import es.udc.ws.app.model.respuesta.SqlRespuestaEncuestaDaoFactory;
import es.udc.ws.app.model.servicio.exceptions.DataAccessRuntimeException;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;

/**
 * Minimal test to verify low-level SQLExceptions are wrapped into
 * DataAccessRuntimeException by the DAO layer.
 */
class DataAccessRuntimeExceptionTest {

	private static SqlRespuestaEncuestaDao respuestaDao;

	@BeforeAll
	static void init() {
		DataSource dataSource = new SimpleDataSource();
		DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);
		respuestaDao = SqlRespuestaEncuestaDaoFactory.getDao();
	}

	@Test
	void testCrearRespuestaConEncuestaInexistenteLanzaDataAccessRuntimeException() throws SQLException {
		// Preparar una respuesta con FK inexistente (encuestaId no existente)
		RespuestaEncuesta respuesta = new RespuestaEncuesta(-1L, "user@example.com", true);

		try (Connection connection = DataSourceLocator.getDataSource(APP_DATA_SOURCE).getConnection()) {
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			connection.setAutoCommit(false);

			assertThrows(DataAccessRuntimeException.class, () -> {
				respuestaDao.crearRespuesta(connection, respuesta);
			});

			connection.rollback();
		}
	}

	@Test
	void testDataAccessRuntimeExceptionConstructorPreservaCausa() {
		Exception cause = new Exception("boom");
		DataAccessRuntimeException ex = new DataAccessRuntimeException(cause);
		// Verifica que la causa se preserve
		org.junit.jupiter.api.Assertions.assertEquals(cause, ex.getCause());
	}
}


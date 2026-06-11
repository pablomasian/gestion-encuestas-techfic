package es.udc.ws.app.model.encuesta;

import java.sql.Connection;
import java.util.List;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

// DAO de persistencia para Encuesta. En esta iteración solo se requiere create.
public interface SqlEncuestaDao {
    
    public Encuesta crear(Connection connection, Encuesta encuesta) ;

    // Obtener encuesta por id con recuento de respuestas
    public Encuesta buscar(Connection connection, Long encuestaId) throws InstanceNotFoundException;

    // Buscar encuestas por palabra clave y estado (finalizada/no finalizada)
    public List<Encuesta> buscarPorPalabraClave(boolean soloNoFinalizadas, String palabraClave);

    // marcar una encuesta como cancelada
    public void actualizar(Connection connection, Encuesta encuesta) throws InstanceNotFoundException;

    // eliminar una encuesta (solo para el testing)
    public void eliminar(Connection connection, Long encuestaId) throws InstanceNotFoundException;

    // Eliminar todas las encuestas (uso en tests)
    public void eliminarTodas(Connection connection);
}
package es.udc.ws.app.model.respuesta;

import java.sql.Connection;
import java.util.List;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

public interface SqlRespuestaEncuestaDao {
    //inserta o actualiza respuesta de empleado para encuesta
    public RespuestaEncuesta crearRespuesta(Connection connection, RespuestaEncuesta respuesta) ;
    
    //obtener respuesta por id
    public RespuestaEncuesta buscar(Connection connection, Long respuestaId) throws InstanceNotFoundException;

    //obtener respuestas de una encuesta
    public List<RespuestaEncuesta> obtenerRespuestas(Connection connection, Long encuestaId, boolean soloPositivas);


    //actualizar respuesta de empleado para encuesta
    public void actualizarRespuesta(Connection connection, RespuestaEncuesta respuesta) throws InstanceNotFoundException;
    
    //eliminar respuesta de empleado para encuesta
    public void eliminar(Connection connection, Long respuestaId) throws InstanceNotFoundException;


    //helpers
    // Eliminar todas las respuestas (uso en tests)
    public void eliminarTodas(Connection connection);
   
    // Buscar respuesta por encuestaId y email
    public RespuestaEncuesta buscarPorEncuestaYEmail(Connection connection, Long encuestaId, String email) throws es.udc.ws.util.exceptions.InstanceNotFoundException;

}

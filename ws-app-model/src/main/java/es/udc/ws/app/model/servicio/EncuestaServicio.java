package es.udc.ws.app.model.servicio;

import java.util.List;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.respuesta.RespuestaEncuesta;
import es.udc.ws.app.model.servicio.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.servicio.exceptions.EncuestaFinalizadaException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

public interface EncuestaServicio {

    public Encuesta crearEncuesta(Encuesta encuesta) 
            throws InputValidationException;

    // Buscar encuestas por palabra clave y estado (finalizada/no finalizada)
    public List<Encuesta> buscarPorPalabraClave(String palabraClave, boolean soloNoFinalizadas)
            throws InputValidationException;

    // Obtener encuesta por id con recuento de respuestas
    public Encuesta buscar(Long encuestaId) 
            throws InstanceNotFoundException , InputValidationException;

    //permite a un empleado responder una encuesta
    public RespuestaEncuesta responderEncuesta(Long encuestaId, String email, boolean respuestaPositiva) 
            throws InstanceNotFoundException, EncuestaCanceladaException, EncuestaFinalizadaException;

    //permite actualizar una encuesta antes de su fecha de finalizacion
    public Encuesta cancelarEncuesta(Long encuestaId) 
            throws InstanceNotFoundException, EncuestaCanceladaException, EncuestaFinalizadaException;
    
    //obtener respuestas de una encuesta (todas o solo positivas)
    public List<RespuestaEncuesta> obtenerRespuestasEncuesta(Long encuestaId, boolean soloPositivas)
            throws InstanceNotFoundException, InputValidationException;

   
}

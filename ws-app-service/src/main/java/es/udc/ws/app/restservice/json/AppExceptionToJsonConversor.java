package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.app.model.servicio.exceptions.DataAccessRuntimeException;
import es.udc.ws.app.model.servicio.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.servicio.exceptions.EncuestaFinalizadaException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

public class AppExceptionToJsonConversor {

    public static ObjectNode toEncuestaCanceladaException(EncuestaCanceladaException ex) {
        
        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();
        
        exceptionObject.put("errorType", "EncuestaCancelada");
        exceptionObject.put("message", ex.getMessage());
        
        return exceptionObject;
    }

    public static ObjectNode toEncuestaFinalizadaException(EncuestaFinalizadaException ex) {
        
        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();
        
        exceptionObject.put("errorType", "EncuestaFinalizada");
        exceptionObject.put("message", ex.getMessage());
        
        return exceptionObject;
    }

    public static ObjectNode toDataAccessRuntimeException(DataAccessRuntimeException ex) {
        
        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();
        
        exceptionObject.put("errorType", "DataAccessRuntimeException");
        exceptionObject.put("message", ex.getMessage());
        
        return exceptionObject;
    }

    public static ObjectNode toInstanceNotFoundException(InstanceNotFoundException ex) {
        
        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();
        
        exceptionObject.put("errorType", "InstanceNotFound");
        exceptionObject.put("message", ex.getMessage());
        
        return exceptionObject;
    }
}

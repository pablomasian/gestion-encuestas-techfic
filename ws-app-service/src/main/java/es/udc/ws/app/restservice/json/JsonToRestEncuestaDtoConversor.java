package es.udc.ws.app.restservice.json;

import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.app.restservice.dto.RestEncuestaDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

public class JsonToRestEncuestaDtoConversor {
    public static ObjectNode toObjectNode(RestEncuestaDto encuesta) {
        
        ObjectNode encuestaNode = JsonNodeFactory.instance.objectNode();
        
        if (encuesta.getEncuestaId() !=null) {
            encuestaNode.put("encuestaId", encuesta.getEncuestaId());
        } 

        encuestaNode.put("encuestaId", encuesta.getEncuestaId());
        encuestaNode.put("pregunta", encuesta.getPregunta());
        encuestaNode.put("fechaCreacion", encuesta.getFechaCreacion());
        encuestaNode.put("fechaHoraFin", encuesta.getFechaHoraFin());
        encuestaNode.put("cancelada", encuesta.isCancelada());
        encuestaNode.put("respuestasPositivas", encuesta.getRespuestasPositivas());
        encuestaNode.put("respuestasNegativas", encuesta.getRespuestasNegativas());  
        
        return encuestaNode;
    }

    public static ArrayNode toArrayNode(List<RestEncuestaDto> encuestas) {
        
        ArrayNode encuestasNode = JsonNodeFactory.instance.arrayNode();
        for (int i=0; i<encuestas.size(); i++) {
            RestEncuestaDto encuestaDto = encuestas.get(i);
            ObjectNode encuestaObject = toObjectNode(encuestaDto);
            encuestaObject.remove("fechaCreacion"); // Requisito: en la lista no incluir fechaCreacion

            encuestasNode.add(encuestaObject);
        }
        return encuestasNode;
    }

    public static RestEncuestaDto toRestEncuestaDto(InputStream jsonEncuesta) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonEncuesta);

            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } 
            else {
                ObjectNode encuestaObject = (ObjectNode) rootNode;

                JsonNode encuestaIdNode = encuestaObject.get("encuestaId");
                Long encuestaId = (encuestaIdNode != null) ? encuestaIdNode.longValue() : null;

                JsonNode preguntaNode = encuestaObject.get("pregunta");
                String pregunta = (preguntaNode != null && preguntaNode.textValue() != null) ? preguntaNode.textValue().trim() : "";
                
                JsonNode fechaFinNode = encuestaObject.get("fechaHoraFin");
                String fechaHoraFin = (fechaFinNode != null && fechaFinNode.textValue() != null) ? fechaFinNode.textValue().trim() : null;
                
                JsonNode canceladaNode = encuestaObject.get("cancelada");
                boolean cancelada = (canceladaNode != null) ? canceladaNode.booleanValue() : false;
                
                JsonNode fechaCreaNode = encuestaObject.get("fechaCreacion");
                String fechaCreacion = (fechaCreaNode != null && fechaCreaNode.textValue() != null) ? fechaCreaNode.textValue().trim() : null;
                
                JsonNode positivasNode = encuestaObject.get("respuestasPositivas");
                int respuestasPositivas = (positivasNode != null) ? positivasNode.intValue() : 0;
                
                JsonNode negativasNode = encuestaObject.get("respuestasNegativas");
                int respuestasNegativas = (negativasNode != null) ? negativasNode.intValue() : 0;

                return new RestEncuestaDto(encuestaId, pregunta, fechaHoraFin, cancelada, fechaCreacion, respuestasPositivas, respuestasNegativas);
            }

        } catch (ParsingException ex) {
            throw ex; 
        } catch (Exception e) {
            throw new ParsingException(e);
        }

    }
}

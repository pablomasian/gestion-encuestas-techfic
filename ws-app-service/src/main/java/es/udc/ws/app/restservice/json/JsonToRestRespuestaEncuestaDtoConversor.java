package es.udc.ws.app.restservice.json;

import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.app.restservice.dto.RestRespuestaEncuestaDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

public class JsonToRestRespuestaEncuestaDtoConversor {
    public static ObjectNode toObjectNode(RestRespuestaEncuestaDto respuesta) {
        
        ObjectNode respuestaEncuestaNode = JsonNodeFactory.instance.objectNode();
        
        if (respuesta.getRespuestaId() !=null) {
            respuestaEncuestaNode.put("respuestaId", respuesta.getRespuestaId());
        } 

        respuestaEncuestaNode.put("encuestaId", respuesta.getEncuestaId());
        respuestaEncuestaNode.put("respuestaId", respuesta.getRespuestaId());
        respuestaEncuestaNode.put("email", respuesta.getEmail());
        respuestaEncuestaNode.put("respuestaPositiva", respuesta.isRespuestaPositiva());
        // No incluir fechaRespuesta en ninguna respuesta
        
        return respuestaEncuestaNode;
    }

    public static ArrayNode toArrayNode(List<RestRespuestaEncuestaDto> respuestas) {
        
        ArrayNode respuestasNode = JsonNodeFactory.instance.arrayNode();
        for (int i = 0; i < respuestas.size(); i++) {
            RestRespuestaEncuestaDto respuestaDto = respuestas.get(i);
            ObjectNode respuestaObject = toObjectNode(respuestaDto);
            respuestasNode.add(respuestaObject);
        }
        return respuestasNode;
    }

    public static RestRespuestaEncuestaDto toRestRespuestaEncuestaDto(InputStream jsonRespuesta) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonRespuesta);

            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } 
            else {
                ObjectNode respuestaObject = (ObjectNode) rootNode;

                JsonNode respuestaIdNode = respuestaObject.get("respuestaId");
                Long respuestaId = (respuestaIdNode != null) ? respuestaIdNode.longValue() : null;
                
                Long encuestaId = respuestaObject.get("encuestaId").longValue();
                String email = respuestaObject.get("email").textValue().trim();
                boolean respuestaPositiva = respuestaObject.get("respuestaPositiva").booleanValue();
                
                JsonNode fechaRespuestaNode = respuestaObject.get("fechaRespuesta");
                String fechaRespuesta = (fechaRespuestaNode != null) ? fechaRespuestaNode.textValue().trim() : null;

                return new RestRespuestaEncuestaDto(encuestaId, respuestaId, email, respuestaPositiva, fechaRespuesta);
            }

        } catch (ParsingException ex) {
            throw ex; 
        } catch (Exception e) {
            throw new ParsingException(e);
        }

    }
}

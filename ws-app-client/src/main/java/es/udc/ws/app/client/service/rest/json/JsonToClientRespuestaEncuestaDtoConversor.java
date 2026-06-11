package es.udc.ws.app.client.service.rest.json;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.app.client.service.dto.ClientRespuestaEncuestaDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

public class JsonToClientRespuestaEncuestaDtoConversor {

    public static ClientRespuestaEncuestaDto toClientRespuestaEncuestaDto(InputStream jsonRespuesta)
            throws ParsingException {
        try {

            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonRespuesta);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                return toClientRespuestaEncuestaDto(rootNode);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    public static List<ClientRespuestaEncuestaDto> toClientRespuestaEncuestaDtos(InputStream jsonRespuestas)
            throws ParsingException {
        try {

            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonRespuestas);
            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (array expected)");
            } else {
                ArrayNode respuestasArray = (ArrayNode) rootNode;
                List<ClientRespuestaEncuestaDto> respuestaDtos = new ArrayList<>(respuestasArray.size());
                for (JsonNode respuestaNode : respuestasArray) {
                    respuestaDtos.add(toClientRespuestaEncuestaDto(respuestaNode));
                }

                return respuestaDtos;
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static ClientRespuestaEncuestaDto toClientRespuestaEncuestaDto(JsonNode respuestaNode)
            throws ParsingException {
        if (respuestaNode.getNodeType() != JsonNodeType.OBJECT) {
            throw new ParsingException("Unrecognized JSON (object expected)");
        } else {
            ObjectNode respuestaObject = (ObjectNode) respuestaNode;

            JsonNode respuestaIdNode = respuestaObject.get("respuestaId");
            Long respuestaId = (respuestaIdNode != null) ? respuestaIdNode.longValue() : null;

            Long encuestaId = respuestaObject.get("encuestaId").longValue();
            String email = respuestaObject.get("email").textValue().trim();
            boolean respuestaPositiva = respuestaObject.get("respuestaPositiva").booleanValue();

            return new ClientRespuestaEncuestaDto(respuestaId, encuestaId, email, respuestaPositiva);
        }
    }

    public static ObjectNode toObjectNode(ClientRespuestaEncuestaDto respuesta) throws ParsingException {

        ObjectNode respuestaObject = JsonNodeFactory.instance.objectNode();

        if (respuesta.getRespuestaId() != null) {
            respuestaObject.put("respuestaId", respuesta.getRespuestaId());
        }
        respuestaObject.put("encuestaId", respuesta.getEncuestaId());
        respuestaObject.put("email", respuesta.getEmail());
        respuestaObject.put("respuestaPositiva", respuesta.isRespuestaPositiva());

        return respuestaObject;
    }

}

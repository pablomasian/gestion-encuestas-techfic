package es.udc.ws.app.client.service.rest.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.app.client.service.dto.ClientEncuestaDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

public class JsonToClientEncuestaDtoConversor {

    public static ObjectNode toObjectNode(ClientEncuestaDto encuesta) throws IOException {

        ObjectNode encuestaObject = JsonNodeFactory.instance.objectNode();

        if (encuesta.getEncuestaId() != null) {
            encuestaObject.put("encuestaId", encuesta.getEncuestaId());
        }
        encuestaObject.put("pregunta", encuesta.getPregunta())
                .put("fechaHoraFin", encuesta.getFechaHoraFin())
                .put("cancelada", encuesta.isCancelada())
                .put("fechaCreacion", encuesta.getFechaCreacion())
                .put("respuestasPositivas", encuesta.getRespuestasPositivas())
                .put("totalRespuestas", encuesta.getTotalRespuestas());

        return encuestaObject;
    }

    public static ClientEncuestaDto toClientEncuestaDto(InputStream jsonEncuesta) throws ParsingException {
        try {

            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonEncuesta);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                return toClientEncuestaDto(rootNode);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    public static List<ClientEncuestaDto> toClientEncuestaDtos(InputStream jsonEncuestas) throws ParsingException {
        try {

            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonEncuestas);
            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (array expected)");
            } else {
                ArrayNode encuestasArray = (ArrayNode) rootNode;
                List<ClientEncuestaDto> encuestaDtos = new ArrayList<>(encuestasArray.size());
                for (JsonNode encuestaNode : encuestasArray) {
                    encuestaDtos.add(toClientEncuestaDto(encuestaNode));
                }

                return encuestaDtos;
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static ClientEncuestaDto toClientEncuestaDto(JsonNode encuestaNode) throws ParsingException {
        if (encuestaNode.getNodeType() != JsonNodeType.OBJECT) {
            throw new ParsingException("Unrecognized JSON (object expected)");
        } else {
            ObjectNode encuestaObject = (ObjectNode) encuestaNode;

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
                
                int totalRespuestas = respuestasPositivas + respuestasNegativas;

                return new ClientEncuestaDto(encuestaId, pregunta, fechaHoraFin, cancelada, fechaCreacion,
                    respuestasPositivas, totalRespuestas);
        }
    }

}
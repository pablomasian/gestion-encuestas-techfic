package es.udc.ws.app.client.service.rest.json;

import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import es.udc.ws.app.client.service.exceptions.ClientEncuestaCanceladaException;
import es.udc.ws.app.client.service.exceptions.ClientEncuestaFinalizadaException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

public class JsonToClientExceptionConversor {

    public static Exception fromBadRequestErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("InputValidation")) {
                    return toInputValidationException(rootNode);
                } else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    public static Exception fromNotFoundErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("InstanceNotFound")) {
                    return toInstanceNotFoundException(rootNode);
                } else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static InputValidationException toInputValidationException(JsonNode rootNode) {
        JsonNode messageNode = rootNode.get("message");
        String message = (messageNode != null && messageNode.textValue() != null) ? messageNode.textValue() : "Validation error";
        return new InputValidationException(message);
    }

    private static InstanceNotFoundException toInstanceNotFoundException(JsonNode rootNode) {
        JsonNode idNode = rootNode.get("instanceId");
        JsonNode typeNode = rootNode.get("instanceType");
        String instanceId = (idNode != null) ? idNode.textValue() : "unknown";
        String instanceType = (typeNode != null) ? typeNode.textValue() : "unknown";
        return new InstanceNotFoundException(instanceId, instanceType);
    }

    public static Exception fromForbiddenErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("EncuestaCancelada")) {
                    return toEncuestaCanceladaException(rootNode);
                } else if (errorType.equals("EncuestaFinalizada")) {
                    return toEncuestaFinalizadaException(rootNode);
                } else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static ClientEncuestaCanceladaException toEncuestaCanceladaException(JsonNode rootNode) {
        // try explicit field first
        Long encuestaId = null;
        JsonNode idNode = rootNode.get("encuestaId");
        if (idNode != null && !idNode.isNull()) {
            encuestaId = idNode.longValue();
        } else {
            String message = (rootNode.get("message") != null && !rootNode.get("message").isNull())
                    ? rootNode.get("message").textValue() : null;
            encuestaId = extractIdFromMessage(message);
        }
        return new ClientEncuestaCanceladaException(encuestaId);
    }

    public static Exception fromGoneErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("EncuestaFinalizada")) {
                    return toEncuestaFinalizadaException(rootNode);
                } else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static ClientEncuestaFinalizadaException toEncuestaFinalizadaException(JsonNode rootNode) {
        Long encuestaId = null;
        JsonNode idNode = rootNode.get("encuestaId");
        if (idNode != null && !idNode.isNull()) {
            encuestaId = idNode.longValue();
        } else {
            String message = (rootNode.get("message") != null && !rootNode.get("message").isNull())
                    ? rootNode.get("message").textValue() : null;
            encuestaId = extractIdFromMessage(message);
        }
        return new ClientEncuestaFinalizadaException(encuestaId);
    }

    private static Long extractIdFromMessage(String message) {
        if (message == null) {
            return null;
        }
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\d+").matcher(message);
        if (m.find()) {
            try {
                return Long.valueOf(m.group());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
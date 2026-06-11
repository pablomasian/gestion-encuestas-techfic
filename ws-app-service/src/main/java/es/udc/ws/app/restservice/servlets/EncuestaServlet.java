package es.udc.ws.app.restservice.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.servicio.EncuestaServicioFactory;
import es.udc.ws.app.model.servicio.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.servicio.exceptions.EncuestaFinalizadaException;
import es.udc.ws.app.model.respuesta.RespuestaEncuesta;
import es.udc.ws.app.restservice.dto.EncuestaToRestEncuestaDtoConversor;
import es.udc.ws.app.restservice.dto.RestEncuestaDto;
import es.udc.ws.app.restservice.dto.RespuestaEncuestaToRestRespuestaEncuestaDtoConversor;
import es.udc.ws.app.restservice.json.AppExceptionToJsonConversor;
import es.udc.ws.app.restservice.json.JsonToRestEncuestaDtoConversor;
import es.udc.ws.app.restservice.json.JsonToRestRespuestaEncuestaDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class EncuestaServlet extends RestHttpServletTemplate {
    
    @Override
    protected void processPost(HttpServletRequest request, HttpServletResponse response) 
        throws IOException, InputValidationException , InstanceNotFoundException {

        String path = ServletUtils.normalizePath(request.getPathInfo());

        // Si la ruta indica respuestas: POST /encuestas/{id}/respuestas
        if (path != null && path.length() > 0) {
            String[] parts = path.split("/");
            if (parts.length == 3 && "respuestas".equals(parts[2])) {
                Long encuestaId = Long.valueOf(parts[1]);
                
                es.udc.ws.app.restservice.dto.RestRespuestaEncuestaDto respuestaDto = 
                    JsonToRestRespuestaEncuestaDtoConversor.toRestRespuestaEncuestaDto(request.getInputStream());

                try {
                    RespuestaEncuesta respuesta = EncuestaServicioFactory.getService().responderEncuesta(
                            encuestaId, respuestaDto.getEmail(), respuestaDto.isRespuestaPositiva());

                    respuestaDto = RespuestaEncuestaToRestRespuestaEncuestaDtoConversor.toRespuestaEncuestaDto(respuesta);

                    ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_CREATED,
                            JsonToRestRespuestaEncuestaDtoConversor.toObjectNode(respuestaDto), null);

                } catch (EncuestaFinalizadaException ex) {
                    ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_FORBIDDEN,
                            AppExceptionToJsonConversor.toEncuestaFinalizadaException(ex), null);
                } catch (EncuestaCanceladaException ex) {
                    ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_FORBIDDEN,
                            AppExceptionToJsonConversor.toEncuestaCanceladaException(ex), null);
                }
                return;
            }
            
            // Si la ruta indica cancelación: POST /encuestas/{id}/cancelar , dividimos url
            if (parts.length == 3 && "cancelar".equals(parts[2])) {
                Long encuestaId = Long.valueOf(parts[1]);
                try {
                    EncuestaServicioFactory.getService().cancelarEncuesta(encuestaId);
                } catch (EncuestaFinalizadaException ex) {
                    ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_FORBIDDEN,
                            AppExceptionToJsonConversor.toEncuestaFinalizadaException(ex), null);
                    return;
                } catch (EncuestaCanceladaException ex) {
                    ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_FORBIDDEN,
                            AppExceptionToJsonConversor.toEncuestaCanceladaException(ex), null);
                    return;
                } catch (InstanceNotFoundException ex) {
                    ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_NOT_FOUND, null, null);
                    return;
                }
                 //si todo va bien devovlvemos 204 (solicitud correcta sin contenido)
                ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_NO_CONTENT, null, null); 
                return;
            }
        }

        //si la url no indica cancelacion, creamos una nueva encuesta: POST /encuestas
        RestEncuestaDto encuestaDto = JsonToRestEncuestaDtoConversor.toRestEncuestaDto(request.getInputStream()); //lectura del cuerpo de la solicitud
        Encuesta encuesta = EncuestaToRestEncuestaDtoConversor.toEncuesta(encuestaDto); //conversion a entidad de negocio

        encuesta = EncuestaServicioFactory.getService().crearEncuesta(encuesta); //creacion de la encuesta

        encuestaDto = EncuestaToRestEncuestaDtoConversor.toRestEncuestaDto(encuesta); //conversion a dto de respuesta
        String encuestaURL =  ServletUtils.normalizePath(request.getRequestURL().toString()) + "/" + encuesta.getEncuestaId(); //construccion de la URL de la nueva encuesta
        Map<String, String> headers = new HashMap<>(1); //creacion de cabeceras de respuesta para incluir la URL de la nueva encuesta
        headers.put("Location", encuestaURL); 
        ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_CREATED, //mandamos respuesta con codigo 201 (creado)
             JsonToRestEncuestaDtoConversor.toObjectNode(encuestaDto), headers);

    }
    
    @Override
    protected void processGet(HttpServletRequest request, HttpServletResponse response) throws IOException, 
            InputValidationException {

        String path = ServletUtils.normalizePath(request.getPathInfo()); //obtencion de la ruta de la solicitud

        if (path == null || path.length() == 0) { //si no hay ruta, buscamos encuestas por palabra clave: GET /encuestas?keywords={palabra}&soloNoFinalizadas={boolean}
            String keyWords = request.getParameter("keywords");
            //siempre devolver solo encuestas no finalizadas (requerimientos capa servicio)
            boolean soloNoFinalizadas = true;

            List<Encuesta> encuestas = EncuestaServicioFactory.getService().buscarPorPalabraClave(keyWords, soloNoFinalizadas);
            List<RestEncuestaDto> encuestaDtos = EncuestaToRestEncuestaDtoConversor.toRestEncuestaDtos(encuestas);

            ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_OK, 
                JsonToRestEncuestaDtoConversor.toArrayNode(encuestaDtos), null); //devolvemos codigo 200 (OK) con la lista de encuestas
        } else {
            try {
                // Soporte de ruta anidada: /encuestas/{id}/respuestas?soloPositivas={true|false}
                String[] parts = path.split("/");
                if (parts.length == 3 && parts[2].equals("respuestas")) {
                    Long encuestaId = Long.valueOf(parts[1]);
                    String soloPositivasParam = request.getParameter("soloPositivas");
                    boolean soloPositivas = soloPositivasParam != null && Boolean.parseBoolean(soloPositivasParam);
                    try {
                        List<RespuestaEncuesta> respuestas = EncuestaServicioFactory.getService()
                            .obtenerRespuestasEncuesta(encuestaId, soloPositivas);
                        List<es.udc.ws.app.restservice.dto.RestRespuestaEncuestaDto> respuestaDtos =
                            RespuestaEncuestaToRestRespuestaEncuestaDtoConversor.toRestRespuestaEncuestaDtos(respuestas);
                        ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_OK,
                            JsonToRestRespuestaEncuestaDtoConversor.toArrayNode(respuestaDtos), null);
                    } catch (InstanceNotFoundException ex) {
                        ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_NOT_FOUND,
                            AppExceptionToJsonConversor.toInstanceNotFoundException(ex), null);
                    }
                    return;
                }

                Long encuestaId = Long.valueOf(path.substring(1));  //si la ruta incluye id, buscamos encuesta por id: GET /encuestas/{id}
                try { 
                    Encuesta encuesta = EncuestaServicioFactory.getService().buscar(encuestaId); //buscamos la encuesta por id
                    RestEncuestaDto encuestaDto = EncuestaToRestEncuestaDtoConversor.toRestEncuestaDto(encuesta); //conversion a dto de respuesta
                    ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_OK, //devolvemos codigo 200 (OK)
                            JsonToRestEncuestaDtoConversor.toObjectNode(encuestaDto), null);
                } catch (InstanceNotFoundException ex) { //si no se encuentra la encuesta, devolvemos 404 (no encontrado)
                    ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_NOT_FOUND,
                        AppExceptionToJsonConversor.toInstanceNotFoundException(ex), null);
                }
            } catch (NumberFormatException ex) {
                ObjectNode errorNode = JsonNodeFactory.instance.objectNode();
                errorNode.put("errorType", "BadRequest");
                errorNode.put("message", "Invalid path: " + path);
                ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    errorNode, null);
            }
        }
    } 
}

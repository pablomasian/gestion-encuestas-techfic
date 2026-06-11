package es.udc.ws.app.restservice.servlets;

import es.udc.ws.app.model.respuesta.RespuestaEncuesta;
import es.udc.ws.app.model.servicio.EncuestaServicioFactory;
import es.udc.ws.app.model.servicio.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.servicio.exceptions.EncuestaFinalizadaException;
import es.udc.ws.app.restservice.dto.RespuestaEncuestaToRestRespuestaEncuestaDtoConversor;
import es.udc.ws.app.restservice.dto.RestRespuestaEncuestaDto;
import es.udc.ws.app.restservice.json.AppExceptionToJsonConversor;
import es.udc.ws.app.restservice.json.JsonToRestRespuestaEncuestaDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class RespuestaEncuestaServlet extends RestHttpServletTemplate {

    @Override
    protected void processPost(HttpServletRequest request, HttpServletResponse response) 
    throws IOException, InputValidationException, InstanceNotFoundException {

        RestRespuestaEncuestaDto respuestaDto = JsonToRestRespuestaEncuestaDtoConversor.toRestRespuestaEncuestaDto(request.getInputStream());

        try {
            RespuestaEncuesta respuesta = EncuestaServicioFactory.getService().responderEncuesta(
                    respuestaDto.getEncuestaId(), respuestaDto.getEmail(), respuestaDto.isRespuestaPositiva());

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
    }

    @Override
    protected void processGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
            InputValidationException, InstanceNotFoundException {

        String StringEncuestaId = request.getParameter("encuestaId");
        String StringSoloAfirmativas = request.getParameter("soloAfirmativas");
        
        if (StringEncuestaId == null) {
            throw new InputValidationException("encuestaId es obligatorio!");
        }

        Long encuestaId = Long.valueOf(StringEncuestaId);
        boolean soloAfirmativas = (StringSoloAfirmativas != null) && Boolean.parseBoolean(StringSoloAfirmativas);

        List<RespuestaEncuesta> respuestas = EncuestaServicioFactory.getService().obtenerRespuestasEncuesta(encuestaId, soloAfirmativas);
        List<RestRespuestaEncuestaDto> respuestaDtos = RespuestaEncuestaToRestRespuestaEncuestaDtoConversor.toRestRespuestaEncuestaDtos(respuestas);

        ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_OK,
                JsonToRestRespuestaEncuestaDtoConversor.toArrayNode(respuestaDtos), null);
    }
}
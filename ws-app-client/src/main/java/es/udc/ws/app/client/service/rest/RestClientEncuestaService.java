package es.udc.ws.app.client.service.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.udc.ws.app.client.service.ClientEncuestaService;
import es.udc.ws.app.client.service.dto.ClientEncuestaDto;
import es.udc.ws.app.client.service.dto.ClientRespuestaEncuestaDto;
import es.udc.ws.app.client.service.exceptions.ClientEncuestaCanceladaException;
import es.udc.ws.app.client.service.exceptions.ClientEncuestaFinalizadaException;
import es.udc.ws.app.client.service.rest.json.JsonToClientEncuestaDtoConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientExceptionConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientRespuestaEncuestaDtoConversor;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;

public class RestClientEncuestaService implements ClientEncuestaService {

    private final static String ENDPOINT_ADDRESS_PARAMETER = "RestClientEncuestaService.endpointAddress";
    private String endpointAddress;

    //FUNC-1
    @Override
    public Long crearEncuesta(ClientEncuestaDto encuesta) throws InputValidationException {

        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.post(getEndpointAddress() + "encuestas").
                    bodyStream(toInputStream(encuesta), ContentType.create("application/json")).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientEncuestaDtoConversor.toClientEncuestaDto(response.getEntity().getContent())
                    .getEncuestaId();

        } catch (InputValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    
     // FUNC-2: Buscar encuestas por palabra clave
    @Override
    public List<ClientEncuestaDto> buscarPorPalabraClave(String palabraClave) {

        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(getEndpointAddress() +
                            "encuestas?keywords=" + URLEncoder.encode(palabraClave, "UTF-8")).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientEncuestaDtoConversor.toClientEncuestaDtos(response.getEntity()
                    .getContent());

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    // FUNC-3: Obtener encuesta por ID
    @Override
    public ClientEncuestaDto buscar(Long encuestaId) throws InstanceNotFoundException {

        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(getEndpointAddress() + "encuestas/" + encuestaId).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientEncuestaDtoConversor.toClientEncuestaDto(response.getEntity().getContent());

        } catch (InstanceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    // FUNC-4: Responder encuesta
    @Override
    public Long responderEncuesta(Long encuestaId, String email, boolean respuestaPositiva)
            throws InstanceNotFoundException, InputValidationException,
            ClientEncuestaCanceladaException, ClientEncuestaFinalizadaException {

        try {

            ClientRespuestaEncuestaDto respuestaDto = new ClientRespuestaEncuestaDto(
                    null, encuestaId, email, respuestaPositiva);

            ClassicHttpResponse response = (ClassicHttpResponse) Request.post(getEndpointAddress() + "encuestas/" +
                            encuestaId + "/respuestas")
                    .bodyStream(toInputStream(respuestaDto), ContentType.create("application/json"))
                    .execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientRespuestaEncuestaDtoConversor.toClientRespuestaEncuestaDto(
                    response.getEntity().getContent()).getRespuestaId();

        } catch (InstanceNotFoundException | InputValidationException |
                ClientEncuestaCanceladaException | ClientEncuestaFinalizadaException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // FUNC-5: Cancelar encuesta
    @Override
    public void cancelarEncuesta(Long encuestaId)
            throws InstanceNotFoundException, ClientEncuestaFinalizadaException {

        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.post(getEndpointAddress() +
                            "encuestas/" + encuestaId + "/cancelar").
                    execute().returnResponse();
                
            validateStatusCode(HttpStatus.SC_NO_CONTENT, response);

        } catch (InstanceNotFoundException | ClientEncuestaFinalizadaException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    //FUNC-6
    @Override
    public List<ClientRespuestaEncuestaDto> obtenerRespuestas(Long encuestaId, boolean soloPositivas)
            throws InstanceNotFoundException {

        try {

            String url = getEndpointAddress() + "encuestas/" + encuestaId + "/respuestas" +
                    "?soloPositivas=" + soloPositivas;

            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(url).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientRespuestaEncuestaDtoConversor
                    .toClientRespuestaEncuestaDtos(response.getEntity().getContent());

        } catch (InstanceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private synchronized String getEndpointAddress() {
        if (endpointAddress == null) {
            endpointAddress = ConfigurationParametersManager
                    .getParameter(ENDPOINT_ADDRESS_PARAMETER);
        }
        return endpointAddress;
    }

    private InputStream toInputStream(ClientEncuestaDto encuesta) {

        try {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream,
                    JsonToClientEncuestaDtoConversor.toObjectNode(encuesta));

            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private InputStream toInputStream(ClientRespuestaEncuestaDto respuesta) {

        try {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream,
                    JsonToClientRespuestaEncuestaDtoConversor.toObjectNode(respuesta));

            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void validateStatusCode(int successCode, ClassicHttpResponse response) throws Exception {

        try {

            int statusCode = response.getCode();

            // Success?
            if (statusCode == successCode) {
                return;
            }

            // Handler error
            switch (statusCode) {
                case HttpStatus.SC_NOT_FOUND -> throw JsonToClientExceptionConversor.fromNotFoundErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_BAD_REQUEST -> throw JsonToClientExceptionConversor.fromBadRequestErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_FORBIDDEN -> throw JsonToClientExceptionConversor.fromForbiddenErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_GONE -> throw JsonToClientExceptionConversor.fromGoneErrorCode(
                        response.getEntity().getContent());
                default -> throw new RuntimeException("HTTP error; status code = "
                        + statusCode);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

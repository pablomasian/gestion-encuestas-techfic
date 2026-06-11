package es.udc.ws.app.client.service.thrift;

import java.util.List;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import es.udc.ws.app.client.service.ClientEncuestaService;
import es.udc.ws.app.client.service.dto.ClientEncuestaDto;
import es.udc.ws.app.client.service.dto.ClientRespuestaEncuestaDto;
import es.udc.ws.app.client.service.exceptions.ClientEncuestaCanceladaException;
import es.udc.ws.app.client.service.exceptions.ClientEncuestaFinalizadaException;
import es.udc.ws.app.thrift.ThriftEncuestaCanceladaException;
import es.udc.ws.app.thrift.ThriftEncuestaFinalizadaException;
import es.udc.ws.app.thrift.ThriftEncuestaService;
import es.udc.ws.app.thrift.ThriftInputValidationException;
import es.udc.ws.app.thrift.ThriftInstanceNotFoundException;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

public class ThriftClientEncuestaService implements ClientEncuestaService {

    private final static String ENDPOINT_ADDRESS_PARAMETER =
            "ThriftClientEncuestaService.endpointAddress";

    private final static String endpointAddress =
            ConfigurationParametersManager.getParameter(ENDPOINT_ADDRESS_PARAMETER);

    // FUNC-1: Crear encuesta
    @Override
    public Long crearEncuesta(ClientEncuestaDto encuesta) throws InputValidationException {

        ThriftEncuestaService.Client client = getClient();

        try (TTransport transport = client.getInputProtocol().getTransport()) {

            transport.open();

            return client.crearEncuesta(
                    ClientEncuestaDtoToThriftEncuestaDtoConversor.toThriftEncuestaDto(encuesta)
            ).getEncuestaId();

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // FUNC-2: Buscar encuestas por palabra clave
    @Override
    public List<ClientEncuestaDto> buscarPorPalabraClave(String palabraClave) {

        ThriftEncuestaService.Client client = getClient();

        try (TTransport transport = client.getInputProtocol().getTransport()) {

            transport.open();

            return ClientEncuestaDtoToThriftEncuestaDtoConversor.toClientEncuestaDtos(
                    client.buscarPorPalabraClave(palabraClave)
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // FUNC-3: Buscar encuesta por ID
    @Override
    public ClientEncuestaDto buscar(Long encuestaId) throws InstanceNotFoundException, InputValidationException {

        ThriftEncuestaService.Client client = getClient();

        try (TTransport transport = client.getInputProtocol().getTransport()) {

            transport.open();

            return ClientEncuestaDtoToThriftEncuestaDtoConversor.toClientEncuestaDto(
                    client.buscar(encuestaId)
            );

        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // FUNC-4: Responder encuesta
    @Override
    public Long responderEncuesta(Long encuestaId, String email, boolean respuestaPositiva)
            throws InstanceNotFoundException, InputValidationException,
            ClientEncuestaCanceladaException, ClientEncuestaFinalizadaException {

        ThriftEncuestaService.Client client = getClient();

        try (TTransport transport = client.getInputProtocol().getTransport()) {

            transport.open();

            return client.responderEncuesta(encuestaId, email, respuestaPositiva);

        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (ThriftEncuestaCanceladaException e) {
            throw new ClientEncuestaCanceladaException(e.getEncuestaId());
        } catch (ThriftEncuestaFinalizadaException e) {
            throw new ClientEncuestaFinalizadaException(e.getEncuestaId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // FUNC-5: Cancelar encuesta
    @Override
    public void cancelarEncuesta(Long encuestaId)
            throws InstanceNotFoundException, ClientEncuestaFinalizadaException {
        
        ThriftEncuestaService.Client client = getClient();
        
        try (TTransport transport = client.getInputProtocol().getTransport()) {
            
            transport.open();
            client.cancelarEncuesta(encuestaId);
            
        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (ThriftEncuestaFinalizadaException e) {
            throw new ClientEncuestaFinalizadaException(e.getEncuestaId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // FUNC-6: Obtener respuestas
    @Override
    public List<ClientRespuestaEncuestaDto> obtenerRespuestas(Long encuestaId, boolean soloPositivas)
            throws InstanceNotFoundException {
        
        ThriftEncuestaService.Client client = getClient();
        
        try (TTransport transport = client.getInputProtocol().getTransport()) {
            
            transport.open();
            
            return ClientRespuestaEncuestaDtoToThriftRespuestaEncuestaDtoConversor.toClientRespuestaEncuestaDtos(
                    client.obtenerRespuestas(encuestaId, soloPositivas)
            );
            
        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ThriftEncuestaService.Client getClient() {

        try {

            TTransport transport = new THttpClient(endpointAddress);
            TProtocol protocol = new TBinaryProtocol(transport);

            return new ThriftEncuestaService.Client(protocol);

        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }
    }

}

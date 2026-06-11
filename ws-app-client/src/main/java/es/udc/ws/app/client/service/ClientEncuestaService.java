package es.udc.ws.app.client.service;

import java.util.List;

import es.udc.ws.app.client.service.dto.ClientEncuestaDto;
import es.udc.ws.app.client.service.dto.ClientRespuestaEncuestaDto;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.app.client.service.exceptions.ClientEncuestaCanceladaException;
import es.udc.ws.app.client.service.exceptions.ClientEncuestaFinalizadaException;

public interface ClientEncuestaService {

    public Long crearEncuesta(ClientEncuestaDto encuesta)
            throws InputValidationException;

    public List<ClientEncuestaDto> buscarPorPalabraClave(String palabraClave); // no hace falta boolean soloNoFinalizadas porque solo se pasan las no finalizadas del servicio

    public ClientEncuestaDto buscar(Long encuestaId)
            throws InstanceNotFoundException, InputValidationException;

    public Long responderEncuesta(Long encuestaId, String email, boolean respuestaPositiva)
            throws InstanceNotFoundException, InputValidationException,
            ClientEncuestaCanceladaException, ClientEncuestaFinalizadaException;

    public void cancelarEncuesta(Long encuestaId)
            throws InstanceNotFoundException, ClientEncuestaFinalizadaException;

    public List<ClientRespuestaEncuestaDto> obtenerRespuestas(Long encuestaId, boolean soloPositivas)
            throws InstanceNotFoundException;

}

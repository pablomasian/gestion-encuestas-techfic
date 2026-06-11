package es.udc.ws.app.thriftservice;

import java.util.List;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.model.respuesta.RespuestaEncuesta;
import es.udc.ws.app.model.servicio.EncuestaServicioFactory;
import es.udc.ws.app.model.servicio.exceptions.EncuestaCanceladaException;
import es.udc.ws.app.model.servicio.exceptions.EncuestaFinalizadaException;
import es.udc.ws.app.thrift.ThriftEncuestaCanceladaException;
import es.udc.ws.app.thrift.ThriftEncuestaDto;
import es.udc.ws.app.thrift.ThriftEncuestaFinalizadaException;
import es.udc.ws.app.thrift.ThriftEncuestaService;
import es.udc.ws.app.thrift.ThriftInputValidationException;
import es.udc.ws.app.thrift.ThriftInstanceNotFoundException;
import es.udc.ws.app.thrift.ThriftRespuestaEncuestaDto;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

public class ThriftEncuestaServiceImpl implements ThriftEncuestaService.Iface {

    // FUNC-1: Crear encuesta
    @Override
    public ThriftEncuestaDto crearEncuesta(ThriftEncuestaDto encuestaDto) 
            throws ThriftInputValidationException {

        Encuesta encuesta = EncuestaToThriftEncuestaDtoConversor.toEncuesta(encuestaDto);

        try {
            Encuesta createdEncuesta = EncuestaServicioFactory.getService().crearEncuesta(encuesta);
            return EncuestaToThriftEncuestaDtoConversor.toThriftEncuestaDto(createdEncuesta);
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        }
    }

    // FUNC-2: Buscar encuestas por palabra clave
    @Override
    public List<ThriftEncuestaDto> buscarPorPalabraClave(String palabraClave) {

        try {
            // En la capa servicios solo se devuelven encuestas no finalizadas
            List<Encuesta> encuestas = EncuestaServicioFactory.getService()
                    .buscarPorPalabraClave(palabraClave, true);
            return EncuestaToThriftEncuestaDtoConversor.toThriftEncuestaDtos(encuestas);
        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        }
    }

    // FUNC-3: Buscar encuesta por ID
    @Override
    public ThriftEncuestaDto buscar(long encuestaId) 
            throws ThriftInstanceNotFoundException, ThriftInputValidationException {

        try {
            Encuesta encuesta = EncuestaServicioFactory.getService().buscar(encuestaId);
            return EncuestaToThriftEncuestaDtoConversor.toThriftEncuestaDto(encuesta);
        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(), e.getInstanceType());
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        }
    }

    // FUNC-4: Responder encuesta
    @Override
    public long responderEncuesta(long encuestaId, String email, boolean respuestaPositiva) 
            throws ThriftInstanceNotFoundException, ThriftInputValidationException, 
                   ThriftEncuestaCanceladaException, ThriftEncuestaFinalizadaException {

        try {
            RespuestaEncuesta respuesta = EncuestaServicioFactory.getService()
                    .responderEncuesta(encuestaId, email, respuestaPositiva);
            return respuesta.getRespuestaId();
        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(), e.getInstanceType());
        } catch (EncuestaCanceladaException e) {
            throw new ThriftEncuestaCanceladaException(encuestaId);
        } catch (EncuestaFinalizadaException e) {
            throw new ThriftEncuestaFinalizadaException(encuestaId);
        }
    }

    // FUNC-5: Cancelar encuesta
    @Override
    public void cancelarEncuesta(long encuestaId) 
            throws ThriftInstanceNotFoundException, ThriftEncuestaFinalizadaException {

        try {
            EncuestaServicioFactory.getService().cancelarEncuesta(encuestaId);
        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(), e.getInstanceType());
        } catch (EncuestaFinalizadaException e) {
            throw new ThriftEncuestaFinalizadaException(encuestaId);
        } catch (EncuestaCanceladaException e) {
            // No puede pasar en cancelación, pero por si acaso
            throw new RuntimeException(e);
        }
    }

    // FUNC-6: Obtener respuestas
    @Override
    public List<ThriftRespuestaEncuestaDto> obtenerRespuestas(long encuestaId, boolean soloPositivas)
            throws ThriftInstanceNotFoundException {

        try {
            List<RespuestaEncuesta> respuestas = EncuestaServicioFactory.getService()
                    .obtenerRespuestasEncuesta(encuestaId, soloPositivas);
            return RespuestaEncuestaToThriftRespuestaEncuestaDtoConversor.toThriftRespuestaEncuestaDtos(respuestas);
        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(), e.getInstanceType());
        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        }
    }

}

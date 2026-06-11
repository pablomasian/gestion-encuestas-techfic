package es.udc.ws.app.thriftservice;

import java.util.ArrayList;
import java.util.List;

import es.udc.ws.app.model.respuesta.RespuestaEncuesta;
import es.udc.ws.app.thrift.ThriftRespuestaEncuestaDto;

public class RespuestaEncuestaToThriftRespuestaEncuestaDtoConversor {

    public static ThriftRespuestaEncuestaDto toThriftRespuestaEncuestaDto(RespuestaEncuesta respuesta) {
        return new ThriftRespuestaEncuestaDto(
            respuesta.getRespuestaId(),
            respuesta.getEncuestaId(),
            respuesta.getEmail(),
            respuesta.isRespuestaPositiva()
        );
    }

    public static List<ThriftRespuestaEncuestaDto> toThriftRespuestaEncuestaDtos(List<RespuestaEncuesta> respuestas) {
        List<ThriftRespuestaEncuestaDto> dtos = new ArrayList<>(respuestas.size());
        for (RespuestaEncuesta respuesta : respuestas) {
            dtos.add(toThriftRespuestaEncuestaDto(respuesta));
        }
        return dtos;
    }

}

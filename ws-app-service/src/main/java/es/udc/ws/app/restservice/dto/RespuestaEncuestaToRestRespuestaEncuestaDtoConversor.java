package es.udc.ws.app.restservice.dto;

import java.util.ArrayList;
import java.util.List;

import es.udc.ws.app.model.respuesta.RespuestaEncuesta;

public class RespuestaEncuestaToRestRespuestaEncuestaDtoConversor {
    
    private RespuestaEncuestaToRestRespuestaEncuestaDtoConversor() {
    }
    
    public static RestRespuestaEncuestaDto toRespuestaEncuestaDto(RespuestaEncuesta respuesta) {
        return new RestRespuestaEncuestaDto(respuesta.getEncuestaId(), respuesta.getRespuestaId(),
                respuesta.getEmail(), respuesta.isRespuestaPositiva(), respuesta.getFechaRespuesta().toString());
    }
    
    public static List<RestRespuestaEncuestaDto> toRestRespuestaEncuestaDtos(List<RespuestaEncuesta> respuestas) {
        List<RestRespuestaEncuestaDto> respuestaDtos = new ArrayList<>(respuestas.size());
        for (RespuestaEncuesta respuesta : respuestas) {
            respuestaDtos.add(toRespuestaEncuestaDto(respuesta));
        }
        return respuestaDtos;
    }
}
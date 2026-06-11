package es.udc.ws.app.thriftservice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import es.udc.ws.app.model.encuesta.Encuesta;
import es.udc.ws.app.thrift.ThriftEncuestaDto;

public class EncuestaToThriftEncuestaDtoConversor {

    public static Encuesta toEncuesta(ThriftEncuestaDto encuestaDto) {
        return new Encuesta(
            encuestaDto.getPregunta(),
            LocalDateTime.parse(encuestaDto.getFechaHoraFin())
        );
    }

    public static List<ThriftEncuestaDto> toThriftEncuestaDtos(List<Encuesta> encuestas) {
        List<ThriftEncuestaDto> dtos = new ArrayList<>(encuestas.size());
        for (Encuesta encuesta : encuestas) {
            dtos.add(toThriftEncuestaDto(encuesta));
        }
        return dtos;
    }

    public static ThriftEncuestaDto toThriftEncuestaDto(Encuesta encuesta) {
        int totalRespuestas = encuesta.getRespuestasPositivas() + encuesta.getRespuestasNegativas();
        
        return new ThriftEncuestaDto(
            encuesta.getEncuestaId(),
            encuesta.getPregunta(),
            encuesta.getFechaHoraFin().toString(),
            encuesta.isCancelada(),
            encuesta.getRespuestasPositivas(),
            totalRespuestas
        );
    }

}

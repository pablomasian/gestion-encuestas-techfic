package es.udc.ws.app.restservice.dto;

import java.util.List;
import java.util.ArrayList;

import es.udc.ws.app.model.encuesta.Encuesta;

public class EncuestaToRestEncuestaDtoConversor {

    public static List<RestEncuestaDto> toRestEncuestaDtos(List<Encuesta> encuestas) {
        List<RestEncuestaDto> encuestaDtos = new ArrayList<>(encuestas.size());
        for (int i=0; i<encuestas.size(); i++) {
            Encuesta encuesta = encuestas.get(i);
            encuestaDtos.add(toRestEncuestaDto(encuesta));
        }
        return encuestaDtos;
    }

    public static RestEncuestaDto toRestEncuestaDto(Encuesta encuesta) {
        String fechaFinStr = (encuesta.getFechaHoraFin() != null) ? encuesta.getFechaHoraFin().toString() : null;
        String fechaCreStr = (encuesta.getFechaCreacion() != null) ? encuesta.getFechaCreacion().toString() : null;
        return new RestEncuestaDto(encuesta.getEncuestaId(), encuesta.getPregunta(),
                fechaFinStr, encuesta.isCancelada(),
                fechaCreStr, encuesta.getRespuestasPositivas(),
                encuesta.getRespuestasNegativas());
    }

        public static Encuesta toEncuesta(RestEncuestaDto encuesta) {
        java.time.LocalDateTime fechaFin = (encuesta.getFechaHoraFin() != null && !encuesta.getFechaHoraFin().isEmpty())
            ? java.time.LocalDateTime.parse(encuesta.getFechaHoraFin()) : null;
        java.time.LocalDateTime fechaCre = (encuesta.getFechaCreacion() != null && !encuesta.getFechaCreacion().isEmpty())
            ? java.time.LocalDateTime.parse(encuesta.getFechaCreacion()) : null;
        return new Encuesta(encuesta.getEncuestaId(), encuesta.getPregunta(),
            fechaFin, encuesta.isCancelada(),
            fechaCre, encuesta.getRespuestasPositivas(),
            encuesta.getRespuestasNegativas());
        }
}


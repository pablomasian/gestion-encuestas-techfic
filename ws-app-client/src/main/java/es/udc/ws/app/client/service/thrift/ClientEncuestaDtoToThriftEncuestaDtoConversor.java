package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.dto.ClientEncuestaDto;
import es.udc.ws.app.thrift.ThriftEncuestaDto;

import java.util.ArrayList;
import java.util.List;

public class ClientEncuestaDtoToThriftEncuestaDtoConversor {

    public static ThriftEncuestaDto toThriftEncuestaDto(ClientEncuestaDto clientEncuestaDto) {
        Long encuestaId = clientEncuestaDto.getEncuestaId();

        return new ThriftEncuestaDto(
            encuestaId == null ? -1 : encuestaId.longValue(),
            clientEncuestaDto.getPregunta(),
            clientEncuestaDto.getFechaHoraFin(),
            clientEncuestaDto.isCancelada(),
            clientEncuestaDto.getRespuestasPositivas(),
            clientEncuestaDto.getTotalRespuestas()
        );
    }

    public static List<ClientEncuestaDto> toClientEncuestaDtos(List<ThriftEncuestaDto> encuestas) {
        List<ClientEncuestaDto> clientEncuestaDtos = new ArrayList<>(encuestas.size());
        for (ThriftEncuestaDto encuesta : encuestas) {
            clientEncuestaDtos.add(toClientEncuestaDto(encuesta));
        }
        return clientEncuestaDtos;
    }

    public static ClientEncuestaDto toClientEncuestaDto(ThriftEncuestaDto encuesta) {
        return new ClientEncuestaDto(
            encuesta.getEncuestaId(),
            encuesta.getPregunta(),
            encuesta.getFechaHoraFin(),
            encuesta.isCancelada(),
            null, // fechaCreacion no se envía en la capa servicios
            encuesta.getRespuestasPositivas(),
            encuesta.getTotalRespuestas()
        );
    }

}

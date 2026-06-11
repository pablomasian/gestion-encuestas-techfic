package es.udc.ws.app.client.service.thrift;

import java.util.ArrayList;
import java.util.List;

import es.udc.ws.app.client.service.dto.ClientRespuestaEncuestaDto;
import es.udc.ws.app.thrift.ThriftRespuestaEncuestaDto;

public class ClientRespuestaEncuestaDtoToThriftRespuestaEncuestaDtoConversor {

    public static List<ClientRespuestaEncuestaDto> toClientRespuestaEncuestaDtos(
            List<ThriftRespuestaEncuestaDto> respuestas) {
        List<ClientRespuestaEncuestaDto> clientRespuestas = new ArrayList<>(respuestas.size());
        for (ThriftRespuestaEncuestaDto respuesta : respuestas) {
            clientRespuestas.add(toClientRespuestaEncuestaDto(respuesta));
        }
        return clientRespuestas;
    }

    public static ClientRespuestaEncuestaDto toClientRespuestaEncuestaDto(ThriftRespuestaEncuestaDto respuesta) {
        ClientRespuestaEncuestaDto clientRespuesta = new ClientRespuestaEncuestaDto(
            respuesta.getRespuestaId(),
            respuesta.getEncuestaId(),
            respuesta.getEmail(),
            respuesta.isRespuestaPositiva()
        );
        // fechaRespuesta no se envía en la capa servicios
        clientRespuesta.setFechaRespuesta(null);
        return clientRespuesta;
    }

}

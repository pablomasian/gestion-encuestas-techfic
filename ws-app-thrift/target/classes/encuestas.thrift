namespace java es.udc.ws.app.thrift

struct ThriftEncuestaDto {
    1: i64 encuestaId
    2: string pregunta
    3: string fechaHoraFin
    4: bool cancelada
    5: i32 respuestasPositivas
    6: i32 totalRespuestas
}

exception ThriftInputValidationException {
    1: string message
}

exception ThriftInstanceNotFoundException {
    1: string instanceId
    2: string instanceType
}

service ThriftEncuestaService {

    // FUNC-1: Crear encuesta
    ThriftEncuestaDto crearEncuesta(1: ThriftEncuestaDto encuestaDto) throws (1: ThriftInputValidationException e)

    // FUNC-2: Buscar encuestas por palabra clave
    list<ThriftEncuestaDto> buscarPorPalabraClave(1: string palabraClave)

}

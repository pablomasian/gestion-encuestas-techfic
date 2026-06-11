namespace java es.udc.ws.app.thrift

struct ThriftEncuestaDto {
    1: i64 encuestaId
    2: string pregunta
    3: string fechaHoraFin
    4: bool cancelada
    5: i32 respuestasPositivas
    6: i32 totalRespuestas
}

struct ThriftRespuestaEncuestaDto {
    1: i64 respuestaId
    2: i64 encuestaId
    3: string email
    4: bool respuestaPositiva
}

exception ThriftInputValidationException {
    1: string message
}

exception ThriftInstanceNotFoundException {
    1: string instanceId
    2: string instanceType
}

exception ThriftEncuestaCanceladaException {
    1: i64 encuestaId
}

exception ThriftEncuestaFinalizadaException {
    1: i64 encuestaId
}

service ThriftEncuestaService {

    // FUNC-1: Crear encuesta
    ThriftEncuestaDto crearEncuesta(1: ThriftEncuestaDto encuestaDto) throws (1: ThriftInputValidationException e)

    // FUNC-2: Buscar encuestas por palabra clave
    list<ThriftEncuestaDto> buscarPorPalabraClave(1: string palabraClave)


    // FUNC-3: Buscar encuesta por ID
    ThriftEncuestaDto buscar(1: i64 encuestaId) throws (1: ThriftInstanceNotFoundException e, 2: ThriftInputValidationException ive)

    // FUNC-4: Responder encuesta
    i64 responderEncuesta(1: i64 encuestaId, 2: string email, 3: bool respuestaPositiva) throws (1: ThriftInstanceNotFoundException e, 2: ThriftInputValidationException ive, 3: ThriftEncuestaCanceladaException ece, 4: ThriftEncuestaFinalizadaException efe)

    // FUNC-5: Cancelar encuesta
    void cancelarEncuesta(1: i64 encuestaId) throws (1: ThriftInstanceNotFoundException infe, 2: ThriftEncuestaFinalizadaException effe)

    // FUNC-6: Obtener respuestas de una encuesta
    list<ThriftRespuestaEncuestaDto> obtenerRespuestas(1: i64 encuestaId, 2: bool soloPositivas) throws (1: ThriftInstanceNotFoundException infe)

}

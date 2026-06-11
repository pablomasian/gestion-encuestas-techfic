package es.udc.ws.app.restservice.dto;

import java.time.LocalDateTime;

public class RestEncuestaDto {

    private Long encuestaId;
    private String pregunta;
    private String fechaHoraFin;
    private boolean cancelada;
    private String fechaCreacion;
    private int respuestasPositivas;
    private int respuestasNegativas;
    

    public RestEncuestaDto(Long encuestaId, String pregunta, LocalDateTime fechaHoraFin, boolean cancelada, LocalDateTime fechaCreacion, int respuestasPositivas, int respuestasNegativas) {
    //el parentesis tendria que estar vacio, pero da error sino en el conversor
    }

    public RestEncuestaDto(Long encuestaId, String pregunta, String fechaHoraFin, boolean cancelada, String fechaCreacion, int respuestasPositivas, int respuestasNegativas) {
        this.encuestaId = encuestaId;
        this.pregunta = pregunta;
        this.fechaHoraFin = fechaHoraFin;
        this.cancelada = cancelada;
        this.fechaCreacion = fechaCreacion;
        this.respuestasPositivas = respuestasPositivas;
        this.respuestasNegativas = respuestasNegativas;
    }


    public Long getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(Long encuestaId) {
        this.encuestaId = encuestaId;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(String fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public boolean isCancelada() {
        return cancelada;
    }

    public void setCancelada(boolean cancelada) {
        this.cancelada = cancelada;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public int getRespuestasPositivas() {
        return respuestasPositivas;
    }

    public void setRespuestasPositivas(int respuestasPositivas) {
        this.respuestasPositivas = respuestasPositivas;
    }

    public int getRespuestasNegativas() {
        return respuestasNegativas;
    }

    
    public void setRespuestasNegativas(int respuestasNegativas) {
        this.respuestasNegativas = respuestasNegativas;
    }   

    @Override
    public String toString() {
        return "RestEncuestaDto [encuestaId=" + encuestaId + ", pregunta=" + pregunta + ", fechaHoraFin=" + fechaHoraFin
                + ", cancelada=" + cancelada + ", fechaCreacion=" + fechaCreacion + ", respuestasPositivas="
                + respuestasPositivas + ", respuestasNegativas=" + respuestasNegativas + "]";   

    }
}

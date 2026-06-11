package es.udc.ws.app.restservice.dto;

public class RestRespuestaEncuestaDto {
    
    private Long encuestaId;
    private Long respuestaId;
    private String email;
    private boolean respuestaPositiva;
    private String fechaRespuesta;
    
    

    public RestRespuestaEncuestaDto() {
    }

    public RestRespuestaEncuestaDto(Long encuestaId, Long respuestaId, String email, boolean respuestaPositiva, String fechaRespuesta) {
        this.encuestaId = encuestaId;
        this.respuestaId = respuestaId;
        this.email = email;
        this.respuestaPositiva = respuestaPositiva;
        this.fechaRespuesta = fechaRespuesta;

    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(Long encuestaId) {
        this.encuestaId = encuestaId;
    }

    public Long getRespuestaId() {
        return respuestaId;
    }

    public void setRespuestaId(Long respuestaId) {
        this.respuestaId = respuestaId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isRespuestaPositiva() {
        return respuestaPositiva;
    }

    public void setRespuestaPositiva(boolean respuestaPositiva) {
        this.respuestaPositiva = respuestaPositiva;
    }

    public String getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(String fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    @Override
    public String toString() {
        return "RestRespuestaEncuestaDto [encuestaId=" + encuestaId + ", respuestaId=" + respuestaId + ", email=" + email
                + ", respuestaPositiva=" + respuestaPositiva + ", fechaRespuesta=" + fechaRespuesta + "]";
    }
}
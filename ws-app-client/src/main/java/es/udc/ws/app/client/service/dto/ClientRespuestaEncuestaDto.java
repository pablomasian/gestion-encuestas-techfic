package es.udc.ws.app.client.service.dto;

public class ClientRespuestaEncuestaDto {

    private Long respuestaId;
    private Long encuestaId;
    private String email;
    private boolean respuestaPositiva;
    private String fechaRespuesta;

    public ClientRespuestaEncuestaDto() {
    }

    public ClientRespuestaEncuestaDto(Long respuestaId, Long encuestaId, String email,
            boolean respuestaPositiva) {
        this.respuestaId = respuestaId;
        this.encuestaId = encuestaId;
        this.email = email;
        this.respuestaPositiva = respuestaPositiva;
    }

    public Long getRespuestaId() {
        return respuestaId;
    }

    public void setRespuestaId(Long respuestaId) {
        this.respuestaId = respuestaId;
    }

    public Long getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(Long encuestaId) {
        this.encuestaId = encuestaId;
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
        return "ClientRespuestaEncuestaDto [respuestaId=" + respuestaId + ", encuestaId="
                + encuestaId + ", email=" + email + ", respuestaPositiva=" + respuestaPositiva 
                + ", fechaRespuesta=" + fechaRespuesta + "]";
    }
}


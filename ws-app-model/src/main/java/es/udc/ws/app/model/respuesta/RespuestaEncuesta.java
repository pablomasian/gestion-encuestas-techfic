package es.udc.ws.app.model.respuesta;

import java.time.LocalDateTime;

public class RespuestaEncuesta {
    
    private Long encuestaId;
    private Long respuestaId;
    private String email;
    private boolean respuestaPositiva;
    private LocalDateTime fechaRespuesta;

    // constructor completo
    public RespuestaEncuesta(Long encuestaId, String email, boolean respuestaPositiva, LocalDateTime fechaRespuesta) {
        this.encuestaId = encuestaId;
        this.email = email;
        this.respuestaPositiva = respuestaPositiva;
        this.fechaRespuesta = (fechaRespuesta != null) ? fechaRespuesta.withNano(0) : null;
    }

    //Constructor sin fecha para crear respuesta antes de guardarla 
    public RespuestaEncuesta(Long encuestaId, String email, boolean respuestaPositiva) {
        this(encuestaId, email, respuestaPositiva, null);
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

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = (fechaRespuesta != null) ? fechaRespuesta.withNano(0) : null;
    }

    @Override
    public boolean equals(Object obj) { //comparamos si dos obj son iguales
        if (this == obj) 
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RespuestaEncuesta other = (RespuestaEncuesta) obj;
        if (encuestaId == null) {
            if (other.encuestaId != null)
                return false;
        } else if (!encuestaId.equals(other.encuestaId))
            return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (respuestaPositiva != other.respuestaPositiva)
            return false;
        if (fechaRespuesta == null) {
            if (other.fechaRespuesta != null)
                return false;
        } else if (!fechaRespuesta.equals(other.fechaRespuesta))
            return false;
        if (respuestaId == null) {
            if (other.respuestaId != null)
                return false;
        } else if (!respuestaId.equals(other.respuestaId))
            return false;
        return true;
    }

    @Override
    public int hashCode() { //generamos hash para el objeto
        final int prime = 31;
        int result = 1;
        result = prime * result + ((encuestaId == null) ? 0 : encuestaId.hashCode());
        result = prime * result + ((respuestaId == null) ? 0 : respuestaId.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + (respuestaPositiva ? 1231 : 1237); // Usamos 1231 para true y 1237 para false según el estándar de hash de booleanos de Java
        result = prime * result + ((fechaRespuesta == null) ? 0 : fechaRespuesta.hashCode());
        return result;
    }

    @Override
    public String toString() { //representaar el objeto como texto 
        return "RespuestaEncuesta{" +
                "encuestaId=" + encuestaId +
                ", respuestaId=" + respuestaId +
                ", email='" + email + '\'' +
                ", respuestaPositiva=" + respuestaPositiva +
                ", fechaRespuesta=" + fechaRespuesta +
                '}';
    }

}

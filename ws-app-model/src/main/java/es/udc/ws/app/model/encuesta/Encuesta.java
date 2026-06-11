package es.udc.ws.app.model.encuesta;

// Entidad de dominio que representa una encuesta.
// Almacena: pregunta, fecha/hora de finalización, estado y fecha de creación (BD).

import java.time.LocalDateTime;
import java.util.Objects;

public class Encuesta {
    private Long encuestaId;
    private String pregunta;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaHoraFin;
    private boolean cancelada;
    private int respuestasPositivas;
    private int respuestasNegativas;

    // Abierta si no está cancelada y todavía no ha pasado la fecha de fin.
    // Constructor completo para búsquedas y consulta por id
    public Encuesta(Long encuestaId, String pregunta, LocalDateTime fechaHoraFin, boolean cancelada, LocalDateTime fechaCreacion, int respuestasPositivas, int respuestasNegativas) {
        this.encuestaId = encuestaId;
        this.pregunta = pregunta;
        this.fechaHoraFin = fechaHoraFin;
        this.cancelada = cancelada;
        this.fechaCreacion = fechaCreacion;
        this.respuestasPositivas = respuestasPositivas;
        this.respuestasNegativas = respuestasNegativas;
    }

    // Constructor para alta
    public Encuesta(String pregunta, LocalDateTime fechaHoraFin) {
        this(null, pregunta, fechaHoraFin, false, null, 0, 0);
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

    public LocalDateTime getFechaHoraFin() { 
        return fechaHoraFin; 
    }
    public void setFechaHoraFin(LocalDateTime fechaHoraFin) { 
        this.fechaHoraFin = fechaHoraFin; 
    }

    public boolean isCancelada() { 
        return cancelada; 
    }
    public void setCancelada(boolean cancelada) { 
        this.cancelada = cancelada; 
    }

    // Recuento de respuestas
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

    // Conveniencia: indica si estaría abierta ahora mismo (sin persistir este valor).
    public boolean isAbierta() {
        return !cancelada && LocalDateTime.now().isBefore(fechaHoraFin);
    }

    public LocalDateTime getFechaCreacion() { 
        return fechaCreacion; 
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) { 
        this.fechaCreacion = (fechaCreacion != null) ? fechaCreacion.withNano(0) : null;
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Encuesta other = (Encuesta) obj;
        if (encuestaId == null) {
            if (other.encuestaId != null)
                return false;
        } else if (!encuestaId.equals(other.encuestaId))
            return false;
        if (pregunta == null) {
            if (other.pregunta != null)
                return false;
        } else if (!pregunta.equals(other.pregunta))
            return false;
        if (fechaHoraFin == null) {
            if (other.fechaHoraFin != null)
                return false;
        } else if (!fechaHoraFin.equals(other.fechaHoraFin))
            return false;
        if (cancelada != other.cancelada)
            return false;
        if (fechaCreacion == null) {
            if (other.fechaCreacion != null)
                return false;
        } else if (!fechaCreacion.equals(other.fechaCreacion))
            return false;
        if (respuestasPositivas != other.respuestasPositivas)
            return false;
        if (respuestasNegativas != other.respuestasNegativas)
            return false;

		return true;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((encuestaId == null) ? 0 : encuestaId.hashCode());
        result = prime * result + ((pregunta == null) ? 0 : pregunta.hashCode());
        result = prime * result + ((fechaHoraFin == null) ? 0 : fechaHoraFin.hashCode());
        result = prime * result + (cancelada ? 1231 : 1237); // Usamos 1231 para true y 1237 para false según el estándar de hash de booleanos de Java
        result = prime * result + ((fechaCreacion == null) ? 0 : fechaCreacion.hashCode());
        result = prime * result + respuestasPositivas;
        result = prime * result + respuestasNegativas;
        return result;
    }

    @Override
    public String toString() {
    return "Encuesta{" +
        "encuestaId=" + encuestaId +
        ", pregunta='" + pregunta + '\'' +
        ", fechaHoraFin=" + fechaHoraFin +
        ", cancelada=" + cancelada +
        ", fechaCreacion=" + fechaCreacion +
        ", respuestasPositivas=" + respuestasPositivas +
        ", respuestasNegativas=" + respuestasNegativas +
        '}';
    }
}

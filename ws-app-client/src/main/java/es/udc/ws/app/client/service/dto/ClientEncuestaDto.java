package es.udc.ws.app.client.service.dto;

public class ClientEncuestaDto {

	private Long encuestaId;
	private String pregunta;
	private String fechaHoraFin;
	private boolean cancelada;
	private String fechaCreacion;
	private int respuestasPositivas;
	private int totalRespuestas; // mostrar total en lugar de respuestas negativas

	public ClientEncuestaDto() {
	}

	public ClientEncuestaDto(Long encuestaId, String pregunta, String fechaHoraFin, boolean cancelada,
			String fechaCreacion, int respuestasPositivas, int totalRespuestas) {
		this.encuestaId = encuestaId;
		this.pregunta = pregunta;
		this.fechaHoraFin = fechaHoraFin;
		this.cancelada = cancelada;
		this.fechaCreacion = fechaCreacion;   
		this.respuestasPositivas = respuestasPositivas;
		this.totalRespuestas = totalRespuestas;
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

	public int getTotalRespuestas() {
		return totalRespuestas;
	}

	public void setTotalRespuestas(int totalRespuestas) {
		this.totalRespuestas = totalRespuestas;
	}

	@Override
	public String toString() {
		return "ClientEncuestaDto [encuestaId=" + encuestaId + ", pregunta=" + pregunta + ", fechaHoraFin=" + fechaHoraFin
				+ ", cancelada=" + cancelada + ", fechaCreacion=" + fechaCreacion + ", respuestasPositivas="
				+ respuestasPositivas + ", totalRespuestas=" + totalRespuestas + "]";
	}

}

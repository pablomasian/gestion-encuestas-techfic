package es.udc.ws.app.client.service.exceptions;

public class ClientEncuestaFinalizadaException extends Exception { //Excepcion en vez de runtime exception porque asi lo maneja el cliente

	private Long encuestaId;
    
	public ClientEncuestaFinalizadaException(Long encuestaId) {
		super("La encuesta con id=\"" + encuestaId + "\" está finalizada");
		this.encuestaId = encuestaId;
	}

	public Long getEncuestaId() {
		return encuestaId;
	}

	public void setEncuestaId(Long encuestaId) {
		this.encuestaId = encuestaId;
	}
}

package es.udc.ws.app.model.servicio.exceptions;

/**
 * Unchecked exception used to wrap SQLExceptions at the model/DAO layer.
 */
public class DataAccessRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DataAccessRuntimeException(Throwable cause) {
        super(cause);
    }
}

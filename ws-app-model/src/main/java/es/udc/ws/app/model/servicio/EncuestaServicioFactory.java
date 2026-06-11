package es.udc.ws.app.model.servicio;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class EncuestaServicioFactory {

    private final static String CLASS_NAME_PARAMETER = "EncuestaServicioFactory.className";
    private static EncuestaServicio service = null;

    private EncuestaServicioFactory() {
    }

    @SuppressWarnings("rawtypes")
    private static EncuestaServicio getInstance() {
        try {
            String serviceClassName = ConfigurationParametersManager
                    .getParameter(CLASS_NAME_PARAMETER);
            Class serviceClass = Class.forName(serviceClassName);
            return (EncuestaServicio) serviceClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public synchronized static EncuestaServicio getService() {

        if (service == null) {
            service = getInstance();
        }
        return service;

    }
}

package es.udc.ws.app.client.service;

import java.lang.reflect.InvocationTargetException;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class ClientEncuestaServiceFactory {

    private final static String CLASS_NAME_PARAMETER = "ClientEncuestaServiceFactory.className";
    private static Class<ClientEncuestaService> serviceClass = null;

    private ClientEncuestaServiceFactory() {
    }

    @SuppressWarnings("unchecked")
    private synchronized static Class<ClientEncuestaService> getServiceClass() {

        if (serviceClass == null) {
            try {
                String serviceClassName = ConfigurationParametersManager
                        .getParameter(CLASS_NAME_PARAMETER);
                serviceClass = (Class<ClientEncuestaService>) Class.forName(serviceClassName);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return serviceClass;

    }

    public static ClientEncuestaService getService() {

        try {
            return (ClientEncuestaService) getServiceClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
                | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }

    }
}

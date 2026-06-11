package es.udc.ws.app.model.respuesta;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

/**
 * A factory to get
 * <code>SqlRespuestaEncuestaDao</code> objects. <p> Required configuration parameters: <ul>
 * <li><code>SqlRespuestaEncuestaDaoFactory.className</code>: it must specify the full class
 * name of the class implementing
 * <code>SqlRespuestaEncuestaDao</code>.</li> </ul>
 */
public class SqlRespuestaEncuestaDaoFactory {

    private final static String CLASS_NAME_PARAMETER = "SqlRespuestaEncuestaDaoFactory.className";
    private static SqlRespuestaEncuestaDao dao = null;

    private SqlRespuestaEncuestaDaoFactory() {
    }

    @SuppressWarnings("rawtypes")
    private static SqlRespuestaEncuestaDao getInstance() {
        try {
            String daoClassName = ConfigurationParametersManager
                    .getParameter(CLASS_NAME_PARAMETER);
            Class daoClass = Class.forName(daoClassName);
            return (SqlRespuestaEncuestaDao) daoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public synchronized static SqlRespuestaEncuestaDao getDao() {

        if (dao == null) {
            dao = getInstance();
        }
        return dao;

    }
}

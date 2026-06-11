package es.udc.ws.app.thriftservice;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

import es.udc.ws.app.thrift.ThriftEncuestaService;
import es.udc.ws.util.servlet.ThriftHttpServletTemplate;

public class ThriftEncuestaServiceServlet extends ThriftHttpServletTemplate {

    public ThriftEncuestaServiceServlet() {
        super(createProcessor(), createProtocolFactory());
    }

    private static TProcessor createProcessor() {
        return new ThriftEncuestaService.Processor<ThriftEncuestaService.Iface>(
            new ThriftEncuestaServiceImpl());
    }

    private static TProtocolFactory createProtocolFactory() {
        return new TBinaryProtocol.Factory();
    }

}

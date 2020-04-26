package net.targetr.photoframe.handler;

import java.util.Collection;

import net.targetr.httpd.HttpRequest;
import net.targetr.httpd.HttpRequestHandler;
import net.targetr.httpd.HttpResponse;
import net.targetr.httpd.SimpleHttpServer;
import net.targetr.httpd.response.TextResponse;

/**
 * Returns the private IP address and port the HTTP server is listening to.
 * @author mike-targetr
 */
public class AddressHandler implements HttpRequestHandler {

    private SimpleHttpServer httpServer;

    public AddressHandler(SimpleHttpServer httpServer) {
        this.httpServer = httpServer;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws Exception {
        
        Collection<String> addresses = httpServer.getLocalAddresses();
        
        for (String addr : addresses) {
            if (addr.startsWith("192.168.")) {
                return new TextResponse("http://" + addr);
            }
        }

        for (String addr : addresses) {
            if (addr.startsWith("10.")) {
                return new TextResponse("http://" + addr);
            }
        }
        
        for (String addr : addresses) {
            if (addr.startsWith("172.")) {
                return new TextResponse("http://" + addr);
            }
        }

        return null;
    }
}

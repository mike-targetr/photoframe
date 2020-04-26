package net.targetr.httpd;

/**
 * Handles a HTTP request and generates a suitable HTTP response.
 * This is similar to a Java Servlet, yet simplified.
 * @author mike-targetr
 */
public interface HttpRequestHandler {

    public HttpResponse handle(HttpRequest request) throws Exception;
}

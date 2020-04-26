package net.targetr.httpd;

/**
 * Encapsulates a full HTTP response to be returned to a HTTP client.
 * @author mike-targetr
 */
public class HttpResponse {

    public String version = "HTTP/1.1";
    public int code = 200;
    public String info = "OK";
    public Headers headers = new Headers();
    public byte[] body;
}

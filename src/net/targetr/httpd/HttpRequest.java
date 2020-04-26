package net.targetr.httpd;

import java.io.BufferedInputStream;
import java.net.Socket;

/**
 * Encapsulates a full HTTP request sent from a HTTP client.
 * @author mike-targetr
 */
public class HttpRequest {
    
    public Socket socket;

    public String method;
    public String path;
    public String version;
    public Headers headers = new Headers();
    public BufferedInputStream in;
}

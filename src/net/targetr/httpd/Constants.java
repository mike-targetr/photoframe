package net.targetr.httpd;

/**
 * Constants that configure the HTTP server.
 * @author mike-targetr
 */
public interface Constants {
    
    public static final int DEFAULT_PORT = 9090;

    public static final int MAX_CONNECTION_THREADS = 64;
    public static final int MAX_CONNECTION_BACKLOG = 128;
    public static final int SOCKET_TIMEOUT = 5000;
    
    public static final String SERVER_USER_AGENT = "TargetR HTTPD/1.0";
}

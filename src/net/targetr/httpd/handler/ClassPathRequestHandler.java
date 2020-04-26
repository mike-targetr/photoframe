package net.targetr.httpd.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import net.targetr.httpd.Headers;
import net.targetr.httpd.HttpRequest;
import net.targetr.httpd.HttpRequestHandler;
import net.targetr.httpd.HttpResponse;
import net.targetr.httpd.response.ByteArrayResponse;
import net.targetr.httpd.util.StreamUtil;

/**
 * Returns binary files accessible on the class path.
 * Used to serve files stored within a .jar file.
 * @author mike-targetr
 */
public class ClassPathRequestHandler implements HttpRequestHandler {
    
    private static final Logger log = Logger.getLogger(ClassPathRequestHandler.class.getName());
    
    private String path;
    private String classPathRoot;
    public Headers headers;
    
    public ClassPathRequestHandler(String path, String classPathRoot) {
        this(path, classPathRoot, null);
    }

    public ClassPathRequestHandler(String path, String classPathRoot, Headers headers) {
        this.path = path;
        this.classPathRoot = classPathRoot;
        this.headers = headers;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        
        String requestPath = request.path;
        int queryIdx = requestPath.indexOf("?");
        if (queryIdx != -1) {
            requestPath = requestPath.substring(0, queryIdx);
        }
        
        String relativePath = requestPath.substring(path.length());
        
        InputStream in = null;
        try {
            String resolved = classPathRoot + "/" + relativePath;
            log.finer("Reading from classpath: " + resolved);
            in = getClass().getResourceAsStream(resolved);
            if (in == null) return null;
            byte[] data = StreamUtil.readFully(in);
            return new ByteArrayResponse(data, getContentType(requestPath));
        }
        finally {
            try { in.close(); } catch (Exception ignored) { }
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=utf-8";
        if (path.endsWith(".css"))  return "text/css; charset=utf-8";
        if (path.endsWith(".js"))   return "application/javascript; charset=utf-8";
        if (path.endsWith(".ico"))  return "image/x-icon";
        if (path.endsWith(".jpg"))  return "image/jpeg";
        if (path.endsWith(".gif"))  return "image/gif";
        if (path.endsWith(".png"))  return "image/png";
        return "unknown/unknown";
    }
}

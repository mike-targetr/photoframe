package net.targetr.httpd.handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.targetr.httpd.Headers;
import net.targetr.httpd.HttpRequest;
import net.targetr.httpd.HttpRequestHandler;
import net.targetr.httpd.HttpResponse;
import net.targetr.httpd.response.FileResponse;
import net.targetr.httpd.response.TempRedirectResponse;

/**
 * Returns binary files accessible on the file system.
 * @author mike-targetr
 */
public class FileRequestHandler implements HttpRequestHandler {
    
    private String path;
    private Path source;
    public Headers headers;
    
    public FileRequestHandler(Path source) {
        this(null, source, null);
    }
    
    public FileRequestHandler(String path, Path source) {
        this(path, source, null);
    }

    public FileRequestHandler(String path, Path source, Headers headers) {
        this.path = path;
        this.source = source.normalize();
        this.headers = headers;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        
        String requestPath = request.path;
        int queryIdx = requestPath.indexOf("?");
        if (queryIdx != -1) {
            requestPath = requestPath.substring(0, queryIdx);
        }
        
        Path responsePath = source;
        
        if (Files.isDirectory(source)) {
            String relativePath = requestPath.substring(path.length());
            responsePath = source.resolve(relativePath).normalize();

            if (responsePath.toString().length() < source.toString().length()) {
                throw new SecurityException("Attempt to access parent directory");
            }
        }
        
        if (!Files.exists(responsePath)) {
            return null;
        }
        
        if (Files.isDirectory(responsePath)) {
            return new TempRedirectResponse(request.path += "/");
        }
        
        FileResponse response = new FileResponse(responsePath);
        if (headers != null) {
            response.headers.putAll(headers);
        }
        
        return response;
    }
}

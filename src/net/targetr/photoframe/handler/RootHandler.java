package net.targetr.photoframe.handler;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.targetr.httpd.Headers;
import net.targetr.httpd.HttpRequest;
import net.targetr.httpd.HttpRequestHandler;
import net.targetr.httpd.HttpResponse;
import net.targetr.httpd.SimpleHttpServer;
import net.targetr.httpd.handler.ClassPathRequestHandler;
import net.targetr.httpd.handler.FileRequestHandler;
import net.targetr.httpd.response.TempRedirectResponse;
import net.targetr.photoframe.PhotoPicker;

/**
 * The root handler is responsible for reading a request path and
 * delegating to other handlers.
 * @author mike-targetr
 */
public class RootHandler implements HttpRequestHandler {
    
    private Path uploadPath;
    private PhotoPicker picker;
    private SimpleHttpServer httpServer;
    
    public RootHandler(Path uploadPath) throws IOException {
        this.picker = new PhotoPicker(uploadPath);
        this.uploadPath = uploadPath;
    }
    
    public void setHttpServer(SimpleHttpServer httpServer) {
        this.httpServer = httpServer;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws Exception {
        
        HttpRequestHandler handler = null;

        if (request.path.equals("/")) {
           return new TempRedirectResponse("/admin/");
        }
        
        else if (request.path.equals("/api/upload")) {
            return new UploadHandler(uploadPath, picker).handle(request);
        }
        
        else if (request.path.equals("/api/select")) {
            return new SelectPhotoHandler(picker).handle(request);
        }
        
        else if (request.path.equals("/api/delete")) {
            return new DeletePhotoHandler(picker).handle(request);
        }
        
        else if (request.path.equals("/api/next")) {
            return new NextPhotoHandler(picker).handle(request);
        }
        
        else if (request.path.equals("/api/address")) {
            return new AddressHandler(httpServer).handle(request);
        }
        
        else if (request.path.equals("/api/previews")) {
            return new PreviewListHandler(picker).handle(request);
        }
        
        else if (request.path.startsWith("/photos/")) {
            Headers headers = new Headers();
            headers.put("Cache-Control", "max-age=31536000");
            return new FileRequestHandler("/photos/", uploadPath, headers).handle(request);
        }

        else {
            
            if (request.path.endsWith("/")) {
                request.path += "index.html";
            }
            
            // Look on file system for resource
            HttpResponse fileResponse = new FileRequestHandler("/", Paths.get("resources/")).handle(request);
            if (fileResponse != null) {
                return fileResponse;
            }
            
            // Look within classpath (used when packaged in a .jar)
            return new ClassPathRequestHandler("/", "/resources").handle(request);
        }
    }
}


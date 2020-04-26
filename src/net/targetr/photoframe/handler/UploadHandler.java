package net.targetr.photoframe.handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

import net.targetr.httpd.HttpRequest;
import net.targetr.httpd.HttpRequestHandler;
import net.targetr.httpd.HttpResponse;
import net.targetr.httpd.util.StreamUtil;
import net.targetr.photoframe.PhotoPicker;

/**
 * Processes a Base64 encoded blob of JPEG data sent from admin interface.
 * @author mike-targetr
 */
public class UploadHandler implements HttpRequestHandler {
    
    private Path path;
    private PhotoPicker picker;

    public UploadHandler(Path path, PhotoPicker picker) {
        this.path = path;
        this.picker = picker;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        
        String blob = StreamUtil.readPostedString(request);
        
        if (!blob.startsWith("data:")) {
            throw new IOException("Unuspported upload");
        }
        
        int idx1 = blob.indexOf(';');
        String contentType = blob.substring(5, idx1);
        
        if (!contentType.equals("image/jpeg")) {
            throw new IOException("Unsupported content type");
        }
        
        int idx2 = blob.indexOf(',', idx1);
        String encType = blob.substring(idx1+1, idx2);
        
        if (!encType.startsWith("base64")) {
            throw new IOException("Unsupported encoding");
        }
        
        String base64 = blob.substring(idx2+1);
        
        byte[] decodedBytes = Base64.getDecoder().decode(base64);

        long id = Long.parseLong(request.headers.get("X-PhotoId"));
        int width = Integer.parseInt(request.headers.get("X-Width"));
        int height = Integer.parseInt(request.headers.get("X-Height"));
        
        String filename = id + "." + width + "." + height + ".jpg";
        Path tmpPath = path.resolve(filename + ".tmp");
        Files.write(tmpPath, decodedBytes, StandardOpenOption.CREATE);
        
        Path finalPath = path.resolve(filename);
        Files.move(tmpPath, finalPath);
        
        picker.scan();
        picker.select(id);
        
        return new PreviewListHandler(picker).handle(request);
    }
}

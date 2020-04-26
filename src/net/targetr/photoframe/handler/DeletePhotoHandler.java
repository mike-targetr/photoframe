package net.targetr.photoframe.handler;

import java.io.IOException;

import net.targetr.httpd.HttpRequest;
import net.targetr.httpd.HttpRequestHandler;
import net.targetr.httpd.HttpResponse;
import net.targetr.httpd.util.StreamUtil;
import net.targetr.photoframe.PhotoPicker;

/**
 * Deletes a photo by ID.
 * @author mike-targetr
 */
public class DeletePhotoHandler implements HttpRequestHandler {

    private PhotoPicker picker;

    public DeletePhotoHandler(PhotoPicker picker) {
        this.picker = picker;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        
        String post = StreamUtil.readPostedString(request);
        long identity = Long.parseLong(post);
        picker.remove(identity);
        
        return new PreviewListHandler(picker).handle(request);
    }
}

package net.targetr.photoframe.handler;

import java.io.IOException;

import net.targetr.httpd.HttpRequest;
import net.targetr.httpd.HttpRequestHandler;
import net.targetr.httpd.HttpResponse;
import net.targetr.httpd.util.StreamUtil;
import net.targetr.photoframe.PhotoPicker;

/**
 * Selects a photo by ID to be displayed by the player.
 * This API call changes the random photo selection in the PhotoPicker.
 * @author mike-targetr
 */
public class SelectPhotoHandler implements HttpRequestHandler {

    private PhotoPicker picker;

    public SelectPhotoHandler(PhotoPicker picker) {
        this.picker = picker;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        
        String post = StreamUtil.readPostedString(request);
        long identity = Long.parseLong(post);
        picker.select(identity);
        
        return new PreviewListHandler(picker).handle(request);
    }
}

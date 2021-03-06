package net.targetr.photoframe.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.targetr.httpd.HttpRequest;
import net.targetr.httpd.HttpRequestHandler;
import net.targetr.httpd.HttpResponse;
import net.targetr.httpd.response.JsonArrayResponse;
import net.targetr.photoframe.PhotoMeta;
import net.targetr.photoframe.PhotoPicker;

/**
 * Uses the PhotoPicker to return photo filenames that should be displayed.
 * Photos will be similar aspect ratio to allow tiling multiple images.
 * @author mike-targetr
 */
public class NextPhotoHandler implements HttpRequestHandler {
    
    private final PhotoPicker picker;

    public NextPhotoHandler(PhotoPicker picker) {
        this.picker = picker;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {

        List<String> filenames = new ArrayList<String>();
        for (PhotoMeta p : picker.pick(2)) {
            filenames.add(p.filename);
        }
        
        return new JsonArrayResponse(filenames);
    }
}

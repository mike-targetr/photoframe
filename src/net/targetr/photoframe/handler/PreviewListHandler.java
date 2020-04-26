package net.targetr.photoframe.handler;

/**
 * Returns a list of photo previews to be shown in the admin interface.
 * @author mike-targetr
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.targetr.httpd.HttpRequest;
import net.targetr.httpd.HttpRequestHandler;
import net.targetr.httpd.HttpResponse;
import net.targetr.httpd.response.JsonArrayResponse;
import net.targetr.photoframe.PhotoMeta;
import net.targetr.photoframe.PhotoPicker;

public class PreviewListHandler implements HttpRequestHandler {
    
    private PhotoPicker picker;

    public PreviewListHandler(PhotoPicker picker) {
        this.picker = picker;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        
        List<String> filenames = new ArrayList<String>();
        for (PhotoMeta p : picker.getAllPreviews()) {
            filenames.add(p.filename);
        }
        
        return new JsonArrayResponse(filenames);
    }
}

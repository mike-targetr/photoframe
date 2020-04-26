package net.targetr.httpd.response;

import net.targetr.httpd.HttpResponse;

/**
 * Used to aid redirection of HTTP requests.
 * @author mike-targetr
 */
public class TempRedirectResponse extends HttpResponse {

    public TempRedirectResponse(String location) {
        headers.put("Location", location);
        code = 302;
        info = "Found";
    }
}

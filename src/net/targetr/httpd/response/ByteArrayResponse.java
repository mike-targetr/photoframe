package net.targetr.httpd.response;

import net.targetr.httpd.HttpResponse;

/**
 * Wraps a byte array into a HttpResponse.
 * @author mike-targetr
 */
public class ByteArrayResponse extends HttpResponse {

    public ByteArrayResponse(byte[] data, String contentType) {
        headers.put("Content-Type", contentType);
        body = data;
    }
}

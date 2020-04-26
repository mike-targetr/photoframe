package net.targetr.httpd.response;

/**
 * Wraps plain text into a HttpResponse.
 * @author mike-targetr
 */
public class TextResponse extends ByteArrayResponse {

    public TextResponse(String text) {
        this(text, "text/plain");
    }

    public TextResponse(String text, String contentType) {
        super(text.getBytes(), contentType);
    }
}

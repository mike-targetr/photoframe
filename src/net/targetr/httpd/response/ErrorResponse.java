package net.targetr.httpd.response;

import java.io.PrintWriter;
import java.io.StringWriter;

import net.targetr.httpd.HttpResponse;

/**
 * Wraps an Exception into a HttpResponse.
 * @author mike-targetr
 */
public class ErrorResponse extends HttpResponse {

    public ErrorResponse(int code, String info) {
        this(code, info, null);
    }

    public ErrorResponse(int code, String info, Throwable ex) {
        headers.put("Connection", "close");
        this.code = code;
        this.info = info;

        if (ex != null) {
            // Useful debugging information
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            headers.put("Content-Type", "text/plain");
            body = sw.toString().getBytes();
        }
    }
}

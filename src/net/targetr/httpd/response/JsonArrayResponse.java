package net.targetr.httpd.response;

import java.util.Collection;
import java.util.Iterator;

/**
 * Wraps a JSON array into a HttpResponse.
 * @author mike-targetr
 */
public class JsonArrayResponse extends TextResponse {

    public JsonArrayResponse(Collection<String> items) {
        super(jsonArray(items), "application/json");
    }

    private static String jsonArray(Collection<String> items) {
        StringBuilder b = new StringBuilder();
        b.append("[");
        Iterator<String> i = items.iterator();
        while (i.hasNext()) {
            b.append("\"");
            b.append(i.next());
            b.append("\"");
            if (i.hasNext()) {
                b.append(",");
            }
        }
        b.append("]");
        return b.toString();
    }
}

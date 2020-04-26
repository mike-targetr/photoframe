package net.targetr.httpd;

import java.util.TreeMap;

/**
 * Simplified header data storage and processing.
 * Note that this does not properly support lists for a given key.
 * However, this is rarely needed in standard HTTP communications.
 * @author mike-targetr
 */
public class Headers extends TreeMap<String, String> {

    private static final long serialVersionUID = 1L;

    public Headers() {
        super(String.CASE_INSENSITIVE_ORDER);
    }
}

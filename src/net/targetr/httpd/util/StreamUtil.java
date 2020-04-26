package net.targetr.httpd.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.targetr.httpd.HttpRequest;

/**
 * Various methods for handling data streams.
 * @author mike-targetr
 */
public class StreamUtil {
    
    public static String readPostedString(HttpRequest request) throws IOException {
        int contentLength = Integer.parseInt(request.headers.get("Content-Length"));
        byte[] data = new byte[contentLength];
        StreamUtil.readInto(request.in, data);
        return new String(data);
    }

    public static byte[] readFully(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        byte[] buffer = new byte[4096];
        int r = in.read(buffer);
        while (r > -1) {
            out.write(buffer, 0, r);
            r = in.read(buffer);
        }
        return out.toByteArray();
    }
    
    public static void readInto(InputStream in, byte[] data) throws IOException {
        int n = 0;
        while (n < data.length) {
            int count = in.read(data, n, data.length-n);
            if (count < 0) break;
            n += count;
        }
    }
    
    public static String md5(byte[] data) {
        return bytesToHex(md5Bytes(data));
    }
    
    private static byte[] md5Bytes(byte[] data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(data);
            return md.digest();
        }
        catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private static final String HEX = "0123456789ABCDEF";
    private static final char[] HEX_LOOKUP = HEX.toCharArray();    

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_LOOKUP[v >>> 4];
            hexChars[j * 2 + 1] = HEX_LOOKUP[v & 0x0F];
        }
        return new String(hexChars);
    }
}

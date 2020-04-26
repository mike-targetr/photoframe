package net.targetr.httpd;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.targetr.httpd.response.ErrorResponse;
import net.targetr.httpd.util.StreamUtil;

/**
 * Handles a HTTP connection from w HTTP client.
 * This is the guts of the simple HTTP server.
 * @author mike-targetr
 */
public class HttpConnectionHandler implements Runnable {
    
    private static final Logger log = Logger.getLogger(HttpConnectionHandler.class.getName());

    private final Socket socket;
    private final HttpRequestHandler requestHandler;

    public HttpConnectionHandler(Socket socket, HttpRequestHandler requestHandler) {
        this.socket = socket;
        this.requestHandler = requestHandler;
    }

    @Override
    public void run() {
        
        InetSocketAddress addr = (InetSocketAddress) socket.getRemoteSocketAddress();
        Thread.currentThread().setName(addr.getHostString());
        log.info("New connection");

        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            boolean keepAlive = true;
            
            while (keepAlive) {
                HttpRequest request = null;
                HttpResponse response = null;
                
                // Read the HTTP request
                try {
                    in = new BufferedInputStream(socket.getInputStream());
                    String rawRequest = readRawRequest(in);
                    log.finest(rawRequest);
                    request = parseRequest(rawRequest);
                    request.socket = socket;
                    request.in = in;
                    
                    
                    // Process the request and generate the response
                    try {
                        response = requestHandler.handle(request);

                        if (response == null) {
                            response = new HttpResponse();
                            response.code = 404;
                            response.info = "Object not found";
                            response.headers.put("Content-Type", "text/plain");
                            response.body = response.info.getBytes();
                        }
                    }
                    catch (Throwable ex) {
                        log.log(Level.WARNING, "Failed to generate response", ex);
                        response = new ErrorResponse(500, "Internal server error", ex);
                    }
                }
                catch (SocketException ex) {
                    return;
                }
                catch (SocketTimeoutException ex) {
                    return;
                }
                catch (Exception ex) {
                    log.log(Level.WARNING, "Failed to read request", ex);
                    response = new ErrorResponse(400, "Bad request", ex);
                }
    
                
                // Write the HTTP response
                try {
                    out = new BufferedOutputStream(socket.getOutputStream());
                    writeResponse(request, response, out);
                }
                catch (Exception ex) {
                    log.log(Level.WARNING, "Failed to send response", ex);
                    response.headers.put("Connection", "close");
                }
                
                log(request, response);
                
                keepAlive = "keep-alive".equalsIgnoreCase(response.headers.get("Connection"));
            }
        }
        finally {
            try { out.close();    } catch (Exception ignored) { }
            try { in.close();     } catch (Exception ignored) { }
            try { socket.close(); } catch (Exception ignored) { }
        }
        
        log.info("Close connection");
    }

    private String readRawRequest(BufferedInputStream in) throws IOException {
        
        StringBuilder request = new StringBuilder(1024);
        int b = in.read();
        while (b > -1) {
            request.append((char)b);
            if (isRequestComplete(request)) {
                return request.toString();
            }
            b = in.read();
        }
        
        throw new SocketException("Failed to read raw request");
    }

    private boolean isRequestComplete(StringBuilder r) {
        if (r.length() <= 4) return false;
        if (r.charAt(r.length()-4) != '\r') return false;
        if (r.charAt(r.length()-3) != '\n') return false;
        if (r.charAt(r.length()-2) != '\r') return false;
        if (r.charAt(r.length()-1) != '\n') return false;
        return true;
    }

    private HttpRequest parseRequest(String rawRequest) {
        
        HttpRequest r = new HttpRequest();
        StringTokenizer t = new StringTokenizer(rawRequest, "\r\n");
        while (t.hasMoreTokens()) {
            String line = t.nextToken();
            if (r.method == null) {
                int pathStart = line.indexOf(" ");
                int versionStart = line.lastIndexOf(" ");
                r.method = line.substring(0, pathStart).trim();
                r.path = line.substring(pathStart, versionStart).trim();
                r.version = line.substring(versionStart).trim();
            }
            else {
                // Regular header
                int sepIdx = line.indexOf(":");
                if (sepIdx > 0) {
                    String name = line.substring(0, sepIdx).trim();
                    String value = line.substring(sepIdx+1).trim();
                    r.headers.put(name, value);
                }
            }
        }
        return r;
    }

    private void writeResponse(HttpRequest request, HttpResponse response, BufferedOutputStream out) throws IOException {
        
        response.headers.put("Server", Constants.SERVER_USER_AGENT);
        
        if (!response.headers.containsKey("Connection")) {
            if (request != null)  {
                // Reuse the connection for more requests if the client supports it
                boolean keepAlive = "keep-alive".equalsIgnoreCase(request.headers.get("Connection"));
                response.headers.put("Connection", keepAlive ? "keep-alive" : "close");
            }
        }
        
        if (response.body != null && response.body.length > 0) {
            String etag = StreamUtil.md5(response.body);
            if (request != null)  {
                // Do not write data if client has it already
                String ifNoneMatch = request.headers.get("If-None-Match");
                if (ifNoneMatch != null && ifNoneMatch.contains(etag)) {
                    response.code = 304;
                    response.info = "Not modified";
                    response.body = null;
                }
            }
            response.headers.put("ETag", "\"" + etag + "\"");
        }
        
        if (response.body != null && response.body.length > 0) {
            response.headers.put("Content-Length", String.valueOf(response.body.length));
        }
        
        String responseLine = response.version + " " + response.code + " " + response.info + "\r\n";
        out.write(responseLine.getBytes());
        
        for (Map.Entry<String, String> entry : response.headers.entrySet()) {
            out.write((entry.getKey() + ": " + entry.getValue() + "\r\n").getBytes());
        }
        out.write("\r\n".getBytes());
        
        if (response.body != null && response.body.length > 0) {
            out.write(response.body);
        }
        
        out.flush();
    }
    
    private void log(HttpRequest request, HttpResponse response) {
        
        String logLine = "\"" + request.method + " " + request.path + "\" " + response.code;
        if (response.body != null) {
            logLine += " " + response.body.length;
        }
        
        log.fine(logLine);
    }
}
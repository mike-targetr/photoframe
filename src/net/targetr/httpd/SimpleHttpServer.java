package net.targetr.httpd;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Starts the HTTP listening on a server socket.
 * The HTTP server will accept connections and start handling the
 * connections in a new thread.
 * @author mike-targetr
 */
public class SimpleHttpServer implements Runnable {
    
    private static final Logger log = Logger.getLogger(SimpleHttpServer.class.getName());
    
    private final ServerSocket serverSocket;
    private final ExecutorService exe;
    private volatile boolean running;

    private final HttpRequestHandler requestHandler;

    public SimpleHttpServer(int port, HttpRequestHandler requestHandler) throws IOException {
        this.requestHandler = requestHandler;
        serverSocket = new ServerSocket(port, Constants.MAX_CONNECTION_BACKLOG);
        exe = Executors.newFixedThreadPool(Constants.MAX_CONNECTION_THREADS);
        running = true;
    }

    @Override
    public void run() {

        try {
            while (running) {
                Socket client = serverSocket.accept();
                client.setTcpNoDelay(true);
                client.setSoTimeout(Constants.SOCKET_TIMEOUT);
                
                HttpConnectionHandler connectionHandler = new HttpConnectionHandler(client, requestHandler);
                exe.execute(connectionHandler);
            }
        }
        catch (Exception ex) {
            log.log(Level.SEVERE, "Failed to listen for connections", ex);
        }
    }

    public Collection<String> getLocalAddresses() throws SocketException {
        
        Collection<String> addresses = new LinkedList<String>();
        
        Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
        while(e.hasMoreElements()) {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration<InetAddress> ee = n.getInetAddresses();
            while (ee.hasMoreElements()) {
                InetAddress i = (InetAddress) ee.nextElement();
                int port = serverSocket.getLocalPort();
                addresses.add(i.getHostAddress() + ":" + port);
            }
        }
        
        return addresses;
    }
}
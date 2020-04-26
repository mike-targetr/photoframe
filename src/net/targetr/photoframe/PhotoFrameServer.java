package net.targetr.photoframe;

import java.io.IOException;
import java.nio.file.Path;

import net.targetr.httpd.SimpleHttpServer;
import net.targetr.photoframe.handler.RootHandler;

/**
 * Starts the PhotoFrame HTTP server used to upload photos and control web players.
 * @author mike-targetr
 */
public class PhotoFrameServer {
    
    private final SimpleHttpServer httpServer;

    public PhotoFrameServer(int port, Path uploadPath) throws IOException {

        RootHandler rootHandler = new RootHandler(uploadPath);

        httpServer = new SimpleHttpServer(port, rootHandler);
        rootHandler.setHttpServer(httpServer);
        
        for (String addr : httpServer.getLocalAddresses()) {
            System.out.println("Listening to " + addr);
        }
    }
    
    public void start() {
        Thread t = new Thread(httpServer);
        t.setName("http-server");
        t.start();
    }
}
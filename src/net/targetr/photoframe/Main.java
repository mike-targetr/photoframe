package net.targetr.photoframe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.targetr.httpd.Constants;
import net.targetr.httpd.util.NiceLogFormatter;

/**
 * Starts the PhotoFrame software.
 * Creates a new HTTP server listening for connections and
 * attempts to open chromium browser in kiosk mode.
 * @author mike-targetr
 */
public class Main {
    
    public static final String DEFAULT_UPLOAD_DIR = "photos";

    public static void main(String[] args) throws IOException {
        
        setupLogging();
        
        int port = Constants.DEFAULT_PORT;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        
        String uploadDir = DEFAULT_UPLOAD_DIR;
        if (args.length >= 2) {
            uploadDir = args[1];
        }
        
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);
        
        PhotoFrameServer server = new PhotoFrameServer(port, uploadPath);
        server.start();
        
        String playerAddress = "http://localhost:" + port + "/player/";
        try {
            String cmd = "chromium-browser --kiosk " + playerAddress;
            System.out.println("Executing: " + cmd);
            Runtime.getRuntime().exec(cmd);
        }
        catch (IOException ex) {
            System.err.println("Failed to start web browser. Please manually open " + playerAddress);
        }
    }

    private static void setupLogging() {
        Logger rootLog = Logger.getLogger("");
        rootLog.setLevel(Level.FINE);
        rootLog.getHandlers()[0].setLevel(Level.FINE);
        Logger logger = Logger.getLogger("net.targetr");
        NiceLogFormatter formatter = new NiceLogFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);
        handler.setLevel(Level.FINE);
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
    }
}

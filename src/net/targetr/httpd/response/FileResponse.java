package net.targetr.httpd.response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Wraps file data into a HttpResponse.
 * @author mike-targetr
 */
public class FileResponse extends ByteArrayResponse {

    public FileResponse(Path path) throws IOException {
        super(Files.readAllBytes(path), Files.probeContentType(path));
    }
}

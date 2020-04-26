package net.targetr.photoframe;

/**
 * Encapsulates photo meta data to aid photo selection.
 * @author mike-targetr
 */
public class PhotoMeta {
    
    public final String filename;
    
    public final long identity;
    public final int width;
    public final int height;
    public final boolean preview;
    public final boolean portrait;
    public final String type;

    public PhotoMeta(String filename) {
        this.filename = filename;
        
        String[] details = filename.split("\\.");
        identity = Long.parseLong(details[0]);
        width = Integer.parseInt(details[1]);
        height = Integer.parseInt(details[2]);
        preview = width < 500 && height < 500;
        portrait = width < height;
        type = details[details.length-1];
    }
}

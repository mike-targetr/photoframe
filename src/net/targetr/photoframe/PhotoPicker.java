package net.targetr.photoframe;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Scans a directory of photos and picks photos to be displayed.
 * Randomly selects similar photos every 15 seconds or recently 
 * uploaded or selected photos for 30 seconds.
 * @author mike-targetr
 */
public class PhotoPicker {

    // The amount of time a recent selection or upload is displayed
    // before resuming random selection.
    public static final long SELECTION_PERIOD = 30000;
    
    // The amount of time a non selected image displays for
    public static final long DISPLAY_PERIOD = 15000;
    
    private final Random random = new Random();
    
    private List<PhotoMeta> photos;
    private List<PhotoMeta> previews;
    
    private long selectionTime;
    private final LinkedList<Long> selection;
    
    private long timeSlot = 0;
    private List<PhotoMeta> shuffle;

    private Path path;
    
    public PhotoPicker(Path path) throws IOException {
        this.path = path;
        selection = new LinkedList<Long>();
        scan();
    }
    
    public synchronized void select(long identity) {
        selection.addFirst(identity);
        selectionTime = System.currentTimeMillis();
    }
    
    public synchronized void remove(long identity) throws IOException {
        
        selection.remove(identity);
        for (PhotoMeta photo : photos) {
            if (photo.identity == identity) {
                Path p = path.resolve(photo.filename);
                Files.delete(p);
            }
        }
        
        scan();
    }
    
    public synchronized List<PhotoMeta> pick(int max) {
        
        LinkedList<PhotoMeta> list = new LinkedList<PhotoMeta>();
        
        if (photos.size() == 0) return list;
        
        List<PhotoMeta> selection = getRecentSelection(); 
        List<PhotoMeta> shuffled = randomSelection();
        
        PhotoMeta first = selection.size() > 0 ? selection.get(0) : shuffled.get(0);
        list.add(first);
        
        // Add similar shaped photos that can be used to fill screen
        addSimilarAspect(selection, list, max);
        addSimilarAspect(shuffled, list, max);
        
        return list;
    }
    
    private void addSimilarAspect(List<PhotoMeta> source, List<PhotoMeta> target, int max) {
        PhotoMeta first = target.get(0);
        for (PhotoMeta p : source) {
            if (target.size() >= max) break;
            if (target.contains(p)) continue;
            if (p.portrait == first.portrait) {
                target.add(p);
            }
        }
    }

    public List<PhotoMeta> getAllPreviews() {
        return previews;
    }
    
    private synchronized List<PhotoMeta> getRecentSelection() {
        
        if (System.currentTimeMillis() > selectionTime + SELECTION_PERIOD) {
            selection.clear();
        }
        
        LinkedList<PhotoMeta> selected = new LinkedList<PhotoMeta>();
        
        for (Long identity : selection) {
            for (PhotoMeta p : photos) {
                if (p.identity == identity) {
                    selected.add(p);
                }
            }
        }
        
        return selected;
    }

    public synchronized void scan() throws IOException {
        
        Map<Long, PhotoMeta> photoMap = new HashMap<Long, PhotoMeta>();
        Map<Long, PhotoMeta> previewMap = new HashMap<Long, PhotoMeta>();
        
        DirectoryStream<Path> stream = null;
        try {
            stream = Files.newDirectoryStream(path);
            for (Path p: stream) {
                PhotoMeta photo = new PhotoMeta(p.getFileName().toString());
                if (photo.type.equals("jpg")) {
                    Map<Long, PhotoMeta> map = photo.preview ? previewMap : photoMap;
                    map.put(photo.identity, photo);
                }
            }
        }
        finally {
            try { stream.close(); } catch (Exception ignored) { }
        }
        
        Set<Long> idSet = new HashSet<Long>();
        idSet.addAll(photoMap.keySet());
        idSet.addAll(previewMap.keySet());
        
        List<Long> idList = new ArrayList<Long>(idSet);
        Collections.sort(idList);
        
        List<PhotoMeta> photos = new ArrayList<PhotoMeta>();
        List<PhotoMeta> previews = new ArrayList<PhotoMeta>();
        for (Long id : idList) {
            PhotoMeta photo = photoMap.get(id);
            PhotoMeta preview = previewMap.get(id);
            if (photo != null && preview != null) {
                photos.add(photo);
                previews.add(preview);
            }
        }
        
        this.timeSlot = 0;
        this.shuffle = null;
        this.photos = Collections.unmodifiableList(photos);
        this.previews = Collections.unmodifiableList(previews);
    }
    
    private List<PhotoMeta> randomSelection() {
        
        if (photos.size() <= 1) return photos;

        // The same photos should be selected for all requests during DISPLAY_PERIOD.
        long timeSlot = (System.currentTimeMillis() - selectionTime) / DISPLAY_PERIOD;
        
        if (timeSlot == this.timeSlot && shuffle != null) {
            return shuffle;
        }
        
        List<PhotoMeta> currentResult = randomSelection(photos, random);

        if (shuffle != null) {
            // Ensure that two sucessive time slots do not display same photos.
            while (shuffle.get(0).equals(currentResult.get(0)) || shuffle.get(1).equals(currentResult.get(1))) {
                currentResult = randomSelection(photos, random);
            }
        }
        
        this.timeSlot = timeSlot;
        this.shuffle = currentResult;
        
        return currentResult;
    }

    private static List<PhotoMeta> randomSelection(Collection<PhotoMeta> source, Random random) {
        
        List<PhotoMeta> bag = new ArrayList<PhotoMeta>(source);
        List<PhotoMeta> result = new ArrayList<PhotoMeta>();
        
        while (bag.size() > 0) {
            int idx = random.nextInt(bag.size());
            result.add(bag.remove(idx));
        }
    
        return result;
    }
}

package pl.mpak.util;

import java.util.EventListener;

public interface ClassLoaderListener extends EventListener {

    public void loadedZipFile(String filename);
        
    public void finishedLoadingZipFiles();
    
}

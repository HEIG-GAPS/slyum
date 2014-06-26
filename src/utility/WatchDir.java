package utility;

import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WatchDir {
  
  private class SlyumKey {
    Path file;
    WatchFileListener listener;
    boolean isDisable = false;
    int ignore = 0;
    
    SlyumKey(Path file, WatchFileListener listener) {
      this.file = file;
      this.listener = listener;
    }
    
    int ignorePostDec() {
      int temp = ignore;
      ignore = (ignore == 0 ? 0 : --ignore);
      return temp;
    }
    
    Path dir() {
      return file.getParent();
    }
  }
  
  @SuppressWarnings("unchecked")
  private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
      return (WatchEvent<T>)event;
  }

  private static WatchDir instance;
  
  /**
   * Register the given directory with the WatchService
   * @param file
   * @param listener
   * @throws java.io.IOException
   */
  public static void register(Path file, WatchFileListener listener) 
      throws IOException {
    getInstance()._register(file, listener);
    System.out.println("File " + file + " registered.");
  }
  
  public static void unregister(Path file) {
    getInstance()._unregister(file);
    System.out.println("File " + file + " unregistered.");
  }
  
  public static void stopWatchingFile(Path file, boolean stopWatching) {
    getInstance()._stopWatchingFile(file, stopWatching);
  }
  
  public void _stopWatchingFile(Path file, boolean stopWatching) {
    System.out.println(file + " " + stopWatching);
    keys.get(searchByPath(file)).isDisable = stopWatching;
  }
  
  public static void ignoreNextEvents(Path file, int count) {
    getInstance()._ignoreNextEvents(file, count);
  }
  
  public void _ignoreNextEvents(Path file, int count) {
    keys.get(searchByPath(file)).ignore = count;
  }
  
  private static WatchDir getInstance() {
    if (instance == null)
      try {
        instance = new WatchDir();
      } catch (IOException ex) {
        Logger.getLogger(WatchDir.class.getName()).log(
            Level.SEVERE, "Unable to create WatchDir.", ex);
      }
    return instance;
  }
  
  private final WatchService watcher;
  private final HashMap<WatchKey, SlyumKey> keys;
  
  private Thread thread = null;

  private WatchDir() throws IOException {
    this.watcher = FileSystems.getDefault().newWatchService();
    this.keys = new HashMap<>();
  }
  
  private void _register(Path file, WatchFileListener listener) 
      throws IOException {
    WatchKey key = file.getParent().register(
        watcher, ENTRY_DELETE, ENTRY_MODIFY);
    keys.put(key, new SlyumKey(file, listener));
    
    if (thread == null) {
      thread = new Thread(new Runnable() {

        @Override
        public void run() {
          processEvents();
          thread = null;
        }
      });
      thread.start();
    }
  }
  
  private void _unregister(Path file) {
    WatchKey key = searchByPath(file);
    if (key == null)
      return;
    
    keys.remove(key);
    key.cancel();
  }
  
  private WatchKey searchByPath(Path file) {
    Iterator it = keys.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry pairs = (Map.Entry)it.next();
        SlyumKey skey = (SlyumKey)pairs.getValue();
        if (skey.file.equals(file))
          return (WatchKey)pairs.getKey();
        it.remove();
    }
    return null;
  }

  /**
   * Process all events for keys queued to the watcher
   */
  private void processEvents() {
    while(true) {

      // wait for key to be signalled
      WatchKey key;
      try {
        key = watcher.take();
      } catch (InterruptedException x) {
        return;
      }

      SlyumKey skey = keys.get(key);
      if (skey.file == null) {
        System.err.println("This file is no longer watched.");
        continue;
      }

      // Get only the last event.
      List<WatchEvent<?>> events = key.pollEvents();
      WatchEvent<?> event = events.get(events.size() - 1);
      WatchEvent.Kind kind = event.kind();

      // Context for directory entry event is the file name of entry
      WatchEvent<Path> ev = cast(event);
      Path name = ev.context();
      Path child = skey.dir().resolve(name);
      
      if (child.equals(skey.file) && !skey.isDisable && skey.ignorePostDec() == 0) {
        if (kind == ENTRY_DELETE)
          skey.listener.fileDeleted();
        else if (kind == ENTRY_MODIFY)
          skey.listener.fileModified(); 
      }

      // reset key and remove from set if directory no longer accessible
      boolean valid = key.reset();
      if (!valid) {
        keys.remove(key);

        // all directories are inaccessible
        if (keys.isEmpty()) {
            break;
        }
      }
    }
  }
}


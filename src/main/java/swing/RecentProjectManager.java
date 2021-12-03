package swing;

import utility.SMessageDialog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class RecentProjectManager {

  public static final String filename = Slyum.getPathAppDir()
                                        + Slyum.FILE_SEPARATOR + "history.properties";
  final static Charset ENCODING = StandardCharsets.ISO_8859_1;

  private static RecentProjectManager instance;

  public static RecentProjectManager getInstance() {
    if (instance == null) instance = new RecentProjectManager();

    return instance;
  }

  private RecentProjectManager() {
    try {
      createFile();
    } catch (IOException e) {
      e.printStackTrace();
      SMessageDialog.showErrorMessage("Error to create projects history file.");
    }
  }

  public List<String> getHistoryListString() {
    List<String> list = null;
    try {
      list = cleanHistory(Files.readAllLines(Paths.get(filename), ENCODING));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return list;
  }

  private List<String> cleanHistory(List<String> files) {
    // Ã‰vite les modifications concurrantes dans la liste files
    // (on ne peut pas itÃ©rer sur une liste et simultanÃ©ment en supprimer des
    // Ã©lÃ©ments).
    List<String> filesToRemove = new LinkedList<>();

    for (String entry : files) {
      Path p = Paths.get(entry);
      if (!Files.exists(p)) filesToRemove.add(entry);
    }

    for (String file : filesToRemove)
      files.remove(file);

    return files;
  }

  public void clearFile() throws IOException {
    File f = new File(filename);
    f.delete();
    f.createNewFile();
  }

  public void addEntry(String projectPath) throws IOException {
    if (projectPath.isEmpty() || projectPath == null)
      throw new IllegalArgumentException("ProjectPath is empty or null.");

    // Get the current history.
    List<String> history = getHistoryListString();
    clearFile();

    moveToEnd(history, projectPath);
    history = checkSize(history);

    // Rewrite the file.
    Path path = Paths.get(filename);
    try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)) {
      for (String entry : history) {
        writer.write(entry);
        writer.newLine();
      }
    }

    Slyum.getInstance().updateMenuItemHistory();
  }

  private List<String> checkSize(List<String> list) {
    if (list.size() > 5) list.remove(0);

    return list;
  }

  private <T extends Object> void moveToEnd(List<T> list, T object) {
    list.remove(object);
    list.add(object);
  }

  private void createFile() throws IOException {
    new File(filename).createNewFile();
  }

  public static void addhistoryEntry(String projectPath) {
    try {
      getInstance().addEntry(projectPath);
    } catch (IOException e) {
      e.printStackTrace();
      SMessageDialog.showErrorMessage("Error to add entry in history.");
    }
  }

  public static String getMoreRecentFile() {
    List<String> list = getHistoryList();

    if (list.size() < 1) return null;

    return list.get(list.size() - 1);
  }

  public static List<String> getHistoryList() {
    return getInstance().getHistoryListString();
  }

}

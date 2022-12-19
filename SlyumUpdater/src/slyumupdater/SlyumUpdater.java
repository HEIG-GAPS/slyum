package slyumupdater;

import tagDownload.TagDownload;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author David Miserez
 */
public class SlyumUpdater extends JFrame {

  private Thread worker;
  private final String root = "update" + System.getProperty("file.separator");
  private static final String slyumNameFile = System.getProperty("file.separator") + "Slyum.jar";
  private static final String macOsPath = "/Contents/Java";
  private static final String macOsAppPath = ".app" + macOsPath;

  public static final String tagDownload = "[download]";

  private JTextArea outText;
  private JButton cancle;
  private JScrollPane sp;
  private JPanel pan1;
  private JPanel pan2;
  private String slyumPath;

  public SlyumUpdater() {
  }

  private SlyumUpdater(String slyumPath) {
    this.slyumPath = slyumPath;
    initComponents();
    outText.setText("Contacting Download Server...");
    download();
  }

  private void initComponents() {

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

    pan1 = new JPanel();
    pan1.setLayout(new BorderLayout());

    pan2 = new JPanel();
    pan2.setLayout(new FlowLayout());

    outText = new JTextArea();
    sp = new JScrollPane();
    sp.setViewportView(outText);

    cancle = new JButton("Cancel Update");
    cancle.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });
    pan2.add(cancle);
    pan1.add(sp, BorderLayout.CENTER);
    pan1.add(pan2, BorderLayout.SOUTH);

    add(pan1);
    pack();
    this.setSize(200, 200);
  }

  private void download() {
    worker = new Thread(
        new Runnable() {
          @Override
          public void run() {
            try {
              System.out.println(getDownloadLinkFromHost());
              downloadFile(getDownloadLinkFromHost());
              unzip();
              copyFiles(new File(root), new File(slyumPath).getAbsolutePath());
              cleanup();
              launch();
              outText.setText(outText.getText() + "\nUpdate Finished!");
            } catch (Exception ex) {
              JOptionPane.showMessageDialog(null, "An error occured while preforming update!");
            }
          }
        });
    worker.start();
  }

  private void launch() {
    String[] run;

    if (isAppBundle())
      run = new String[] {"open", getAppBundlePath()};
    else
      run = new String[] {"java", "-jar", slyumPath + slyumNameFile};

    try {
      Runtime.getRuntime().exec(run);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    System.exit(0);
  }

  private String getAppBundlePath() {
    return slyumPath.substring(0, slyumPath.indexOf(macOsPath));
  }

  private boolean isAppBundle() {
    return isMac() && slyumPath.contains(macOsAppPath);
  }

  private static boolean isMac() {
    final String os = System.getProperty("os.name").toLowerCase();
    return os.contains("mac");
  }

  private void cleanup() {
    outText.setText(outText.getText() + "\nPreforming clean up...");
    new File("update.zip").delete();
    remove(new File(root));
    new File(root).delete();
  }

  private void remove(final File f) {
    File[] files = f.listFiles();
    for (File ff : files) {
      if (ff.isDirectory()) {
        remove(ff);
        ff.delete();
      } else {
        ff.delete();
      }
    }
  }

  private void copyFiles(final File f, final String dir) throws IOException {
    File[] files = f.listFiles();
    for (File ff : files) {
      if (ff.isDirectory()) {
        new File(dir + "/" + ff.getName()).mkdir();
        copyFiles(ff, dir + "/" + ff.getName());
      } else {
        copy(ff.getAbsolutePath(), dir + "/" + ff.getName());
      }
    }
  }

  public void copy(final String srFile, final String dtFile) throws IOException {

    File f1 = new File(srFile);
    File f2 = new File(dtFile);
    try (InputStream in = new FileInputStream(f1);
         OutputStream out = new FileOutputStream(f2)) {
      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }
    }
  }

  private void unzip() throws IOException {
    int BUFFER = 2048;
    ZipEntry entry;
    try (ZipFile zipfile = new ZipFile("update.zip")) {
      Enumeration e = zipfile.entries();
      (new File(root)).mkdir();
      while (e.hasMoreElements()) {
        entry = (ZipEntry) e.nextElement();
        outText.setText(outText.getText() + "\nExtracting: " + entry);
        if (entry.isDirectory()) {
          (new File(root + entry.getName())).mkdirs();
        } else {
          File target = new File(root + entry.getName());
          File parent = target.getParentFile();
          if (!parent.exists() && !parent.mkdirs())
            throw new IllegalStateException("Couldn't create dir: " + parent);
          target.createNewFile();
          try (BufferedInputStream is = new BufferedInputStream(zipfile.getInputStream(entry))) {
            int count;
            byte[] data = new byte[BUFFER];
            FileOutputStream fos = new FileOutputStream(root + entry.getName());
            try (BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER)) {
              while ((count = is.read(data, 0, BUFFER))
                     != -1) {
                dest.write(data, 0, count);
              }
            }
          }
        }
      }
    }
  }

  private void downloadFile(final String link) throws MalformedURLException, IOException {

    outText.setText(outText.getText() + "\n" + "Downloading file...");
    URL website = new URL(link);
    try (ReadableByteChannel rbc = Channels.newChannel(website.openStream());
         FileOutputStream fos = new FileOutputStream("update.zip")) {
      fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }
    outText.setText(outText.getText() + "\nDownload Complete!");
  }

  private String getDownloadLinkFromHost() throws Exception {
    return TagDownload.getContentTag(tagDownload);
  }

  public static void main(final String... args) {
    if (args.length != 1)
      JOptionPane.showMessageDialog(null, "Arguments length probleme!");

    java.awt.EventQueue.invokeLater(() -> new SlyumUpdater(args[0]).setVisible(true));
  }

}

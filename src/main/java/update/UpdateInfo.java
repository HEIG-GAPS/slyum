package update;

import swing.PanelClassDiagram;
import swing.PropertyLoader;
import swing.Slyum;
import swing.slyumCustomizedComponents.FlatButton;
import swing.slyumCustomizedComponents.SScrollPane;
import utility.SMessageDialog;
import utility.TagDownload;
import utility.Utility;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static swing.Slyum.openURL;

/**
 * @author MiserezDavid
 */
public class UpdateInfo extends JDialog {

  public static boolean isUpdateAvailable() {
    try {
      return getIntVersion(Updater.getLatestVersion()) > getIntVersion(Slyum.VERSION);
    } catch (Exception ex) {
      Logger.getLogger(UpdateInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  public static void getNewUpdate() {
    getNewUpdate(false);
  }

  public static void getNewUpdate(boolean askForDisableCheckingUpdate) {
    try {
      if (isUpdateAvailable())
        new UpdateInfo(Updater.getWhatsNew(), askForDisableCheckingUpdate);
    } catch (Exception ex) {
      Logger.getLogger(UpdateInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void getPatchNote() {
    try {
      new UpdateInfo(Updater.getWhatsNew());
    } catch (Exception ex) {
      Logger.getLogger(UpdateInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static final String updaterDirectory = Slyum.getPathAppDir();
  public static final String UPDATER_FILE =
      updaterDirectory + Slyum.FILE_SEPARATOR + "SlyumUpdater.jar";
  public static final String TAG_UPDATER = "[updater]";
  public static final String TAG_UPDATER_VERSION = "[updaterVersion]";

  private JEditorPane infoPane;
  private JScrollPane scp;
  private JButton ok;
  private JButton cancel;
  private JPanel pan1;
  private JPanel pan2;
  private Boolean askForDisableCheckingUpdate = false;
  private Boolean isUpdater = true;

  private UpdateInfo(String info) {
    super(Slyum.getInstance(), true);
    isUpdater = false;
    initComponents();
    infoPane.setText(info);
    setVisible(true);
  }

  private UpdateInfo(String info, boolean askForDisableCheckingUpdate) {
    super(Slyum.getInstance(), true);
    initComponents();
    infoPane.setText(info);
    this.askForDisableCheckingUpdate = askForDisableCheckingUpdate;
    setVisible(true);
  }

  private void initComponents() {

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    if (isUpdater)
      setTitle("Slyum - New update available");
    else
      setTitle("Slyum - Patch notes");

    JComponent glassPane = new JComponent() {
      @Override
      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;
        final URL imageLogoURL = Slyum.class.getResource(Slyum.ICON_PATH
                                                         + "logo148.png");
        Rectangle bounds = getBounds();
        BufferedImage imgLogo;
        Utility.setRenderQuality(g2);
        try {
          imgLogo = ImageIO.read(imageLogoURL);
          Point imgLocation = new Point(bounds.x + bounds.width
                                        - imgLogo.getWidth() - 40, bounds.y + 20);
          g2.drawImage(imgLogo, imgLocation.x, imgLocation.y, this);
        } catch (final IOException e) {
          System.err.println("Unable to get Slyum's logo for updater.");
        }
      }
    };
    setGlassPane(glassPane);
    glassPane.setVisible(true);

    pan1 = new JPanel();
    pan1.setLayout(new BorderLayout());

    pan2 = new JPanel();
    pan2.setLayout(new FlowLayout());

    HTMLEditorKit kit = new HTMLEditorKit();
    infoPane = new JEditorPane();
    infoPane.setEditable(false);
    infoPane.setEditorKit(kit);
    infoPane.setDocument(kit.createDefaultDocument());
    infoPane.setContentType("text/html; charset=utf-8");

    StyleSheet s = kit.getStyleSheet();
    s.addRule("body {color:#444444; margin: 10px 20px; font-family: Ubuntu;}");
    s.addRule("h1 {color:#2A60FF; margin-left: 40px; font-size: 1.5em;}");
    s.addRule("h2 {color:#444444; font-size: 1.2em;}");

    scp = new SScrollPane(infoPane);
    scp.setBorder(null);

    ok = new FlatButton("Update");
    ok.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        update();
      }
    });

    cancel = new FlatButton("Close");
    cancel.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (askForDisableCheckingUpdate &&
            JOptionPane.showConfirmDialog(
                UpdateInfo.this,
                "Would you continue to check for updates at launch of Slyum?",
                "Slyum",
                JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
          setUpdateCheckedAtLaunch(false);
        UpdateInfo.this.dispose();
      }
    });

    if (isUpdater)
      pan2.add(ok);
    pan2.add(cancel);
    pan1.add(pan2, BorderLayout.SOUTH);
    pan1.add(scp, BorderLayout.CENTER);
    add(pan1);
    pack();
    setSize(750, 600);
    setLocationRelativeTo(Slyum.getInstance());
  }

  private void initializeUpdater() throws MalformedURLException, Exception {
    File f = new File(UPDATER_FILE);
    int updaterVersion = Integer.parseInt(TagDownload.getContentTag(TAG_UPDATER_VERSION));

    if (f.exists() && Slyum.getUpdaterVersion() >= updaterVersion)
      return;

    if (!new File(updaterDirectory).exists())
      new File(updaterDirectory).mkdirs();

    URL website = new URL(TagDownload.getContentTag(TAG_UPDATER));
    try (ReadableByteChannel rbc = Channels.newChannel(website.openStream());
         FileOutputStream fos = new FileOutputStream(UPDATER_FILE)) {
      fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }

    Slyum.setUpdaterVersion(updaterVersion);
  }

  private void update() {
    // Check if a current project is open and unsaved.
    if (Slyum.getInstance() != null && Slyum.getInstance().isVisible()) {
      if (JOptionPane.showConfirmDialog(
          UpdateInfo.this,
          "Slyum need to be closed before installing new update.\n" +
          "Would you like to continue? Your project will be saved.",
          "Slyum",
          JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
        return;
      PanelClassDiagram.getInstance().saveToXML(false);
    }

    try {
      initializeUpdater();
    } catch (Exception ex) {
      Logger.getLogger(UpdateInfo.class.getName()).log(Level.SEVERE, null, ex);
      SMessageDialog.showErrorMessage("Unable to get the updater.");
    }
    try {
      String applicationPath = new File(
          Slyum.class.getProtectionDomain().getCodeSource()
                     .getLocation().toURI().getPath()).getParent();

      // Test if the user has the permission to write in the Slyum directory.
      // This method is used since Java is shit and the canWrite method of SecurityManager
      // always return true because there is no fucking SecurityManager.
      File fileTest = new File(applicationPath + Slyum.FILE_SEPARATOR + "test");
      if (fileTest.createNewFile())
        fileTest.delete();

      String[] run = {"java", "-jar", UPDATER_FILE, applicationPath};
      Runtime.getRuntime().exec(run);

    } catch (URISyntaxException | IOException ex) {

      JButton btnClose = new JButton("Close");
      btnClose.addActionListener((ActionEvent e) -> {
        Utility.getOptionPane((JComponent) e.getSource()).setValue(btnClose);
      });

      JButton btnUpdateManually = new JButton("Update manually");
      btnUpdateManually.addActionListener((ActionEvent e) -> {
        openURL(Slyum.URL_PROJECT_PAGE);
        Utility.getOptionPane((JComponent) e.getSource()).setValue(btnUpdateManually);
      });

      String msgError = "An error occured when trying to update Slyum.";
      if (ex instanceof IOException)
        msgError += "\nIt seems you don't have write access for the destination directory.";

      JOptionPane.showOptionDialog(
          this,
          msgError,
          "Slyum - Updater error",
          JOptionPane.OK_CANCEL_OPTION,
          JOptionPane.ERROR_MESSAGE,
          null,
          new JButton[] {btnUpdateManually, btnClose},
          btnUpdateManually);
    }
    System.exit(0);
  }

  private static int getIntVersion(String version) {
    return Integer.parseInt(version.replace(".", ""));
  }

  public static boolean isUpdateCheckedAtLaunch() {
    final String prop = PropertyLoader.getInstance().getProperties()
                                      .getProperty(PropertyLoader.CHECK_UPDATE_AT_LAUNCH);
    boolean enable = true;

    if (prop != null)
      enable = Boolean.parseBoolean(prop);
    return enable;
  }

  public static void setUpdateCheckedAtLaunch(boolean enable) {
    Properties properties = PropertyLoader.getInstance().getProperties();
    properties.put(
        PropertyLoader.CHECK_UPDATE_AT_LAUNCH,
        String.valueOf(enable));
  }

}


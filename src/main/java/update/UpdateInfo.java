package update;

import swing.PanelClassDiagram;
import swing.PropertyLoader;
import swing.Slyum;
import swing.slyumCustomizedComponents.FlatButton;
import swing.slyumCustomizedComponents.SScrollPane;
import utility.SMessageDialog;
import utility.Utility;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.event.ActionEvent;
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
public final class UpdateInfo extends JDialog {

  public static boolean isUpdateAvailable() {
    return Updater.getInstance().couldContactServer() &&
           Updater.getInstance().getLatestVersion().isGreaterThan(Slyum.VERSION);
  }

  public static void getNewUpdate() {
    getNewUpdate(false);
  }

  public static void getNewUpdate(final boolean askForDisableCheckingUpdate) {
    try {
      if (isUpdateAvailable())
        new UpdateInfo(Updater.getInstance().getWhatsNew(), askForDisableCheckingUpdate);
    } catch (Exception ex) {
      Logger.getLogger(UpdateInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void getPatchNote() {
    try {
      new UpdateInfo(Updater.getInstance().getWhatsNew());
    } catch (Exception ex) {
      Logger.getLogger(UpdateInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private JEditorPane infoPane;
  private JScrollPane scp;
  private JButton ok;
  private JButton cancel;
  private JPanel pan1;
  private JPanel pan2;
  private boolean askForDisableCheckingUpdate = false;
  private boolean isUpdater = true;

  private UpdateInfo(final String info) {
    super(Slyum.getInstance(), true);
    isUpdater = false;
    initComponents();
    infoPane.setText(info);
    setVisible(true);
  }

  private UpdateInfo(final String info, final boolean askForDisableCheckingUpdate) {
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
      public void paintComponent(final Graphics g) {
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
          Slyum.LOGGER.log(Level.SEVERE, "Unable to get Slyum's logo for updater.", e);
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
    ok.addActionListener(e -> update());

    cancel = new FlatButton("Close");
    cancel.addActionListener(e -> {
      if (askForDisableCheckingUpdate &&
          JOptionPane.showConfirmDialog(
              UpdateInfo.this,
              "Would you continue to check for updates at launch of Slyum?",
              "Slyum",
              JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
        setUpdateCheckedAtLaunch(false);
      UpdateInfo.this.dispose();
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
      openURL(Updater.getInstance().getRedirectUrl());
    } catch (Exception ex) {
      Logger.getLogger(UpdateInfo.class.getName()).log(Level.SEVERE, null, ex);
      SMessageDialog.showErrorMessage("Unable to get the updater.");
    }

    System.exit(0);
  }

  public static boolean isUpdateCheckedAtLaunch() {
    final String prop = PropertyLoader.getInstance().getProperties()
                                      .getProperty(PropertyLoader.CHECK_UPDATE_AT_LAUNCH);
    boolean enable = true;

    if (prop != null)
      enable = Boolean.parseBoolean(prop);
    return enable;
  }

  public static void setUpdateCheckedAtLaunch(final boolean enable) {
    Properties properties = PropertyLoader.getInstance().getProperties();
    properties.put(
        PropertyLoader.CHECK_UPDATE_AT_LAUNCH,
        String.valueOf(enable));
  }

}


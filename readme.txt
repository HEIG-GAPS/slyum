--------------------------------------------------------------------------------
 > INTRODUCTION
--------------------------------------------------------------------------------
Slyum is an UML diagramming application for building class diagrams. Slyum makes
the creation of class diagrams faster with an intuitive, 
clean and easy-to-use interface.

--------------------------------------------------------------------------------
 > INSTALLATION
--------------------------------------------------------------------------------
Slyum works with Java, see Java download page for more informations 
(Slyum needs jre 7). Before installing Slyum, make sure to have a complete and
operational installation of Java.

More informations about Java:
  http://www.oracle.com/technetwork/java/javase/downloads/index.html

Mac OS X only (Bundle)
--------------------------------------------------------------------------------
  Use "Slyum.app" to install Slyum like any other app bundle on Mac OS X.

Windows, Linux and Mac OS X
--------------------------------------------------------------------------------

> Installation from Java Web Start (.jnlp)

  Use "Slyum.jnlp" for installing Slyum (by double-cliking on it).
  It will add (possibly*) the following to your system:
  - Add a desktop shortcut
  - Associate .sly files with Slyum
  - Auto-update Slyum
  - Add Slyum to your Start menu (Windows)
  - Add Slyum to your "add/remove programs" (Windows)

  *Use JNLP with precaution, see chapter "Known issues" for more informations.

  After installing Slyum, you can launch it by double-clicking on the desktop 
  shortcut or on the "Slyum.jnlp" file.

  Extra informations about JNLP

    What is the Java Web Start cache?
      Java Web Start stores resources (images, jar, jnlp) in Java Cache. Wich means
      that after the first launch, Java will use cached files. Delete these files
      if Slyum don't update itself.

    How to access (and remove) cached files?
      Windows
        From controls panel, click on Java icon. 
        In general tab, click on the last button "View...".
      Linux
        You're a Linux user, search by yourself.
      Mac OS X
        From System Preferences, click on the Java icon.
        In general tab, click on the last button "View...".

      From here, you can manage (remove) cached files.
      If you no longer want to use cached files (and fix updater problem for ever),
      disable cached files from "Parameters" button in general tab.

> No installation (.jar)

  Double-click on the "Slyum.jar" file (on Linux you have to authorize the execution
  (right-click -> Properties -> tab Permissions -> check "Allow executing file as program")).

  If nothing happens, try to run the following command in a console:
    java -jar "Slyum.jar"

--------------------------------------------------------------------------------
 > KNOWN ISSUES
--------------------------------------------------------------------------------

  JNLP is not known to be reliable:
    - Auto-update on Windows seems to work randomly (see extra informations to force update).
    - Desktop shortcut and association file don't work on Ubuntu (13.04).
    - Default splash screen on Linux render bad. Customized splash screen 
      for Linux didn't seems to work.
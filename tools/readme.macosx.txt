---------------------------------------
Bundle the app for Mac OS X
---------------------------------------

https://java.net/downloads/appbundler/appbundler.html

With a Terminal, call "ant bundle-slyum". The AppBundle will be created in the dist directory.

For adding document type association, edit the "Info.plist" file located in "YouAppBundle.app/Contents/Info.plist" and add following lines:

<key>CFBundleDocumentTypes</key>
<array>
  <dict>
	  <key>CFBundleTypeExtensions</key>
	  <array>
		  <string>sly</string>
	  </array>
	  <key>CFBundleTypeMIMETypes</key>
	  <array>
		  <string>text/xml</string>
	  </array>
	  <key>CFBundleTypeName</key>
	  <string>Slyum File</string>
	  <key>CFBundleTypeOSTypes</key>
	  <array>
		  <string>FSLY</string>
	  </array>
	  <key>CFBundleTypeRole</key>
	  <string>Editor</string>
  </dict>
</array>
  
More informations on CFBundleDocumentTypes: 
http://etutorials.org/Mac+OS/macos+x+for+java+geeks/Chapter+7.+Standalone+Applications/7.3+Application+Bundles/

----------------
Create icns
----------------

Prepare a directory with all images in the PNG format. To match the current example, call it "slyum.iconset".

With a Terminal, call "iconutil -c icns -o slyum.icns slyum.iconset".
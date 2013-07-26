---------------------------------------
Bundle the app for Mac OS X
---------------------------------------

https://java.net/downloads/appbundler/appbundler.html

With a Terminal, call "ant bundle-slyum". The AppBundle will be created in the dist directory.

----------------
Create icns
----------------

Prepare a directory with all images in the PNG format. To match the current example, call it "slyum.iconset".

With a Terminal, call "iconutil -c icns -o slyum.icns slyum.iconset".
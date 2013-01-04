@echo off
java -Xms100m -Xmx800m -cp .;nengo-BUILDNUMBER.jar;LIBS ca.nengo.ui.NengoLauncher

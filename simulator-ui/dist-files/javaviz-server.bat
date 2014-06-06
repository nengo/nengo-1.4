@echo off
java -Xms100m -Xmx800m -cp .;nengo-BUILDNUMBER.jar;LIBS org.python.util.jython ./python/startup_cl.py python/rpyc/scripts/rpyc_classic.py

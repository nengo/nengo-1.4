.. java:import:: java.awt HeadlessException

.. java:import:: java.io File

.. java:import:: javax.swing JFileChooser

.. java:import:: javax.swing.filechooser FileFilter

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.util NengoConfigManager.UserProperties

AllNeoFiles
===========

.. java:package:: ca.nengo.ui.util
   :noindex:

.. java:type::  class AllNeoFiles extends FileExtensionFilter

   File filter which allows all NEO files

   :author: Shu Wu

Methods
-------
acceptExtension
^^^^^^^^^^^^^^^

.. java:method:: @Override public boolean acceptExtension(String str)
   :outertype: AllNeoFiles

getDescription
^^^^^^^^^^^^^^

.. java:method:: @Override public String getDescription()
   :outertype: AllNeoFiles


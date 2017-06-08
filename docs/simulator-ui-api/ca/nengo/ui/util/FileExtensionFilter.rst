.. java:import:: java.io File

.. java:import:: javax.swing.filechooser FileFilter

.. java:import:: ca.nengo.ui.lib.util Util

FileExtensionFilter
===================

.. java:package:: ca.nengo.ui.util
   :noindex:

.. java:type:: public abstract class FileExtensionFilter extends FileFilter

   Filters files based on file extension.

   :author: Shu Wu

Methods
-------
accept
^^^^^^

.. java:method:: @Override public boolean accept(File f)
   :outertype: FileExtensionFilter

acceptExtension
^^^^^^^^^^^^^^^

.. java:method:: public abstract boolean acceptExtension(String extension)
   :outertype: FileExtensionFilter

   :param extension: Extension
   :return: Whether this type of extension is accepted


.. java:import:: java.awt HeadlessException

.. java:import:: java.io File

.. java:import:: javax.swing JFileChooser

.. java:import:: javax.swing.filechooser FileFilter

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.util NengoConfigManager.UserProperties

NeoFileChooser
==============

.. java:package:: ca.nengo.ui.util
   :noindex:

.. java:type:: public class NeoFileChooser

   File chooser used for NEO Model files.

   :author: Shu Wu

Constructors
------------
NeoFileChooser
^^^^^^^^^^^^^^

.. java:constructor:: public NeoFileChooser()
   :outertype: NeoFileChooser

Methods
-------
getSelectedFile
^^^^^^^^^^^^^^^

.. java:method:: public File getSelectedFile()
   :outertype: NeoFileChooser

   :return: Selected file

setSelectedFile
^^^^^^^^^^^^^^^

.. java:method:: public void setSelectedFile(File file)
   :outertype: NeoFileChooser

   :param file: File to select

showOpenDialog
^^^^^^^^^^^^^^

.. java:method:: public int showOpenDialog() throws HeadlessException
   :outertype: NeoFileChooser

   Shows a dialog for opening files

   :throws HeadlessException:
   :return: value returned by the Swing File Chooser

showSaveDialog
^^^^^^^^^^^^^^

.. java:method:: public int showSaveDialog() throws HeadlessException
   :outertype: NeoFileChooser

   Shows a dialog for saving ensembles

   :throws HeadlessException:
   :return: value returned by Swing File Chooser


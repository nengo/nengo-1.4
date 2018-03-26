.. java:import:: java.awt Component

.. java:import:: java.awt Dialog

.. java:import:: java.awt Dimension

.. java:import:: java.awt Frame

.. java:import:: java.awt.event ActionEvent

.. java:import:: java.awt.event ActionListener

.. java:import:: java.awt.event KeyEvent

.. java:import:: java.util Iterator

.. java:import:: java.util List

.. java:import:: java.util Vector

.. java:import:: javax.swing BorderFactory

.. java:import:: javax.swing Box

.. java:import:: javax.swing BoxLayout

.. java:import:: javax.swing JButton

.. java:import:: javax.swing JComponent

.. java:import:: javax.swing JDialog

.. java:import:: javax.swing JOptionPane

.. java:import:: javax.swing JPanel

.. java:import:: javax.swing KeyStroke

.. java:import:: javax.swing.text MutableAttributeSet

.. java:import:: ca.nengo.ui NengoGraphics

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable PropertyInputPanel

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.objects.activities TrackedAction

.. java:import:: ca.nengo.ui.lib.util UserMessages

ConfigDialogClosedException
===========================

.. java:package:: ca.nengo.ui.configurable.managers
   :noindex:

.. java:type::  class ConfigDialogClosedException extends ConfigException

   Exception to be thrown if the Dialog is intentionally closed by the User

   :author: Shu

Constructors
------------
ConfigDialogClosedException
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public ConfigDialogClosedException()
   :outertype: ConfigDialogClosedException

Methods
-------
defaultHandleBehavior
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public void defaultHandleBehavior()
   :outertype: ConfigDialogClosedException


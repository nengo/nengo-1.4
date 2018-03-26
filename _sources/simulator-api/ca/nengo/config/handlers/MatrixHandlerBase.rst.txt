.. java:import:: java.awt BorderLayout

.. java:import:: java.awt Component

.. java:import:: java.awt Container

.. java:import:: java.awt Dialog

.. java:import:: java.awt Dimension

.. java:import:: java.awt Frame

.. java:import:: javax.swing JButton

.. java:import:: javax.swing JComponent

.. java:import:: javax.swing JDialog

.. java:import:: javax.swing JPanel

.. java:import:: javax.swing JSeparator

.. java:import:: javax.swing JTextField

.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.config.ui ConfigurationChangeListener

.. java:import:: ca.nengo.config.ui MatrixEditor

MatrixHandlerBase
=================

.. java:package:: ca.nengo.config.handlers
   :noindex:

.. java:type:: public abstract class MatrixHandlerBase extends BaseHandler

   Base class for ConfigurationHandlers that deal with 2D arrays

Constructors
------------
MatrixHandlerBase
^^^^^^^^^^^^^^^^^

.. java:constructor:: public MatrixHandlerBase(Class<?> c)
   :outertype: MatrixHandlerBase

   Base class for ConfigurationHandlers that deal with 2D arrays

   :param c: Class being configured

Methods
-------
CreateMatrixEditor
^^^^^^^^^^^^^^^^^^

.. java:method:: public abstract MatrixEditor CreateMatrixEditor(Object o, ConfigurationChangeListener configListener)
   :outertype: MatrixHandlerBase

   :param o: Object being configured
   :param configListener: Listener for configuration changes
   :return: A MatrixEditor object (currently defaults to float[][])

getEditor
^^^^^^^^^

.. java:method:: @Override public final Component getEditor(Object o, ConfigurationChangeListener configListener, JComponent parent)
   :outertype: MatrixHandlerBase


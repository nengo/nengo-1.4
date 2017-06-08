.. java:import:: javax.swing ImageIcon

.. java:import:: javax.swing JPanel

.. java:import:: javax.swing JProgressBar

.. java:import:: javax.swing JButton

.. java:import:: java.util Timer

.. java:import:: java.util TimerTask

.. java:import:: org.python.core PyException

.. java:import:: org.python.core PyFrame

.. java:import:: org.python.core PyObject

.. java:import:: org.python.core TraceFunction

.. java:import:: org.python.core ThreadState

.. java:import:: org.python.core Py

.. java:import:: java.awt.event ActionListener

.. java:import:: java.awt.event ActionEvent

.. java:import:: java.awt BorderLayout

.. java:import:: java.awt Insets

.. java:import:: ca.nengo.sim SimulatorEvent

.. java:import:: ca.nengo.sim SimulatorListener

.. java:import:: ca.nengo.ui NengoGraphics

ProgressIndicator
=================

.. java:package:: ca.nengo.ui.util
   :noindex:

.. java:type:: public class ProgressIndicator extends JPanel implements ActionListener, SimulatorListener

Fields
------
bar
^^^

.. java:field::  JProgressBar bar
   :outertype: ProgressIndicator

interruptFlag
^^^^^^^^^^^^^

.. java:field::  boolean interruptFlag
   :outertype: ProgressIndicator

isRunning
^^^^^^^^^

.. java:field::  boolean isRunning
   :outertype: ProgressIndicator

javaThread
^^^^^^^^^^

.. java:field::  Thread javaThread
   :outertype: ProgressIndicator

percentage
^^^^^^^^^^

.. java:field::  int percentage
   :outertype: ProgressIndicator

pythonThread
^^^^^^^^^^^^

.. java:field::  ThreadState pythonThread
   :outertype: ProgressIndicator

serialVersionUID
^^^^^^^^^^^^^^^^

.. java:field:: public static final long serialVersionUID
   :outertype: ProgressIndicator

stop
^^^^

.. java:field::  JButton stop
   :outertype: ProgressIndicator

text
^^^^

.. java:field::  String text
   :outertype: ProgressIndicator

timer
^^^^^

.. java:field::  Timer timer
   :outertype: ProgressIndicator

timerStart
^^^^^^^^^^

.. java:field::  long timerStart
   :outertype: ProgressIndicator

Constructors
------------
ProgressIndicator
^^^^^^^^^^^^^^^^^

.. java:constructor:: public ProgressIndicator()
   :outertype: ProgressIndicator

Methods
-------
actionPerformed
^^^^^^^^^^^^^^^

.. java:method:: public void actionPerformed(ActionEvent e)
   :outertype: ProgressIndicator

interrupt
^^^^^^^^^

.. java:method:: public void interrupt()
   :outertype: ProgressIndicator

interruptViaPython
^^^^^^^^^^^^^^^^^^

.. java:method:: protected void interruptViaPython()
   :outertype: ProgressIndicator

processEvent
^^^^^^^^^^^^

.. java:method:: public void processEvent(SimulatorEvent event)
   :outertype: ProgressIndicator

setText
^^^^^^^

.. java:method:: public void setText(String text)
   :outertype: ProgressIndicator

setThread
^^^^^^^^^

.. java:method:: public void setThread()
   :outertype: ProgressIndicator

start
^^^^^

.. java:method:: public void start(String text)
   :outertype: ProgressIndicator

stop
^^^^

.. java:method:: public void stop()
   :outertype: ProgressIndicator

updateBarString
^^^^^^^^^^^^^^^

.. java:method::  void updateBarString()
   :outertype: ProgressIndicator


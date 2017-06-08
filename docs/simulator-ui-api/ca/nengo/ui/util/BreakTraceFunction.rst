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

BreakTraceFunction
==================

.. java:package:: ca.nengo.ui.util
   :noindex:

.. java:type::  class BreakTraceFunction extends TraceFunction

Methods
-------
traceCall
^^^^^^^^^

.. java:method:: public TraceFunction traceCall(PyFrame frame)
   :outertype: BreakTraceFunction

traceException
^^^^^^^^^^^^^^

.. java:method:: public TraceFunction traceException(PyFrame frame, PyException exc)
   :outertype: BreakTraceFunction

traceLine
^^^^^^^^^

.. java:method:: public TraceFunction traceLine(PyFrame frame, int line)
   :outertype: BreakTraceFunction

traceReturn
^^^^^^^^^^^

.. java:method:: public TraceFunction traceReturn(PyFrame frame, PyObject ret)
   :outertype: BreakTraceFunction


.. java:import:: java.util ArrayList

.. java:import:: java.util HashMap

.. java:import:: java.util List

.. java:import:: java.util Properties

.. java:import:: java.lang StringBuilder

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math.impl ConstantFunction

.. java:import:: ca.nengo.math.impl FourierFunction

.. java:import:: ca.nengo.math.impl PostfixFunction

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model Probeable

.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.util ScriptGenException

.. java:import:: ca.nengo.util TimeSeries

.. java:import:: ca.nengo.util VisiblyMutable

.. java:import:: ca.nengo.util VisiblyMutableUtils

.. java:import:: ca.nengo.util.impl TimeSeriesImpl

FunctionInput
=============

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class FunctionInput implements Node, Probeable

   A class to compute functions analytically and provide that input to other Nodes in a network.

Fields
------
ORIGIN_NAME
^^^^^^^^^^^

.. java:field:: public static final String ORIGIN_NAME
   :outertype: FunctionInput

   Name for the default origin

STATE_NAME
^^^^^^^^^^

.. java:field:: public static final String STATE_NAME
   :outertype: FunctionInput

   Name for the default input

Constructors
------------
FunctionInput
^^^^^^^^^^^^^

.. java:constructor:: public FunctionInput(String name, Function[] functions, Units units) throws StructuralException
   :outertype: FunctionInput

   :param name: The name of this Node
   :param functions: Functions of time (simulation time) that produce the values that will be output by this Node. Each given function corresponds to a dimension in the output vectors. Each function must have input dimension 1.
   :param units: The units in which the output values are to be interpreted
   :throws StructuralException: if functions are not all 1D functions of time

Methods
-------
addChangeListener
^^^^^^^^^^^^^^^^^

.. java:method:: public void addChangeListener(Listener listener)
   :outertype: FunctionInput

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.addChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

clone
^^^^^

.. java:method:: @Override public Node clone() throws CloneNotSupportedException
   :outertype: FunctionInput

getChildren
^^^^^^^^^^^

.. java:method:: public Node[] getChildren()
   :outertype: FunctionInput

getDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public String getDocumentation()
   :outertype: FunctionInput

   **See also:** :java:ref:`ca.nengo.model.Node.getDocumentation()`

getFunctions
^^^^^^^^^^^^

.. java:method:: public Function[] getFunctions()
   :outertype: FunctionInput

   :return: array of functions

getHistory
^^^^^^^^^^

.. java:method:: public TimeSeries getHistory(String stateName) throws SimulationException
   :outertype: FunctionInput

   **See also:** :java:ref:`ca.nengo.model.Probeable.getHistory(java.lang.String)`

getMode
^^^^^^^

.. java:method:: public SimulationMode getMode()
   :outertype: FunctionInput

   :return: SimulationMode.DEFAULT

   **See also:** :java:ref:`ca.nengo.model.Node.getMode()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: FunctionInput

   **See also:** :java:ref:`ca.nengo.model.Node.getName()`

getOrigin
^^^^^^^^^

.. java:method:: public Origin getOrigin(String name) throws StructuralException
   :outertype: FunctionInput

   **See also:** :java:ref:`ca.nengo.model.Node.getOrigin(java.lang.String)`

getOrigins
^^^^^^^^^^

.. java:method:: public Origin[] getOrigins()
   :outertype: FunctionInput

   **See also:** :java:ref:`ca.nengo.model.Node.getOrigins()`

getTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination getTermination(String name) throws StructuralException
   :outertype: FunctionInput

   **See also:** :java:ref:`ca.nengo.model.Node.getTermination(java.lang.String)`

getTerminations
^^^^^^^^^^^^^^^

.. java:method:: public Termination[] getTerminations()
   :outertype: FunctionInput

   **See also:** :java:ref:`ca.nengo.model.Node.getTerminations()`

listStates
^^^^^^^^^^

.. java:method:: public Properties listStates()
   :outertype: FunctionInput

   **See also:** :java:ref:`ca.nengo.model.Probeable.listStates()`

removeChangeListener
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeChangeListener(Listener listener)
   :outertype: FunctionInput

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.removeChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: FunctionInput

   This method does nothing, as the FunctionInput has no state.

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public void run(float startTime, float endTime)
   :outertype: FunctionInput

   **See also:** :java:ref:`ca.nengo.model.Node.run(float,float)`

setDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public void setDocumentation(String text)
   :outertype: FunctionInput

   **See also:** :java:ref:`ca.nengo.model.Node.setDocumentation(java.lang.String)`

setFunctions
^^^^^^^^^^^^

.. java:method:: public void setFunctions(Function[] functions) throws StructuralException
   :outertype: FunctionInput

   :param functions: New list of functions (of simulation time) that define the output of this Node. (Must have the same length as existing Function list.)
   :throws StructuralException: if functions are not all 1D functions of time

setMode
^^^^^^^

.. java:method:: public void setMode(SimulationMode mode)
   :outertype: FunctionInput

   This call has no effect. DEFAULT mode is always used.

   **See also:** :java:ref:`ca.nengo.model.Node.setMode(ca.nengo.model.SimulationMode)`

setName
^^^^^^^

.. java:method:: public void setName(String name) throws StructuralException
   :outertype: FunctionInput

   :param name: The new name

toScript
^^^^^^^^

.. java:method:: public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException
   :outertype: FunctionInput


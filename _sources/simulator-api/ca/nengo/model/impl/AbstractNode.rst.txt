.. java:import:: java.util ArrayList

.. java:import:: java.util LinkedHashMap

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.util VisiblyMutable

.. java:import:: ca.nengo.util VisiblyMutableUtils

AbstractNode
============

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public abstract class AbstractNode implements Node

   A base implementation of Node.

   :author: Bryan Tripp

Constructors
------------
AbstractNode
^^^^^^^^^^^^

.. java:constructor:: public AbstractNode(String name, List<Origin> origins, List<Termination> terminations)
   :outertype: AbstractNode

   :param name: Name of Node
   :param origins: List of Origins from the Node
   :param terminations: List of Terminations onto the Node

Methods
-------
addChangeListener
^^^^^^^^^^^^^^^^^

.. java:method:: public void addChangeListener(Listener listener)
   :outertype: AbstractNode

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.addChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

clone
^^^^^

.. java:method:: @Override public Node clone() throws CloneNotSupportedException
   :outertype: AbstractNode

   Performs a shallow copy. Origins and Terminations are not cloned, because generally they will have to be reparameterized, at least to point to the new Node.

getDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public String getDocumentation()
   :outertype: AbstractNode

   **See also:** :java:ref:`ca.nengo.model.Node.getDocumentation()`

getMode
^^^^^^^

.. java:method:: public SimulationMode getMode()
   :outertype: AbstractNode

   **See also:** :java:ref:`ca.nengo.model.Node.getMode()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: AbstractNode

   **See also:** :java:ref:`ca.nengo.model.Node.getName()`

getOrigin
^^^^^^^^^

.. java:method:: public Origin getOrigin(String name) throws StructuralException
   :outertype: AbstractNode

   **See also:** :java:ref:`ca.nengo.model.Node.getOrigin(java.lang.String)`

getOrigins
^^^^^^^^^^

.. java:method:: public Origin[] getOrigins()
   :outertype: AbstractNode

   **See also:** :java:ref:`ca.nengo.model.Node.getOrigins()`

getTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination getTermination(String name) throws StructuralException
   :outertype: AbstractNode

   **See also:** :java:ref:`ca.nengo.model.Node.getTermination(java.lang.String)`

getTerminations
^^^^^^^^^^^^^^^

.. java:method:: public Termination[] getTerminations()
   :outertype: AbstractNode

   **See also:** :java:ref:`ca.nengo.model.Node.getTerminations()`

removeChangeListener
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeChangeListener(Listener listener)
   :outertype: AbstractNode

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.removeChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

reset
^^^^^

.. java:method:: public abstract void reset(boolean randomize)
   :outertype: AbstractNode

   Does nothing.

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public abstract void run(float startTime, float endTime) throws SimulationException
   :outertype: AbstractNode

   Does nothing.

   **See also:** :java:ref:`ca.nengo.model.Node.run(float,float)`

setDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public void setDocumentation(String text)
   :outertype: AbstractNode

   **See also:** :java:ref:`ca.nengo.model.Node.setDocumentation(java.lang.String)`

setMode
^^^^^^^

.. java:method:: public void setMode(SimulationMode mode)
   :outertype: AbstractNode

   **See also:** :java:ref:`ca.nengo.model.Node.setMode(ca.nengo.model.SimulationMode)`

setName
^^^^^^^

.. java:method:: public void setName(String name) throws StructuralException
   :outertype: AbstractNode

   :param name: The new name


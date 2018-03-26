.. java:import:: java.io Serializable

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model StructuralException

NodeFactory
===========

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public interface NodeFactory extends Serializable

   Produces Nodes. This interface does not define rules as to how the Nodes are parameterized, but a given implementation might use parameters that are constant across nodes, drawn from a PDF, selected from a database, etc.

   :author: Bryan Tripp

Methods
-------
getTypeDescription
^^^^^^^^^^^^^^^^^^

.. java:method:: public String getTypeDescription()
   :outertype: NodeFactory

   :return: A short description of the type of Node created by this factory

make
^^^^

.. java:method:: public Node make(String name) throws StructuralException
   :outertype: NodeFactory

   :param name: The name of the Node (unique within containing Ensemble or Network)
   :throws StructuralException: for any problem that prevents construction
   :return: A new Node


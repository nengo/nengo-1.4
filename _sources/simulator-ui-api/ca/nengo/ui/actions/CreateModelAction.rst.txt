.. java:import:: javax.swing JOptionPane

.. java:import:: javax.swing SwingUtilities

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.lib.actions ActionException

.. java:import:: ca.nengo.ui.lib.actions ReversableAction

.. java:import:: ca.nengo.ui.lib.actions UserCancelledException

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util UserMessages

.. java:import:: ca.nengo.ui.models NodeContainer

.. java:import:: ca.nengo.ui.models NodeContainer.ContainerException

.. java:import:: ca.nengo.ui.models UINeoNode

.. java:import:: ca.nengo.ui.models.constructors AbstractConstructable

.. java:import:: ca.nengo.ui.models.constructors ConstructableNode

.. java:import:: ca.nengo.ui.models.constructors ModelFactory

.. java:import:: ca.nengo.ui.models.nodes UINodeViewable

CreateModelAction
=================

.. java:package:: ca.nengo.ui.actions
   :noindex:

.. java:type:: public class CreateModelAction extends ReversableAction

   Creates a new NEO model

   :author: Shu

Constructors
------------
CreateModelAction
^^^^^^^^^^^^^^^^^

.. java:constructor:: public CreateModelAction(NodeContainer nodeContainer, ConstructableNode constructable)
   :outertype: CreateModelAction

   :param nodeContainer: The container to which the created node should be added to
   :param constructable: Type of Node to be create, such as PNetwork

CreateModelAction
^^^^^^^^^^^^^^^^^

.. java:constructor:: public CreateModelAction(String modelTypeName, NodeContainer nodeContainer, AbstractConstructable constructable)
   :outertype: CreateModelAction

   :param modelTypeName: TODO
   :param nodeContainer: The container to which the created node should be added to
   :param constructable: Type of Node to be create, such as PNetwork

Methods
-------
action
^^^^^^

.. java:method:: @Override protected void action() throws ActionException
   :outertype: CreateModelAction

ensureNonConflictingName
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static void ensureNonConflictingName(Node node, NodeContainer container) throws UserCancelledException
   :outertype: CreateModelAction

   Prompts the user to select a non-conflicting name

   :param node:
   :param container:
   :throws UserCancelledException: If the user cancels

setPosition
^^^^^^^^^^^

.. java:method:: public void setPosition(double x, double y)
   :outertype: CreateModelAction

   :param x: TODO
   :param y: TODO

undo
^^^^

.. java:method:: @Override protected void undo() throws ActionException
   :outertype: CreateModelAction


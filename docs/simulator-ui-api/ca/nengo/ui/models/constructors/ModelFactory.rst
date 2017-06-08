.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable.managers UserTemplateConfigurer

.. java:import:: ca.nengo.ui.models NodeContainer

.. java:import:: ca.nengo.ui.models UINeoNode

ModelFactory
============

.. java:package:: ca.nengo.ui.models.constructors
   :noindex:

.. java:type:: public class ModelFactory

Methods
-------
constructModel
^^^^^^^^^^^^^^

.. java:method:: public static Object constructModel(AbstractConstructable configurable) throws ConfigException
   :outertype: ModelFactory

constructModel
^^^^^^^^^^^^^^

.. java:method:: public static Object constructModel(UINeoNode nodeParent, AbstractConstructable configurable) throws ConfigException
   :outertype: ModelFactory

   :param nodeParent: Used for animation purposes only
   :param configurable:
   :throws ConfigException:

getNodeConstructables
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static ConstructableNode[] getNodeConstructables(NodeContainer container)
   :outertype: ModelFactory


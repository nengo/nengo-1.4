.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable Property

.. java:import:: ca.nengo.ui.configurable.descriptors PString

ProjectionConstructor
=====================

.. java:package:: ca.nengo.ui.models.constructors
   :noindex:

.. java:type:: public abstract class ProjectionConstructor extends AbstractConstructable

Fields
------
pName
^^^^^

.. java:field:: protected static final Property pName
   :outertype: ProjectionConstructor

Methods
-------
IsNameAvailable
^^^^^^^^^^^^^^^

.. java:method:: protected abstract boolean IsNameAvailable(String name)
   :outertype: ProjectionConstructor

configureModel
^^^^^^^^^^^^^^

.. java:method:: @Override protected final Object configureModel(ConfigResult configuredProperties) throws ConfigException
   :outertype: ProjectionConstructor

createModel
^^^^^^^^^^^

.. java:method:: protected abstract Object createModel(ConfigResult configuredProperties, String uniqueName) throws ConfigException
   :outertype: ProjectionConstructor


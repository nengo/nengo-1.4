.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable ConfigResult

.. java:import:: ca.nengo.ui.configurable ConfigSchema

.. java:import:: ca.nengo.ui.configurable IConfigurable

AbstractConstructable
=====================

.. java:package:: ca.nengo.ui.models.constructors
   :noindex:

.. java:type:: public abstract class AbstractConstructable implements IConfigurable

   A UIModel which can be configured through the IConfigurable interface

   :author: Shu Wu

Constructors
------------
AbstractConstructable
^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public AbstractConstructable()
   :outertype: AbstractConstructable

Methods
-------
completeConfiguration
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void completeConfiguration(ConfigResult properties) throws ConfigException
   :outertype: AbstractConstructable

configureModel
^^^^^^^^^^^^^^

.. java:method:: protected abstract Object configureModel(ConfigResult configuredProperties) throws ConfigException
   :outertype: AbstractConstructable

   This function is called from a common thread, so it is not safe to put UI stuff here If there's UI Stuff to be done, put it in afterModelCreated Creates a model for the configuration process, called if a ConfigManager is used

   :param configuredProperties: the configured properties

getDescription
^^^^^^^^^^^^^^

.. java:method:: public String getDescription()
   :outertype: AbstractConstructable

getExtendedDescription
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public String getExtendedDescription()
   :outertype: AbstractConstructable

getModel
^^^^^^^^

.. java:method:: public Object getModel()
   :outertype: AbstractConstructable

getSchema
^^^^^^^^^

.. java:method:: public abstract ConfigSchema getSchema()
   :outertype: AbstractConstructable

preConfiguration
^^^^^^^^^^^^^^^^

.. java:method:: public void preConfiguration(ConfigResult props) throws ConfigException
   :outertype: AbstractConstructable


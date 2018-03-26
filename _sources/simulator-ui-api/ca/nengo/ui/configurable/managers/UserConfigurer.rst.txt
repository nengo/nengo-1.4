.. java:import:: java.awt Container

.. java:import:: java.awt Dialog

.. java:import:: java.awt Frame

.. java:import:: ca.nengo.ui.configurable ConfigException

.. java:import:: ca.nengo.ui.configurable IConfigurable

.. java:import:: ca.nengo.ui.lib.util UIEnvironment

.. java:import:: ca.nengo.ui.lib.util Util

UserConfigurer
==============

.. java:package:: ca.nengo.ui.configurable.managers
   :noindex:

.. java:type:: public class UserConfigurer extends ConfigManager

   Configuration Manager which creates a dialog and let's the user enter parameters to used for configuration

   :author: Shu Wu

Fields
------
parent
^^^^^^

.. java:field:: protected Container parent
   :outertype: UserConfigurer

   Parent, if there is one

Constructors
------------
UserConfigurer
^^^^^^^^^^^^^^

.. java:constructor:: public UserConfigurer(IConfigurable configurable)
   :outertype: UserConfigurer

   :param configurable: Object to be configured

UserConfigurer
^^^^^^^^^^^^^^

.. java:constructor:: public UserConfigurer(IConfigurable configurable, Container parent)
   :outertype: UserConfigurer

   :param configurable: Object to be configured
   :param parent: Frame the user configuration dialog should be attached to

Methods
-------
configureAndWait
^^^^^^^^^^^^^^^^

.. java:method:: @Override public synchronized void configureAndWait() throws ConfigException
   :outertype: UserConfigurer

createConfigDialog
^^^^^^^^^^^^^^^^^^

.. java:method:: protected ConfigDialog createConfigDialog()
   :outertype: UserConfigurer

   Creates the configuration dialog

   :return: Created Configuration dialog

dialogConfigurationFinished
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: protected void dialogConfigurationFinished(ConfigException configException)
   :outertype: UserConfigurer

   :param configException: Configuration Exception thrown during configuration, none if everything went smoothly


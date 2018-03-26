SimulationMode.ModeConfigurable
===============================

.. java:package:: ca.nengo.model
   :noindex:

.. java:type:: public static interface ModeConfigurable
   :outertype: SimulationMode

   Something that has runs in different SimulationModes.

   :author: Bryan Tripp

Methods
-------
getMode
^^^^^^^

.. java:method:: public SimulationMode getMode()
   :outertype: SimulationMode.ModeConfigurable

   :return: The SimulationMode in which the object is running

setMode
^^^^^^^

.. java:method:: public void setMode(SimulationMode mode)
   :outertype: SimulationMode.ModeConfigurable

   Sets the object to run in either the given mode or the closest mode that it supports (all ModeConfigurables must support SimulationMode.DEFAULT, and must default to this mode).

   :param mode: SimulationMode in which it is desired that the object runs.


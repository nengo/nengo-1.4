.. java:import:: ca.nengo.ui.configurable Property

RangedConfigParam
=================

.. java:package:: ca.nengo.ui.configurable.descriptors
   :noindex:

.. java:type:: public abstract class RangedConfigParam extends Property

   A Config Descriptor which can have a confined integer range

   :author: Shu Wu

Constructors
------------
RangedConfigParam
^^^^^^^^^^^^^^^^^

.. java:constructor:: public RangedConfigParam(String name)
   :outertype: RangedConfigParam

RangedConfigParam
^^^^^^^^^^^^^^^^^

.. java:constructor:: public RangedConfigParam(String name, String description)
   :outertype: RangedConfigParam

RangedConfigParam
^^^^^^^^^^^^^^^^^

.. java:constructor:: public RangedConfigParam(String name, Object defaultValue)
   :outertype: RangedConfigParam

RangedConfigParam
^^^^^^^^^^^^^^^^^

.. java:constructor:: public RangedConfigParam(String name, String description, Object defaultValue)
   :outertype: RangedConfigParam

RangedConfigParam
^^^^^^^^^^^^^^^^^

.. java:constructor:: public RangedConfigParam(String name, int defaultValue, int min, int max)
   :outertype: RangedConfigParam

   :param name: Name of the Config Descriptor
   :param defaultValue: default value
   :param min: Min value
   :param max: Max value

Methods
-------
getMax
^^^^^^

.. java:method:: public int getMax()
   :outertype: RangedConfigParam

   :return: Max value

getMin
^^^^^^

.. java:method:: public int getMin()
   :outertype: RangedConfigParam

   :return: Min value

isCheckRange
^^^^^^^^^^^^

.. java:method:: public boolean isCheckRange()
   :outertype: RangedConfigParam

   :return: Whether range checking is enabled


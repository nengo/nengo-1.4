.. java:import:: java.util Collection

.. java:import:: java.util Vector

TooltipBuilder
==============

.. java:package:: ca.nengo.ui.models.tooltips
   :noindex:

.. java:type:: public class TooltipBuilder

   Builds tooltips from parts

   :author: Shu Wu

Constructors
------------
TooltipBuilder
^^^^^^^^^^^^^^

.. java:constructor:: public TooltipBuilder(String name)
   :outertype: TooltipBuilder

   :param name: Name of this tooltip

Methods
-------
addPart
^^^^^^^

.. java:method:: public void addPart(ITooltipPart obj)
   :outertype: TooltipBuilder

addProperty
^^^^^^^^^^^

.. java:method:: public void addProperty(String propertyName, String propertyValue)
   :outertype: TooltipBuilder

addTitle
^^^^^^^^

.. java:method:: public void addTitle(String titleName)
   :outertype: TooltipBuilder

getName
^^^^^^^

.. java:method:: protected String getName()
   :outertype: TooltipBuilder

   :return: Name of this tooltip

getParts
^^^^^^^^

.. java:method:: protected Collection<ITooltipPart> getParts()
   :outertype: TooltipBuilder

   :return: Collection of tooltip parts


.. java:import:: java.util Collection

.. java:import:: java.util Iterator

.. java:import:: ca.nengo.ui.lib Style.NengoStyle

.. java:import:: ca.nengo.ui.lib.world WorldObject

.. java:import:: ca.nengo.ui.lib.world.piccolo WorldObjectImpl

.. java:import:: edu.umd.cs.piccolo.nodes PText

Tooltip
=======

.. java:package:: ca.nengo.ui.models.tooltips
   :noindex:

.. java:type:: public class Tooltip extends WorldObjectImpl

   UI Object which builds itself from a ToolTipBuilder

   :author: Shu Wu

Fields
------
DEFAULT_WIDTH
^^^^^^^^^^^^^

.. java:field:: public static final double DEFAULT_WIDTH
   :outertype: Tooltip

Constructors
------------
Tooltip
^^^^^^^

.. java:constructor:: public Tooltip(TooltipBuilder tooltipBuilder)
   :outertype: Tooltip

Tooltip
^^^^^^^

.. java:constructor:: public Tooltip(TooltipBuilder tooltipBuilder, double width)
   :outertype: Tooltip


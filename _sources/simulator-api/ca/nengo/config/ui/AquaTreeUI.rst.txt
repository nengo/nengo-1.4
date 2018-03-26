.. java:import:: javax.swing.plaf.basic BasicTreeUI

AquaTreeUI
==========

.. java:package:: ca.nengo.config.ui
   :noindex:

.. java:type:: public class AquaTreeUI extends BasicTreeUI

   To be used in place of the Mac look & feel apple.laf.AquaTreeUI (and CUIAquaTree), which seem not to respect differences in tree cell size, or to expand tree cells when they change size.

   :author: Bryan Tripp

Methods
-------
getRowHeight
^^^^^^^^^^^^

.. java:method:: @Override protected int getRowHeight()
   :outertype: AquaTreeUI

setRowHeight
^^^^^^^^^^^^

.. java:method:: @Override public void setRowHeight(int rowHeight)
   :outertype: AquaTreeUI


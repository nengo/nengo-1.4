.. java:import:: ca.nengo.ui.actions ConfigureAction

.. java:import:: ca.nengo.ui.lib.objects.models ModelObject

.. java:import:: ca.nengo.ui.lib.util.menus PopupMenuBuilder

UINeoModel
==========

.. java:package:: ca.nengo.ui.models
   :noindex:

.. java:type:: public abstract class UINeoModel extends ModelObject

   UI Wrapper for a NEO Node Model

   :author: Shu

Constructors
------------
UINeoModel
^^^^^^^^^^

.. java:constructor:: public UINeoModel(Object model)
   :outertype: UINeoModel

Methods
-------
afterModelCreated
^^^^^^^^^^^^^^^^^

.. java:method:: protected final void afterModelCreated()
   :outertype: UINeoModel

altClicked
^^^^^^^^^^

.. java:method:: @Override public void altClicked()
   :outertype: UINeoModel

constructMenu
^^^^^^^^^^^^^

.. java:method:: @Override protected void constructMenu(PopupMenuBuilder menu)
   :outertype: UINeoModel


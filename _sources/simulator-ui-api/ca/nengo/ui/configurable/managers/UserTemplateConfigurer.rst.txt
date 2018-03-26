.. java:import:: java.awt Container

.. java:import:: java.awt Dialog

.. java:import:: java.awt Frame

.. java:import:: ca.nengo.ui.configurable IConfigurable

.. java:import:: ca.nengo.ui.lib.util Util

UserTemplateConfigurer
======================

.. java:package:: ca.nengo.ui.configurable.managers
   :noindex:

.. java:type:: public class UserTemplateConfigurer extends UserConfigurer

   A lot like UserConfigurer, except it allows the user to use templates to save and re-use values

   :author: Shu Wu

Fields
------
DEFAULT_TEMPLATE_NAME
^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final String DEFAULT_TEMPLATE_NAME
   :outertype: UserTemplateConfigurer

   Name of the default template

PREFERRED_TEMPLATE_NAME
^^^^^^^^^^^^^^^^^^^^^^^

.. java:field:: public static final String PREFERRED_TEMPLATE_NAME
   :outertype: UserTemplateConfigurer

   TODO

Constructors
------------
UserTemplateConfigurer
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UserTemplateConfigurer(IConfigurable configurable)
   :outertype: UserTemplateConfigurer

   :param configurable: TODO

UserTemplateConfigurer
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UserTemplateConfigurer(IConfigurable configurable, Container parent)
   :outertype: UserTemplateConfigurer

   :param configurable: TODO
   :param parent: TODO

UserTemplateConfigurer
^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public UserTemplateConfigurer(IConfigurable configurable, Container parent, boolean isTemplateEditable)
   :outertype: UserTemplateConfigurer

   :param configurable: TODO
   :param parent: TODO
   :param isTemplateEditable: TODO

Methods
-------
createConfigDialog
^^^^^^^^^^^^^^^^^^

.. java:method:: @Override protected ConfigDialog createConfigDialog()
   :outertype: UserTemplateConfigurer

isTemplateEditable
^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean isTemplateEditable()
   :outertype: UserTemplateConfigurer

   :return: TODO


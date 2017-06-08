.. java:import:: java.io Serializable

.. java:import:: ca.nengo.ui.lib.util Util

Property
========

.. java:package:: ca.nengo.ui.configurable
   :noindex:

.. java:type:: public abstract class Property implements Serializable

   :author: Shu

Fields
------
defaultValue
^^^^^^^^^^^^

.. java:field:: protected Object defaultValue
   :outertype: Property

Constructors
------------
Property
^^^^^^^^

.. java:constructor:: public Property(String name)
   :outertype: Property

   :param name: TODO

Property
^^^^^^^^

.. java:constructor:: public Property(String name, Object defaultValue)
   :outertype: Property

   :param name: TODO
   :param defaultValue: TODO

Property
^^^^^^^^

.. java:constructor:: public Property(String name, String description)
   :outertype: Property

   :param name: TODO
   :param description: TODO

Property
^^^^^^^^

.. java:constructor:: public Property(String name, String description, Object defaultValue)
   :outertype: Property

   :param name: Name to be given to the parameter
   :param description: Description of the parameter
   :param defaultValue: Default value of this parameter

Methods
-------
createInputPanel
^^^^^^^^^^^^^^^^

.. java:method:: protected abstract PropertyInputPanel createInputPanel()
   :outertype: Property

   :return: UI Input panel which can be used for User Configuration of this property, null if none exists

getDefaultValue
^^^^^^^^^^^^^^^

.. java:method:: public Object getDefaultValue()
   :outertype: Property

   :return: Default value of this parameter

getDescription
^^^^^^^^^^^^^^

.. java:method:: public String getDescription()
   :outertype: Property

   :return: TODO

getInputPanel
^^^^^^^^^^^^^

.. java:method:: public PropertyInputPanel getInputPanel()
   :outertype: Property

   Gets the input panel.

   :return: TODO

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: Property

   :return: Name of this parameter

getTooltip
^^^^^^^^^^

.. java:method:: public String getTooltip()
   :outertype: Property

   :return: The HTML tooltip for the property

getTypeClass
^^^^^^^^^^^^

.. java:method:: public abstract Class<?> getTypeClass()
   :outertype: Property

   :return: Class type that this parameter's value must be

getTypeName
^^^^^^^^^^^

.. java:method:: public abstract String getTypeName()
   :outertype: Property

   :return: A name given to the Class type of this parameter's value

isEditable
^^^^^^^^^^

.. java:method:: public boolean isEditable()
   :outertype: Property

   :return: TODO

setDefaultValue
^^^^^^^^^^^^^^^

.. java:method:: public void setDefaultValue(Object defaultValue)
   :outertype: Property

   :param defaultValue: TODO

setDescription
^^^^^^^^^^^^^^

.. java:method:: public void setDescription(String description)
   :outertype: Property

   :param description: TODO

setEditable
^^^^^^^^^^^

.. java:method:: public void setEditable(boolean bool)
   :outertype: Property

   Sets whether this property can be changed from its default value

   :param bool:

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: Property


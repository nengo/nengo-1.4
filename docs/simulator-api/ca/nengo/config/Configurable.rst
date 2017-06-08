Configurable
============

.. java:package:: ca.nengo.config
   :noindex:

.. java:type:: public interface Configurable

   This interface can be implemented by objects that wish to expose a customized Configuration. If an object does not implement the getConfiguration() method, its properties are deduced by Java reflection.

   :author: Bryan Tripp

Methods
-------
getConfiguration
^^^^^^^^^^^^^^^^

.. java:method:: public Configuration getConfiguration()
   :outertype: Configurable

   :return: This Configurable's Configuration data


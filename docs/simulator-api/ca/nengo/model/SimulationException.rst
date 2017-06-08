.. java:import:: ca.nengo NengoException

SimulationException
===================

.. java:package:: ca.nengo.model
   :noindex:

.. java:type:: public class SimulationException extends NengoException

   A problem encountered while trying to run a simulation.

   :author: Bryan Tripp

Constructors
------------
SimulationException
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public SimulationException(String message)
   :outertype: SimulationException

   :param message: Text explanation of the exception.

SimulationException
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public SimulationException(Throwable cause)
   :outertype: SimulationException

   :param cause: Another throwable that indicates a problem underlying this exception.

SimulationException
^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public SimulationException(String message, Throwable cause)
   :outertype: SimulationException

   :param message: Text explanation of the exception.
   :param cause: Another throwable that indicates a problem underlying this exception.

Methods
-------
getMessage
^^^^^^^^^^

.. java:method:: public String getMessage()
   :outertype: SimulationException

   Adds ensemble name to message.

setEnsemble
^^^^^^^^^^^

.. java:method:: public void setEnsemble(String name)
   :outertype: SimulationException

   :param name: Name of the ensemble in which the exception occured


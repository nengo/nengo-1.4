.. java:import:: ca.nengo.dynamics Integrator

.. java:import:: ca.nengo.dynamics LinearSystem

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model StructuralException

BiasTermination
===============

.. java:package:: ca.nengo.model.nef.impl
   :noindex:

.. java:type:: public class BiasTermination extends DecodedTermination

   Termination which is somehow used in the Bias process? TODO: Figure out where this is used and why.

Constructors
------------
BiasTermination
^^^^^^^^^^^^^^^

.. java:constructor:: public BiasTermination(Node node, String name, String baseName, LinearSystem dynamics, Integrator integrator, float[] biasEncoders, boolean interneurons) throws StructuralException
   :outertype: BiasTermination

   :param node: Parent node
   :param name: Termination name
   :param baseName: Original termination name?
   :param dynamics: Linear system that defines dynamics
   :param integrator: Integrator to integrate dynamics
   :param biasEncoders: biased encoders?
   :param interneurons: Is parent a population of interneurons...?
   :throws StructuralException: if DecodedTermination can't be made

Methods
-------
getBaseTerminationName
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public String getBaseTerminationName()
   :outertype: BiasTermination

   :return: Underlying termination name

getBiasEncoders
^^^^^^^^^^^^^^^

.. java:method:: public float[] getBiasEncoders()
   :outertype: BiasTermination

   :return: biased encoders?

getOutput
^^^^^^^^^

.. java:method:: @Override public float[] getOutput()
   :outertype: BiasTermination

isEnabled
^^^^^^^^^

.. java:method:: public boolean isEnabled()
   :outertype: BiasTermination

   :return: True if this Termination is enabled

setEnabled
^^^^^^^^^^

.. java:method:: public void setEnabled(boolean enable)
   :outertype: BiasTermination

   :param enable: If true, the Termination is enabled; if false, it is disabled (so that inputs have no effect)


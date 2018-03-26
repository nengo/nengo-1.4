.. java:import:: ca.nengo.dynamics DynamicalSystem

.. java:import:: ca.nengo.model Units

AbstractDynamicalSystem
=======================

.. java:package:: ca.nengo.dynamics.impl
   :noindex:

.. java:type:: public abstract class AbstractDynamicalSystem implements DynamicalSystem

   Base implementation of DynamicalSystem.

   :author: Bryan Tripp

Constructors
------------
AbstractDynamicalSystem
^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public AbstractDynamicalSystem(float[] state)
   :outertype: AbstractDynamicalSystem

   Arbitrary dynamical system

   :param state: Initial state

Methods
-------
clone
^^^^^

.. java:method:: @Override public DynamicalSystem clone() throws CloneNotSupportedException
   :outertype: AbstractDynamicalSystem

f
^

.. java:method:: public abstract float[] f(float t, float[] u)
   :outertype: AbstractDynamicalSystem

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.f(float,float[])`

g
^

.. java:method:: public abstract float[] g(float t, float[] u)
   :outertype: AbstractDynamicalSystem

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.g(float,float[])`

getInputDimension
^^^^^^^^^^^^^^^^^

.. java:method:: public abstract int getInputDimension()
   :outertype: AbstractDynamicalSystem

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.getInputDimension()`

getOutputDimension
^^^^^^^^^^^^^^^^^^

.. java:method:: public abstract int getOutputDimension()
   :outertype: AbstractDynamicalSystem

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.getOutputDimension()`

getOutputUnits
^^^^^^^^^^^^^^

.. java:method:: public Units getOutputUnits(int outputDimension)
   :outertype: AbstractDynamicalSystem

   Returns Units.UNK by default.

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.getOutputUnits(int)`

getState
^^^^^^^^

.. java:method:: public float[] getState()
   :outertype: AbstractDynamicalSystem

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.getState()`

setState
^^^^^^^^

.. java:method:: public void setState(float[] state)
   :outertype: AbstractDynamicalSystem

   **See also:** :java:ref:`ca.nengo.dynamics.DynamicalSystem.setState(float[])`


.. java:import:: Jama EigenvalueDecomposition

.. java:import:: Jama Matrix

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.util MU

CanonicalModel
==============

.. java:package:: ca.nengo.dynamics.impl
   :noindex:

.. java:type:: public class CanonicalModel

   Utilities related to state-space models that are in controllable-canonical form.

   :author: Bryan Tripp

Methods
-------
changeTimeConstant
^^^^^^^^^^^^^^^^^^

.. java:method:: public static LTISystem changeTimeConstant(LTISystem system, float tau)
   :outertype: CanonicalModel

   :param system: An LTI system in controllable-canonical form
   :param tau: A desired new time constant
   :return: A copy of the system, with its slowest time constant changed to the new value

getDominantTimeConstant
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static float getDominantTimeConstant(LTISystem dynamics)
   :outertype: CanonicalModel

   :param dynamics: A linear time-invariant dynamical system
   :return: Time constant associated with the system's dominant eigenvalue

getRealization
^^^^^^^^^^^^^^

.. java:method:: public static LTISystem getRealization(float[] numerator, float[] denominator, float passthrough)
   :outertype: CanonicalModel

   Realizes a transfer function in the form:

   H(s) = d + (b1*s^(n-1) + b2*s^(n-2) + ... + bn) / (s^n + a1*s^(n-1) + ... + an).

   :param numerator: Coefficients of the numerator of a transfer function (b1 to bn above)
   :param denominator: Coefficients of the denominator of a transfer function (a1 to an above)
   :param passthrough: Passthrough value (d above). If your transfer function has numerator and denominator of equal degree, divide them, give the result here, and give the remainder as the numerator and denominator arguments, so that the new numerator will have degree less than denominator. There is no state-space realization for TF with numerator degree > denominator degree.
   :return: A controllable-canonical state-space realization of the specified transfer function

isControllableCanonical
^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public static boolean isControllableCanonical(LTISystem system)
   :outertype: CanonicalModel

   :param system: Any SISO linear time-invariant system
   :return: True if the system is in controllable-canonical form

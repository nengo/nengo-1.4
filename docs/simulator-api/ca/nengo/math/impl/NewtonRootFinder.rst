.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math RootFinder

NewtonRootFinder
================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class NewtonRootFinder implements RootFinder

   Root finder that uses Newton's method. Assumes that functions are generally increasing. TODO: test

   :author: Bryan Tripp

Constructors
------------
NewtonRootFinder
^^^^^^^^^^^^^^^^

.. java:constructor:: public NewtonRootFinder(int maxIterations, boolean additiveBoundarySearch)
   :outertype: NewtonRootFinder

   :param maxIterations: Maximum search iterations to attempt before returning an error
   :param additiveBoundarySearch: If true, when low and high boundaries need to be widened, a proportion of their difference is added/substracted. If false, they are multiplied/divided by a constant. False is a good idea for boundaries that should not cross zero.

Methods
-------
findRoot
^^^^^^^^

.. java:method:: public float findRoot(Function function, float startLow, float startHigh, float tolerance)
   :outertype: NewtonRootFinder

   **See also:** :java:ref:`ca.nengo.math.RootFinder.findRoot(ca.nengo.math.Function,float,float,float)`


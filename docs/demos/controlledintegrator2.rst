Controlled integrator 2
=======================

**Purpose**:
This demo implements a controlled one-dimensional neural integrator.

**Comments**:
This is functionally the same
as the other controlled integrator.
However, the control signal is zero for integration,
less than one for low-pass filtering,
and greater than 1 for saturation.
This behavior maps more directly
to the differential equation
used to describe an integrator
(i.e., :math:`\dot{x}(t) = Ax(t) + Bu(t)`).
The control in this circuit is A in that equation.
This is also the controlled integrator
described in the book "How to Build a Brain."

**Usage**:
When you run this demo,
it will automatically put in
some step functions on the input,
so you can see that the output is integrating
(i.e., summing over time) the input.
You can also input your own values.
It is quite sensitive like the integrator.
But, if you reduce the "control" input below 0,
it will not continuously add its input,
but slowly allow that input to leak away.
It's interesting to rerun the simulation from the start,
but decreasing the "control" input
before the automatic input starts
to see the effects of "leaky" integration.

.. image:: images/controlledintegrator.png
   :width: 100%

.. literalinclude:: ../../simulator-ui/dist-files/demo/controlledintegrator2.py

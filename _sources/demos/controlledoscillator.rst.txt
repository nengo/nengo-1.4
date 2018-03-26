Controlled oscillator
=====================

**Purpose**:
This demo implements a controlled two-dimensional oscillator.

**Comments**:
This is functionally analogous
to the controlled integrator, but in a 2D space.
The available slider allows the frequency
to be directly controlled, to be negative or positive.
This behavior maps directly to
the differential equation used
to describe a simple harmonic oscillator
(i.e., :math:`\dot{x}(t) = Ax(t) + Bu(t)`
where ``A = [[0, freq], [-freq, 0]]``).
The control in this circuit changes
the ``freq`` variable in that equation.

**Usage**:
When you run this demo,
it will automatically put in a step function
on the input to start the oscillator moving.
You can see where it is in phase space
in the XY plot
(if you want to see that over time,
right-click on the Oscillator population
and select X->value).
You can change the frequency of rotation
by moving the visible slider.
Positive values rotate clockwise and negative values counter-clockwise.

.. image:: images/controlledoscillator.png
   :width: 100%

.. literalinclude:: ../../simulator-ui/dist-files/demo/controlledoscillator.py

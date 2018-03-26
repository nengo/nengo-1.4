Controlled integrator
=====================

**Purpose**:
This demo implements a controlled one-dimensional neural integrator.

**Comments**:
This is the first demo of a controlled dynamic network.
This is the same as the integrator circuit,
but we now have introduced a population
that explicitly affects how well
the circuit "remembers" its past state.
The "control" input can be used
to make the integrator change
from a pure communication channel (0) to an integrator (1)
with various low-pass filtering occurring in between.

**Usage**:
When you run this demo,
it will automatically put in
some step functions on the input,
so you can see that the output is integrating
(i.e., summing over time) the input.
You can also input your own values.
It is quite sensitive like the integrator.
But, if you reduce the "control" input below 1,
it will not continuously add its input,
but slowly allow that input to leak away.
It's interesting to rerun the simulation from the start,
but decreasing the "control" input
before the automatic input starts
to see the effects of "leaky" integration.

.. image:: images/controlledintegrator.png
   :width: 100%

.. literalinclude:: ../../simulator-ui/dist-files/demo/controlledintegrator.py

.. topic:: Video:

   .. raw:: html

      <iframe width="100%" height="400" src="https://www.youtube.com/embed/l4HMNj1qlaw" frameborder="0" allowfullscreen></iframe>

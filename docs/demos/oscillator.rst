A simple harmonic oscillator
============================

**Purpose**:
This demo implements a simple harmonic oscillator in a 2D neural population.

**Comments**:
This is more visually interesting on its own than the integrator,
but the principle is the same.
Here, instead of having the recurrent input integrate
(i.e., feeding the full input value back to the population),
we have two dimensions which interact.
In Nengo there is a "Linear System" template
which can also be used to quickly construct a harmonic oscillator
(or any other linear system).

**Usage**:
When you run this demo,
it will sit at zero while there is no input,
and then the input will cause it to begin oscillating.
It will continue to oscillate without further input.
You can put inputs in to see the effects.
It is very difficult to stop it from oscillating.
You can imagine this would be easy to do
by either introducing control
as in the controlled integrator demo
(so you can stop the oscillation wherever it is),
or by changing the tuning curves of the neurons
(hint: so none represent values between -.3 and 3, say)
so when the state goes inside the (e.g., .3 radius) circle,
the state goes to zero.
Also, if you want a very robust oscillator,
you can increase the feedback matrix
to be slightly greater than identity.

.. image:: images/oscillator.png
   :width: 100%

You will get a sine and cosine in the 2D output.

.. literalinclude:: ../../simulator-ui/dist-files/demo/oscillator.py

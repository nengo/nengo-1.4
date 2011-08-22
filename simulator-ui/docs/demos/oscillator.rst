A Simple Harmonic Oscillator
============================
*Purpose*: This demo implements a simple harmonic oscillator in a 2D neural population.

*Comments*: This is more visually interesting on its own than the integrator, but the principle is the same.  Here, instead of having the recurrent input just integrate (i.e. feeding the full input value back to the population), we have two dimensions which interact.  In Nengo there is a 'Linear System' template which can also be used to quickly construct a harmonic oscillator (or any other linear system).

*Usage*: When you run this demo, it will sit at zero while there is no input, and then the input will cause it to begin oscillating.  It will continue to oscillate without further input.  You can put inputs in to see the effects.  It is very difficult to have it stop oscillating.  You can imagine this would be easy to do by either introducing control as in the controlled integrator demo, or by changing the tuning curves of the neurons (hint: so none represent values between -.3 and 3, say).

*Output*: See the screen capture below. You will get a sine and cosine in the 2D output.

.. image:: images/oscillator.png

*Code*:
    .. literalinclude:: ../../dist-files/demo/oscillator.py



A Simple Harmonic Oscillator
============================
*Purpose*: This demo implements a simple harmonic oscillator in a 2D neural population.

*Comments*: This is more visually interesting on its own than the integrator, but the principle is the same.  Here, instead of having the recurrent input just integrate (i.e. feeding the full input value back to the population), we have two dimensions which interact.  In Nengo there is a 'Linear System' template which can also be used to quickly construct a harmonic oscillator (or any other linear system).

*Usage*: When you run this demo, it will sit at zero while there is no input, and then the input will cause it to begin oscillating.  It will continue to oscillate without further input.  You can put inputs in to see the effects.  It is very difficult to have it stop oscillating.  You can imagine this would be easy to do by either introducing control as in the controlled integrator demo, or by changing the tuning curves of the neurons (hint: so none represent values between -.3 and 3, say).

*Output*: See the screen capture below. You will get a sine and cosine in the 2D output.

.. image:: images/controlledintegrator.png

*Code*::

    import nef
    
    net=nef.Network('Oscillator')
    input=net.make_input('input',[0,0])
    input.functions=[ca.nengo.math.impl.PiecewiseConstantFunction([0.2,0.3],[0,1,0]),ca.nengo.math.impl.PiecewiseConstantFunction([0],[0,0])]
    A=net.make('A',200,2,quick=False)
    net.connect(input,A,weight=1,pstc=0.1)
    net.connect(A,A,[[1,1],[-1,1]],pstc=0.1)
    net.add_to(world)



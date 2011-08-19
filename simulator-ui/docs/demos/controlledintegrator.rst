Controlled Integrator
============================
*Purpose*: This demo implements a controlled one-dimensional neural integrator.

*Comments*: This is the first example of a controlled dynamic network in the demos. This is the same as the integrator circuit, but we now have introduced a population that explicitly effects how well the circuit 'remembers' its past state.  The 'control' input can be used to make the integrator change from a pure communication channel (0) to an integrator (1) with various low-pass filtering occurring in between.

*Usage*: When you run this demo, it will automatically put in some step functions on the input, so you can see that the output is integrating (i.e. summing over time) the input.  You can also input your own values.  It is quite sensitive like the integrator.  But, if you reduce the 'control' input below 1, it will not continuously add its input, but slowly allow that input to leak away.  It's interesting to rerun the simulation from the start, but decreasing the 'control' input before the automatic input starts to see the effects of 'leaky' integration.

*Output*: See the screen capture below

.. image:: images/controlledintegrator.png

*Code*::

    import nef
    
    net=nef.Network('Controlled Integrator')
    input=net.make_input('input',[0])
    input.functions=[ca.nengo.math.impl.PiecewiseConstantFunction([0.2,0.3,0.44,0.54,0.8,0.9],[0,5,0,-10,0,5,0])]
    control=net.make_input('control',[1])
    A=net.make('A',225,2,radius=1.5,quick=True)
    net.connect(input,A,transform=[0.1,0],pstc=0.1)
    net.connect(control,A,transform=[0,1],pstc=0.1)
    def feedback(x):
        return x[0]*x[1]
    net.connect(A,A,transform=[1,0],func=feedback,pstc=0.1)
    net.add_to(world)



Squaring the Input
================================================
*Purpose*: This demo shows how to construct a network that squares its input.

*Comments*: This is a simple nonlinear function being decoded from the input to the cells.  Previous demos are all linear decodings.

*Usage*: Grab the slider control and move it up and down to see the effects of increasing or decreasing input. Notice that the output value does not go negative even for negative inputs.  Dragging the input slowly from -1 to 1 will approximately trace a quadratic curve in the output.

*Output*: See the screen capture below

.. image:: images/squaring.png

*Code*::
    
    import nef
    
    net=nef.Network('Squaring')
    input=net.make_input('input',[0])
    A=net.make('A',100,1,quick=True)
    B=net.make('B',100,1,quick=True,storage_code='B')
    net.connect(input,A)
    net.connect(A,B,func=lambda x: x[0]*x[0])
    net.add_to(world)



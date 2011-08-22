Arm Control
============================
*Purpose*: This demo shows an example of having an interaction between a neural and non-neural dynamical simulation.

*Comments*: The majority of the simulation is a non-neural dynamical simulation, with just one crucial population being neural.  That population is plotted in the visualizer, as are the generated control signals.  The control signals are used to drive the arm which is run in the physics simulator.

*Usage*: When you run the network, it will reach to the target (red ball).  If you change refX and refY, that will move the ball, and the arm will reach for the new target location.

*Output*: See the screen capture below. 

.. image:: images/armcontrol.png

*Code*:
    .. literalinclude:: ../../dist-files/demo/armcontrol.py

	
	

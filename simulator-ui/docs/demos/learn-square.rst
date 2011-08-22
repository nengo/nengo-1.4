Learning to Compute the Square of a Vector
==============================================
*Purpose*: This is demo shows learning a nonlinear function of a vector.

*Comments*: The set up here is very similar to the Learning a Communication Channel demo.  The main difference is that this demo works in a 2D vector space (instead of a scalar), and that it is learning to compute a nonlinear function (the element-wise square) of its input.

*Usage*: When you run the network, it automatically has a random white noise input injected into it in both dimensions.

Turn learning on: To allow the learning rule to work, you need to move the 'switch' to +1.

Monitor the error:  When the simulation starts and learning is on, the error is high.  The average error slowly begins to decrease as the simulation continues.  After 15s or so of simulation, it will do a reasonable job of computing the square, and the error in both dimensions should be quite small.

Is it working? To see if the right function is being computed, compare the 'pre' and 'post' population value graphs. You should note that 'post' looks kind of like an absolute value of 'pre', the 'post' will be a bit squashed.  You can also check that both graphs of either dimension should hit zero at about the same time.

*Output*: See the screen capture below. 

.. image:: images/learn-square.png


*Code*:
    .. literalinclude:: ../../dist-files/demo/learn-square.py


Learning a Communication Channel
==============================================
*Purpose*: This is the first demo showing learning in Nengo.  It learns the same circuit constructed in the Communication Channel demo.

*Comments*: The particular connection that is learned is the one between the 'pre' and 'post' populations.  This particular learning rule is a kind of modulated Hebb-like learning (see Bekolay, 2011 for details).  

Note: The red and blue graph is a plot of the connection weights, which you can watch change as learning occurs (you may need to zoom in with the scroll wheel; the learning a square demo has a good example). Typtically, the largest changes occur at the beginning of a simulation. Red indicates negative weights and blue positive weights.

*Usage*: When you run the network, it automatically has a random white noise input injected into it.  So the input slider moves up and down randomly.  However, learning is turned off, so there is little correlation between the representation of the pre and post populations.

Turn learning on: To allow the learning rule to work, you need to move the 'switch' to +1.  Because the learning rule is modulated by an error signal, if the error is zero, the weights won't change.  Once learning is on, the post will begin to track the pre.

Monitor the error:  When the switch is 0 at the beginning of the simulation, there is no 'error', though there is an 'actual error'.  The difference here is that 'error' is calculated by a neural population, and used by the learning rule, while 'actual error' is computed mathematically and is just for information.

Repeat the experiment: After a few simulated seconds, the post and pre will match well.  You can hit the 'reset' button (bottom left) and the weights will be reset to their original random values, and the switch will go to zero.  For a different random starting point, you need to re-run the script.

*Output*: See the screen capture below. 

.. image:: images/learn-communicate.png

*Code*:
    .. literalinclude:: ../../dist-files/demo/learn_communicate.py




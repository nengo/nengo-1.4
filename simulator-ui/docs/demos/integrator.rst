A Simple Integrator
============================
*Purpose*: This demo implements a one-dimensional neural integrator.

*Comments*: This is the first example of a recurrent network in the demos. It shows how neurons can be used to implement stable dynamics.  Such dynamics are important for memory, noise cleanup, statistical inference, and many other dynamic transformations.

*Usage*: When you run this demo, it will automatically put in some step functions on the input, so you can see that the output is integrating (i.e. summing over time) the input.  You can also input your own values.  Note that since the integrator constantly sums its input, it will saturate quickly if you leave the input non-zero.  This reminds us that neurons have a finite range of representation.  Such saturation effects can be exploited to perform useful computations (e.g. soft normalization).

*Output*: See the screen capture below

.. image:: images/integrator.png

*Code*:
    .. literalinclude:: ../../dist-files/demo/integrator.py





Feedback and Dynamics
========================

Storing Information Over Time: Constructing an Integrator
----------------------------------------------------------

* The basis of many of our cognitive models is the integrator.  Mathematically, the output of this network should be the integral of the inputs to this network.

   * Practically speaking, this means that if the input to the network is zero, then its output will stay at whatever value it is currently at.  This makes it the basis of a neural memory system, as a representation can be stored over time.
   * Integrators are also often used in sensorimotor systems, such as eye control
   
* For an integrator, a neural ensemble needs to connect to itself with a transformation weight of 1, and have an input with a weight of &tau;, which is the same as the synaptic time constant of the neurotransmitter used.
* Create a one-dimensional ensemble called Integrator.  Use 100 neurons and a radius of 1.
* Add two terminations with synaptic time constants of 0.1s.  Call the first one â€œinputâ€ and give it a weight of 0.1.  Call the second one â€œfeedbackâ€ and give it a weight of 1.
* Create a new Function input using a Constant Function with a value of 1.
* Connect the Function input to the input termination
* Connect the X origin of the ensemble back to its own feedback termination.

.. image:: images/p4-101.png

* Go to Interactive Plots.  Create a graph for the value of the ensemble (right-click on the ensemble and select "value").
* Press Play to run the simulation.  The value stored in the ensemble should linearly increase, reaching a value of 1 after approximately 1 second.

   * You can increase the amount of time shown on the graphs in Interactive Plots.  Do this by clicking on the small downwards-pointing arrow at the bottom of the window.  This will reveal a variety of settings for Interactive Plots.  Change the "time shown" to 1.

.. image:: images/p4-102.png

Representation Range
----------------------

* What happens if the previous simulation runs for longer than one second?
* The value stored in the ensemble does not increase after a certain point.  This is because all neural ensembles have a range of values they can represent (the radius), and cannot accurately represent outside of that range.

.. image:: images/p4-103.png

* Adjust the radius of the ensemble to 1.5 using either the Configure interface or the script console (that.radii=[1.5]).  Run the model again.  It should now accurately integrate up to a maximum of 1.5.

.. image:: images/p4-104.png

Complex Input
--------------

* We can also run the model with a more complex input.  Change the Function input using the following command from the script console (after clicking on it in the black model editing mode interface).  Press Ctrl-P to show the script console::

    that.functions=[ca.nengo.math.impl.PiecewiseConstantFunction([0.2,0.3,0.44,0.54,0.8,0.9],[0,5,0,-10,0,5,0])]

* You can see what this function looks like by right-clicking on it in the editing interface and selecting "Plot".

.. image:: images/p4-5.png

* Return to Interactive Plots and run the simulation.

.. image:: images/p4-105.png

Adjusting Synaptic Time Constants
----------------------------------

* You can adjust the accuracy of an integrator by using different neurotransmitters.
* Change the input termination to have a tau of 0.01 (10ms: GABA) and a transform to be 0.01. Also change the feedback termination to have a tau of 0.01 (but leave its transform at 1).

.. image:: images/p4-106.png

* By using a shorter time constant, the network dynamics are more sensitive to small-scale variation (i.e. noise).
* This indicates how important the use of a particular neurotransmitter is, and why there are so many different types with vastly differing time constants.

   * AMPA: 2-10ms
   * GABA:subscript:`A`: 10-20ms
   * NMDA: 20-150ms
   * The actual details of these time constants vary across the brain as well.  We are collecting empirical data on these from various sources at http://ctn.uwaterloo.ca/~cnrglab/?q=node/505

* You can also run this example using scripting::

    run demo/integrator.py


Controlled Integrator
-----------------------

* We can also build an integrator where the feedback transformation (1 in the previous model) can be controlled.

   * This allows us to build a tunable filter.
   
* This requires the use of multiplication, since we need to multiply two stored values together. This was covered in the previous part of the tutorial.
* We can efficiently implement this by using a two-dimensional ensemble.  One dimension will hold the value being represented, and the other dimension will hold the transformation weight.
* Create a two-dimensional neural ensemble with 225 neurons and a radius of 1.5.
* Create the following three terminations:

   * ``input``: time constant of 0.1, 1 dimensional, with a transformation matrix of ``[0.1 0]``.  This acts the same as the input in the previous model, but only affects the first dimension.
   * ``control``: time constant of 0.1, 1 dimensional, with a transformation matrix of ``[0 1]``.  This stores the input control signal into the second dimension of the ensemble.
   * ``feedback``: time constant of 0.1, 1 dimensional, with a transformation matrix of ``[1 0]``.  This will be used in the same manner as the feedback termination in the previous model.
   
* Create a new origin that multiplies the values in the vector together

   * This is exactly the same as the multiplier in the previous part of this tutorial
   * This is a 1 dimensional output, with a User-defined Function of ``x0*x1``
   
* Create two function inputs called ``input`` and ``control``.   Start with Constant functions with a value of 1

   * Use the script console to set the ``input`` function by clicking on it and entering the same input function as used above::
   
        that.functions=[ca.nengo.math.impl.PiecewiseConstantFunction([0.2,0.3,0.44,0.54,0.8,0.9],[0,5,0,-10,0,5,0])]
        
* Connect the input function to the input termination, the control function to the control termination, and the product origin to the feedback termination.

.. image:: images/p4-9.png

* Go to Interactive Plots and show a graph for the value of the ensemble (right-click->X->value).  If you run the simulation, this graph will show the values of both variables stored in this ensemble (the integrated value and the control signal).  For clarity, turn off the display of the cotrol signal by right-clicking on the graph and removing the checkmark beside "v[1]".
* The performance of this model should be similar to that of the non-controlled integrator.

.. image:: images/p4-107.png

* Now adjust the control input to be 0.3 instead of 1.  This will make the integrator into a leaky integrator.  This value adjusts how quickly the integrator forgets over time.

.. image:: images/p4-108.png


* You can also run this example using scripting::

    run demo/controlledintegrator.py


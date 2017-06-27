2D representation
=================

**Purpose**:
This demo shows how to construct and manipulate
a population of 2D neurons.

**Comments**:
These are 100 leaky integrate-and-fire (LIF) neurons.
The neuron tuning properties have been randomly selected
to encode a 2D space
(i.e. each neuron has an encoder randomly selected from the unit circle).

**Usage**:
Grab the slider controls and move then up and down
to see the effects of shifting the input throughout the 2D space.
As a population, these neurons do a good job
of representing a 2D vector value.
This can be seen by the fact that
the input graph and neurons graphs match well.


.. image:: images/2drepresentation.png
   :width: 100%

The "circle" plot is showing
the preferred direction vector
of each neuron multplied by its firing rate.
This kind of plot was made famous by Georgoupolos et al.

.. literalinclude:: ../../simulator-ui/dist-files/demo/2drepresentation.py

.. topic:: Video:

   .. raw:: html

      <iframe width="100%" height="400" src="https://www.youtube.com/embed/zRYDaNcsekU" frameborder="0" allowfullscreen></iframe>

   .. container:: toggle

      .. container:: header

         **Transcript**

      Here we again have a simulation of 100 neurons in Nengo.
      The difference between this simulation
      and the previous one is that these neurons
      are now representing a 2-dimensional vector space,
      rather than a one-dimensional scalar.
      For this reason, we now have 2 inputs,
      one controlling the x direction
      and the other controlling the y direction.
      So, as I change the input in the x direction,
      we can see that the point being encoded
      by this population of cells changes.
      Here we're plotting the point in the 2-D space
      that is being represented by the cells.
      And in this upper graph with the blue and the black lines,
      we are showing that same information plotted over time,
      where the black line indicates the x value
      and the blue line indicates the y value.
      The plot of the cortical sheet is the same as before,
      where yellow is indicating that that particular neuron is spiking.

      In the bottom right corner we have a new kind of plot.
      This is one that was made famous by Georgopoulos
      with his experiments on monkey arm movements.
      In the plot there we are showing
      the preferred direction vector of the neuron
      multiplied by the activity of that neuron.
      The "preferred direction vector" of a neuron
      is the direction which,
      when we move the stimulus in that direction,
      causes the neuron's response to increase most rapidly.
      Georgopoulos found that in the motor cortex
      neurons tend to have an even distribution
      of preferred direction vectors around the circle.
      So we have that same kind of distribution here in this representation.
      For this reason, we can see that as we move the input,
      for instance far over to the left,
      that neurons whose preferred direction vector
      is leftwards are the most active cells
      and those whose preferred direction vectors
      are rightward are the least active cells.
      As we move that same stimulus back towards the right,
      a different set of neurons turns on
      and those with preferred direction vectors rightward
      are most active.
      As we move throughout the space we can see
      that the decoded value of the spiking activity
      does a good job of representing the input.
      We can choose some arbitrary points
      to see if they are approximately correct.
      You will notice that the output is in fact
      quite correct all the way up to the extremes of the unit circle.

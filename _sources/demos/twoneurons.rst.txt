Two neurons
===========

**Purpose**:
This demo shows how to construct and manipulate a complementary pair of neurons.

**Comments**:
These are leaky integrate-and-fire (LIF) neurons. The neuron tuning properties have been selected so there is one 'on' and one 'off' neuron.

**Usage**:
Grab the slider control and move it up and down to see the effects of increasing or decreasing input. One neuron will increase for positive input, and the other will decrease.  This can be thought of as the simplest population to give a reasonable representation of a scalar value.

.. image:: images/twoneurons.png
   :width: 100%

.. literalinclude:: ../../simulator-ui/dist-files/demo/twoneurons.py

.. topic:: Video:

   .. raw:: html

      <iframe width="100%" height="400" src="https://www.youtube.com/embed/EgHs9BE4XzU" frameborder="0" allowfullscreen></iframe>

   .. container:: toggle

      .. container:: header

         **Transcript**

      Here we have a simulation of 2 neurons in Nengo.
      This is much like the simulation of a single neuron.
      Just to remind you quickly, this is the input.
      Here we have the subthreshold voltage of the 2 neurons.
      These are the spike trains of those 2 neurons shown over time.
      Here we have those neurons shown as if they were on a cortical sheet.
      And last we have the postsynaptic current
      that would be induced in a cell
      that received the signals from these 2 neurons.
      Although, more appropriately this can now begin
      to be thought of as a decoding of the input.
      In other words, it's supposed to be an estimate of about -.45.
      This estimate currently is not very good
      but it gets much better as we include more neurons.
      What is of interest here is to note
      that the neurons respond very differently to the input.
      As I increase the input, the top neuron begins to fire more,
      but the bottom begins to fire less.
      So you can see that the neurons are encoding information
      about the input in a complementary, push-pull fashion.
      This is not an uncommon feature found in cortex.
      You can also see that the decoded input is following the input.
      Although it is doing so in a fairly noisy manner.
      In the neural engineering framework
      we refer to the preferences of the cells as their encoders.
      So we would say that the top neuron has a positive encoder
      because as the input goes up it is more active,
      and the bottom cell has a negative encoder
      because as the input goes down it becomes more active.

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

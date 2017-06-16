Population of neurons
=====================

**Purpose**:
This demo shows how to construct and manipulate a population of neurons.

**Comments**:
These are 100 leaky integrate-and-fire (LIF) neurons.
The neuron tuning properties have been randomly selected.

**Usage**:
Grab the slider control and move it up and down
to see the effects of increasing or decreasing input.
As a population, these neurons do a good job
of representing a single scalar value.
This can be seen by the fact that
the input graph and neurons graphs match well.

.. image:: images/manyneurons.png
   :width: 100%

.. literalinclude:: ../../simulator-ui/dist-files/demo/manyneurons.py

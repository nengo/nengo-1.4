A single neuron
===============

**Purpose**:
This demo shows how to construct and manipulate a single neuron.

**Comments**:
This leaky integrate-and-fire (LIF) neuron
is a simple, standard model of a spiking single neuron.
It resides inside a neural "population",
even though there is only one neuron.

**Usage**:
Grab the slider control and move it up and down
to see the effects of increasing or decreasing input.
This neuron will fire faster with more input (an "on" neuron).

.. image:: images/singleneuron.png
   :width: 100%

.. literalinclude:: ../../simulator-ui/dist-files/demo/singleneuron.py

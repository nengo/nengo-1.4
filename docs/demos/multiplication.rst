Multiplication
==============

**Purpose**:
This demo shows how to construct a network that multiplies two inputs.

**Comments**:
This can be thought of as a combination
of the combining demo and the squaring demo.
Essentially, we project both inputs independently into a 2D space,
and then decode a nonlinear transformation of that space
(the product of the first and second vector elements).

Multiplication is extremely powerful.
Following the simple usage instructions below
suggests how you can exploit it
to do gating of information into a population,
as well as radically change
the response of a neuron to its input
(i.e., completely invert its "tuning"
to one input dimension by manipulating the other).

**Usage**:
Grab the slider controls and move them up and down
to see the effects of increasing or decreasing input.
The output is the product of the inputs.
To see this quickly, leave one at zero and move the other.
Or, set one input at a negative value
and watch the output slope go down as you move the other input up.

.. image:: images/multiplication.png
   :width: 100%

.. literalinclude:: ../../simulator-ui/dist-files/demo/multiplication.py

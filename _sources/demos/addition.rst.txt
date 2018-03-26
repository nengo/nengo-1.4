Addition
========

**Purpose**:
This demo shows how to construct a network that adds two inputs.

**Comments**:
Essentially, this is two communication channels into the same population.
Addition is thus somewhat "free" since the incoming currents
from different synaptic connections interact linearly
(though two inputs don't have to combine in this way;
see the combining demo).

**Usage**:
Grab the slider controls and move them up and down
to see the effects of increasing or decreasing input.
The C population represents
the sum of A and B representations.
Note that the "addition" is
a description of neural firing in the decoded space.
Neurons don't just add all the incoming spikes
(the NEF has determined appropriate connection weights
to make the result in C interpretable (i.e., decodable)
as the sum of A and B).

.. image:: images/addition.png
   :width: 100%

.. literalinclude:: ../../simulator-ui/dist-files/demo/addition.py

.. topic:: Video:

   .. raw:: html

      <iframe width="100%" height="400" src="https://www.youtube.com/embed/w9UFvrlhtIc" frameborder="0" allowfullscreen></iframe>

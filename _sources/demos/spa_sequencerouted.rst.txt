Routed sequencing
=================

**Purpose**:
This demo uses the basal ganglia model
to cycle through a 5 element sequence of semantic pointers,
where an arbitrary start can be presented to the model.

**Comments**:
This basal ganglia is now hooked up to a memory and includes routing.
The addition of routing allows the system
to choose between two different actions:
whether to go through the sequence,
or be driven by the visual input.
If the visual input has its value
set to "0.8*START+D" (for instance),
it will begin cycling through at D -> E.
The 0.8 scaling helps ensure that "START"
is unlikely to accidently match other SPs
(which can be a problem in low-dimensional examples like this one).

The "utility" graph shows the utility
of each rule going into the basal ganglia.
The "rule" graph shows which one has been selected
and is driving thalamus.

**Usage**:
When you run the network,
it will go through the sequence forever,
starting at D.
You can right-click the SPA graph
and set the value to anything else
(e.g., "0.8*START+B")
and it will start at the new letter
and then begin to sequence through.
The point is partly that it can
ignore the input after it has first been shown
and doesn't perseverate on the letter
as it would without gating.

.. image:: images/spa_sequencerouted.png
   :width: 100%

.. literalinclude:: ../../simulator-ui/dist-files/demo/spa_sequencerouted.py

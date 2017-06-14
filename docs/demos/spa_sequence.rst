Cycling Through a Sequence
================================================
*Purpose*: This demo uses the basal ganglia model to cycle through a 5 element sequence.

*Comments*: This basal ganglia is now hooked up to a memory.  This allows it to update that memory based on its current input/action mappings.  The mappings are defined in the code such that A->B, B->C, etc. until E->A completing a loop.  This uses the 'spa' module from Nengo.

The 'utility' graph shows the utility of each rule going into the basal ganglia. The 'rule' graph shows which one has been selected and is driving thalamus.

*Usage*: When you run the network, it will go through the sequence forever.  It's interesting to note the distance between the 'peaks' of the selected items.  It's about 40ms for this simple action.  We like to make a big deal of this.

*Output*: See the screen capture below.

.. image:: images/spa_sequence.png

*Code*:

.. literalinclude:: ../../simulator-ui/dist-files/demo/spa_sequence.py

Cycling through a SP sequence
=============================

**Purpose**:
This demo uses the basal ganglia model
to cycle through a 5 element sequence of semantic pointers.

**Comments**:
This basal ganglia is now hooked up to a memory.
This allows it to update that memory
based on its current input/action mappings.
The mappings are defined in the code
such that A -> B, B -> C, ..., E -> A, completing a loop.
This uses the "spa" module from Nengo.

The "utility" graph shows the utility
of each rule going into the basal ganglia.
The "rule" graph shows which one has been selected
and is driving thalamus.

**Usage**:
When you run the network,
it will go through the sequence forever.
It's interesting to note the distance
between the "peaks" of the selected items.
It's about 40 ms for this simple action.
We like to make a big deal of this.

.. image:: images/spa_sequence.png
   :width: 100%

.. literalinclude:: ../../simulator-ui/dist-files/demo/spa_sequence.py

.. topic:: Video:

   .. raw:: html

      <iframe width="100%" height="400" src="https://www.youtube.com/embed/jVWrGEmQJlo" frameborder="0" allowfullscreen></iframe>

   .. container:: toggle

      .. container:: header

         **Transcript**

      The BG network demonstrated in a previous video
      is included as a subnetwork of the simulation shown here.
      It is connected to cortex, labeled as the 'state' population,
      which represents the current cortical state of the system.
      This projects to basal ganglia.

      The most valuable state is selected by BG,
      which then projects to thalamus,
      disinhibiting an associated action.
      The action then changes the state of cortex,
      completing the famous cortex-bg-thalamus loop.
      This recurrent loop allows the system
      to progress through a sequence of states.

      Let me begin the simulation.
      As you can see, the input is only turned on briefly
      for the first 100 milliseconds.
      This starts the sequence in state 'D'.
      The subsequent progression through other states
      is solely the result of internal dynamics.

      We can see the cortical state with the highest utility value in this graph.
      BG is selecting the action, or 'rule'
      corresponding to the highest valued state.
      The rules in this example cause cortex in state A, to go to state B.
      If it is in B, it is changed to C, and so on,
      with E being changed back to A.

      Pausing the simulation allows us to see this behaviour more clearly.
      Now, the highest utility is D,
      so the rule that changes state D to E has its action selected.
      Stepping ahead allows that rule to take effect,
      changing the cortical state to E.
      This activates the action to change the cortical state to A.
      Which happens when we restart the simulation.

      This same progression can be seen in this semantic pointer graph,
      where the similarity of all states
      to the current cortical state is displayed.
      Examining the distance between these peaks
      demonstrates that it takes about 40 to 50 milliseconds
      to change from one cortical state to the next.

      The network is very stable,
      and will progress through this set of states
      until the simulation is stopped,
      or there is other interrupting input.

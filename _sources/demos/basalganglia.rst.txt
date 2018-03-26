Basal Ganglia
=============

**Purpose**:
This demo introduces the basal ganglia model
that the SPA exploits to do action selection.

**Comments**:
This is the basal ganglia in isolation, not hooked up to anything.
It demonstrates that this model operates as expected;
i.e., it supresses the output corresponding
to the input with the highest input value.

This is an extension to a spiking, dynamic model
of the Redgrave et al. work.
It is more fully described in several CNRG lab publications.
It exploits the "nps" class from Nengo.

**Usage**:
After running the demo, play with the 5 input sliders.
The highest slider should always be selected in the output.
When they are close, interesting things happen.
You may even be able to tell that things
are selected more quickly for larger differences in input values.

.. image:: images/basalganglia.png
   :width: 100%

.. literalinclude:: ../../simulator-ui/dist-files/demo/basalganglia.py

.. topic:: Video:

   .. raw:: html

      <iframe width="100%" height="400" src="https://www.youtube.com/embed/5latplytFwM" frameborder="0" allowfullscreen></iframe>

   .. container:: toggle

      .. container:: header

         **Transcript**

      This video shows a simulation of the basal ganglia circuit,
      whose network structure is shown here on the right.
      The input can be thought of as cortex
      and the output is going to thalamus.
      This graph shows the output
      and the input is controlled by these 5 sliders.

      Let me start the simulation.
      When a given cortical state has a high value
      its corresponding action should be selected by thalamus.
      This can be seen in the graph below.
      Here, selection means decreasing the activity
      of the corresponding action because basal ganglia
      is known to release thalamus from inhibition.

      Notably, there is little response of the circuit
      to negative values or values below about .3.
      Nevertheless the circuit can select
      the most valuable state as soon as one of the actions
      goes significantly higher than .3
      regardless of the value of the other states.
      In addition, the bigger the difference,
      the more clear the selected activity is.

      Notice that when several competing states have high,
      near threshold values the dynamics of the circuit
      still allow it to make fine distinctions.
      For instance, here it can distinguish a difference
      of only .06, selecting the highest action appropriately.
      As I move the slider back to near
      the threshold value the corresponding action
      quickly becomes indistinguishable from other possible actions.

      Critically the specific dynamics we see here,
      while looking something like a winner take all circuit,
      are much more sophisticated than the dynamics
      we would get by merely inhibitorily connecting cells.
      Specifically, this network responds particularly quickly,
      and over a wider range of possible network inputs.

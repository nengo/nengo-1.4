.. java:import:: ca.nengo.model Node

NEFNode
=======

.. java:package:: ca.nengo.model.nef
   :noindex:

.. java:type:: public interface NEFNode extends Node

   A Node with a distinguished Termination that corresponds to a net effect of input. This direct channel is used by NEFEnsembles. NEFEnsembles run more efficiently by combining and filtering inputs at the Ensemble level, so that these same combinations and filterings need not be performed multiple times, for each Node in the Ensemble. Differences in net input to the different Nodes in an NEFEnsemble are accounted for by encoding vectors (see Eliasmith & Anderson, 2003).

   There can also be additional inputs to an NEFNode, beyond the distinguished input. The manner in which such inputs are combined with each other and with the distinguished input is determined by the NEFNode.

   :author: Bryan Tripp

Methods
-------
setRadialInput
^^^^^^^^^^^^^^

.. java:method:: public void setRadialInput(float value)
   :outertype: NEFNode

   :param value: Value of filtered summary input. This value is typically in the range [-1 1], and correponds to an inner product of vectors in the space represented by the NEFEnsemble to which this Node belongs.


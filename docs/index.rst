*********
Nengo 1.4
*********

.. warning::
   Nengo 1.4 is no longer actively maintained.
   Please use `Nengo 2.0 <https://nengo.github.io/>`_ instead.

   If you have written models in Nengo 1.4 and wish to
   update those modesl to work in Nengo 2.0, see
   `this model conversion guide
   <http://pythonhosted.org/nengo/converting.html>`_.

Nengo is a software package for simulating large-scale neural systems.

To use it, you define groups of neurons in terms of what they represent,
and then form connections between neural groups
in terms of what computation should be performed on those representations.
Nengo then uses the Neural Engineering Framework (NEF)
to solve for the appropriate synaptic connection weights
to achieve this desired computation.
This makes it possible to produce detailed spiking neuron models
that implement complex high-level cognitive algorithms.

.. topic:: Demo video

   .. raw:: html

      <iframe width="100%" height="400" src="https://www.youtube.com/embed/q4jxI26gUtA" frameborder="0" allowfullscreen></iframe>

   This is a general demo video for Nengo,
   which shows models of motor control, cognition, and working memory,
   as well as an example of model construction in Nengo.

If you use Nengo 1.4 in a publication, please cite:

  Stewart, T.C., Tripp, B., Eliasmith, C. (2009).
  `Python scripting in the Nengo simulator
  <http://www.frontiersin.org/neuroinformatics/paper/10.3389/neuro.11/007.2009/>`_.
  *Frontiers in Neuroinformatics.* 3(7)

.. toctree::
   :maxdepth: 2

   getting_started
   videos/index
   tutorials/index
   demos/index
   scripting/index
   advanced/index
   nef_algorithm

Indices and tables
==================

* :ref:`genindex`
* :ref:`search`

*********
Nengo 1.4
*********

Nengo is a software package for simulating large-scale neural systems.

To use it, you define groups of neurons in terms of what they represent,
and then form connections between neural groups
in terms of what computation should be performed on those representations.
Nengo then uses the Neural Engineering Framework (NEF)
to solve for the appropriate synaptic connection weights
to achieve this desired computation.
This makes it possible to produce detailed spiking neuron models
that implement complex high-level cognitive algorithms.

.. warning::
   Nengo 1.4 is no longer actively maintained.
   Please use `Nengo 2.0 <https://nengo.github.io/>`_ instead.

   If you have written models in Nengo 1.4 and wish to
   update those modesl to work in Nengo 2.0, see
   `this model conversion guide
   <http://pythonhosted.org/nengo/converting.html>`_.

.. toctree::
   :maxdepth: 2

   tutorial
   demos/demos
   scripting
   advanced/index
   nef_algorithm
   api

Indices and tables
==================

* :ref:`genindex`
* :ref:`modindex`
* :ref:`search`

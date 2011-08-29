.. Nengo Scripting documentation master file, created by
   sphinx-quickstart on Fri Aug  5 11:13:46 2011.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

Nengo User Manual
===========================================

`Nengo <http://nengo.ca>`_ is a software package for simulating large-scale neural systems.

To use it, you define groups of neurons in terms of what they represent, and then form connections between neural 
groups in terms of what computation should be performed on those representations. Nengo then uses the Neural 
Engineering Framework (NEF) to solve for the appropriate synaptic connection weights to achieve this desired 
computation. This makes it possible to produce detailed spiking neuron models that implement complex 
high-level cognitive algorithms.


.. toctree::
   :maxdepth: 2

   tutorial
   demos/demos
   scripting
   advanced/index
   java_api

* :ref:`genindex`
* :ref:`search`


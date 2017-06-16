The NEF Algorithm
=================

While Nengo provides
a flexible, general-purpose approach to neural modelling,
it is sometimes useful to get a complete look
at exactly what is going on "under the hood".
The theory behind the Neural Engineering Framework is
developed at length in `Eliasmith & Anderson, 2003: "Neural Engineering"
<http://www.amazon.com/Neural-Engineering-Representation-Neurobiological-Computational/dp/0262550601>`_,
and a short summary is available in
`Stewart, 2012: "A Technical Overview of the
Neural Engineering Framework
<http://compneuro.uwaterloo.ca/publications/stewart2012d.html>`_.

However, for some people, the best description of an algorithm
is the code itself.
With that in mind, the following is
a complete implementation of the NEF
for the special case of two one-dimensional populations
with a single connection between them.
You can adjust the function being computed,
the input to the system,
and various neural parameters.
In it written in Python, and requires NumPy (for the matrix inversion)
and Matplotlib (to produce graphs of the output).

.. literalinclude:: ../simulator/src/python/nef_minimal/nef_minimal.py

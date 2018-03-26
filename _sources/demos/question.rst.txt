A question answering network
============================

**Purpose**:
This demo shows how to do question answering using binding
(see the convolution demo for binding on its own).

**Comments**:
This example binds the "A" and "B" inputs to give "C".
The "E" input is used to decode the contents of "C"
and the result is shown in "F",
essentially showing unbinding as it gives back what was bound.

.. note:: The black and white graphs show the decoded vector values,
          not neural activity.
          So, the result in "F" should visually look like
          the "A" element if "B" is being unbound from "C".

**Usage**:
When you run the network,
it will start by binding "RED" and "CIRCLE"
and then unbinding "RED" from that result,
and the output will be "CIRCLE".
Then it does the same kind of thing with BLUE SQUARE.
You can set the input values
by right-clicking the SPA graphs
and setting the value by typing something in.
If you type in a vocabulary word that is not there,
it will be added to the vocabulary.

.. image:: images/question.png
   :width: 100%

.. literalinclude:: ../../simulator-ui/dist-files/demo/question.py

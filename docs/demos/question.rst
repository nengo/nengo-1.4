A Question Answering Network
============================
*Purpose*: This demo shows how to do question answering using binding (i.e. see the convolution demo).

*Comments*: This example binds the A and B inputs to give C. Then the E input is used to decode the contents of C and the result is shown in F.  Essentially showing unbinding gives back what was bound.

Note: The b/w graphs show the decoded vector values, not neural activity.  So, the result in F should visually look like the A element if B is being unbound from C.

*Usage*: When you run the network, it will start by binding 'RED' and 'CIRCLE' and then unbinding 'RED' from that result, and the output will be 'CIRCLE'.  Then it does the same kind of thing with BLUE SQUARE.  You can set the input values by right-clicking the SPA graphs and setting the value by typing somethign in.  If you type in a vocabulary word that is not there, it will be added to the vocabulary.

*Output*: See the screen capture below.

.. image:: images/question.png

*Code*:

.. literalinclude:: ../../simulator-ui/dist-files/demo/question.py

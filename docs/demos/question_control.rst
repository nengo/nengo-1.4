A controlled question answering network
=======================================

**Purpose**:
This demo performs question answering
based on storing items in a working memory,
while under control of a basal ganglia.

**Comments**:
This example is very much like
the question answering with memory demo
(it would be good to read that first).
However, both the information to bind
and the questions to answer
come in through the same visual channel.
The basal ganglia decides
what to do with the information in the visual channel
based on its content
(i.e., whether it is a statement or a question).

**Usage**:
When you run the network,
it will start by binding "RED" and "CIRCLE"
and then binding "BLUE" and "SQUARE"
so the memory essentially has "RED*CIRCLE + BLUE*SQUARE".
It does this because it is told that
"RED*CIRCLE" is a STATEMENT
(i.e., "RED*CIRCLE+STATEMENT" in the code), as is BLUE*SQUARE.
Then it is presented with something like "QUESTION+RED"
(i.e., "What is red?").
The basal ganglia then reroutes that input
to be compared to what is in working memory
and the result shows up in the motor channel.

.. image:: images/question_control.png
   :width: 100%

.. literalinclude:: ../../simulator-ui/dist-files/demo/question_control.py

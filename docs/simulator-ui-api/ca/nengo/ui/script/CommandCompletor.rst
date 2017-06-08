.. java:import:: java.util ArrayList

.. java:import:: java.util List

CommandCompletor
================

.. java:package:: ca.nengo.ui.script
   :noindex:

.. java:type:: public abstract class CommandCompletor

   Base class for command completors, which provide suggestions for filling in the remainder of partially-specified scripting commands.

   :author: Bryan Tripp

Constructors
------------
CommandCompletor
^^^^^^^^^^^^^^^^

.. java:constructor:: public CommandCompletor()
   :outertype: CommandCompletor

Methods
-------
getIndex
^^^^^^^^

.. java:method:: protected int getIndex()
   :outertype: CommandCompletor

getOptions
^^^^^^^^^^

.. java:method:: protected List<String> getOptions()
   :outertype: CommandCompletor

   :return: The list of completion options currently under consideration

next
^^^^

.. java:method:: public String next(String partial)
   :outertype: CommandCompletor

   :param partial: Partial command string
   :return: Next command (from current index in options list) that begins with given partial command. Returns the arg if end of list is reached.

previous
^^^^^^^^

.. java:method:: public String previous(String partial)
   :outertype: CommandCompletor

   :param partial: Partial command string
   :return: Next most recent command (from current index in options list) that begins with given partial command. Returns the arg if end of list is reached.

resetIndex
^^^^^^^^^^

.. java:method:: public void resetIndex()
   :outertype: CommandCompletor

   Resets the index to the list of completion options to its default location.


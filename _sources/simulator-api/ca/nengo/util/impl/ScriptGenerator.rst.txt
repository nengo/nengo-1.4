.. java:import:: java.io File

.. java:import:: java.io FileNotFoundException

.. java:import:: java.io PrintWriter

.. java:import:: java.util HashMap

.. java:import:: java.util ArrayList

.. java:import:: java.util Stack

.. java:import:: ca.nengo.model Network

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Projection

.. java:import:: ca.nengo.util ScriptGenException

ScriptGenerator
===============

.. java:package:: ca.nengo.util.impl
   :noindex:

.. java:type:: public class ScriptGenerator extends DFSIterator

Fields
------
inTemplateNetwork
^^^^^^^^^^^^^^^^^

.. java:field::  int inTemplateNetwork
   :outertype: ScriptGenerator

parentNetwork
^^^^^^^^^^^^^

.. java:field::  Stack<Network> parentNetwork
   :outertype: ScriptGenerator

prefixes
^^^^^^^^

.. java:field::  HashMap<Node, String> prefixes
   :outertype: ScriptGenerator

script
^^^^^^

.. java:field::  StringBuilder script
   :outertype: ScriptGenerator

spaceDelimiter
^^^^^^^^^^^^^^

.. java:field::  char spaceDelimiter
   :outertype: ScriptGenerator

topLevelPrefix
^^^^^^^^^^^^^^

.. java:field::  String topLevelPrefix
   :outertype: ScriptGenerator

writer
^^^^^^

.. java:field::  PrintWriter writer
   :outertype: ScriptGenerator

Constructors
------------
ScriptGenerator
^^^^^^^^^^^^^^^

.. java:constructor:: public ScriptGenerator(File file) throws FileNotFoundException
   :outertype: ScriptGenerator

Methods
-------
finish
^^^^^^

.. java:method:: protected void finish()
   :outertype: ScriptGenerator

post
^^^^

.. java:method:: @SuppressWarnings protected void post(Node node)
   :outertype: ScriptGenerator

pre
^^^

.. java:method:: @SuppressWarnings protected void pre(Node node)
   :outertype: ScriptGenerator

startDFS
^^^^^^^^

.. java:method:: public DFSIterator startDFS(Node node)
   :outertype: ScriptGenerator


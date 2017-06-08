.. java:import:: java.lang.reflect Constructor

.. java:import:: java.lang.reflect Field

.. java:import:: java.lang.reflect Method

.. java:import:: java.lang.reflect Modifier

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util StringTokenizer

.. java:import:: org.python.core PyClass

.. java:import:: org.python.core PyJavaType

.. java:import:: org.python.core PyList

.. java:import:: org.python.core PyObject

.. java:import:: org.python.core PyObjectDerived

.. java:import:: org.python.core PyString

.. java:import:: org.python.core PyStringMap

.. java:import:: org.python.util PythonInterpreter

.. java:import:: ca.nengo.config JavaSourceParser

CallChainCompletor
==================

.. java:package:: ca.nengo.ui.script
   :noindex:

.. java:type:: public class CallChainCompletor extends CommandCompletor

   A CommandCompletor that suggests completions based on Python variable names and methods/fields of Python objects.

   :author: Bryan Tripp

Constructors
------------
CallChainCompletor
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public CallChainCompletor(PythonInterpreter interpreter)
   :outertype: CallChainCompletor

   :param interpreter: The Python interpreter from which variables are gleaned

Methods
-------
getConstructors
^^^^^^^^^^^^^^^

.. java:method:: public List<String> getConstructors(PyObject pc)
   :outertype: CallChainCompletor

getDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public String getDocumentation()
   :outertype: CallChainCompletor

   :return: Documentation for the current completion item if available, otherwise null

getMembers
^^^^^^^^^^

.. java:method:: public List<String> getMembers(String base)
   :outertype: CallChainCompletor

   :param base: A variable name in the interpreter. For variables that wrap Java objects, this arg can consist of a call chain, eg x.getY().getZ() For native python variables, the return type of a call isn't known, so this method can't return anything given a call chain.
   :return: A list of completion options including methods (with args) and fields on the named variable.

getVariables
^^^^^^^^^^^^

.. java:method:: public List<String> getVariables()
   :outertype: CallChainCompletor

   :return: List of variable names known to the interpreter.

setBase
^^^^^^^

.. java:method:: public void setBase(String callChain)
   :outertype: CallChainCompletor

   Rebuilds the completion options list from a "base" call chain.

   :param callChain: A partial call chain, eg "x.getY().get", from which we would extract the base call chain "x.getY()". (In this case the new options list might include "x.getY().toString()", "x.getY().wait()", etc.)


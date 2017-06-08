.. java:import:: java.io Serializable

.. java:import:: java.util ArrayList

.. java:import:: java.util HashMap

.. java:import:: java.util Iterator

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Stack

.. java:import:: java.util StringTokenizer

.. java:import:: ca.nengo.math Function

.. java:import:: ca.nengo.math FunctionInterpreter

DefaultFunctionInterpreter
==========================

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class DefaultFunctionInterpreter implements FunctionInterpreter

   Default implementation of FunctionInterpreter. This implementation produces PostfixFunctions.

   TODO: faster Functions could be produced by compiling expressions into Java classes.

   :author: Bryan Tripp

Constructors
------------
DefaultFunctionInterpreter
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public DefaultFunctionInterpreter()
   :outertype: DefaultFunctionInterpreter

   Initializes data structures TODO: Make this a static list or something, why are we doing this for each function

Methods
-------
getPostfixList
^^^^^^^^^^^^^^

.. java:method:: public List<Serializable> getPostfixList(String expression)
   :outertype: DefaultFunctionInterpreter

   :param expression: Mathematical expression, as in parse(...)
   :return: List of operators and operands in postfix order

getRegisteredFunctions
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Map<String, Function> getRegisteredFunctions()
   :outertype: DefaultFunctionInterpreter

parse
^^^^^

.. java:method:: public Function parse(String expression, int dimension)
   :outertype: DefaultFunctionInterpreter

   **See also:** :java:ref:`ca.nengo.math.FunctionInterpreter.parse(java.lang.String,int)`

registerFunction
^^^^^^^^^^^^^^^^

.. java:method:: public void registerFunction(String name, Function function)
   :outertype: DefaultFunctionInterpreter

   **See also:** :java:ref:`ca.nengo.math.FunctionInterpreter.registerFunction(java.lang.String,ca.nengo.math.Function)`

removeRegisteredFunction
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeRegisteredFunction(String name)
   :outertype: DefaultFunctionInterpreter

sharedInstance
^^^^^^^^^^^^^^

.. java:method:: public static synchronized DefaultFunctionInterpreter sharedInstance()
   :outertype: DefaultFunctionInterpreter

   :return: A singleton instance of DefaultFunctionInterpreter


.. java:import:: java.io Serializable

.. java:import:: java.util ArrayList

.. java:import:: java.util Iterator

.. java:import:: java.util List

.. java:import:: java.util Stack

.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.math Function

PostfixFunction
===============

.. java:package:: ca.nengo.math.impl
   :noindex:

.. java:type:: public class PostfixFunction implements Function

   A Function based on a mathematical expression and on other functions. The expression must be given as a list of list of operators, literal operands, and operand placeholders.

   An operator can be any Function. A literal operand must be a Float.

   An operand placeholder is an Integer, which points to the dimension of the input vector from which the corresponding operand is to be drawn. For example the expression for a PostfixFunction with 2 dimensions can include the Integers 0 and 1. When map(float[] from) is called, Integer 0 will be replaced with from[0] and so on.

   The expression list must be given in postfix order.

   TODO: need a way to manage user-defined functions that ensures they can be accessed from saved networks

   :author: Bryan Tripp

Constructors
------------
PostfixFunction
^^^^^^^^^^^^^^^

.. java:constructor:: public PostfixFunction(List<Serializable> expressionList, String expression, int dimension)
   :outertype: PostfixFunction

   :param expressionList: Postfix expression list (as described in class docs)
   :param expression: String representation of the expression
   :param dimension: Dimension of the function (must be at least as great as any operand placeholders that appear in the expression)

PostfixFunction
^^^^^^^^^^^^^^^

.. java:constructor:: public PostfixFunction(String expression, int dimension)
   :outertype: PostfixFunction

   :param expression: String representation of the expression (infix)
   :param dimension: Dimension of the function (must be at least as great as any operand placeholders that appear in the expression)

Methods
-------
clone
^^^^^

.. java:method:: @Override public Function clone() throws CloneNotSupportedException
   :outertype: PostfixFunction

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: PostfixFunction

   **See also:** :java:ref:`ca.nengo.math.Function.getDimension()`

getExpression
^^^^^^^^^^^^^

.. java:method:: public String getExpression()
   :outertype: PostfixFunction

   :return: A human-readable string representation of the function

getExpressionList
^^^^^^^^^^^^^^^^^

.. java:method:: protected List<Serializable> getExpressionList()
   :outertype: PostfixFunction

   :return: Postfix expression list

map
^^^

.. java:method:: public float map(float[] from)
   :outertype: PostfixFunction

   **See also:** :java:ref:`ca.nengo.math.Function.map(float[])`

multiMap
^^^^^^^^

.. java:method:: public float[] multiMap(float[][] from)
   :outertype: PostfixFunction

   **See also:** :java:ref:`ca.nengo.math.Function.multiMap(float[][])`

setDimension
^^^^^^^^^^^^

.. java:method:: public void setDimension(int dimension)
   :outertype: PostfixFunction

   :param dimension: Dimension of the function (must be at least as great as any operand placeholders that appear in the expression)

setExpression
^^^^^^^^^^^^^

.. java:method:: public void setExpression(String expression)
   :outertype: PostfixFunction

   :param expression: String representation of the expression (infix)


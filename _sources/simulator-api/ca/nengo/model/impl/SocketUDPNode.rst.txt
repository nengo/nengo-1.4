.. java:import:: java.util ArrayList

.. java:import:: java.util Arrays

.. java:import:: java.util Comparator

.. java:import:: java.util HashMap

.. java:import:: java.util Iterator

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util PriorityQueue

.. java:import:: java.io IOException

.. java:import:: java.net InetAddress

.. java:import:: java.net DatagramSocket

.. java:import:: java.net DatagramPacket

.. java:import:: java.net SocketException

.. java:import:: java.net SocketTimeoutException

.. java:import:: java.net UnknownHostException

.. java:import:: java.nio ByteBuffer

.. java:import:: java.nio ByteOrder

.. java:import:: org.apache.log4j Logger

.. java:import:: ca.nengo.model InstantaneousOutput

.. java:import:: ca.nengo.model Node

.. java:import:: ca.nengo.model Origin

.. java:import:: ca.nengo.model RealOutput

.. java:import:: ca.nengo.model Resettable

.. java:import:: ca.nengo.model SimulationException

.. java:import:: ca.nengo.model SimulationMode

.. java:import:: ca.nengo.model SpikeOutput

.. java:import:: ca.nengo.model StructuralException

.. java:import:: ca.nengo.model Termination

.. java:import:: ca.nengo.model Units

.. java:import:: ca.nengo.model.impl PassthroughNode.PassthroughTermination

.. java:import:: ca.nengo.util MU

.. java:import:: ca.nengo.util ScriptGenException

.. java:import:: ca.nengo.util VisiblyMutable

.. java:import:: ca.nengo.util VisiblyMutableUtils

.. java:import:: ca.nengo.util VisiblyMutable.Listener

SocketUDPNode
=============

.. java:package:: ca.nengo.model.impl
   :noindex:

.. java:type:: public class SocketUDPNode implements Node, Resettable

   A Node that sends and receives data through sockets via a UDP connection.

   This can be useful if an input to a Network is actually routed to multiple destinations, but you want to handle this connectivity within the Network rather than expose multiple terminations.

   :author: Bryan Tripp

Fields
------
ORIGIN
^^^^^^

.. java:field:: public static final String ORIGIN
   :outertype: SocketUDPNode

   Default name for an origin

TERMINATION
^^^^^^^^^^^

.. java:field:: public static final String TERMINATION
   :outertype: SocketUDPNode

   Default name for a termination

Constructors
------------
SocketUDPNode
^^^^^^^^^^^^^

.. java:constructor:: public SocketUDPNode(String name, int dimension, int recvDimension, int localPort, String destAddress, int destPort, int socketTimeout, boolean ignoreTimestamp) throws UnknownHostException
   :outertype: SocketUDPNode

   Constructor for a SocketUDPNode that sends and receives data.

   :param name: Node name
   :param dimension: Dimension of data passing through
   :param localPort: Port number on the local machine to bind to. Set to 0 to bind to first available port.
   :param destAddress: Destination address to connect to
   :param destPort: Destination port to connect to
   :param socketTimeout: Timeout on socket in milliseconds (socket blocks until timeout expires)
   :throws SocketException:
   :throws UnknownHostException:

SocketUDPNode
^^^^^^^^^^^^^

.. java:constructor:: public SocketUDPNode(String name, int dimension, int recvDimension, int localPort, int socketTimeout) throws UnknownHostException
   :outertype: SocketUDPNode

   Constructor for a SocketUDPNode that only receives data.

   :param name: Node name
   :param dimension: Dimension of data passing through
   :param localPort: Port number on the local machine to bind to. Set to 0 to bind to first available port.
   :param socketTimeout: Timeout on socket in milliseconds (socket blocks until timeout expires)

SocketUDPNode
^^^^^^^^^^^^^

.. java:constructor:: public SocketUDPNode(String name, int dimension, int recvDimension, int localPort, int socketTimeout, boolean ignoreTimestamp) throws UnknownHostException
   :outertype: SocketUDPNode

   Constructor for a SocketUDPNode that only receives data, with an option to ignore the timestamp on incoming packets.

   :param name: Node name
   :param dimension: Dimension of data passing through
   :param localPort: Port number on the local machine to bind to. Set to 0 to bind to first available port.
   :param ignoreTimestamp: Set to true to ignore timestamps on incoming packets.
   :param socketTimeout: Timeout on socket in milliseconds (socket blocks until timeout expires)

SocketUDPNode
^^^^^^^^^^^^^

.. java:constructor:: public SocketUDPNode(String name, int dimension, String destAddress, int destPort) throws UnknownHostException
   :outertype: SocketUDPNode

   Constructor for a SocketUDPNode that only sends data.

   :param name: Node name
   :param dimension: Dimension of data passing through
   :param destAddress: Destination address to connect to
   :param destPort: Destination port to connect to

Methods
-------
addChangeListener
^^^^^^^^^^^^^^^^^

.. java:method:: public void addChangeListener(Listener listener)
   :outertype: SocketUDPNode

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.addChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

addTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination addTermination(String name, float[][] transform) throws StructuralException
   :outertype: SocketUDPNode

clone
^^^^^

.. java:method:: @Override public Node clone() throws CloneNotSupportedException
   :outertype: SocketUDPNode

close
^^^^^

.. java:method:: public void close()
   :outertype: SocketUDPNode

getChildren
^^^^^^^^^^^

.. java:method:: public Node[] getChildren()
   :outertype: SocketUDPNode

getDestInetAddress
^^^^^^^^^^^^^^^^^^

.. java:method:: public InetAddress getDestInetAddress()
   :outertype: SocketUDPNode

getDestPort
^^^^^^^^^^^

.. java:method:: public int getDestPort()
   :outertype: SocketUDPNode

getDimension
^^^^^^^^^^^^

.. java:method:: public int getDimension()
   :outertype: SocketUDPNode

getDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public String getDocumentation()
   :outertype: SocketUDPNode

   **See also:** :java:ref:`ca.nengo.model.Node.getDocumentation()`

getIgnoreTimestamp
^^^^^^^^^^^^^^^^^^

.. java:method:: public boolean getIgnoreTimestamp()
   :outertype: SocketUDPNode

getLocalPort
^^^^^^^^^^^^

.. java:method:: public int getLocalPort()
   :outertype: SocketUDPNode

getMode
^^^^^^^

.. java:method:: public SimulationMode getMode()
   :outertype: SocketUDPNode

   **See also:** :java:ref:`ca.nengo.model.SimulationMode.ModeConfigurable.getMode()`

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: SocketUDPNode

   **See also:** :java:ref:`ca.nengo.model.Node.getName()`

getOrigin
^^^^^^^^^

.. java:method:: public Origin getOrigin(String name) throws StructuralException
   :outertype: SocketUDPNode

   **See also:** :java:ref:`ca.nengo.model.Node.getOrigin(java.lang.String)`

getOrigins
^^^^^^^^^^

.. java:method:: public Origin[] getOrigins()
   :outertype: SocketUDPNode

   **See also:** :java:ref:`ca.nengo.model.Node.getOrigins()`

getSocketTimeout
^^^^^^^^^^^^^^^^

.. java:method:: public int getSocketTimeout()
   :outertype: SocketUDPNode

getTermination
^^^^^^^^^^^^^^

.. java:method:: public Termination getTermination(String name) throws StructuralException
   :outertype: SocketUDPNode

   **See also:** :java:ref:`ca.nengo.model.Node.getTermination(java.lang.String)`

getTerminations
^^^^^^^^^^^^^^^

.. java:method:: public Termination[] getTerminations()
   :outertype: SocketUDPNode

   **See also:** :java:ref:`ca.nengo.model.Node.getTerminations()`

initialize
^^^^^^^^^^

.. java:method:: public void initialize() throws SimulationException
   :outertype: SocketUDPNode

isReceiver
^^^^^^^^^^

.. java:method:: public boolean isReceiver()
   :outertype: SocketUDPNode

isSender
^^^^^^^^

.. java:method:: public boolean isSender()
   :outertype: SocketUDPNode

releaseMemory
^^^^^^^^^^^^^

.. java:method:: public void releaseMemory()
   :outertype: SocketUDPNode

removeChangeListener
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void removeChangeListener(Listener listener)
   :outertype: SocketUDPNode

   **See also:** :java:ref:`ca.nengo.util.VisiblyMutable.removeChangeListener(ca.nengo.util.VisiblyMutable.Listener)`

reset
^^^^^

.. java:method:: public void reset(boolean randomize)
   :outertype: SocketUDPNode

   **See also:** :java:ref:`ca.nengo.model.Resettable.reset(boolean)`

run
^^^

.. java:method:: public void run(float startTime, float endTime) throws SimulationException
   :outertype: SocketUDPNode

   **See also:** :java:ref:`ca.nengo.model.Node.run(float,float)`

setByteOrder
^^^^^^^^^^^^

.. java:method:: public void setByteOrder(ByteOrder byteOrder)
   :outertype: SocketUDPNode

setByteOrder
^^^^^^^^^^^^

.. java:method:: public void setByteOrder(String byteOrder)
   :outertype: SocketUDPNode

setDimension
^^^^^^^^^^^^

.. java:method:: public void setDimension(int dimension)
   :outertype: SocketUDPNode

setDocumentation
^^^^^^^^^^^^^^^^

.. java:method:: public void setDocumentation(String text)
   :outertype: SocketUDPNode

   **See also:** :java:ref:`ca.nengo.model.Node.setDocumentation(java.lang.String)`

setMode
^^^^^^^

.. java:method:: public void setMode(SimulationMode mode)
   :outertype: SocketUDPNode

   Does nothing (only DEFAULT mode is supported).

   **See also:** :java:ref:`ca.nengo.model.SimulationMode.ModeConfigurable.setMode(ca.nengo.model.SimulationMode)`

setName
^^^^^^^

.. java:method:: public void setName(String name) throws StructuralException
   :outertype: SocketUDPNode

   :param name: The new name

setUpdateInterval
^^^^^^^^^^^^^^^^^

.. java:method:: public void setUpdateInterval(float interval)
   :outertype: SocketUDPNode

toScript
^^^^^^^^

.. java:method:: public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException
   :outertype: SocketUDPNode


TaskSpawner
===========

.. java:package:: ca.nengo.util
   :noindex:

.. java:type:: public interface TaskSpawner

   A node that uses ThreadTasks. Provides a way to easily collect every task defined

   :author: Jonathan Lai

Methods
-------
addTasks
^^^^^^^^

.. java:method:: public void addTasks(ThreadTask[] tasks)
   :outertype: TaskSpawner

   :param tasks: Adds the this to the tasks of the spawner

getTasks
^^^^^^^^

.. java:method:: public ThreadTask[] getTasks()
   :outertype: TaskSpawner

   :return: The ThreadTasks used by this Node

setTasks
^^^^^^^^

.. java:method:: public void setTasks(ThreadTask[] tasks)
   :outertype: TaskSpawner

   :param tasks: Sets the tasks of the spawner to this


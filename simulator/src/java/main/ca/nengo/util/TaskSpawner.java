package ca.nengo.util;

/**
 * A node that uses ThreadTasks.
 * Provides a way to easily collect every task defined
 *
 * @author Jonathan Lai
 */
public interface TaskSpawner {
    
    /**
     * @return The ThreadTasks used by this Node
     */
    public ThreadTask[] getTasks();
}

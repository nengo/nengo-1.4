package ca.nengo.math;

import java.util.ArrayList;
import java.util.Set;

import ca.nengo.model.Node;
import ca.nengo.model.Projection;

public interface NetworkPartitioner {
	
	public void initialize(Node[] nodes, Projection[] projections, int numPartitions);
	
	public ArrayList<Set<Node>> getPartitions();
	public int[] getPartitionsAsIntArray();
}

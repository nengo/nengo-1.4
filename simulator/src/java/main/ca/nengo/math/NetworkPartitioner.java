package ca.nengo.math;

import java.util.ArrayList;
import java.util.Set;

import ca.nengo.model.Node;
import ca.nengo.model.Projection;

public interface NetworkPartitioner {
	
	void initialize(Node[] nodes, Projection[] projections, int numPartitions);
	
	ArrayList<Set<Node>> getPartitions();
	int[] getPartitionsAsIntArray();
}


/**
 * Implements a Disjoint Set Forest
 * 
 * @author Alex Ackerman
 * @author Annabel Winters-McCabe
 * 
 * Time Spent: 2 hours
 */
import java.util.*;

public class DisjointSetForest {
	public Node[][] forest;

	/**
	 * Node class for use in the forest [][]
	 * 
	 * stores a Parent Node, a corresponding Pixel, a double Interior Distance
	 * and integer rank, and an integer size for the segment the Node belongs 
	 * in.
	 * 
	 */
	public static class Node {
		public Node parent;
		public Pixel pixelValue;
		public double intDist;
		public int rank;
		public int size;
		
		/**
		 * Node constructor. Stores the corresponding Pixel, initializes size
		 * to 1, and initializes interior Distance and rank to 0. As well as 
		 * the parent to null.
		 * 
		 * @param p the Pixel the Node is being constructed from
		 */
		public Node(Pixel p) {
			pixelValue = p;
			parent = null;
			size = 1;
			intDist = 0;
			rank = 0;

		}
	}

	/**
	 * Constructor for a DisjointSetForest Object
	 * 
	 * Creates and initializes a new DisjointSetForest from a 2D Pixel Array
	 * by creating a corresponding 2D Array of Nodes.
	 * 
	 * @param pixels the 2D Pixel Array
	 */
	public DisjointSetForest(Pixel[][] pixels) {
		forest = new Node[pixels.length][pixels[0].length];
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[0].length; j++) {
				Node n = new Node(pixels[i][j]);
				forest[i][j] = n;
			}
		}
	}

	/**
	 * Finds the representative Node in the segment corresponding to 
	 * the given Pixel. 
	 * 
	 * In searching for the representative Node,
	 * also stores visited Nodes along the way to eventually point 
	 * them up to the representative Node in order to compress the path.
	 * 
	 * @param p a given Pixel
	 * @return c, the representative Node corresponding to the found segment
	 */
	public Node find(Pixel p) {
		//create place to store visited Nodes
		Set<Node> visited = new HashSet<Node>();
		//set c to the Node corresponding to p
		Node c = forest[p.getRow()][p.getCol()];
		//if c is not a representative Node
		if (c.parent != null) {
			//climb up tree until you reach the representative Node
			while (c.parent != null) {
				//store visited nodes along the way
				visited.add(c);
				c = c.parent;
			}
			//adjust parent pointers of visited Nodes
			for (Node n : visited) {
				n.parent = c;
			}
			forest[p.getRow()][p.getCol()].parent = c;
		}
		//return representative Node for p
		return c;
	}
	
	/**
	 * Merges the segments corresponding to 2 given Pixels
	 * 
	 * Calls find on each Pixel to get the representative Node. Then, based on 
	 * the ranks of each node, adjusts parent pointers and the rank (if they 
	 * are of equal rank). Also adjusts the interior Distance (using the edge
	 * weight) and size of each Node.
	 * 
	 * @param p1 the first Pixel
	 * @param p2 the second Pixel
	 * @param e the Edge between the Pixels
	 */
	public void union(Pixel p1, Pixel p2, Edge e) {
		//creates Nodes to store the representative Nodes
		Node rNode1, rNode2;
		// the "New" size of each segment
		int nSize;
		//gets rep. Nodes
		rNode1 = find(p1);
		rNode2 = find(p2);
		//calculates new Size
		nSize = rNode1.size + rNode2.size;
		//if the rank of p1's rep Node is greater
		if (rNode1.rank > rNode2.rank) {
			rNode2.parent = rNode1;
		}
		//if the rank of p2's rep Node is greater
		if (rNode2.rank > rNode1.rank) {
			rNode1.parent = rNode2;
		}
		//if the rep Nodes have equal ranks
		if (rNode2.rank == rNode1.rank) {
			rNode2.parent = rNode1;
			//update rank
			rNode1.rank += 1;
		}
		//update interior Distance
		rNode2.intDist = e.getWeight();
		rNode1.intDist = e.getWeight();
		//update size of each node
		rNode1.size = nSize;
		rNode2.size = nSize;
	}
}

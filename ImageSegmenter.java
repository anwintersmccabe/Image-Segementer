
/**
 * 
 * @author Alex Ackerman
 * @author Annabel Winters-McCabe
 * 
 * Time Spent: 5 hours
 */

import java.awt.Color;
import java.util.*;

public class ImageSegmenter {
	SortedSet<Edge> edges = new TreeSet<Edge>();

	/**
	 * Segments an image and assigns random colors to each, returning the 
	 * result in a 2D Color Array of size corresponding to the image.
	 * 
	 * Uses a 2D Pixel Array, a Disjoint Set Forest, and an Image Segmenter
	 * object (graph) to group similarly colored Pixels together in segments
	 * (trees) via a union operation. Assigns each segment a random color,
	 * and then stores the randomly colored pixels in a 2D Color Array. Uses
	 * granularity to control the level of detail in the produced image.
	 * 
	 * @param rgbArray initial 2D Color Array for the Image
	 * @param granularity double parameter controlling level of detail
	 * in the segmented image
	 * @return pixColors, the 2D Color Array of randomized colors
	 */
	public static Color[][] segment(Color[][] rgbArray, double granularity) {

		Pixel p1, p2;
		// stores computations for nodes involved in potential unions
		double computation1, computation2;
		Color[][] pixColors;
		// makes pixel array
		Pixel[][] pixels = makePixelArray(rgbArray);
		// creates a new DisjointSetForest
		DisjointSetForest f = new DisjointSetForest(pixels);
		// creates a new ImageSegmenter object (the graph)
		ImageSegmenter graph = new ImageSegmenter(pixels);
		// for each edge in the graph
		for (Edge e : graph.edges) {
			//get the Pixels in the edge
			p1 = e.getFirstPixel();
			p2 = e.getSecondPixel();
			//find their representative Nodes
			DisjointSetForest.Node s1 = f.find(p1);
			DisjointSetForest.Node s2 = f.find(p2);
			//calculate using the formula
			computation1 = s1.intDist + (granularity / s1.size);
			computation2 = s2.intDist + (granularity / s2.size);
			// if the Pixels are not in the same segment
			if (!s1.equals(s2)) {
				if (e.getWeight() < Math.min(computation1, computation2)) {
					//perform a union operation if true
					f.union(p1, p2, e);
				}
			}
		}

		pixColors = assignColors(f, pixels);

		return pixColors;
	}

	/**
	 * Assigns random colors to each segment in the DisjointSetForest.
	 * 
	 * Creates a Hashmap of representative Nodes and colors. Calls find
	 * on each Pixel in the Pixel [][] and then checks to see if the
	 * rep Node has a mapped Color. If not, assigns and stores one. 
	 * Then uses that color or a previously saved color to store a color
	 * for each Pixel in pixColors.
	 * 
	 * @param f the DisjointSetForest
	 * @param pixels the 2D Pixel Array for the image
	 * @return pixColors, the 2D Color Array of randomized segment colors
	 */
	private static Color[][] assignColors(DisjointSetForest f, Pixel[][] pixels) {
		ColorPicker colorGenerator = new ColorPicker();
		Color randomColor;
		Map<DisjointSetForest.Node, Color> colorKeys = new HashMap<DisjointSetForest.Node, Color>();
		Color[][] pixColors = new Color[f.forest.length][f.forest[0].length];
		for (int i = 0; i < f.forest.length; i++) {
			for (int j = 0; j < f.forest[0].length; j++) {
				if (!colorKeys.containsKey(f.find(pixels[i][j]))) {
					randomColor = colorGenerator.nextColor();
					colorKeys.put(f.find(pixels[i][j]), randomColor);
					pixColors[i][j] = randomColor;
				} else {
					pixColors[i][j] = colorKeys.get(f.find(pixels[i][j]));
				}
			}
		}
		return pixColors;
	}

	/**
	 * Makes an array of Pixel objects from an array of Colors
	 * 
	 * @param rgbArray a 2D array of Colors
	 * @return pixels, the 2D Pixel array
	 */
	private static Pixel[][] makePixelArray(Color[][] rgbArray) {
		Pixel[][] pixels = new Pixel[rgbArray.length][rgbArray[0].length];
		for (int i = 0; i < rgbArray.length; i++) {
			for (int j = 0; j < rgbArray[0].length; j++) {
				pixels[i][j] = new Pixel(i, j, rgbArray[i][j]);
			}
		}
		return pixels;
	}

	/**
	 * Constructor for an ImageSegmenter Object
	 * 
	 * creates a graph (Sorted Set of edges) from a pixel array
	 * 
	 * @param pixels the 2D Pixel Array
	 */
	public ImageSegmenter(Pixel[][] pixels) {
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[0].length; j++) {
				graphHelper(pixels, pixels[i][j]);
			}
		}
	}

	/**
	 * Helper method for the ImageSegmenter constructor
	 * 
	 * Uses a Pixel array and a current Pixel to check if potential Edges are in
	 * bounds and then add them to the Sorted Set of Edges if they are. Thus
	 * creating a graph.
	 * 
	 * @param pixels the 2D Pixel Array
	 * @param cPixel the current Pixel
	 */
	private void graphHelper(Pixel[][] pixels, Pixel cPixel) {

		int cRow = cPixel.getRow();
		int cCol = cPixel.getCol();
		// down
		if (inBounds(cRow + 1, cCol, pixels)) {
			Edge e = new Edge(cPixel, pixels[cRow + 1][cCol]);
			edges.add(e);
		}
		// diagonal up right
		if (inBounds(cRow - 1, cCol + 1, pixels)) {
			Edge e = new Edge(cPixel, pixels[cRow - 1][cCol + 1]);
			edges.add(e);
		}
		// right
		if (inBounds(cRow, cCol + 1, pixels)) {
			Edge e = new Edge(cPixel, pixels[cRow][cCol + 1]);
			edges.add(e);
		}
		// diagonal down right
		if (inBounds(cRow + 1, cCol + 1, pixels)) {
			Edge e = new Edge(cPixel, pixels[cRow + 1][cCol + 1]);
			edges.add(e);
		}
	}

	/**
	 * Helper method for graphHelper that checks if a potential edge is in bounds
	 * for the given 2D Pixel array using a current Row and current Column.
	 * 
	 * @param cRow   int representing current Row
	 * @param cCol   int representing current Column
	 * @param pixels the 2D Pixel array
	 * @return a boolean, depending on if in bounds or not
	 */
	private static boolean inBounds(int cRow, int cCol, Pixel[][] pixels) {
		if (cRow > -1 && cRow < pixels.length) {

		} else {
			return false;
		}

		if (cCol > -1 && cCol < pixels[0].length) {

		} else {
			return false;
		}

		return true;
	}
}

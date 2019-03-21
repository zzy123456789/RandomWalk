package matrix;

import java.io.FileReader;
import java.util.Arrays;

import weka.core.*;

public class CompressedMatrix {
	public static final int MAX_WEIGHT = 100000;

	Instances data;

	/**
	 * The matrix.
	 */
	public Triple[] matrix;
	
	/**
	 * The cluster number for each node.
	 */
	int[] clusterNumbers;
	
   /**
    * The queue for width first clustering
    */
	IntLoopQueue intLoopQueue;
	
	/**
	 * Is the respective node visited?
	 */
	boolean[] visited;

	/**
	 *********************
	 * Make a copy. Clone.
	 *********************
	 */
	public CompressedMatrix(CompressedMatrix paraMatrix) {
		matrix = new Triple[paraMatrix.matrix.length];
		Triple tempTail, tempCurrentTriple;
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = new Triple();
			tempTail = matrix[i];
			
			tempCurrentTriple = paraMatrix.matrix[i].next;
			while (tempCurrentTriple != null) {
				Triple tempNewTriple = new Triple(tempCurrentTriple.column, tempCurrentTriple.weight, null);
				//Now insert
				tempTail.next = tempNewTriple;
				tempTail = tempNewTriple;
				
				tempCurrentTriple = tempCurrentTriple.next;
			}//Of while
		}//Of for i
		initialize();
	}//Of the first constructor
	
	/**
	 *********************
	 * Read from a triple file.
	 *********************
	 */
	public CompressedMatrix(String paraFilename) {
		data = null;
		try {
			FileReader fileReader = new FileReader(paraFilename);
			data = new Instances(fileReader);
			fileReader.close();

			System.out.println(data);
		} catch (Exception ee) {
			System.out.println("Error occurred while trying to read \'" + paraFilename + ".\r\n" + ee);
			return;
		} // Of try

		Instance tempLastInstance = data.instance(data.numInstances() - 1);
		int tempNumNodes = (int) tempLastInstance.value(0) + 1;
		System.out.println("There are " + tempNumNodes + " nodes.");
		matrix = new Triple[tempNumNodes];
		Triple[] tails = new Triple[tempNumNodes];

		// Initialize
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = new Triple();
			tails[i] = matrix[i];
		} // Of for i
		
		//Now read data
		for (int i = 0; i < data.numInstances(); i++) {
			int tempRow = (int)data.instance(i).value(0);
			int tempColumn = (int)data.instance(i).value(1);
			double tempWeight = data.instance(i).value(2);
			
			Triple tempNewTriple = new Triple(tempColumn, tempWeight, null);
			tails[tempRow].next = tempNewTriple;
			
			//Update the tail
			tails[tempRow] = tempNewTriple;
		}//Of for i
		initialize();
	}// Of the second constructor

	/**
	 *********************
	 * Construct an empty compressed matrix with the given number of nodes.
	 *********************
	 */
	public CompressedMatrix(int paraNumNodes) {
		data = null;
		matrix = new Triple[paraNumNodes];

		// Initialize
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = new Triple();
		} // Of for i
		initialize();
	}//Of the third constructor
	
	/**
	 *********************
	 * Initialize some variables.
	 *********************
	 */
	void initialize(){
		clusterNumbers = null;
		intLoopQueue = null;
		visited = null;
	}//Of initialize
	
	/**
	 *********************
	 * The constructor. Invoke the constructor of the superclass directly.
	 * 
	 * @param paraFilename
	 *            The data filename.
	 *********************
	 */
	public CompressedMatrix(String paraFilename, int paraK) {
		data = null;
		try {
			FileReader fileReader = new FileReader(paraFilename);
			data = new Instances(fileReader);
			fileReader.close();

			System.out.println(data);
		} catch (Exception ee) {
			System.out.println("Error occurred while trying to read \'" + paraFilename + ".\r\n" + ee);
			return;
		} // Of try

		int totalInserted = 0;

		// Initialize matrix
		matrix = new Triple[data.numInstances()];
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = new Triple();
		} // Of for i

		// kNearestNeighbors(0, 3);
		// Scan the data to construct the matrix
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = kNearestNeighbors(i, paraK);
		} // Of for i

		System.out.println(this);
		symmetrize();
		System.out.println("After symmtrize\r\n.The vector is: " + this);

		System.out.println("Total inserted: " + totalInserted);
	}// of the first constructor
	
	/**
	 *********************
	 * Compute the k-nearest neighbors of the node.
	 *********************
	 */
	Triple kNearestNeighbors(int paraNode, int paraK) {
		Triple resultHeader = new Triple();
		Triple tempReference = resultHeader;

		int[] tempIndices = new int[paraK + 1];
		double[] tempWeights = new double[paraK + 1];
		double tempSimilarity = 0;

		for (int i = 0; i < data.numInstances(); i++) {
			if (i == paraNode) {
				continue;
			} // Of if

			double tempDistance = manhattanBetweenInstances(paraNode, i);
			if (tempDistance < 1e-10) {
				tempSimilarity = MAX_WEIGHT;
			} else {
				tempSimilarity = 1.0 / tempDistance;
			} // Of else
				// System.out.println("tempSimilarity = " + tempSimilarity);
			for (int j = 0; j < paraK; j++) {
				if (tempSimilarity > tempWeights[j]) {
					// Move the tail
					for (int k = paraK; k > j; k--) {
						tempIndices[k] = tempIndices[k - 1];
						tempWeights[k] = tempWeights[k - 1];
					} // Of for k

					// Now insert
					tempIndices[j] = i;
					tempWeights[j] = tempSimilarity;

					break;
				} // Of if
			} // Of for j
		} // Of for i

		// System.out.println("Indices: " + Arrays.toString(tempIndices) + ",
		// weights " + Arrays.toString(tempWeights));

		boolean[] tempProcessed = new boolean[paraK];
		int tempMinimalIndex = Integer.MAX_VALUE;
		double tempWeight = -1;
		int tempIndexInInicesArray = -1;
		// Search the whole array
		for (int i = 0; i < paraK; i++) {
			tempMinimalIndex = Integer.MAX_VALUE;
			// Find the minimal index
			for (int j = 0; j < paraK; j++) {
				if (tempProcessed[j]) {
					continue;
				} // Of if

				if (tempIndices[j] < tempMinimalIndex) {
					tempMinimalIndex = tempIndices[j];
					tempWeight = tempWeights[j];
					tempIndexInInicesArray = j;
				} // Of if
			} // of for j

			Triple tempNewTriple = new Triple();
			tempNewTriple.column = tempMinimalIndex;
			tempNewTriple.weight = tempWeight;
			tempProcessed[tempIndexInInicesArray] = true;

			// Link it now!
			tempReference.next = tempNewTriple;
			tempReference = tempNewTriple;
		} // Of for i

		// System.out.println("The vector is: " + resultHeader);

		return resultHeader;
	}// Of kNearestNeighbors

	/**
	 *********************
	 * Compute the Manhattan distance between two data points. The decision
	 * attribute is ignored.
	 * 
	 * @return
	 *********************
	 */
	protected double manhattanBetweenInstances(int paraI, int paraJ) {
		double tempDistance = 0;

		for (int i = 0; i < data.numAttributes() - 1; i++) {
			tempDistance += Math.abs(data.instance(paraI).value(i) - data.instance(paraJ).value(i));
		} // Of for i

		return tempDistance;
	}// Of manhattanBetweenInstances

	/**
	 *********************
	 * Make the matrix symmetric.
	 *********************
	 */
	void symmetrize() {
		Triple tempTriple;
		int tempTarget;
		int tempI;
		double tempWeight;

		for (int i = 0; i < matrix.length; i++) {
			tempTriple = matrix[i].next;
			while (tempTriple != null) {
				tempTarget = tempTriple.column;
				tempI = i;
				tempWeight = tempTriple.weight;
				insertToList(tempTarget, tempI, tempWeight);

				tempTriple = tempTriple.next;
			} // Of while
		} // Of for i
	}// Of symmetrize
	
	/**
	 *********************
	 * Compute the k-nearest neighbors of the node.
	 *********************
	 */
	boolean insertToList(int paraTargetIndex, int paraI, double paraWeight) {
		boolean tempAlreadyExists = false;

		Triple tempP = matrix[paraTargetIndex];
		// Search the position
		Triple tempQ = tempP.next;

		while (tempQ != null) {
			if (tempQ.column == paraI) {
				tempAlreadyExists = true;
				break;
			} // Of if

			if (tempQ.column < paraI) {
				tempP = tempQ;
				tempQ = tempQ.next;
			} else {
				// Now insert
				//totalInserted++;
				System.out.print("insert " + paraI + ", " + paraTargetIndex);
				Triple tempNewTriple = new Triple(paraI, paraWeight, tempQ);
				tempP.next = tempNewTriple;
				break;
			} // Of if
		} // Of while

		return !tempAlreadyExists;
	}// Of insertToList

	/**
	 *********************
	 * Multiply matrices.
	 *********************
	 */
	public static CompressedMatrix multiply(CompressedMatrix paraMatrix1,
			CompressedMatrix paraMatrix2) {
		System.out.println("multiply test 1");
		int tempNumNodes = paraMatrix1.matrix.length;
		
		CompressedMatrix tempTransposedMatrix = transpose(paraMatrix2);
		CompressedMatrix resultMatrix = new CompressedMatrix(tempNumNodes);

		OrderedIntArray tempArray = new OrderedIntArray(10000);
		
		int tempMiddle;
		Triple tempOuterTriple, tempInnerTriple;
		// Step 1. Compute each row of the new matrix
		System.out.println("multiply test 2");
		for (int i = 0; i < paraMatrix1.matrix.length; i++) {
			tempArray.reset();
			// Step 1.1 Compute the array of available nodes
			tempOuterTriple = paraMatrix1.matrix[i].next;
			//System.out.println("multiply test 2.1");
			while (tempOuterTriple != null) {
				tempMiddle = tempOuterTriple.column;
				tempInnerTriple = paraMatrix2.matrix[tempMiddle].next;
				while (tempInnerTriple != null) {
					//System.out.print("\t" + i + "->" + tempMiddle + "->" + tempInnerTriple.column);
					tempArray.insert(tempInnerTriple.column);
					tempInnerTriple = tempInnerTriple.next;
				} // Of inner while
				tempOuterTriple = tempOuterTriple.next;
			} // Of while
			//System.out.println("multiply test 2.2");
			//System.out.println(tempArray);
			
			// Step 1.2 Compute weights
			Triple tempPointer = resultMatrix.matrix[i];
			for (int j = 0; j < tempArray.size; j ++) {
				//Add one node
				Triple tempNewTriple = new Triple();
				tempNewTriple.column = tempArray.data[j];
				tempNewTriple.weight = Triple.multiply(paraMatrix1.matrix[i], tempTransposedMatrix.matrix[tempNewTriple.column]);
				tempPointer.next = tempNewTriple;
				tempPointer = tempNewTriple;
			}//Of for j
		} // Of for i

		System.out.println("After multiply: " + resultMatrix);
		return resultMatrix;
	}// Of multiply
	
	/**
	 *********************
	 * Add matrices.
	 *********************
	 */
	public static CompressedMatrix add(CompressedMatrix paraMatrix1,
			CompressedMatrix paraMatrix2) {
		int tempNumNodes = paraMatrix1.matrix.length;
		
		CompressedMatrix resultMatrix = new CompressedMatrix(tempNumNodes);
		
		for (int i = 0; i < resultMatrix.matrix.length; i++) {
			resultMatrix.matrix[i].next = Triple.add(paraMatrix1.matrix[i], paraMatrix2.matrix[i]);
		}//Of for i
		
		System.out.println("After addition: " + resultMatrix);
		return resultMatrix;
	}//Of add

	/**
	 *********************
	 * For output.
	 *********************
	 */
	public String toString() {
		String resultString = "";
		Triple tempTriple;
		for (int i = 0; i < matrix.length; i++) {
			resultString += "\r\n" + i + ":";
			tempTriple = matrix[i].next;
			while (tempTriple != null) {
				resultString += "(" + tempTriple.column + "," + tempTriple.weight + "); ";
				tempTriple = tempTriple.next;
			} // Of while
		} // Of for i

		return resultString;
	}// Of toString
	
	/**
	 *********************
	 * Transpose a n*n matrix.
	 *********************
	 */
	public static CompressedMatrix transpose(CompressedMatrix paraMatrix) {
		int tempNumNodes = paraMatrix.matrix.length;
		CompressedMatrix resultMatrix = new CompressedMatrix(tempNumNodes);
		
		Triple[] tempTails = new Triple[tempNumNodes];

		// Initialize
		for (int i = 0; i < tempNumNodes; i++) {
			tempTails[i] = resultMatrix.matrix[i];
		} // Of for i
		
		//Scan each row and copy
		Triple tempTriple, tempNewTriple;
		for (int i = 0; i < tempNumNodes; i++) {
			tempTriple = paraMatrix.matrix[i].next;
			while (tempTriple != null) {
				//Construct a new triple
				int tempNewRow = tempTriple.column;
				int tempNewColum = i;
				tempNewTriple = new Triple(tempNewColum, tempTriple.weight, null);
				
				//Now insert
				tempTails[tempNewRow].next = tempNewTriple;
				tempTails[tempNewRow] = tempNewTriple;

				tempTriple = tempTriple.next;
			}//Of while
			
		}//Of for i
		return resultMatrix;
	}//Of transpose

	/**
	 *********************
	 * Compute the transition probabilities
	 * 
	 * @param paraMatrix1
	 *            The first matrix.
	 *********************
	 */
	public CompressedMatrix computeTransitionProbabilities() {
		CompressedMatrix resultMatrix = new CompressedMatrix(matrix.length);
		double tempRowSum;
		Triple tempTriple;
		//For each node
		for (int i = 0; i < matrix.length; i++) {
			//First scan of the row to obtain row sum
			tempRowSum = 0;
			tempTriple = matrix[i].next;
			while (tempTriple != null) {
				tempRowSum += tempTriple.weight;
				tempTriple = tempTriple.next;
			}//Of while
			
			//Second scan of the row to construct nodes
			tempTriple = matrix[i].next;
			Triple tempTail = resultMatrix.matrix[i];
			while (tempTriple != null) {
				Triple tempNewTriple = new Triple(tempTriple.column, tempTriple.weight/tempRowSum, null);
				//Now append
				tempTail.next = tempNewTriple;
				tempTail = tempNewTriple;
				
				tempTriple = tempTriple.next;
			}//Of while
		}//Of for i
		
		return resultMatrix;
	}//Of computeTransitionProbabilities
	
	/**
	 *********************
	 * Compute the neighborhood similarity
	 * 
	 * @param 
	 *********************
	 */
	public double neighborhoodSimilarity(int paraI, int paraJ, int paraK) {
		double resultValue = 0;
		double tempDistance = Triple.manhattan(matrix[paraI], matrix[paraJ]);
		if (tempDistance > 6) {
			System.out.print("manhattan(" + paraI + ", " + paraJ + ") = " + tempDistance + " Reason: ");
			System.out.println(matrix[paraI].next + " vs. " + matrix[paraJ].next);
		}
		
		resultValue = Math.exp(2 * paraK - tempDistance) - 1;
		
		return resultValue;
	}//Of neighborhoodSimilarity

	/**
	 *********************
	 * Depth-first clustering
	 * 
	 * @param 
	 *********************
	 */
	public int[] depthFirstClustering(double paraCutThreshold) {
		if (clusterNumbers == null) {
			clusterNumbers = new int[matrix.length];
		}//Of if
		Arrays.fill(clusterNumbers, -1);
		if (visited == null) {
			visited = new boolean[matrix.length];
		}//Of if
		Arrays.fill(visited, false);
		
		
		int tempNumber = 0;
		Triple tempTriple;
		for (int i = 0; i < matrix.length; i++) {
			if (visited[i]) {
				continue;
			}//Of if
			
			tempNumber ++;
			System.out.println("Assign " + tempNumber + " to node#" + i);
			clusterNumbers[i] = tempNumber;
			visited[i] = true;
			
			//Handle the neighbors
			tempTriple = matrix[i].next;
			while (tempTriple != null) {
				if ((!visited[tempTriple.column]) && (tempTriple.weight > paraCutThreshold)) {
					clusterNumbers[tempTriple.column] = tempNumber;
					System.out.println("Assign " + tempNumber + " to node#" + tempTriple.column);
					visited[tempTriple.column] = true;
					//Find the neighbor through the column
					
					depthFirstAssignment(tempTriple.column, tempNumber, paraCutThreshold);
				}//Of if
				tempTriple = tempTriple.next;
			}//Of while
		}//Of for i
		
		System.out.println("The clustering result is: " + Arrays.toString(clusterNumbers));
		return clusterNumbers;
	}//Of depthFirstClustering

	/**
	 *********************
	 * Width-first clustering
	 * 
	 * @param 
	 *********************
	 */
	public int[] widthFirstClustering(double paraCutThreshold) throws Exception{
		if (clusterNumbers == null) {
			clusterNumbers = new int[matrix.length];
		}//Of if
		Arrays.fill(clusterNumbers, -1);
		
		if (visited == null) {
			visited = new boolean[matrix.length];
		}//Of if
		Arrays.fill(visited, false);
		
		if (intLoopQueue == null) {
			intLoopQueue = new IntLoopQueue(matrix.length);
		}//Of if
		
		int tempNumber = 0;
		Triple tempTriple;
		int tempNode;
		for (int i = 0; i < matrix.length; i++) {
			if (visited[i]) {
				continue;
			}//Of if
			
			tempNumber ++;
			intLoopQueue.reset();
			intLoopQueue.enqueue(i);
			clusterNumbers[i] = tempNumber;
			visited[i] = true;
			while (!intLoopQueue.isEmpty()){
				tempNode = intLoopQueue.dequeue();
				clusterNumbers[tempNode] = tempNumber;
				
				tempTriple = matrix[tempNode].next;
				while (tempTriple != null) {
					if ((!visited[tempTriple.column]) && (tempTriple.weight > paraCutThreshold)) {
						intLoopQueue.enqueue(tempTriple.column);
						clusterNumbers[tempTriple.column] = tempNumber;
						System.out.println("Assign " + tempNumber + " to node#" + tempTriple.column);
						visited[tempTriple.column] = true;
					}//Of if
					tempTriple = tempTriple.next;
				}//Of while
			}//Of while
		}//Of for i
		
		System.out.println("The clustering result is: " + Arrays.toString(clusterNumbers));
		
		return clusterNumbers;
	}//Of widthFirstClustering

	/**
	 *********************
	 * Depth-first assignement
	 * 
	 * @param 
	 *********************
	 */
	private void depthFirstAssignment(int paraNode, int paraNumber, double paraCutThreshold) {
		Triple tempTriple = matrix[paraNode].next;
		while (tempTriple != null) {
			if ((!visited[tempTriple.column]) && (tempTriple.weight > paraCutThreshold)) {
				System.out.println("Assign " + paraNumber + " to node#" + tempTriple.column);
				clusterNumbers[tempTriple.column] = paraNumber;
				visited[tempTriple.column] = true;
				
				//Find the neighbor through the column
				depthFirstAssignment(tempTriple.column, paraNumber, paraCutThreshold);
			}//Of if
			tempTriple = tempTriple.next;
		}//Of while
	}//Of depthFirstAssignment
	
	/**
	 *********************
	 * The test method.
	 *********************
	 */
	public static void main(String args[]) {
		System.out.println("Testing CompressedMatrix!");
		CompressedMatrix tempMatrix = new CompressedMatrix("D:/workspace/randomwalk/data/example21.arff");
		System.out.println("The matrix is: \r\n" + tempMatrix);
		
		CompressedMatrix tempMatrix2 = CompressedMatrix.transpose(tempMatrix);
		System.out.println("The transposed matrix is: \r\n" + tempMatrix2);

		CompressedMatrix tempMatrix3 = CompressedMatrix.multiply(tempMatrix, tempMatrix);
		System.out.println("The multiplied matrix is: \r\n" + tempMatrix3);

		CompressedMatrix tempMatrix4 = CompressedMatrix.add(tempMatrix, tempMatrix3);
		System.out.println("The added matrix is: \r\n" + tempMatrix4);
		
		CompressedMatrix tempMatrix5 = tempMatrix.computeTransitionProbabilities();
		System.out.println("The probability matrix is: \r\n" + tempMatrix5);
		
	}// Of main
}// Of class CompressedMatrix

package randomwalk;

import java.util.Arrays;

import clustering.*;
import matrix.CompressedMatrix;
import matrix.CompressedSymmetricMatrix;
import matrix.Triple;

public class RandomWalk {
	/**
	 *********************
	 * The main algorithm.
	 * 
	 * @param paraFilename
	 *            The name of the decision table, or triple file.
	 * @param paraNumRounds
	 *            The rounds for random walk, each round update the weights,
	 *            however does not change the topology.
	 * @param paraK
	 *            The maximal times for matrix multiplex.
	 * @param paraMinNeighbors
	 *            For converting decision system into matrix only.
	 * @param paraCutThreshold
	 *            For final clustering from the result matrix. Links smaller
	 *            than the threshold will break.
	 *********************
	 */
	public void randomWalk(String paraFilename, int paraNumRounds, int paraK, int paraMinNeighbors,
			double paraCutThreshold) {
		// Step 1. Read data
		CompressedMatrix tempMatrix = new CompressedMatrix(paraFilename, paraMinNeighbors);
		System.out.println("The original matrix is: " + tempMatrix);
		CompressedMatrix tempMultiplexion, tempCombinedTransitionMatrix;

		// Step 2. Run a number of rounds to obtain new matrices
		for (int i = 0; i < paraNumRounds; i++) {
			// Step 2.1 Compute probability matrix
			CompressedMatrix tempProbabilityMatrix = tempMatrix.computeTransitionProbabilities();
			System.out.println("\r\nThe probability matrix is:" + tempProbabilityMatrix);
			// Make a copy
			tempMultiplexion = new CompressedMatrix(tempProbabilityMatrix);

			// Step 2.2 Multiply and add
			// Reinitialize
			tempCombinedTransitionMatrix = new CompressedMatrix(tempProbabilityMatrix);
			for (int j = 2; j <= paraK; j++) {
				System.out.println("j = " + j);
				tempMultiplexion = CompressedMatrix.multiply(tempMultiplexion, tempProbabilityMatrix);
				tempCombinedTransitionMatrix = CompressedMatrix.add(tempCombinedTransitionMatrix, tempMultiplexion);
			} // Of for j

			System.out.println("Find the error!" + tempMatrix);

			// Step 2.3 Distance between adjacent nodes
			for (int j = 0; j < tempMatrix.matrix.length; j++) {
				Triple tempCurrentTriple = tempMatrix.matrix[j].next;
				while (tempCurrentTriple != null) {
					// Update the weight
					tempCurrentTriple.weight = tempCombinedTransitionMatrix.neighborhoodSimilarity(j,
							tempCurrentTriple.column, paraK);

					tempCurrentTriple = tempCurrentTriple.next;
				} // Of while
			} // Of for i
		} // Of for i

		System.out.println("The new matrix is:" + tempMatrix);

		// Step 3. Depth-first clustering and output
		//tempMatrix.depthFirstClustering(paraCutThreshold);
		
		// Step 3'. Width-first clustering and output
		try {
			tempMatrix.widthFirstClustering(paraCutThreshold);
		} catch (Exception ee) {
			System.out.println("Error occurred in random walk: " + ee);
		}//Of try
	}// Of randomWalk

	public static void main(String args[]) {
		System.out.println("Let's randomly walk!");
//		 KMeans tempMeans = new
//		 KMeans("data/3features.arff");
		// KMeans tempMeans = new
		// KMeans("D:/workspace/randomwalk/data/iris.arff");
//		 Walk tempWalk = new Walk("data/3features.arff");
//		 int[] tempIntArray = {1, 2};
//		 for (int i = 3; i < 10; ) {
//				System.out.println("Group number is " + i);
////		 tempMeans.kMeans(5, KMeans.MANHATTAN);
//		 tempMeans.kMeans(i, KMeans.EUCLIDEAN);
//		 i += 2;
//		 }
		// tempWalk.computeVkS(tempIntArray, 3);
//		 double[][] tempMatrix = tempWalk.computeTransitionProbabilities();
//		 double[][] tempTransition =
//		 tempWalk.computeKStepTransitionProbabilities(100);
//		 double[][] tempTransition =
//		 tempWalk.computeAtMostKStepTransitionProbabilities(5);

		// double[][] tempNewGraph = tempWalk.ngSeparate(3);

		// System.out.println(Arrays.deepToString(tempMatrix));

		// System.out.println("The new graph is:\r\n" +
		// Arrays.deepToString(tempNewGraph));

//		 CompressedSymmetricMatrix tempMatrix = new
//		 CompressedSymmetricMatrix("D:/workspace/randomwalk/data/iris.arff",
//		 3);
//		 CompressedSymmetricMatrix tempMatrix2 =
//		 CompressedSymmetricMatrix.multiply(tempMatrix, tempMatrix);
//		 CompressedSymmetricMatrix tempMatrix2 =
//		 CompressedSymmetricMatrix.weightMatrixToTransitionProbabilityMatrix(tempMatrix);

		// System.out.println("The new matrix is: \r\n" + tempMatrix2);
		// System.out.println("The accuracy is: " + tempMeans.computePurity());

//		 new
//		 RandomWalk().randomWalk("D:/workspace/randomwalk/data/example21.arff",
//		// 1, 3);
		new RandomWalk().randomWalk("data/3features.arff", 3, 3, 3, 0.03);
	}// Of main
//	public static void main(String args[]){
//		System.out.println("Let's randomly walk!");
//		//KMeans tempMeans = new KMeans("D:/workplace/randomwalk/data/iris.arff");
//		//KMeans tempMeans = new KMeans("D:/workspace/randomwalk/data/iris.arff");
//		Walk tempWalk = new Walk("data/3features.arff");
//		int[] tempIntArray = {1, 2};
//		
//		//tempMeans.kMeans(3, KMeans.MANHATTAN);
//		//tempMeans.kMeans(3, KMeans.EUCLIDEAN);
//		//tempWalk.computeVkS(tempIntArray, 3);
//		double[][] tempMatrix = tempWalk.computeTransitionProbabilities();
//		double[][] tempTwoStepTransition = Walk.doubleMatricesMultiplex(tempMatrix, tempMatrix);
//		System.out.println("The two step transitionProbabilities is: " + Arrays.deepToString(tempTwoStepTransition));
//		double[][] tempThreeStepTransition = Walk.doubleMatricesMultiplex(tempTwoStepTransition, tempMatrix);
//		System.out.println("The three step transitionProbabilities is: " + Arrays.deepToString(tempThreeStepTransition));
//		
//		//System.out.println("The accuracy is: " + tempMeans.computePurity());
//	}//Of main
}// Of class RandomWalk

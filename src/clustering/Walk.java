package clustering;

import java.util.Arrays;

public class Walk extends Clustering{
	double[][] weightedGraph = {
			{0, 5, 2, 0, 1},
			{5, 0, 6, 0, 0},
			{2, 6, 0, 0, 0},
			{0, 0, 0, 0, 4},
			{1, 0, 0, 4, 0}
	};
	
	int[][] connections = {
		{0, 1, 1, 0, 1},
		{1, 0, 1, 0, 0},
		{1, 1, 0, 0, 0},
		{0, 0, 0, 0, 1},
		{1, 0, 0, 1, 0}
	};
	
	double[][] transitionProbabilities;
	
	/**
	 *********************
	 * The constructor. Invoke the constructor of the superclass directly.
	 * 
	 * @param paraFilename
	 *            The data filename.
	 *********************
	 */
	public Walk(String paraFilename) {
		super(paraFilename);
	}// of the first constructor

	/**
	 *********************
	 * Multiply two matrices. The number of rows of the second matrix is equal to the number of columns of the first.
	 * 
	 * @param paraMatrix1
	 *            The first matrix.
	 *********************
	 */
	public static int[][] intMatricesMultiplex(int[][] paraMatrix1, int[][] paraMatrix2) {
		int[][] tempResultMatrix = new int[paraMatrix1.length][paraMatrix2[0].length];
		for (int i = 0; i < paraMatrix1.length; i++) {
			for (int j = 0; j < paraMatrix2[0].length; j++) {
				for (int k = 0; k < paraMatrix2.length; k++) {
					tempResultMatrix[i][j] += paraMatrix1[i][k] * paraMatrix2[k][j];
				}//Of for k
			}//Of for k
		}//Of for i
		
		return tempResultMatrix;
	}//of intMatricesMultiplex

	/**
	 *********************
	 * Add two matrices. 
	 *
	 * @param paraMatrix1
	 *            The first matrix.
	 *********************
	 */
	public static double[][] doubleMatricesAddition(double[][] paraMatrix1, double[][] paraMatrix2) {
		double[][] tempResultMatrix = new double[paraMatrix1.length][paraMatrix1[0].length];
		for (int i = 0; i < paraMatrix1.length; i++) {
			for (int j = 0; j < paraMatrix1[0].length; j++) {
				tempResultMatrix[i][j] += paraMatrix1[i][j] + paraMatrix2[i][j];
			}//Of for k
		}//Of for i
		
		return tempResultMatrix;
	}//of doubleMatricesAddition

	/**
	 *********************
	 * Multiply double two matrices. The number of rows of the second matrix is equal to the number of columns of the first.
	 * 
	 * @param paraMatrix1
	 *            The first matrix.
	 *********************
	 */
	public static double[][] doubleMatricesMultiplex(double[][] paraMatrix1, double[][] paraMatrix2) {
		double[][] tempResultMatrix = new double[paraMatrix1.length][paraMatrix2[0].length];
		for (int i = 0; i < paraMatrix1.length; i++) {
			for (int j = 0; j < paraMatrix2[0].length; j++) {
				for (int k = 0; k < paraMatrix2.length; k++) {
					tempResultMatrix[i][j] += paraMatrix1[i][k] * paraMatrix2[k][j];
				}//Of for k
			}//Of for k
		}//Of for i
		
		return tempResultMatrix;
	}//of doubleMatricesMultiplex

	/**
	 *********************
	 * Multiply two matrices. The number of rows of the second matrix is equal to the number of columns of the first.
	 * 
	 * @param paraMatrix1
	 *            The first matrix.
	 *********************
	 */
	public int[] computeVkS(int[] paraS, int paraK) {
		int[] tempResultSet = new int[weightedGraph.length];
		//Initialization V^0(S)
		for (int i = 0; i < paraS.length; i++) {
			tempResultSet[paraS[i]] = 1;
		}//Of for i
		
		if (paraK < 1) {
			return tempResultSet;
		}//Of if
		
		System.out.println("At the beginning, TempResultSet = " + Arrays.toString(tempResultSet));
		
		//Initialization V^1(S)
		//Add from connections
		for (int i = 0; i < paraS.length; i++) {
			for (int j = 0; j < tempResultSet.length; j++) {
				tempResultSet[j] += connections[paraS[i]][j];
			}//Of for j
		}//Of for i
		System.out.println("k = 1, TempResultSet = " + Arrays.toString(tempResultSet));

		int[][] tempCurrentConnections = connections;
		
		for (int i = 2; i <= paraK; i++) {
			tempCurrentConnections = intMatricesMultiplex(tempCurrentConnections, connections);
			for (int j = 0; j < paraS.length; j++) {
				for (int k = 0; k < tempResultSet.length; k++) {
					tempResultSet[k] += tempCurrentConnections[paraS[j]][k];
				}//Of for j
			}//Of for i
			System.out.println("k = " + i + ", TempResultSet = " + Arrays.toString(tempResultSet));
		}//Of for i

		return tempResultSet;
	}//Of computeVkS

	/**
	 *********************
	 * Compute the transition probabilities
	 * 
	 * @param paraMatrix1
	 *            The first matrix.
	 *********************
	 */
	public double[][] computeTransitionProbabilities() {
		transitionProbabilities = new double[weightedGraph.length][weightedGraph.length];
		double[] tempRowSum = new double[weightedGraph.length];
		//For each node
		for (int i = 0; i < weightedGraph.length; i++) {
			for (int j = 0; j < weightedGraph.length; j++) {
				tempRowSum[i] += weightedGraph[i][j];
			}//Of for j
			
			//No connection to any other node
			if (tempRowSum[i] == 0) {
				transitionProbabilities[i][i] = 1;
				continue;
			}//Of if
			
			for (int j = 0; j < weightedGraph.length; j++) {
				transitionProbabilities[i][j] = weightedGraph[i][j]/tempRowSum[i];
			}//Of for j
		}//Of for i
		
		System.out.println("The transitionProbabilities is: " + Arrays.deepToString(transitionProbabilities));
		
		return transitionProbabilities;
	}//Of computeTransitionProbabilities

	/**
	 *********************
	 * Compute the transition probabilities for k steps.
	 * 
	 * @param paraMatrix1
	 *            The first matrix.
	 *********************
	 */
	public double[][] computeKStepTransitionProbabilities(int paraK) {
		double[][] tempMatrix = transitionProbabilities;
		
		if (paraK == 1) {
			return tempMatrix;
		}//Of if
		
		for (int i = 1; i < paraK; i++) {
			tempMatrix = doubleMatricesMultiplex(tempMatrix, transitionProbabilities);
		}//Of for i
		
		return tempMatrix;
	}//Of computeKStepTransitionProbabilities

	/**
	 *********************
	 * Compute the transition probabilities for k steps.
	 * 
	 * @param paraMatrix1
	 *            The first matrix.
	 *********************
	 */
	public double[][] computeAtMostKStepTransitionProbabilities(int paraK) {
		double[][] resultMatrix = new double[transitionProbabilities.length][transitionProbabilities.length];
		double[][] tempMatrix = transitionProbabilities;
		
		if (paraK == 0) {
			return resultMatrix;
		}//Of if
		
		int i = 1;

		while(true) {
			resultMatrix = doubleMatricesAddition(resultMatrix, tempMatrix);
			i ++;
			if (i > paraK) {
				break;
			}//Of if
			tempMatrix = doubleMatricesMultiplex(tempMatrix, transitionProbabilities);
		}//Of for i
		
		return resultMatrix;
	}//Of computeMostKStepTransitionProbabilities
	
	/**
	 *********************
	 * Compute the exponential similarity between two vectors.
	 * 
	 * @param paraMatrix1
	 *            The first matrix.
	 *********************
	 */
	public static double computeExponentialSimilarity(double[] paraVector1, double[] paraVector2, int paraK) {
		double tempSimilarity = 0;
		
		tempSimilarity = manhattanDistance(paraVector1, paraVector2);
		tempSimilarity = Math.exp(2 * paraK - tempSimilarity) - 1;
		
		return tempSimilarity;
	}//Of computeExponentialSimilarity
	
	/**
	 *********************
	 * Compute the new graph after separation.
	 * 
	 * @param paraMatrix1
	 *            The first matrix.
	 *********************
	 */
	public double[][] ngSeparate(int paraK) {
		double[][] tempMatrix = new double[weightedGraph.length][weightedGraph.length];
		
		//Step 1. Compute aggregated transition matrix
		double[][] tempTransitionMatrix = computeAtMostKStepTransitionProbabilities(paraK);
		
		//Step 2. Compute new graph
		for (int i = 0; i < weightedGraph.length - 1; i++) {
			for (int j = i + 1; j < weightedGraph.length; j++) {
				//Do not add new edges
				if (weightedGraph[i][j] == 0) {
					continue;
				}//Of if
				
				tempMatrix[i][j] = computeExponentialSimilarity(tempTransitionMatrix[i], tempTransitionMatrix[j], paraK);
				tempMatrix[j][i] = tempMatrix[i][j];
			}//Of for j
		}//Of for i
		
		return tempMatrix;
	}//Of ngSeparate
}//Of class Walk

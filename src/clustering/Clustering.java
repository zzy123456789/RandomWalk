package clustering;

import java.io.FileReader;

import weka.core.Instances;

public class Clustering {
	/**
	 * The data set. The final attribute is the class. However in the process of
	 * clustering we cannot use it.
	 */
	protected Instances data;

	/**
	 * The clusters without redundant elements
	 */
	int[][] clusters;

	/**
	 * Manhattan distance
	 */
	public static final int MANHATTAN = 0;

	/**
	 * Euclidian distance
	 */
	public static final int EUCLIDEAN = 1;

	/**
	 *********************
	 * The constructor.
	 * 
	 * @param paraFilename
	 *            The data filename.
	 * 
	 * @return
	 *********************
	 */
	public Clustering(String paraFilename) {
		try {
			FileReader fileReader = new FileReader(paraFilename);
			data = new Instances(fileReader);
			fileReader.close();

			//System.out.println(data);
		} catch (Exception ee) {
			System.out.println("Error occurred while trying to read \'" + paraFilename + ".\r\n" + ee);
		} // Of try
	}// of the first constructor

	/**
	 *********************
	 * Compute the Manhattan distance between two data points. The decision
	 * attribute is ignored.
	 * 
	 * @return Manhattan distance
	 *********************
	 */
	protected double manhattanDistance(int paraI, int paraJ) {
		double tempDistance = 0;

		for (int i = 0; i < data.numAttributes() - 1; i++) {
			tempDistance += Math.abs(Math.abs(data.instance(paraI).value(i)) - Math.abs(data.instance(paraJ).value(i)));
		} // Of for i

		return tempDistance;
	}// Of manhattanDistance

	/**
	 *********************
	 * Compute the Manhattan distance between two data points. The decision
	 * attribute is ignored.
	 * 
	 * @return
	 *********************
	 */
	double manhattanDistance(int paraI, double[] paraArray) {
		double tempDistance = 0;

		for (int i = 0; i < data.numAttributes() - 1; i++) {
			tempDistance += Math.abs(Math.abs(data.instance(paraI).value(i)) - Math.abs(paraArray[i]));
		} // Of for i

		return tempDistance;
	}// Of manhattanDistance
	
	/**
	 *********************
	 * Compute the Manhattan distance between two arrays. The last element is
	 * ignored.
	 * 
	 * @return
	 *********************
	 */
	public static double manhattanDistance(double[] paraArray1, double[] paraArray2) {
		double tempDistance = 0;

		for (int i = 0; i < paraArray1.length - 1; i++) {
			tempDistance += Math.abs(Math.abs(paraArray1[i]) - Math.abs(paraArray2[i]));
		} // Of for i

		return tempDistance;
	}// Of manhattanDistance

	/**
	 *********************
	 * Compute the Euclidean distance between two data points. The decision
	 * attribute is ignored.
	 * 
	 * @return
	 *********************
	 */
	double euclideanDistance(int paraI, int paraJ) {
		double tempDistance = 0;
		double tempValue = 0;

		for (int i = 0; i < data.numAttributes() - 1; i++) {
			tempValue = Math.abs(Math.abs(data.instance(paraI).value(i))) - Math.abs(data.instance(paraJ).value(i));
			tempDistance += tempValue * tempValue;
		} // Of for i

		return Math.sqrt(tempDistance);
	}// Of euclidianDistance

	/**
	 *********************
	 * Compute the Euclidean distance between one data points and one virtual
	 * data point.
	 * 
	 * @return
	 *********************
	 */
	double euclideanDistance(int paraI, double[] paraArray) {
		double tempDistance = 0;
		double tempValue = 0;

		for (int i = 0; i < data.numAttributes() - 1; i++) {
			tempValue = Math.abs(Math.abs(data.instance(paraI).value(i))) - Math.abs(paraArray[i]);
			tempDistance += tempValue * tempValue;
		} // Of for i

		return Math.sqrt(tempDistance);
	}// Of euclidianDistance

	/**
	 *********************
	 * Compute the Euclidean distance between one data points and one virtual
	 * data point.
	 * 
	 * @return
	 *********************
	 */
	public static double euclideanDistance(double[] paraArray1, double[] paraArray2) {
		double tempDistance = 0;
		double tempValue = 0;

		for (int i = 0; i < paraArray1.length - 1; i++) {
			tempValue = Math.abs(Math.abs(paraArray1[i])) - Math.abs(paraArray2[i]);
			tempDistance += tempValue * tempValue;
		} // Of for i

		return Math.sqrt(tempDistance);
	}// Of euclidianDistance

	/**
	 *********************
	 * Compute the purity of the clustering result. For each block, choose the
	 * majority class label.
	 * 
	 * @return The purity
	 *********************
	 */
	public double computePurity() {
		double tempCorrect = 0;

		int tempLabelIndex = data.numAttributes() - 1;
		int tempNumLabels = data.attribute(tempLabelIndex).numValues();
		System.out.println("Number of labels: " + tempNumLabels);
		// For each block
		int[] tempLabelCounts;
		int tempLabel;
		int tempMajority;
		for (int i = 0; i < clusters.length; i++) {
			tempLabelCounts = new int[tempNumLabels];
			for (int j = 0; j < clusters[i].length; j++) {
				tempLabel = (int)Math.abs( data.instance(clusters[i][j]).value(tempLabelIndex));
				tempLabelCounts[tempLabel]++;
			} // Of for j

			// The majority for this block
			tempMajority = 0;
			for (int j = 0; j < tempNumLabels; j++) {
				if (tempMajority < tempLabelCounts[j]) {
					tempMajority = tempLabelCounts[j];
				} // Of if
			} // Of for j

			tempCorrect += tempMajority;
		} // Of for i

		return tempCorrect / data.numInstances();
	}// Of computePurity

	/**
	 *********************
	 * Judge whether or not two matrices are equal. We assume that these
	 * matrices have the same size and simplify the method.
	 * 
	 * @return yes or no
	 *********************
	 */
	public static boolean matricesEqual(double[][] paraMatrix1, double[][] paraMatrix2) {
		for (int i = 0; i < paraMatrix1.length; i++) {
			for (int j = 0; j < paraMatrix1[i].length; j++) {
				if (paraMatrix1[i][j] != paraMatrix2[i][j]) {
					return false;
				} // Of if
			} // Of for j
		} // Of for i

		// Pass all tests.
		return true;
	}// Of matricesEqual

}// Of class Clustering

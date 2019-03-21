package clustering;

import java.util.Arrays;

//import weka.core.Instances;

public class KMeans extends Clustering {

	/**
	 *********************
	 * The constructor. Invoke the constructor of the superclass directly.
	 * 
	 * @param paraFilename
	 *            The data filename.
	 *********************
	 */
	public KMeans(String paraFilename) {
		super(paraFilename);
	}// of the first constructor

	/**
	 *********************
	 * KMeans algorithm.
	 * 
	 * @param paraDistanceMeasure
	 *            The distance measure, Manhattan or Euclidean
	 * @return
	 *********************
	 */
	public int[][] kMeans(int paraK, int paraDistanceMeasure) {
		int[][] tempClusters = new int[paraK][data.numInstances()];
		int[] tempClusterSizes = new int[paraK];
		double[][] tempCenters = new double[paraK][data.numAttributes()];
		// Used to judge whether or not the centers are stable
		double[][] tempLastRoundCenters = new double[paraK][data.numAttributes()];

		// Step 1. Select k centers randomly.
		for (int i = 0; i < paraK; i++) {
			for (int j = 0; j < data.numAttributes(); j++) {
				tempCenters[i][j] = Math.abs(data.instance(i).value(j));
				tempLastRoundCenters[i][j] = tempCenters[i][j];
//				System.out.print("" + tempCenters[i][j] + ", ");
			} // Of for j
//			System.out.println();
		} // Of for i

		// Step 2. Cluster and compute new centers.
		int tempRound = 0;
		while (true) {
			// Step 2.1 Allocate the points to the new clusters.
			double tempMaxDistance;
			int tempClusterIndex;
			// Reinitialize the cluster sizes
			for (int i = 0; i < tempClusterSizes.length; i++) {
				tempClusterSizes[i] = 0;
			} // Of for i

			// For each instance
			for (int i = 0; i < data.numInstances(); i++) {
				tempMaxDistance = Double.MAX_VALUE;
				tempClusterIndex = -1;
				// For each centers
				for (int j = 0; j < paraK; j++) {
					// Distance selection
					double tempDistance = 0;
					if (paraDistanceMeasure == MANHATTAN) {
						tempDistance = manhattanDistance(i, tempCenters[j]);
					} else {
						tempDistance = euclideanDistance(i, tempCenters[j]);
					} // Of if
					if (tempDistance < tempMaxDistance) {
						tempMaxDistance = tempDistance;
						tempClusterIndex = j;
					} // Of if
				} // Of for j

				tempClusters[tempClusterIndex][tempClusterSizes[tempClusterIndex]] = i;
				tempClusterSizes[tempClusterIndex]++;
			} // Of for i

			// Print the clusters
//			System.out.println("\r\n***********Round " + tempRound);
			for (int i = 0; i < paraK; i++) {
				for (int j = 0; j < tempClusterSizes[i]; j++) {
//					System.out.print("," + tempClusters[i][j]);
				} // Of for j
//				System.out.println();
			} // Of for i

			// Step 2.2 Compute new centers.
			// Reinitialze them
			for (int i = 0; i < tempCenters.length; i++) {
				for (int j = 0; j < tempCenters[i].length; j++) {
					tempCenters[i][j] = 0;
				} // Of for j
			} // Of for j

			for (int i = 0; i < paraK; i++) {
				for (int j = 0; j < tempClusterSizes[i]; j++) {
					for (int k = 0; k < data.numAttributes(); k++) {
						tempCenters[i][k] += Math.abs(data.instance(tempClusters[i][j]).value(k)) / tempClusterSizes[i];
					} // Of for k
				} // Of for j

//				System.out.println("New center #" + i);
				for (int j = 0; j < tempCenters[i].length; j++) {
//					System.out.print("," + tempCenters[i][j]);
				} // Of for j
//				System.out.println();
			} // Of for i

			// Is it stable?
			if (matricesEqual(tempCenters, tempLastRoundCenters)) {
				break;
			} // Of if

			// Copy to tempLastRoundCenters
			for (int i = 0; i < paraK; i++) {
				for (int j = 0; j < data.numAttributes(); j++) {
					tempLastRoundCenters[i][j] = tempCenters[i][j];
				} // Of for j
			} // Of for i

			tempRound++;
		} // Of while

		clusters = new int[paraK][];
		for (int i = 0; i < paraK; i++) {
			// Determine the size of this block
			clusters[i] = new int[tempClusterSizes[i]];

			// Copy
			for (int j = 0; j < tempClusterSizes[i]; j++) {
				clusters[i][j] = tempClusters[i][j];
			} // Of for j
		} // Of for i

		// Print the clusters
//		System.out.println("This is the final results");
//		System.out.println(Arrays.deepToString(clusters));
		
		//the quality of clustering
		double[] avg = new double[paraK];
		double[] diam = new double[paraK];
		double tempCount = 0;
		double temp = 0;
		for (int i = 0; i < paraK; i++) {
			for (int j = 0; j <  clusters[i].length - 1; j++) {
				for (int k = j + 1; k < clusters[i].length; k++) {
					if (paraDistanceMeasure == MANHATTAN) {
						temp = manhattanDistance(j, k);
					} else {
						temp = euclideanDistance(j, k);
					} // Of if
					if(temp > diam[i]) {
						diam[i] = temp;
					}
					tempCount += temp;
				}//Of for k
			}//Of for j
			avg[i] = 2/(double)(tempClusterSizes[i]*(tempClusterSizes[i] - 1)) * tempCount;
			tempCount = 0;
		}//Of for i
//		System.out.println("avg" + Arrays.toString(avg));
//		System.out.println("diam" + Arrays.toString(diam));
		
		double[][] dCen = new double[paraK][paraK];
		for (int i = 0; i < dCen.length; i++) {
			for (int j = 0; j < dCen.length; j++) {
				if (paraDistanceMeasure == MANHATTAN) {
					dCen[i][j] = manhattanDistance(tempLastRoundCenters[i], tempLastRoundCenters[j]);
				} else {
					dCen[i][j] = euclideanDistance(tempLastRoundCenters[i], tempLastRoundCenters[j]);
				} // Of if
			}//Of for j
		}//Of for i
//		System.out.println("dCen " + Arrays.deepToString(dCen));
		
		double DBI = 0;
		double tempDBI = 0;
		double tempAvg = 0;
		for (int i = 0; i < paraK; i++) {
			double maxDBI = 0;
			for (int j = 0; j < paraK; j++) {
				if(dCen[i][j] == 0) {
					continue;
				}//Of if
				tempAvg = (avg[i] + avg[j]) / dCen[i][j];
				if(tempAvg > maxDBI) {
					maxDBI = tempAvg;
				}//Of if
			}//Of for j
			tempDBI += maxDBI;
		}//Of for i
		DBI = tempDBI / paraK;
		System.out.println(" DBI = " + DBI);
		
		double[][] dmin = new double[paraK][paraK];
		double distance = 0;
		for (int i = 0; i < paraK; i++) {
			for (int j = 0; j < paraK; j++) {
				if(dCen[i][j] == 0) {
					continue;
				}//Of if
				double minDistance = 10;
				for (int q = 0; q < tempClusterSizes[i]; q++) {
					for (int p = 0; p < tempClusterSizes[j]; p++) {
						if (paraDistanceMeasure == MANHATTAN) {
							distance = manhattanDistance(clusters[i][q], clusters[j][p]);
						} else {
							distance = euclideanDistance(clusters[i][q], clusters[j][p]);
						} // Of if
						
						if(distance < minDistance) {
							minDistance = distance;
						}
					}//Of for p
				}//Of for q
				
				dmin[i][j] = minDistance;
			}//Of for j
		}//Of for i
//		System.out.println(Arrays.deepToString(dmin));
		
		double DI = 10;
		double[] DIList = new double[paraK];
		double tempDI = 0;
		double maxDiam = 0;
		for (int i = 0; i < paraK; i++) {
			if(diam[i] > maxDiam) {
				maxDiam = diam[i];
			}//Of if
		}//Of for i
//		System.out.println("maxDiam" + maxDiam);
		for (int i = 0; i < paraK; i++) {
			double minDI = 10;
			for (int j = 0; j < paraK; j++) {
				if(dCen[i][j] == 0) {
					continue;
				}//Of if
				tempDI = dmin[i][j] / maxDiam;
				if(tempDI < minDI) {
					minDI = tempDI;
				}//Of if
			}//Of for j
			DIList[i] = minDI;
		}//Of for i
		for (int i = 0; i < paraK; i++) {
			if(DIList[i] < DI) {
				DI = DIList[i];
			}//Of if
		}//Of for i
		
//		System.out.println("DI " + Arrays.toString(DIList));
		System.out.println("DI = " + DI);

		return clusters;
	}// Of kMeans

}// Of class KMeans

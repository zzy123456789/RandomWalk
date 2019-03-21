package matrix;

/**
 *********************
 * A inner class.
 *********************
 */
public class Triple {
	public int column;
	public double weight;
	public Triple next;

	public Triple() {
		column = 0;
		weight = 0;
		next = null;
	}// Of the constructor

	public Triple(int paraColumn, double paraWeight, Triple paraNext) {
		column = paraColumn;
		weight = paraWeight;
		next = paraNext;
	}// Of the constructor

	/**
	 *********************
	 * Multiply two arrays.
	 *********************
	 */
	public static double multiply(Triple paraHeader1, Triple paraHeader2) {
		double tempWeightSum = 0;

		Triple tempTriple1 = paraHeader1.next;
		Triple tempTriple2 = paraHeader2.next;

		while ((tempTriple1 != null) && (tempTriple2 != null)) {
			if (tempTriple1.column < tempTriple2.column) {
				tempTriple1 = tempTriple1.next;
			} else if (tempTriple2.column < tempTriple1.column) {
				tempTriple2 = tempTriple2.next;
			} else {
				tempWeightSum += tempTriple1.weight * tempTriple2.weight;
				tempTriple1 = tempTriple1.next;
				tempTriple2 = tempTriple2.next;
			} // Of if
		} // Of while

		return tempWeightSum;
	}// Of multiply

	/**
	 *********************
	 * add two arrays.
	 *********************
	 */
	public static Triple add(Triple paraHeader1, Triple paraHeader2) {
		Triple resultHeader = new Triple();
		Triple tempTail = resultHeader;
		Triple tempTriple;

		Triple tempTriple1 = paraHeader1.next;
		Triple tempTriple2 = paraHeader2.next;

		while ((tempTriple1 != null) && (tempTriple2 != null)) {
			if (tempTriple1.column < tempTriple2.column) {
				// Copy the triple of the first array
				tempTriple = new Triple();
				tempTriple.column = tempTriple1.column;
				tempTriple.weight = tempTriple1.weight;

				// Insert to the new array
				tempTail.next = tempTriple;
				tempTail = tempTriple;

				tempTriple1 = tempTriple1.next;
			} else if (tempTriple2.column < tempTriple1.column) {
				// Copy the triple of the second array
				tempTriple = new Triple();
				tempTriple.column = tempTriple2.column;
				tempTriple.weight = tempTriple2.weight;

				// Insert to the new array
				tempTail.next = tempTriple;
				tempTail = tempTriple;

				tempTriple2 = tempTriple2.next;
			} else {
				// Compute the sum
				tempTriple = new Triple();
				tempTriple.column = tempTriple1.column;
				tempTriple.weight = tempTriple1.weight + tempTriple2.weight;

				// Insert to the new array
				tempTail.next = tempTriple;
				tempTail = tempTriple;

				tempTriple1 = tempTriple1.next;
				tempTriple2 = tempTriple2.next;
			} // Of if
		} // Of while

		// Copy the remaining part of the first array
		while (tempTriple1 != null) {
			// Copy the triple of the first array
			tempTriple = new Triple();
			tempTriple.column = tempTriple1.column;
			tempTriple.weight = tempTriple1.weight;

			// Insert to the new array
			tempTail.next = tempTriple;
			tempTail = tempTriple;

			tempTriple1 = tempTriple1.next;
		} // Of while

		// Copy the remaining part of the second array
		while (tempTriple2 != null) {
			// Copy the triple of the first array
			tempTriple = new Triple();
			tempTriple.column = tempTriple2.column;
			tempTriple.weight = tempTriple2.weight;

			// Insert to the new array
			tempTail.next = tempTriple;
			tempTail = tempTriple;

			tempTriple2 = tempTriple2.next;
		} // Of while

		return resultHeader.next;
	}// Of add

	/**
	 *********************
	 * The manhattan distance between two arrays.
	 *********************
	 */
	public static double manhattan(Triple paraHeader1, Triple paraHeader2) {
		double resultValue = 0;

		Triple tempTriple1 = paraHeader1.next;
		Triple tempTriple2 = paraHeader2.next;

		while ((tempTriple1 != null) && (tempTriple2 != null)) {
			if (tempTriple1.column < tempTriple2.column) {
				resultValue += tempTriple1.weight;

				tempTriple1 = tempTriple1.next;
			} else if (tempTriple2.column < tempTriple1.column) {
				resultValue += tempTriple2.weight;

				tempTriple2 = tempTriple2.next;
			} else {
				resultValue += Math.abs(tempTriple1.weight - tempTriple2.weight);
				// Compute the sum

				tempTriple1 = tempTriple1.next;
				tempTriple2 = tempTriple2.next;
			} // Of if
		} // Of while

		// Add the remaining part of the first array
		while (tempTriple1 != null) {
			resultValue += tempTriple1.weight;

			tempTriple1 = tempTriple1.next;
		} // Of while

		// Add the remaining part of the second array
		while (tempTriple2 != null) {
			resultValue += tempTriple2.weight;

			tempTriple2 = tempTriple2.next;
		} // Of while

		return resultValue;
	}// Of manhattan

	public String toString() {
		String resultString = "";
		Triple tempReference = this;
		while (tempReference != null) {
			resultString += tempReference.column;
			resultString += ", ";
			resultString += tempReference.weight;
			resultString += "; ";
			tempReference = tempReference.next;
		} // Of while
		return resultString;
	}// Of toString
}// Of triple

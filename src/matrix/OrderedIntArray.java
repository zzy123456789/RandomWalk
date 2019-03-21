package matrix;

import java.util.Arrays;

//import clustering.CompressedSymmetricMatrix.Triple;

public class OrderedIntArray {
	int[] data;
	
	int space;
	
	int size;
	
	/**
	 *********************
	 * The constructor.
	 * 
	 * @param paraFilename
	 *********************
	 */
	public OrderedIntArray(int paraSpace) {
		space = paraSpace;
		size = 0;
		data = new int[space];
	}//Of the constructor
	
	/**
	 *********************
	 * Insert a value if it does not exist.
	 * 
	 *********************
	 */
	public boolean insert(int paraValue){
		for (int i = 0; i < size; i++) {
			if (paraValue == data[i]) {
				return false;
			} else if (paraValue < data[i]) {
				//Now insert
				for (int j = size; j > i; j--) {
					data[j] = data[j - 1];
				}//Of for j
				data[i] = paraValue;
				size ++;
				
				return true;
			}//Of if
		}//Of if
		
		//It should be added to the tail
		data[size] = paraValue;
		size ++;
		
		return true;
	}//Of insert

	/**
	 *********************
	 * Reset.
	 *********************
	 */
	public void reset(){
		size = 0;
	}//Of insert
	
	/**
	 *********************
	 * Reset.
	 *********************
	 */
	public int[] toCompactArray(){
		int[] resultArray = new int[size];
		for (int i = 0; i < size; i++) {
			resultArray[i] = data[i];
		}//Of for i
		
		return resultArray;
	}//Of insert
	
	public String toString() {
		String resultString = "";
		for (int i = 0; i < size; i ++) {
			resultString += ", " + data[i];
		} // Of for
		return resultString;
	}// Of toString

	public static void main(String args[]) {
		OrderedIntArray tempArray = new OrderedIntArray(10);
		tempArray.insert(3);
		tempArray.insert(4);
		tempArray.insert(1);
		
		System.out.println(Arrays.toString(tempArray.data));
	}//Of main
	
}//Of class OrderedIntArray

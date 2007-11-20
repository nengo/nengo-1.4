package ca.neo.ui;

import java.util.ArrayList;
import java.util.Hashtable;

public class Test {

	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ArrayList<String> testArray = new ArrayList<String>();

		testArray.add("test");

		String testStr = "test";
		
		if (testArray.contains(testStr)) {
			System.out.println("BLAH asdhsghf!");
		}

		Hashtable<String, Object> nameLUT = new Hashtable<String, Object>();
		nameLUT.put("test", new Object());
		
		
		if (nameLUT.get("test2") != null) {
			System.out.println("BLAH2!");
		}
		
//		System.out.println("BLAH2!");

	}
}

package uk.ac.ed.inf.powergrab;

import java.util.regex.*;
//import java.util.;

public class InputValidator {
	
	public static boolean isValid(String[] args) {
		
		if (args.length != 7) {
			System.out.println("Need 7 arguments to play PowerGrab");
			return false;
		}
		
		boolean validInput = true;
		
//		boolean isDay = args[0].matches(regex)
		
		return validInput;
	}
	
}

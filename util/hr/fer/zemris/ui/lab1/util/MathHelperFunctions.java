package hr.fer.zemris.ui.lab1.util;

import java.util.Random;

/**
 * Klasa pomoćnih matematičkih funkcija
 */
public class MathHelperFunctions {

	/** Generator slučajnih brojeva */
	private static Random random = new Random();
	
	/** Broj slučajnih realizacija prije resetiranja generatora */
	private static final int COMPUTATIONS_BEFORE_RESET = 5000;
	
	/** Koliko nam je još ostalo slučajnih realizacija prije resetiranja */
	private static int computeBeforeReset = COMPUTATIONS_BEFORE_RESET;
	
	/**
	 * Provjerava da li treba i resetira generator slučajnih brojeva
	 */
	private static void resetRandomNumberGeneratorIfNecessary() {
		if(computeBeforeReset == 0) {
			random = new Random();
			computeBeforeReset = COMPUTATIONS_BEFORE_RESET;
		} else {
			computeBeforeReset--;
		}
	}
	
	/**
	 * Vraća realizaciju slučajne varijable uniformne gustoće vjerojatnosti
	 * na intervalu (a,b)
	 * @param a početak intervala
	 * @param b kraj intervala
	 * @return realizacija slučajne varijable
	 */
	public static float getUniformValueFromInterval(float a, float b) {
		resetRandomNumberGeneratorIfNecessary();
		
		return random.nextFloat() * (b-a) + a;
	}

	/**
	 * Vraća realizaciju slučajne varijable normalne gustoće vjerojatnosti s
	 * očekivanjem (a+b)/2 i disperzijom (b-a)
	 * @param a prva vrijednost
	 * @param b druga vrijednost
	 * @return realizacija slučajne varijable
	 */
	public static float getDispersedMedian(float a, float b) {
		resetRandomNumberGeneratorIfNecessary();
		
		return (a + b)/2 + (float)random.nextGaussian()*(b - a);
	}
	
	/**
	 * Vraća realzicaju slučajne varijable normalne gustoće vjerojatnosti s
	 * očekivanjem e i disperzijom d
	 * @param e očekivanje
	 * @param d disperzija
	 * @return realizacija slučajne varijable
	 */
	public static float getGaussianValue(float e, float d) {
		resetRandomNumberGeneratorIfNecessary();
		
		return e + (float)random.nextGaussian()*d;
	}

}

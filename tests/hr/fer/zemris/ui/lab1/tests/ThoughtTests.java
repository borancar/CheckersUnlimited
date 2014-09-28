package hr.fer.zemris.ui.lab1.tests;

import hr.fer.zemris.ui.lab1.Thought;

import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Testira metoda razreda misli o potezu.
 */
public class ThoughtTests {

	/** Generator slučajnih vrijednosti */
	private Random random;
	
	/** Broj testova */
	private final int NUMBER_OF_TESTS = 1000000;
	
	/**
	 * Postavlja sve bitno za svaki test zasebno.
	 */
	@Before
	public void init() {
		this.random = new Random();
	}
	
	/**
	 * Testira da li ispravno radi uspoređivanje 2 podatka o mislima o potezu.
	 */
	@Test
	public void testCompareTo() {
		for (int i = 0; i < NUMBER_OF_TESTS; i++) {
			int moveAValue = random.nextInt();
			int moveBValue = random.nextInt();
			
			Thought thoughtA = new Thought(moveAValue);
			Thought thoughtB = new Thought(moveBValue);
			
			Assert.assertEquals("Uspoređivanje ne radi ispravno.", Math.signum((double)moveAValue - moveBValue), Math.signum(thoughtA.compareTo(thoughtB)));
		}
	}
}

package hr.fer.zemris.ui.lab1.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import Simulator.CheckerPlayer;

import hr.fer.zemris.ui.lab1.CheckerGame;
import hr.fer.zemris.ui.lab1.NaiveHeuristic;

/**
 * Testira igrača šaha dajući mu određene situacije i očekujući od njega specifične
 * odgovore (poteze koje radi).
 */
public class CheckerPlayerTests {
	
	/** Igrač šaha */
	private CheckerPlayer checkerPlayer;
	
	/**
	 * Postavlja ploču za svaki test zasebno.
	 */
	@Before
	public void init() {
		this.checkerPlayer = new CheckerPlayer();
		this.checkerPlayer.setHeuristic(new NaiveHeuristic(1));
		this.checkerPlayer.TimePerMove = 200;
	}
	
	/**
	 * Testira situaciju gdje je bijeli igrač na potezu i može se uvući u gabulu gdje će ga pojesti.
	 * Prikaz ploče:
	 * . - prazno polje
	 * c - crni pijun
	 * b - bijeli pijun
	 * 
	 * 8 . . . . c . . .
	 * 7 . . . . . . . .
	 * 6 . . b . . . . .
	 * 5 . . . . . . . .
	 * 4 . . . . . . . .
	 * 3 . . . . . . . .
	 * 2 . . . . . . . .
	 * 1 . . . . . . . . 
	 *   a b c d e f g h
	 */
	@Test
	public void testAPossibleAmbushSituation() {
		checkerPlayer.Color = CheckerGame.COLOR_WHITE;
		
		Assert.assertEquals("AI igrač nije dobro promislio o potezu!", "6C 7B", checkerPlayer.Move("6CBP 8ECP"));
	}
	
	/**
	 * Testira situaciju gdje je bijeli igrač na potezu i može pojesti 2 protivnička pijuna.
	 * Prikaz ploče:
	 * . - prazno polje
	 * c - crni pijun
	 * b - bijeli pijun
	 * 
	 * 8 . . . . . . . .
	 * 7 . c . C . . . .
	 * 6 . . b . . . . .
	 * 5 . . . . . . . .
	 * 4 . . . . . . . .
	 * 3 . . . . . . . .
	 * 2 . . . . . . . .
	 * 1 . . . . . . . . 
	 *   a b c d e f g h
	 */
	@Test
	public void testSimpleJumpSituation() {
		checkerPlayer.Color = CheckerGame.COLOR_WHITE;
		
		Assert.assertEquals("AI igrač nije dobro promislio o potezu!", "6C 8E", checkerPlayer.Move("7BCP 6CBP 7DCK"));
	}
	
	/**
	 * Testira situaciju gdje je bijeli igrač na potezu i ne bi smio pohlepno jesti
	 * Prikaz ploče:
	 * . - prazno polje
	 * c - crni pijun
	 * b - bijeli pijun
	 *   a b c d e f g h
	 * 8 . . . . c . . . 8
	 * 7 . . . . . c . . 7 
	 * 6 . . . . . . . . 6
	 * 5 . . . . . c . . 5
	 * 4 . . . . . . . . 4
	 * 3 . c . c . . . . 3
	 * 2 . . b . . . . . 2
	 * 1 . . . . . . . . 1
	 *   a b c d e f g h
	 */
	@Test
	public void testDoNotBeGreedy() {
		checkerPlayer.Color = CheckerGame.COLOR_WHITE;
		
		Assert.assertEquals("AI igrač nije dobro promislio o potezu!", "2C 4A", checkerPlayer.Move("3BCP 2CBP 3DCP 5FCP 7FCP 8ECP"));
	}
	
	/**
	 * Testira situaciju gdje je bijeli igrač na potezu i morao bi izabrati pravilan smjer 
	 * ulančanog uzimanja
	 * Prikaz ploče:
	 * . - prazno polje
	 * c - crni pijun
	 * b - bijeli pijun
	 *   a b c d e f g h
	 * 8 . . . . . . . . 8
	 * 7 . . . . . . . . 7 
	 * 6 . . . . . . . . 6
	 * 5 . . . c . . . . 5
	 * 4 . . . . c . c . 4
	 * 3 . . . . . . . . 3
	 * 2 . . c . c . . . 2
	 * 1 . . . b . . . . 1
	 *   a b c d e f g h
	 */
	@Test 
	public void testEatRight() {
		checkerPlayer.Color = CheckerGame.COLOR_WHITE;
		
		Assert.assertEquals("AI nije dobro odigrao!", "1D 3F 5H", checkerPlayer.Move("1DBP 2CCP 2ECP 4ECP 4GCP 5DCP"));
	}
	
	/**
	 * Testira situaciju gdje je bijeli kralj na potezu i morao bi igrati pravilno 
	 * Prikaz ploče:
	 * . - prazno polje
	 * c - crni pijun
	 * b - bijeli pijun
	 *   a b c d e f g h
	 * 8 . . . B . . . . 8
	 * 7 . . c . c . . . 7 
	 * 6 . . . . . . . . 6
	 * 5 c . . . . . . . 5
	 * 4 . . . . . . . . 4
	 * 3 . . . . . . . . 3
	 * 2 . . . . . . . . 2
	 * 1 . . . . . . . . 1
	 *   a b c d e f g h
	 */
	
	@Test
	public void testWhiteKingMove () {
		checkerPlayer.Color = CheckerGame.COLOR_WHITE;
		
		Assert.assertEquals("Beli kralj se loše lreće","8D 6B",checkerPlayer.Move("8DBK 7CCP 7ECP 5ACP"));
	}
	
		
	/**
	 * Testira situaciju gdje je bijeli igrač na potezu i može jesti više figurica
	 * Prikaz ploče:
	 * . - prazno polje
	 * c - crni pijun
	 * b - bijeli pijun
	 *   a b c d e f g h
	 * 8 . . . . . . . . 8
	 * 7 . . . . . c . . 7 
	 * 6 . . . . . . . . 6
	 * 5 . . . . . c . . 5
	 * 4 . . . . . . . . 4
	 * 3 . c . c . . . . 3
	 * 2 . . b . . . . . 2
	 * 1 . . . . . . . . 1
	 *   a b c d e f g h
	 */
	@Test
	public void testCanEatMultiple() {
		checkerPlayer.Color = CheckerGame.COLOR_WHITE;
		
		Assert.assertEquals("AI igrač nije dobro promislio o potezu!", "2C 4E 6G 8E", checkerPlayer.Move("3BCP 2CBP 3DCP 5FCP 7FCP"));
		
	}
	
	/**
	 * Testira zaustavljanje skanja ako dođe do okrunjenja
	 * Prikaz ploče:
	 * . - prazno polje
	 * c - crni pijun
	 * b - bijeli pijun
	 *   a b c d e f g h
	 * 8 . . . . . . . . 8
	 * 7 . . . c . c . . 7 
	 * 6 . . . . . . b . 6
	 * 5 . . . . . . . . 5
	 * 4 . . . . . . . . 4
	 * 3 . . . . . . . . 3
	 * 2 . . . . . . . . 2
	 * 1 . . . . . . . . 1
	 *   a b c d e f g h
	 */
	@Test
	public void testZaustaviSkakanjeAkoDodjeDoOkrunjenja() {
		checkerPlayer.Color = CheckerGame.COLOR_WHITE;
		
		Assert.assertEquals("AI igrač je napravio nelegalan potez!", "6G 8E", checkerPlayer.Move("6GBP 7DCP 7FCP"));
		
	}
	
	/**
	 * Testira igrača na popunjenoj ploči
	 * Prikaz ploče:
	 * . - prazno polje
	 * c - crni pijun
	 * b - bijeli pijun
	 *   a b c d e f g h
	 * 8 c . c . c . c . 8
	 * 7 . c . c . c . c 7 
	 * 6 c . c . c . c . 6
	 * 5 . . . . . . . . 5
	 * 4 . . . . . . . . 4
	 * 3 . b . b . b . b 3
	 * 2 b . b . b . b . 2
	 * 1 . b . b . b . b 1
	 *   a b c d e f g h
	 */
	@Test
	public void testFullBoardFirstMove() {
		checkerPlayer.Color = CheckerGame.COLOR_WHITE;
		checkerPlayer.TimePerMove = 3000;
		
		Assert.assertEquals("AI igrač je napravio nelegalan potez!", "3H 4G", checkerPlayer.Move("1BBP 1DBP 1FBP 1HBP 2ABP 2CBP 2EBP 2GBP 3BBP 3DBP 3FBP 3HBP 6ACP 6CCP 6ECP 6GCP 7BCP 7DCP 7FCP 7HCP 8ACP 8CCP 8ECP 8GCP"));
		
	}
}


package hr.fer.zemris.ui.lab1.tests;

import hr.fer.zemris.ui.lab1.CheckerGame;
import hr.fer.zemris.ui.lab1.NaiveHeuristic;
import hr.fer.zemris.ui.lab1.Square;

import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Testira naivnu heuristiku.
 */
public class NaiveHeuristicTests {

	/** Generator slučajnih vrijednosti */
	private Random random;
	
	/** Ploča na kojoj se obavlja testiranje */
	private Square[][] board;
	
	/**
	 * Inicijalizira sve potrebno za testiranje prije svakog testa
	 */
	@Before
	public void init() {
		this.random = new Random();
		this.board = new Square[8][8];
		
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				board[row][column] = Square.EMPTY;
			}
		}
	}
	
	/**
	 * Testira da li heuristika pravilno ocjenjuje stanje na ploči
	 */
	@Test
	public void testBoardValue() {
		final int MAX_BLACK_FIGURES = 5;
		final int MAX_WHITE_FIGURES = 3;
		
		int nBlack = random.nextInt(MAX_BLACK_FIGURES - 1) + 1;
		int nWhite = random.nextInt(MAX_WHITE_FIGURES - 1) + 1;
		
		for (int i = 0; i < nBlack; i++) {
			int row, column;
			
			do {
				row = random.nextInt(8);
				column = random.nextInt(8);
			} while(!board[row][column].isEmpty());
			
			board[row][column] = Square.BLACK_PAWN;
		}
		
		for (int i = 0; i < nWhite; i++) {
			int row, column;
			
			do {
				row = random.nextInt(8);
				column = random.nextInt(8);
			} while(!board[row][column].isEmpty());
			
			board[row][column] = Square.WHITE_PAWN;			
		}
		
		Assert.assertEquals("Heuristika nije ispravna!", nBlack - nWhite, new NaiveHeuristic().valueState(board, CheckerGame.COLOR_BLACK));
		Assert.assertEquals("Heuristika nije ispravna!", nWhite - nBlack, new NaiveHeuristic().valueState(board, CheckerGame.COLOR_WHITE));
	}
	
	/**
	 * Testira da li heuristička funkcija pravilno ocjenjuje vrijednost kralja
	 */
	@Test
	public void testKingValue() {
		final int MAX_BLACK_FIGURES = 5;
		final int MAX_KING_VALUE = 10;
		
		int nBlack = random.nextInt(MAX_BLACK_FIGURES - 1) + 1;
		int kingValue = random.nextInt(MAX_KING_VALUE - 1) + 1;
		
		for (int i = 0; i < nBlack; i++) {
			int row, column;
			
			do {
				row = random.nextInt(8);
				column = random.nextInt(8);
			} while(!board[row][column].isEmpty());
			
			board[row][column] = Square.BLACK_KING;
		}
		

		
		Assert.assertEquals("Heuristika nije ispravna!", nBlack*kingValue, new NaiveHeuristic(kingValue).valueState(board, false));		
	}
}

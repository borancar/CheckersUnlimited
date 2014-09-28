package hr.fer.zemris.ui.lab1.utilities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Baza podataka završnih poteza
 */
public class EndgameDatabase implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** Ploča na kojoj se odvija igra. Ploča je prikazana kao [redak][stupac] matrica*/
	Map<Board, EndgameDatabaseElement> data;

	/**
	 * Defaultni konstruktor koji stvara bazu podataka
	 */
	public EndgameDatabase() {
		this.data = new HashMap<Board, EndgameDatabaseElement>();
	}
	
	/**
	 * Dohvaća element pohranjen za stanje ploče
	 * @param board ploča
	 * @return element završne igre
	 */
	public EndgameDatabaseElement get(Board board) {
		return this.data.get(board);
	}
	
	/**
	 * Postavlja element završne igre za stanje ploče
	 * @param board stanje ploče
	 * @param endgameDatabaseElement element završne igre
	 */
	public void put(Board board, EndgameDatabaseElement endgameDatabaseElement) {
		this.data.put(board, endgameDatabaseElement);
	}
}

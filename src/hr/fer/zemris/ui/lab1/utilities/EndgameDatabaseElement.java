package hr.fer.zemris.ui.lab1.utilities;

import java.io.Serializable;

import hr.fer.zemris.ui.lab1.Thought;

/**
 * Element koji se nalazi u bazi završnih poteza. Svaki element sadrži najbolji
 * potez bijelog i crnog igrača
 */
public class EndgameDatabaseElement implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** Najbolji potez crnog igrača */
	private Thought blackMove;
	
	/** Najbolji potez bijelog igrača */
	private Thought whiteMove;
	
	/**
	 * Defaultni konstruktor, stvara prazni element
	 *
	 */
	public EndgameDatabaseElement() {
		
	}

	/**
	 * Konstruktor koji stvara gotov element
	 * @param blackMove najbolji potez crnog igrača
	 * @param whiteMove najbolji potez bijelog igrača
	 */
	public EndgameDatabaseElement(Thought blackMove, Thought whiteMove) {
		this.blackMove = blackMove;
		this.whiteMove = whiteMove;
	}

	/**
	 * Postavlja potez crnog igrača
	 * @param blackMove potez crnog igrača
	 */
	public void setBlackMove(Thought blackMove) {
		this.blackMove = blackMove;
	}
	
	/**
	 * Postavlja potez bijelog igrača
	 * @param ulaz potez bijelog igrača
	 */
	public void setWhiteMove(Thought whiteMove) {
		this.whiteMove = whiteMove;
	}
	
	/**
	 * Dohvaća potez crnog igrača
	 * @return potez crnog igrača
	 */
	public Thought getBlackMove() {
		return this.blackMove;
	}
	
	/**
	 * Dohvaća potez bijelog igrača
	 * @return potez bijelog igrača
	 */
	public Thought getWhiteMove(){
		return this.whiteMove;
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof EndgameDatabaseElement)) {
			return false;
		}

		EndgameDatabaseElement other = (EndgameDatabaseElement) obj;

		return this.blackMove.equals(other.blackMove) && this.whiteMove.equals(other.whiteMove);
	}
	
	public int hashCode() {
		return blackMove.hashCode() ^ whiteMove.hashCode();
	}
}

package hr.fer.zemris.ui.lab1;

import java.io.Serializable;

/**
 * Klasa predstavlja misao o potezu (koji je to potez i kolika je njegova vrijednost
 * tj. koliko je pametno napraviti taj potez)
 */
public class Thought implements Comparable<Thought>, Serializable {

	private static final long serialVersionUID = 1L;

	/** Redak i stupac figurice koja može napraviti ovaj potez */
	public int row, column;
	
	/** Vrijednost poteza */
	public float value;
	
	/** Da li se radi o skoku ili ne */
	public boolean jump;
	
	/** Smjer poteza, ovisno o kombinaciji */
	public boolean up, right;
	
	/** Potez koji se nastavlja na ovaj (u slučaju ulančanih poteza) */
	public Thought chainedMove;
	
	/** Misao najmanje moguće vrijednosti poteza */
	public static final Thought MIN_VALUE_THOUGHT = new Thought(Float.NEGATIVE_INFINITY);
	
	/** Misao najveće moguće vrijednosti poteza */
	public static final Thought MAX_VALUE_THOUGHT = new Thought(Float.POSITIVE_INFINITY);
	
	/**
	 * Konstruktor misli o potezu
	 * @param row redak
	 * @param column stupac
	 * @param value vrijednost poteza
	 * @param up true ako gore, false ako dolje
	 * @param right true ako desno, false ako lijevo
	 * @param jump da li se radi o skoku ili ne
	 */
	public Thought(int row, int column, float value, boolean up, boolean right, boolean jump) {
		this.up = up;
		this.right = right;
		this.row = row;
		this.column = column;
		this.value = value;
		this.chainedMove = null;
		this.jump = jump;
	}
	
	/**
	 * Konstruktor misli o potezu na kojeg se ulančava drugi potez (to se događa prilikom
	 * skakanja)
	 * @param row redak
	 * @param column stupac
	 * @param value vrijednost poteza
	 * @param up true ako gore, false ako dolje
	 * @param right true ako desno, false ako lijevo
	 */
	public Thought(int row, int column, Thought chainedMove, boolean up, boolean right) {
		this(row, column, chainedMove.value, up, right, true);
		this.chainedMove = chainedMove;
	}
	
	/**
	 * Konstruktor koji stvara misao o potezu zadane vrijednosti
	 * @param value vrijednost poteza
	 */
	public Thought(float value) {
		this.value = value;
		row = -1;
		column = -1;
		up = false;
		right = false;
	}
	
	public int compareTo(Thought other) {
		if (this.value < other.value) {
			return -1;
		} else if (this.value > other.value) {
			return +1;
		} else {
			if (other.row == -1 || other.column == -1) {
				return 1;
			} else if (this.row == -1 || this.column == -1) {
				return -1;
			} else {
				return 0;
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
				
		if(row != -1 && column != -1) {
			int newRow = this.row;
			int newColumn = this.column;
			
			sb.append((char)(newRow + '1'));
			sb.append((char)(newColumn + 'A'));
			
			for(Thought iterator = this; iterator != null; iterator = iterator.chainedMove) {
				sb.append(' ');
				
				if(iterator.up) {
					newRow += (iterator.jump ? 2 : 1); 
				} else {
					newRow -= (iterator.jump ? 2 : 1); 				
				}
				
				if(iterator.right) {
					newColumn += (iterator.jump ? 2 : 1);
				} else {
					newColumn -= (iterator.jump ? 2 : 1);				
				}
				
				sb.append((char)(newRow + '1'));
				sb.append((char)(newColumn + 'A'));
			}
		}
		
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Thought)) {
			return false;
		}
		
		Thought other = (Thought) obj;
		
		return this.right == other.right && this.up == other.right &&
					this.row == other.row && this.column == other.column;
	}
}

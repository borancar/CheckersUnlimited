package hr.fer.zemris.ui.lab1;

/** Enumeracija kvadrata ploče */
public enum Square {
	
	EMPTY,
	BLACK_KING(false, true),
	WHITE_KING(true, true),
	BLACK_PAWN(false, false),
	WHITE_PAWN(true, false);
	
	/** True ako je prazno polje, false ako nešto sadrži */
	private final boolean empty;
	
	/** True za bijelu boju, false za crnu */
	private final boolean color;
	
	/** True ako je figura kralj, false ako je pijun */
	private final boolean king;
	
	/**
	 * Konstruktor koji stvara prazni kvadar
	 */
	private Square() {
		this.empty = true;
		this.color = false;
		this.king = false;
	}
	
	/**
	 * Konstruktor koji stvara figuru u kvadratu
	 * @param color true za bijelu figuru, false za crnu
	 * @param dama true za kralja, false za pijuna
	 */
	private Square(boolean color, boolean king) {
		this.empty = false;
		this.color = color;
		this.king = king;
	}
	
	public String toString() {
		if(empty == true) {
			return "Prazno polje";
		} else {
			return (color ? "Bijeli " : "Crni ") + (king ? "kralj" : "pijun");
		}
	}
	
	/**
	 * Dohvaća boju figure
	 * @return true ako je bijela, false ako je crna. Za prazno polje rezultat 
	 * je nedefiniran
	 */
	public boolean getColor() {
		return color;
	}
	
	/**
	 * Vraća da li se unutar kvadrata nalazi kralj.
	 * @return true ako da, false inače
	 */
	public boolean isKing() {
		return !this.empty && this.king;
	}
	
	/**
	 * Vraća da li se unutar kvadrata nalazi pijun.
	 * @return true ako da, false inače
	 */
	public boolean isPawn() {
		return !this.empty && !this.king;
	}
	
	/**
	 * Vraća da li je kvadrat prazan (unutar njega nema nikakve figure)
	 * @return true ako da, false inače
	 */
	public boolean isEmpty() {
		return this.empty;
	}
}

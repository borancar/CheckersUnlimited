package hr.fer.zemris.ui.lab1;

/**
 * Slušač promjene stanja ploče
 *
 */
public interface IBoardChangeListener {

	/**
	 * Funkcija kojoj se dojavljuje promjena stanja na ploči
	 * @param board novo stanje na ploči
	 */
	public void notify(Square[][] board);
}

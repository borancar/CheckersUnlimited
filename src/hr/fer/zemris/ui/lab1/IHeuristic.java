package hr.fer.zemris.ui.lab1;

import java.util.List;

/**
 * Sučelje heuristike igrača.
 *
 */
public interface IHeuristic {


	/**
	 * Vrednuje stanje na ploči za predanog igrača.
	 * @param board stanje na ploči
	 * @param color igračeva boja
	 * @return bodovi stanja, što je veća igračeva korist, veća mora biti vrijednost
	 */
	public float valueState(Square[][] board, boolean color);
	
	/**
	 * Vraća detaljan opis heuristike, u više redova
	 * @return redovi detaljnog opisa heuristike
	 */
	public List<String> describeHeuristic();
	
	/**
	 * Dohvaća ime heuristike
	 * @return ime heuristike
	 */
	public String getName();
	
	/**
	 * Interpolira dvije heuristike kako bi se možda postigla bolja heuristika
	 * @param other heuristika s kojom treba interpolirati heuristiku
	 * @return interpolirana heuristika ili null ukoliko nije moguće interpolirati
	 */
	public IHeuristic merge(IHeuristic other);
	
	/**
	 * Mutira heuristiku tako da joj izmijeni parametre unutar nekih granica
	 * @return mutirana heuristika s ponešto izmijenjenim parametrima
	 */
	public IHeuristic mutate();
}

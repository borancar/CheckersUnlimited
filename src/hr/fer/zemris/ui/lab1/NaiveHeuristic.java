package hr.fer.zemris.ui.lab1;

import java.util.Arrays;
import java.util.List;

import hr.fer.zemris.ui.lab1.util.MathHelperFunctions;

/**
 * Naivna heuristika koja samo uzima u obzir broj figura na ploči.
 * Za kralja se uzima da vrijedi par pijuna.
 *
 */
public class NaiveHeuristic implements IHeuristic {

	/** Maksimalna vrijednost kralja */
	protected static final float MAX_KING_VALUE = 2.0f;
	
	/** Koliko pijuna jedan kralj vrijedi */
	protected float kingValue;
	
	/**
	 * Defaultni konstruktor, uzima da kralj vrijedi 2 pijuna
	 *
	 */
	public NaiveHeuristic() {
		this(2.0f);
	}
	
	/**
	 * Stvara naivnu heuristiku kojoj se predaje koliko jedan kralj vrijedni pijuna
	 * @param kingValue vrijednost kralja (koliko pijuna on vrijedi)
	 */
	public NaiveHeuristic(float kingValue) {
		this.kingValue = kingValue;
	}
	
	public float valueState(Square[][] board, boolean color) {
		
		float stateValueBlack = 0.0f;
		float stateValueWhite = 0.0f;
		
		int nBlackFigures = 0;
		int nWhiteFigures = 0;
		
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				switch(board[row][column]) {
					case WHITE_PAWN:
					case WHITE_KING:
						stateValueWhite += valueFigure(board, row, column);
						nWhiteFigures++;						
						break;
						
					case BLACK_PAWN:
					case BLACK_KING:
						stateValueBlack += valueFigure(board, row, column);
						nBlackFigures++;						
						break;
				}
			}
		}
		
		boolean lost = color && nWhiteFigures == 0 || !color && nBlackFigures == 0;
		boolean won = color && nBlackFigures == 0 || !color && nWhiteFigures == 0;
		
		if(lost) {
			return Float.NEGATIVE_INFINITY;
		} else if(won) {
			return Float.POSITIVE_INFINITY;
		} else {
			return color == CheckerGame.COLOR_BLACK ? stateValueBlack - stateValueWhite : stateValueWhite - stateValueBlack;
		}
	}
	
	/**
	 * Vrednuje koliko vrijedi figura na predanoj poziciji. Što je ta figura
	 * važnija igraču, veća joj je vrijednost.
	 * @param board ploča
	 * @param row redak na kojem se nalazi figura
	 * @param column stupac na kojem se nalazi figura
	 * @return vrijednost figure
	 */
	protected float valueFigure(Square[][] board, int row, int column) {
		if(board[row][column].isKing()) {
			return kingValue;
		} else {
			return 1.0f;
		}
	}

	public List<String> describeHeuristic() {
		return Arrays.asList(new String[]{
				getName(),
				"King value: " + kingValue
			});
	}

	/**
	 * Metoda stvara heuristiku ove klase sa slučajno parametriranim koeficijentima
	 * @return heuristika ove klase
	 */
	public static NaiveHeuristic getRandomHeuristic() {
		NaiveHeuristic randomNaiveHeuristic = new NaiveHeuristic();
		
		randomNaiveHeuristic.kingValue = MathHelperFunctions.getUniformValueFromInterval(1.0f, MAX_KING_VALUE);
	
		return randomNaiveHeuristic;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof NaiveHeuristic)) {
			return false;
		}
		
		NaiveHeuristic other = (NaiveHeuristic) obj;
		
		return this.kingValue == other.kingValue;
	}
	
	@Override
	public int hashCode() {
		return Float.valueOf(kingValue).hashCode();
	}

	public IHeuristic merge(IHeuristic other) {
		if(!(other instanceof NaiveHeuristic)) {
			return null;
		}
		
		NaiveHeuristic otherNaive = (NaiveHeuristic) other;
		
		NaiveHeuristic result = new NaiveHeuristic();
		result.kingValue = MathHelperFunctions.getDispersedMedian(this.kingValue, otherNaive.kingValue);
	
		return result;
	}

	public String getName() {
		return "NaiveHeuristic";
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public IHeuristic mutate() {
		NaiveHeuristic mutated = new NaiveHeuristic();
		mutated.kingValue = MathHelperFunctions.getGaussianValue(this.kingValue, MAX_KING_VALUE/3);
		
		return mutated;
	}
}

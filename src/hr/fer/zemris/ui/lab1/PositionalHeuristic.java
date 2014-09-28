package hr.fer.zemris.ui.lab1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hr.fer.zemris.ui.lab1.util.MathHelperFunctions;

/**
 * Heuristika koja pokušava dati na važnosti pozicijskim prednostima.
 * Pozicijske preednosti su blizine pijuna okrunjenju i centralne pozicije na ploči.
 */
public class PositionalHeuristic extends NaiveHeuristic {
	
	/** Maksimalna dodatna vrijednost kraljeva pozicioniranih na centralnim pozicijama */
	protected static final float MAX_CENTER_COLUMNS_KING_ADDITION = 0.05f;

	/** Maksimalna dodatna vrijednost kraljeva pozicioniranih na centralnim pozicijama */
	protected static final float MAX_CENTER_ROWS_KING_ADDITION = 0.05f;

	/** Maksimalni koeficijent redaka pijuna u pozitivnom smjeru igrača */
	protected static final float MAX_ROW_COEFFICIENT = 0.05f;

	/** Maksimalni koeficijent redaka pijuna u negativnom smjeru igrača */
	protected static final float MAX_NEGATIVE_ROW_COEFFICIENT = 0.05f;

	/** Koeficijent redaka u pozitivnom smjeru pijuna */
	protected float rowCoefficient;
	
	/** Koeficijent redaka u negativnom smjeru pijuna */
	protected float negativeRowCoefficient;
	
	/** Dodatna vrijednost kraljeva na centralnim pozicijama ploče */
	protected float centerRowsKingAddition;
	
	/** Dodatna vrijednost kraljeva na centralnim stupcima ploče */
	protected float centerColumnsKingAddition;
	
	/**
	 * Defaultni konstrktor
	 */
	public PositionalHeuristic() {
		kingValue = 2.0f;
		rowCoefficient = 0.007f;
		negativeRowCoefficient = 0.002f;
		centerRowsKingAddition = 0.001f;
		centerColumnsKingAddition = 0.0005f;
	}
	
	/**
	 * Stvara heuristiku ove klase koja je bitnim sadržajem jednaka
	 * heuristici nad-klase
	 * @param naiveHeuristic naivna heuristika od koje želimo napraviti bitno istu pozicijsku heuristiku
	 */
	public PositionalHeuristic(NaiveHeuristic naiveHeuristic) {
		kingValue = naiveHeuristic.kingValue;
	}
	
	@Override
	protected float valueFigure (Square[][] board, int row, int column) {
		switch(board[row][column]) {
		case WHITE_KING:
		case BLACK_KING:
			return kingValue + ((row>2 && row<6) ? centerRowsKingAddition : 0f) + ((column>2 && column <6) ? centerColumnsKingAddition : 0f);
		
		case WHITE_PAWN:
			return 1.0f + rowCoefficient*(row) - ((row<2) ? negativeRowCoefficient*(2 - row) : 0f) ;
			
		case BLACK_PAWN:
			return 1.0f + rowCoefficient*(7 - row) - ((row>5) ? negativeRowCoefficient*(row - 5) : 0f);
		
		default:
			return 0.0f;
		}
	}
	
	@Override
	public List<String> describeHeuristic() {
		List<String> description = new ArrayList<String>();
		
		description.addAll(super.describeHeuristic());
		description.addAll(Arrays.asList(new String[]{
			"Row coefficient: " + rowCoefficient,
			"Negative row coefficient: " + negativeRowCoefficient,
			"Center rows king addition: " + centerRowsKingAddition,
			"Center columns king addition: " + centerColumnsKingAddition
		}));
		
		return description;
	}
	
	/**
	 * Metoda stvara heuristiku ove klase sa slučajno parametriranim koeficijentima
	 * @return heuristika ove klase
	 */
	public static PositionalHeuristic getRandomHeuristic() {
		PositionalHeuristic randomPositionalHeuristic = new PositionalHeuristic(NaiveHeuristic.getRandomHeuristic());
		
		randomPositionalHeuristic.centerColumnsKingAddition = MathHelperFunctions.getUniformValueFromInterval(0.0f, MAX_CENTER_COLUMNS_KING_ADDITION);
		randomPositionalHeuristic.centerRowsKingAddition = MathHelperFunctions.getUniformValueFromInterval(0.0f, MAX_CENTER_COLUMNS_KING_ADDITION);
		randomPositionalHeuristic.rowCoefficient = MathHelperFunctions.getUniformValueFromInterval(0.0f, MAX_ROW_COEFFICIENT);
		randomPositionalHeuristic.negativeRowCoefficient = MathHelperFunctions.getUniformValueFromInterval(0.0f, MAX_NEGATIVE_ROW_COEFFICIENT);
	
		return randomPositionalHeuristic;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof PositionalHeuristic)) {
			return false;
		}
		
		PositionalHeuristic other = (PositionalHeuristic) obj;
		
		return super.equals(other) && this.centerColumnsKingAddition == other.centerColumnsKingAddition &&
				this.centerRowsKingAddition == other.centerRowsKingAddition &&
				this.negativeRowCoefficient == other.negativeRowCoefficient &&
				this.rowCoefficient == other.rowCoefficient;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() ^ Float.valueOf(centerColumnsKingAddition).hashCode() ^
				Float.valueOf(centerRowsKingAddition).hashCode() ^ 
				Float.valueOf(negativeRowCoefficient).hashCode() ^
				Float.valueOf(rowCoefficient).hashCode();
	}
	
	@Override
	public IHeuristic merge(IHeuristic other) {
		if(!(other instanceof PositionalHeuristic)) {
			return null;
		}
		
		PositionalHeuristic otherPositional = (PositionalHeuristic) other;
		
		PositionalHeuristic result = new PositionalHeuristic((NaiveHeuristic) super.merge(other));
		result.centerColumnsKingAddition = MathHelperFunctions.getDispersedMedian(this.centerColumnsKingAddition, otherPositional.centerColumnsKingAddition);
		result.centerRowsKingAddition = MathHelperFunctions.getDispersedMedian(this.centerRowsKingAddition, otherPositional.centerRowsKingAddition);
		result.negativeRowCoefficient = MathHelperFunctions.getDispersedMedian(this.negativeRowCoefficient, otherPositional.negativeRowCoefficient);
		result.rowCoefficient = MathHelperFunctions.getDispersedMedian(this.rowCoefficient, otherPositional.rowCoefficient);
		
		return result;
	}
	
	@Override
	public String getName() {
		return "PositionalHeuristic";
	}
	
	@Override
	public IHeuristic mutate() {
		PositionalHeuristic mutated = new PositionalHeuristic((NaiveHeuristic) super.mutate());
		
		mutated.centerColumnsKingAddition = MathHelperFunctions.getGaussianValue(this.centerColumnsKingAddition, MAX_CENTER_COLUMNS_KING_ADDITION/3);
		mutated.centerRowsKingAddition = MathHelperFunctions.getGaussianValue(this.centerRowsKingAddition, MAX_CENTER_ROWS_KING_ADDITION/3);
		mutated.negativeRowCoefficient = MathHelperFunctions.getGaussianValue(this.negativeRowCoefficient, MAX_NEGATIVE_ROW_COEFFICIENT/3);
		mutated.rowCoefficient = MathHelperFunctions.getGaussianValue(this.rowCoefficient, MAX_ROW_COEFFICIENT/3);
		
		return mutated;
	}
}

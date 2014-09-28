package hr.fer.zemris.ui.lab1;

import java.util.Arrays;
import java.util.List;

/**
 * Heuristika koja gleda sve ili ništa. Postoji pobjeda, gubitak ili neriješeno
 *
 */
public class AllOrNothingHeuristic implements IHeuristic {
	
	/**
	 * Defaultni konstruktor
	 */
	public AllOrNothingHeuristic() {
		
	}
	
	public float valueState(Square[][] board, boolean color) {
			
		int nBlackFigures = 0;
		int nWhiteFigures = 0;
		
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				switch(board[row][column]) {
					case WHITE_PAWN:
					case WHITE_KING:
						nWhiteFigures++;						
						break;
						
					case BLACK_PAWN:
					case BLACK_KING:
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
			return 0.0f;
		}
	}

	public List<String> describeHeuristic() {
		return Arrays.asList(new String[]{
				getName(),
			});
	}

	/**
	 * Metoda stvara heuristiku ove klase sa slučajno parametriranim koeficijentima
	 * @return heuristika ove klase
	 */
	public static AllOrNothingHeuristic getRandomHeuristic() {	
		return new AllOrNothingHeuristic();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof AllOrNothingHeuristic)) {
			return false;
		}

		return true;
	}
	
	@Override
	public int hashCode() {
		return 0;
	}

	public IHeuristic merge(IHeuristic other) {
		return null;
	}

	public String getName() {
		return "AllOrNothingHeuristic";
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public IHeuristic mutate() {
		return this;
	}
}

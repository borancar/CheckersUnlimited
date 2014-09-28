package hr.fer.zemris.ui.lab1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hr.fer.zemris.ui.lab1.util.MathHelperFunctions;

public class ImprovedPositionalHeuristic extends PositionalHeuristic {
	
	private int[] whitePawnPosition = new int[32*2];
	
	private int[] blackPawnPosition = new int[32*2];
	
	private int[] blackKingPosition = new int[32*2];
	
	private int[] whiteKingPosition = new int[32*2];
	
	private int whitePawnCount = 0;
	
	private int blackPawnCount = 0;
	
	private int whiteKingCount = 0;
	
	private int blackKingCount = 0;
	
	protected static final float MAX_RUNAWAY_COEFFICIENT = 0.05f;
	
	protected static final float MAX_STRUCTURAL_COEFFICIENT = 0.05f;
	
	protected float runawayCoefficient;
	
	protected float structuralCoefficient;
	
	public ImprovedPositionalHeuristic() {
		kingValue = 1.2f;
		runawayCoefficient = 0.3f;
		structuralCoefficient = -0.005f;
		rowCoefficient = 0.002f;
		negativeRowCoefficient = 0.05f;
		centerRowsKingAddition = 0.001f;
		centerColumnsKingAddition = 0.0005f;
	}

	/**
	 * Stvara heuristiku ove klase koja je bitnim sadržajem jednaka heuristici
	 * nad-klase
	 * @param randomHeuristic heuristika nad-klase
	 */
	public ImprovedPositionalHeuristic(PositionalHeuristic positionalHeuristic) {
		super(positionalHeuristic);
		this.centerColumnsKingAddition = positionalHeuristic.centerColumnsKingAddition;
		this.centerRowsKingAddition = positionalHeuristic.centerRowsKingAddition;
		this.negativeRowCoefficient = positionalHeuristic.negativeRowCoefficient;
		this.rowCoefficient = positionalHeuristic.negativeRowCoefficient;
	}

	@Override
	public float valueState(Square[][] board, boolean color) {
		whitePawnCount = 0;
		blackPawnCount = 0;
		whiteKingCount = 0;
		blackKingCount = 0;
		float stateValueBlack = 0.0f;
		float stateValueWhite = 0.0f;
		
		int nBlackFigures = 0;
		int nWhiteFigures = 0;
		
		for(int row = 0; row<8; row++) {
			for(int column = 0; column<8; column++) {
				switch (board[row][column]) {
				case WHITE_PAWN:
					whitePawnPosition[whitePawnCount++] = row;
					whitePawnPosition[whitePawnCount++] = column;
					break;
					
				case BLACK_PAWN:
					blackPawnPosition[blackPawnCount++] = row;
					blackPawnPosition[blackPawnCount++] = column;
					break;
				case WHITE_KING:
					whiteKingPosition[whiteKingCount++] = row;
					whiteKingPosition[whiteKingCount++] = column;
					break;
				case BLACK_KING:
					blackKingPosition[blackKingCount++] = row;
					blackKingPosition[blackKingCount++] = column;
					break;
				default:
					break;
				}
			
			}
		}
		
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
			return (color == CheckerGame.COLOR_BLACK ? stateValueBlack - stateValueWhite : stateValueWhite - stateValueBlack) * (24 - nWhiteFigures - nBlackFigures + 1);
		}
	}
	
	@Override
	protected float valueFigure (Square[][] board, int row, int column) {
		switch(board[row][column]) {
		case WHITE_KING:
		case BLACK_KING:
				return kingValue + ((row>2 && row<6) ? centerRowsKingAddition : 0f) + ((column>2 && column <6) ? centerColumnsKingAddition : 0f);

		case WHITE_PAWN:
			return 1.0f + rowCoefficient*(row ) - ((row<2) ? negativeRowCoefficient*(2 - row) : 0f) + runawayWhite( row, column) - structuralWhite(row,column);
			
		case BLACK_PAWN:
				return 1.0f  +  rowCoefficient*(7 - row) - ((row>5) ? negativeRowCoefficient*(row - 5) : 0f) + runawayBlack( row, column) - structuralBlack(row,column);
		
		default:
		return 0.0f;
		}
	}
	
	float runawayWhite(int tempRow,int tempColumn) {
			boolean runaway = true;
			
			for(int j = 0; j<blackPawnCount; j+=2) {
				int ydiff = blackPawnPosition[j] - tempRow;
				int xdiff = Math.abs(tempColumn - blackPawnPosition[j + 1]);
				if( ydiff <= 0) continue;
				else if(xdiff > ydiff ) continue;
				runaway = false;
				break;
			}
			if(!runaway) return 0f;
			
			for(int j = 0; j<blackKingCount; j+=2) {
				int ydiff = Math.abs(tempRow - blackKingPosition[j]);
				int xdiff = Math.abs(tempColumn - blackKingPosition[j + 1]);
				if( ydiff <= -2 + ((false) ? 1 : 0)) continue;
				else if( (7 - tempRow) >= xdiff + ((false) ? 1 : 2) ) continue;
				runaway = false;
				break;
				
			}
			
			if(runaway) return runawayCoefficient;
			else return 0f;
	}
	
	float runawayBlack(int tempRow,int tempColumn) {
		boolean runaway = true;
		
		for(int j = 0; j<whitePawnCount; j+=2) {
			int ydiff = tempRow - whitePawnPosition[j];
			int xdiff = Math.abs(tempColumn - whitePawnPosition[j + 1]);
			if( ydiff <= 0) continue;
			else if(xdiff > ydiff ) continue;
			runaway = false;
			break;
		}
		if(!runaway) return 0f;
		
		for(int j = 0; j<whiteKingCount; j+=2) {
			int ydiff = Math.abs(tempRow - whiteKingPosition[j]);
			int xdiff = Math.abs(tempColumn - whiteKingPosition[j + 1]);
			if( ydiff <= -2 + ((false) ? 1 : 0)) continue;
			else if( (tempRow) >= xdiff + ((false) ? 1 : 2) ) continue;
			runaway = false;
			break;
			
		}
		
		if(runaway) return runawayCoefficient;
		else return 0.0f;
	}
	
	float structuralWhite(int tempRow,int tempColumn) {
		float rValue = 0f;
		for(int j = 0; j<whitePawnCount; j+=2){
			int jRow = whitePawnPosition[j];
			int jColumn = whitePawnPosition[j + 1];
			if (tempRow > 0 && tempRow < 7 && tempColumn > 0 && tempColumn < 7 && jRow > 0 && jRow < 7 && jColumn > 0 && jColumn < 7) {
				float udaljenost = dist(tempRow,tempColumn,jRow,jColumn);
				if(udaljenost == (float)Math.sqrt(8.0) || udaljenost == (float)Math.sqrt(4.0)) rValue += structuralCoefficient;
			}
		}
		return rValue;
	}
	
	float structuralBlack(int tempRow,int tempColumn) {
		float rValue = 0f;
		for(int j = 0; j<blackPawnCount; j+=2){
			int jRow = blackPawnPosition[j];
			int jColumn = blackPawnPosition[j + 1];
			if (tempRow > 0 && tempRow < 7 && tempColumn > 0 && tempColumn < 7 && jRow > 0 && jRow < 7 && jColumn > 0 && jColumn < 7) {
				float udaljenost = dist(tempRow,tempColumn,jRow,jColumn);
				if(udaljenost == (float)Math.sqrt(8.0) || udaljenost == (float)Math.sqrt(4.0)) rValue += structuralCoefficient;
			}
		}
		return rValue;
	}
	
	float dist(int aRow,int aColumn,int bRow,int bColumn) {
		return (float) Math.sqrt((aRow - bRow)*(aRow - bRow)-(aColumn - bColumn)*(aColumn - bColumn));
		}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ImprovedPositionalHeuristic)) {
			return false;
		}
		
		ImprovedPositionalHeuristic other = (ImprovedPositionalHeuristic) obj;
		
		return super.equals(obj) && this.runawayCoefficient == other.runawayCoefficient &&
				this.structuralCoefficient == other.structuralCoefficient;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() ^ Float.valueOf(runawayCoefficient).hashCode() ^
				Float.valueOf(structuralCoefficient).hashCode();
	}
	
	/**
	 * Metoda stvara heuristiku ove klase sa slučajno parametriranim koeficijentima
	 * @return heuristika ove klase
	 */
	public static ImprovedPositionalHeuristic getRandomHeuristic() {
		ImprovedPositionalHeuristic randomImprovedPositionalHeuristic = new ImprovedPositionalHeuristic(PositionalHeuristic.getRandomHeuristic());
		
		randomImprovedPositionalHeuristic.structuralCoefficient = MathHelperFunctions.getUniformValueFromInterval(0.0f, MAX_STRUCTURAL_COEFFICIENT);
		randomImprovedPositionalHeuristic.runawayCoefficient = MathHelperFunctions.getUniformValueFromInterval(0.0f, MAX_RUNAWAY_COEFFICIENT);
		
		return randomImprovedPositionalHeuristic;
	}
	
	@Override
	public IHeuristic merge(IHeuristic other) {
		if(!(other instanceof ImprovedPositionalHeuristic)) {
			return null;
		}
		
		ImprovedPositionalHeuristic otherImproved = (ImprovedPositionalHeuristic) other;
		
		ImprovedPositionalHeuristic result = new ImprovedPositionalHeuristic((PositionalHeuristic) super.merge(other));
		result.runawayCoefficient = MathHelperFunctions.getDispersedMedian(this.runawayCoefficient, otherImproved.runawayCoefficient);
		result.structuralCoefficient = MathHelperFunctions.getDispersedMedian(this.structuralCoefficient, otherImproved.structuralCoefficient);
		
		return result;
	}
	
	@Override
	public String getName() {
		return "ImprovedPositionalHeuristic";
	}
	
	@Override
	public List<String> describeHeuristic() {
		List<String> description = new ArrayList<String>();
		
		description.addAll(super.describeHeuristic());
		description.addAll(Arrays.asList(new String[]{
				"Structural coefficient: " + structuralCoefficient,
				"Runaway coefficient: " + runawayCoefficient,
		}));
				
		return description;
	}
	
	@Override
	public IHeuristic mutate() {
		ImprovedPositionalHeuristic mutated = new ImprovedPositionalHeuristic((PositionalHeuristic) super.mutate());
		
		mutated.runawayCoefficient = MathHelperFunctions.getUniformValueFromInterval(this.runawayCoefficient, MAX_RUNAWAY_COEFFICIENT/3);
		mutated.structuralCoefficient = MathHelperFunctions.getUniformValueFromInterval(this.structuralCoefficient, MAX_STRUCTURAL_COEFFICIENT/3);
		
		return mutated;
	}
}

package hr.fer.zemris.ui.lab1;
import java.lang.Math;

public class BetterHeuristic extends  NaiveHeuristic {
	
	float kingValue = 3.5f;
	float pawnValue = 2;
	float positionalValue = 0.001f;
	
	public BetterHeuristic () {
	}
	
	public float valueState (Square[][] board, boolean color) {
		float whiteValue = 0;
		float blackValue = 0;
		int[] whitePawnPosition = new int[32*2];
		int[] blackPawnPosition = new int[32*2];
		int[] blackKingPosition = new int[32*2];
		int[] whiteKingPosition = new int[32*2];
		int whitePawnCount = 0;
		int blackPawnCount = 0;
		int whiteKingCount = 0;
		int blackKingCount = 0;
		
		for(int row = 0; row<8; row++) {
			for(int column = row % 2; column<8; column+=2) {
				switch (board[row][column]) {
				case WHITE_PAWN:
					whiteValue += pawnValue + (7 - row) * positionalValue;
					whitePawnPosition[whitePawnCount++] = row;
					whitePawnPosition[whitePawnCount++] = column;
					break;
					
				case BLACK_PAWN:
					blackValue += pawnValue + row * positionalValue;
					blackPawnPosition[blackPawnCount++] = row;
					blackPawnPosition[blackPawnCount++] = column;
					break;
				case WHITE_KING:
					whiteValue += kingValue;
					whiteKingPosition[whiteKingCount++] = row;
					whiteKingPosition[whiteKingCount++] = column;
					break;
				case BLACK_KING:
					blackValue += kingValue;
					blackKingPosition[blackKingCount++] = row;
					blackKingPosition[blackKingCount++] = column;
					break;
				}
			
			}
				
		}
		
		/* traži odbjegle pješake za bijelog igrača*/
		for(int i = 0; i<whitePawnCount; i+=2) {
			int tempRow = whitePawnPosition[i];
			int tempColumn = whitePawnPosition[i + 1];
			boolean runaway = true;
			
			for(int j = 0; j<blackPawnCount; j+=2) {
				int ydiff = blackPawnPosition[j] - tempRow;
				int xdiff = abs(tempColumn - blackPawnPosition[j + 1]);
				if( ydiff <= 0) continue;
				else if(xdiff > ydiff ) continue;
				runaway = false;
				break;
			}
			if(!runaway) continue;
			
			for(int j = 0; j<blackKingCount; j+=2) {
				int ydiff = abs(tempRow - blackPawnPosition[j]);
				int xdiff = abs(tempColumn - blackPawnPosition[j + 1]);
				if( ydiff <= -2 + ((false) ? 1 : 0)) continue;
				else if( (7 - tempRow) >= xdiff + ((false) ? 1 : 2) ) continue;
				runaway = false;
				break;
				
			}
			
			if(runaway) whiteValue += 0.4;
		}
		
		/* traži odbjegle pješake za crnoga igrača*/
		for(int i = 0; i<blackPawnCount; i+=2) {
			int tempRow = blackPawnPosition[i];
			int tempColumn = blackPawnPosition[i + 1];
			boolean runaway = true;
			
			for(int j = 0; j<whitePawnCount; j+=2) {
				int ydiff = tempRow - whitePawnPosition[j];
				int xdiff = abs(tempColumn - whitePawnPosition[j + 1]);
				if( ydiff <= 0) continue;
				else if(xdiff > ydiff ) continue;
				runaway = false;
				break;
			}
			if(!runaway) continue;
			
			for(int j = 0; j<whiteKingCount; j+=2) {
				int ydiff = abs(tempRow - whitePawnPosition[j]);
				int xdiff = abs(tempColumn - whitePawnPosition[j + 1]);
				if( ydiff <= -2 + ((false) ? 1 : 0)) continue;
				else if( (tempRow) >= xdiff + ((false) ? 1 : 2) ) continue;
				runaway = false;
				break;
				
			}
			
			if(runaway) blackValue += 0.4;
		}
		
		for(int i = 0; i<whitePawnCount; i+=2) {
			int pozCount = 0;
			for(int j = i + 2; j<whitePawnCount; j+=2){
				//if (whitePawnPosition[i] > 0 && whitePawnPosition[i] < 7 && whitePawnPosition[j] > 0 && whitePawnPosition[j+1] < 7) {
					float udaljenost = dist(whitePawnPosition[i], whitePawnPosition[i+1], whitePawnPosition[j], whitePawnPosition[j+1]);
					if( udaljenost == 3f) pozCount++;
					else if( udaljenost == 1f) pozCount--;
				//}
			}
			whiteValue += pozCount * 0.0001;
		}
		
		for(int i = 0; i<blackPawnCount; i+=2) {
			int pozCount = 0;
			for(int j = i + 2; j<blackPawnCount; j+=2){
				//if (blackPawnPosition[i] > 0 && blackPawnPosition[i] < 7 && blackPawnPosition[j] > 0 && blackPawnPosition[j+1] < 7) 
					//pozCount++;
					float udaljenost = dist(blackPawnPosition[i], blackPawnPosition[i+1], blackPawnPosition[j], blackPawnPosition[j+1]);
					if( udaljenost == 3f) pozCount++;
					else if( udaljenost == 1f) pozCount--;
			}
			blackValue += pozCount * 0.0001;
		}
		
		
	
	if( (blackKingCount == 0) && (blackPawnCount == 0) ) 
		return ((color == false) ?  Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY);
	
	if( (whiteKingCount == 0) && (whitePawnCount == 0) )
		return ((color == true) ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY);
	
	
	return ( (color == true) ? (whiteValue - blackValue) : (blackValue - whiteValue) ) * (24 - whitePawnCount - blackPawnCount - whiteKingCount - blackKingCount + 1 )/10;
	}
	
	int abs(int x) { return (x<0) ? -x : x;}
	
	float dist(int aRow,int aColumn,int bRow,int bColumn) {
			return (float) Math.sqrt(abs(aRow - bRow)*abs(aRow - bRow)-abs(aColumn - bColumn)*abs(aColumn - bColumn));
		
	}
	
}

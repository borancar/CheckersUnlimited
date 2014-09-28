package hr.fer.zemris.ui.lab1.util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import hr.fer.zemris.ui.lab1.AllOrNothingHeuristic;
import hr.fer.zemris.ui.lab1.CheckerGame;
import hr.fer.zemris.ui.lab1.IHeuristic;
import hr.fer.zemris.ui.lab1.Square;
import hr.fer.zemris.ui.lab1.Thought;
import hr.fer.zemris.ui.lab1.utilities.Board;
import hr.fer.zemris.ui.lab1.utilities.EndgameDatabase;
import hr.fer.zemris.ui.lab1.utilities.EndgameDatabaseElement;

/**
 * Klasa za generiranje završnih stanja igraće ploče
 */
public class GenerateEndGameDatabase {

	/** Ploča za igru */
	private Square[][] board = new Square[8][8];
	
	/** Boja igrača */
	private boolean Color;
	
	/** Heuristika */
	private IHeuristic heuristic;
		
	/** Maksimalni broj figurica za koji još uvijek ima smisla generirati stanja */
	private static final int MAX_FIGURES = 3;
	
	/** Maksimalna dubina do koje smo spremni ići */
	private static final int MAX_DEPTH = 11;
	
	/**
	 * Defaultni konstruktor, inicijalizira sve potrebno
	 *
	 */
	public GenerateEndGameDatabase() {
		this.heuristic = new AllOrNothingHeuristic();
	}
	
	/**
	 * Postavlja igraću ploču
	 * @param board igraća ploča
	 */
	public void setBoard(Square[][] board) {
		this.board = board;
	}
	
	/**
	 * Metoda generira sve kombinacije ploče sa predanim brojem figurica
	 * i za svaku kombinaciju nalazi najbolje poteze bijelog i crnog igrača i
	 * zapisuje ih u bazu
	 * @param startRow redak od kojeg počinjemo
	 * @param startColumn stupac od kojeg počinjemo
	 * @param remainingFigures traženi broj figurica na ploči
	 * @param baza podataka u koju treba zapisati
	 */
	public void generateBoardCombinations(int startRow, int startColumn, int remainingFigures, EndgameDatabase endgameDatabase) {
		if(remainingFigures == 0) {
			this.Color = CheckerGame.COLOR_BLACK;
			Thought bestBlack = thinkAhead(CheckerGame.COLOR_BLACK, MAX_DEPTH, Float.POSITIVE_INFINITY);
			this.Color = CheckerGame.COLOR_WHITE;
			Thought bestWhite = thinkAhead(CheckerGame.COLOR_WHITE, MAX_DEPTH, Float.POSITIVE_INFINITY);
		
			endgameDatabase.put(new Board(board), new EndgameDatabaseElement(bestBlack, bestWhite));
		}
		
		for (int row = startRow; row < 8; row++) {
			for (int column = startColumn; column < 8; column++) {
				if((row + column) % 2 == 0) {
					board[row][column] = Square.WHITE_KING;
					generateBoardCombinations(row, column+1, remainingFigures - 1, endgameDatabase);
					board[row][column] = Square.WHITE_PAWN;
					generateBoardCombinations(row, column+1, remainingFigures - 1, endgameDatabase);
					board[row][column] = Square.BLACK_KING;
					generateBoardCombinations(row, column+1, remainingFigures - 1, endgameDatabase);
					board[row][column] = Square.BLACK_PAWN;
					generateBoardCombinations(row, column+1, remainingFigures - 1, endgameDatabase);
					board[row][column] = Square.EMPTY;					
				}
			}
		}
	}
		
	/**
	 * Metoda razmišlja o potezima unaprijed
	 * @param color true ako smo bijeli igrač, false inače
	 * @param depth na kojoj smo trenutnoj dubini
	 * @param parentValue vrijednost roditelja čvora za koji je pozvana metoda (služi za alfa-beta podrezivanje)
	 * @return misao o potezu
	 */
	private Thought thinkAhead(boolean color, int depth, float parentValue) {
				
		if(depth == 0) {
			return new Thought(this.heuristic.valueState(board, this.Color));
		}
		
		Thought minThought = Thought.MAX_VALUE_THOUGHT;
		Thought maxThought = Thought.MIN_VALUE_THOUGHT;
		Thought currentThought;
				
		boolean couldJump = false;
		
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				if(board[row][column].isEmpty() || board[row][column].getColor() != color) {
					continue;
				}
				
				boolean up = true;
				boolean right = true;
				
				for (int i = 0; i < 2; i++) {
					for (int j = 0; j < 2; j++) {
						if(CheckerGame.canJump(board, row, column, up, right)) {
							couldJump = true;
							boolean whiteBecrowned = false;
							boolean blackBecrowned = false;
							
							/* ako napravimo uzimanje onda se granamo na dijete koje ima pokazivač na trenutnu 
							 * vrijednost minmax jednaku svome roditelju
							 */
							float parentToBeValueJump = (Color == color) ? minThought.value : maxThought.value;
							float parentToBeValue = (Color == color) ? maxThought.value : minThought.value;
							
							Square jumpedOver = CheckerGame.jumpFigure(board, row, column, up, right);						
									
							// ovo bi možda se moglo optimirati	(ako igrač može skakati, onda on mora
							// nastavit skakati, inače, drugi igrač igra sljedeći potez)
							
							int newRow = row + (up ? 2 : -2);
							int newColumn = column + (right ? 2 : -2);
							
							if(newRow == 0 && board[newRow][newColumn] == Square.BLACK_PAWN ) {
								blackBecrowned = true;
								board[newRow][newColumn] = Square.BLACK_KING;
							}
							
							if(newRow == 7 && board[newRow][newColumn] == Square.WHITE_PAWN) {
								whiteBecrowned = true;
								board[newRow][newColumn] = Square.WHITE_KING;
							}
							
							if(!(blackBecrowned || whiteBecrowned) && 
									(CheckerGame.canJump(board, newRow, newColumn, up, right) ||
									CheckerGame.canJump(board, newRow, newColumn, up, !right) ||
									CheckerGame.canJump(board, newRow, newColumn, !up, right) ||
									CheckerGame.canJump(board, newRow, newColumn, !up, !right))) {
								currentThought = new Thought(row, column, thinkAhead(color, depth, parentToBeValueJump), up, right);
							} else {
								currentThought = new Thought(row, column, thinkAhead(!color, depth-1, parentToBeValue).value, up, right, true);
							}
							
							if(currentThought.compareTo(maxThought) >= 0) maxThought = currentThought;
							if(currentThought.compareTo(minThought) <= 0) minThought = currentThought;
							
							if(blackBecrowned) board[newRow][newColumn] = Square.BLACK_PAWN;
							if(whiteBecrowned) board[newRow][newColumn] = Square.WHITE_PAWN;
							CheckerGame.unJumpFigure(board, row, column, up, right, jumpedOver);
							
							// Alpha podrezivanje
							if(Color != color) { 
								if(minThought.value <= parentValue) return minThought;
							}
							
							// Beta podrezivanje
							if(Color == color) {
								if(maxThought.value >= parentValue) return maxThought;
							}
						
						}
	
						right = !right;
					}
					
					up = !up;
				}
			}
		}
		
		if(!couldJump) {
			for (int row = 0; row < 8; row++) {
				for (int column = 0; column < 8; column++) {
					if(board[row][column].isEmpty() || board[row][column].getColor() != color) {
						continue;
					}
					
					boolean up = true;
					boolean right = true;
		
					for (int i = 0; i < 2; i++) {
						for (int j = 0; j < 2; j++) {
							if(CheckerGame.canMove(board, row, column, up, right)) {
								boolean whiteBecrowned = false;
								boolean blackBecrowned = false;
								//moveFigure(row, column, up, right);
								
								int newRow = row + (up ? 1 : -1);
								int newColumn = column + (right ? 1 : -1);
								board[newRow][newColumn] = board[row][column];
								board[row][column] = Square.EMPTY;
								
								if(newRow == 0 && board[newRow][newColumn] == Square.BLACK_PAWN ) {
									blackBecrowned = true;
									board[newRow][newColumn] = Square.BLACK_KING;
								}
								
								if(newRow == 7 && board[newRow][newColumn] == Square.WHITE_PAWN) {
									whiteBecrowned = true;
									board[newRow][newColumn] = Square.WHITE_KING;
								}
								
								/*ako napravimo pomak onda dijete ima pokazivač na trenutnu vrijednost minmax čvora svoga roditelja*/
								float parentToBeValue = (Color == color) ? maxThought.value : minThought.value;
								
								currentThought = new Thought(row, column, thinkAhead(!color, depth-1, parentToBeValue).value, up, right, false);
								
								if(currentThought.compareTo(maxThought) >= 0) maxThought = currentThought;
								if(currentThought.compareTo(minThought) <= 0) minThought = currentThought;
								
								if(blackBecrowned) board[newRow][newColumn] = Square.BLACK_PAWN;
								if(whiteBecrowned) board[newRow][newColumn] = Square.WHITE_PAWN;
								board[row][column] = board[newRow][newColumn];
								board[newRow][newColumn] = Square.EMPTY;
								
								//unMoveFigure(row, column, up, right);
								
								// Alpha podrezivanje
								if(Color != color) { 
									if(minThought.value <= parentValue) return minThought;
								}
								
								// Beta podrezivanje
								if(Color == color) {
									if(maxThought.value >= parentValue) return maxThought;
								}
								
							}
	
							right = !right;
						}
						
						up = !up;
					}
				}
			}
		}
		
		return (color == this.Color ? maxThought : minThought);
	}

	public static void main(String[] args) {
		GenerateEndGameDatabase generate = new GenerateEndGameDatabase();
		
		EndgameDatabase[] databases = new EndgameDatabase[MAX_FIGURES+1];
		
		for (int figures = 2; figures <= MAX_FIGURES; figures++) {
			generate.setBoard(CheckerGame.emptyBoard());
			databases[figures] = new EndgameDatabase();
			generate.generateBoardCombinations(0, 0, figures, databases[figures]);

			ObjectOutputStream oos = null;
			
			try {
				oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(figures + ".db")));
			
				oos.writeObject(databases[figures]);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			} finally {
				try {
					oos.close();
				} catch (IOException ignorable) {
					
				}
			}
		}
	}
}

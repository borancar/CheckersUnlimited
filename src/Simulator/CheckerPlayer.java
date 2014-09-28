package Simulator;

import hr.fer.zemris.ui.lab1.CheckerGame;
import hr.fer.zemris.ui.lab1.IBoardChangeListener;
import hr.fer.zemris.ui.lab1.IHeuristic;
import hr.fer.zemris.ui.lab1.ImprovedPositionalHeuristic;
import hr.fer.zemris.ui.lab1.Square;
import hr.fer.zemris.ui.lab1.Thought;

/**
 * Igrač dame.
 *
 */
public class CheckerPlayer {

	/** Ime programa */
	public String Name = "Šigureca";
	
	/** Boja (true - bijela, false - crna) */
	public boolean Color;

	/** Dozvoljeno vrijene za napraviti potez (u ms) */
	public int TimePerMove;
	
	/** Heuristika igrača dame */
	private IHeuristic heuristic;
	
	/** Ploča na kojoj se odvija igra. Ploča je prikazana kao [redak][stupac] matrica*/
	private Square board[][];
	
	/** Slušač kojemu se javlja promjena stanja na ploči */
	private IBoardChangeListener boardChangeListener;
	
	/**
	 * Defaultni konstruktor
	 *
	 */
	public CheckerPlayer() {
		this.heuristic = new ImprovedPositionalHeuristic();
	}
	
	/**
	 * Getter slušača promjene stanja
	 * @return slušač promjene stanja ploče
	 */
	public IBoardChangeListener getBoardChangeListener() {
		return boardChangeListener;
	}

	/**
	 * Setter slušača promjene stanja
	 * @param boardChangeListener novi slušač promjene stanja
	 */
	public void setBoardChangeListener(IBoardChangeListener boardChangeListener) {
		this.boardChangeListener = boardChangeListener;
	}

	/**
	 * Metoda popunjava ploču ovisno o predanom stanju u stringu
	 * @param state stanje na ploči
	 */
	private void fillUpBoard(String state) {
		board = new Square[8][8];
		
		for (int row = 0; row < 8; row ++) {
			for (int column = 0; column < 8; column++) {
				board[row][column] = Square.EMPTY;
			}
		}

		String[] listaFigura = state.split(" ");
		
		for (String figura : listaFigura) {
			int row = (int) (figura.charAt(0) - '1');
			int column = (int) (figura.charAt(1) - 'A');
			
			String tip = figura.substring(2);
			
			if(tip.equals("CP")) {
				board[row][column] = Square.BLACK_PAWN;
			} else if(tip.equals("CK")) {
				board[row][column] = Square.BLACK_KING;
			} else if(tip.equals("BP")) {
				board[row][column] = Square.WHITE_PAWN;
			} else if(tip.equals("BK")) {
				board[row][column] = Square.WHITE_KING;
			}
		}		
	}
	
	/** 
	 * Igra jedan potez igrača
	 * @param state stanje na ploči
	 * @return potez igrača
	 */
	public String Move(String state) {		
		fillUpBoard(state);
		
		int maxDepth = 2;
		Thought bestMove;
		
		int timeAvailable = TimePerMove;
		long startTime;
		long endTime;
		int timeTaken;
				
		do {
			startTime = System.currentTimeMillis();

			bestMove = thinkAhead(this.Color, maxDepth, Float.POSITIVE_INFINITY);
			
			maxDepth++;
			
			endTime = System.currentTimeMillis();
			
			timeTaken = (int) (endTime - startTime);
			
			timeAvailable -= timeTaken;
		} while(timeAvailable >= 20*timeTaken);
		
		return bestMove.toString();
	}
	
	/**
	 * Pronalazi potez za koji AI igrač misli da je najbolji
	 * @param board ploča na kojoj su figure
	 * @return najbolji potez (prema mišljenju AI igrača)
	 */
	public Thought findBestMove(Square[][] board) {
		this.board = CheckerGame.copyOfBoard(board);
		
		int maxDepth = 2;
		Thought bestMove;
		
		int timeAvailable = TimePerMove;
		long startTime;
		long endTime;
		int timeTaken;
				
		do {
			startTime = System.currentTimeMillis();

			bestMove = thinkAhead(this.Color, maxDepth, Float.POSITIVE_INFINITY);
					
			maxDepth++;
			
			endTime = System.currentTimeMillis();
			
			timeTaken = (int) (endTime - startTime);
			
			timeAvailable -= timeTaken;
		} while(timeAvailable >= 20*timeTaken);
		
		return bestMove;
	}
	
	/** 
	 * Igra jedan potez AI igrača. Javlja slušaču da je došlo do promjene na ploči.
	 * @param board stanje na ploči
	 * @return true ako je AI igrač napravio potez, false ako nije mogao napraviit potez
	 */
	public boolean makeMove(Square[][] board) {		

		Thought bestMove = findBestMove(board);
		
		for(Thought iterator = bestMove; iterator != null; iterator = iterator.chainedMove) {
			if(iterator.row == -1 || iterator.column == -1) {
				return false;
			}
			
			if(iterator.jump) {
				CheckerGame.jumpFigure(board, iterator.row, iterator.column, iterator.up, iterator.right);
				CheckerGame.checkAndDoPromotion(board, (iterator.up ? iterator.row + 2 : iterator.row - 2), (iterator.right ? iterator.column + 2 : iterator.column - 2));
			} else {
				CheckerGame.moveFigure(board, iterator.row, iterator.column, iterator.up, iterator.right);			
				CheckerGame.checkAndDoPromotion(board, (iterator.up ? iterator.row + 1 : iterator.row - 1), (iterator.right ? iterator.column + 1 : iterator.column - 1));
			}
		}
		
		if(this.boardChangeListener != null) {
			this.boardChangeListener.notify(board);
		}
		
		return true;
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

	/**
	 * Metoda koja "maksimalno" čisti memoriju. Uništava sve što je ova klasa
	 * stvorila i zauzela.
	 *
	 */
	public void Dispose() {

	}

	/**
	 * Dohvaća heuristiku ovog igrača
	 * @return heuristika igrača
	 */
	public IHeuristic getHeuristic() {
		return heuristic;
	}

	/**
	 * Postavlja heurstiku ovom igraču
	 * @param heuristic nova heuristika
	 */
	public void setHeuristic(IHeuristic heuristic) {
		this.heuristic = heuristic;
	}
}

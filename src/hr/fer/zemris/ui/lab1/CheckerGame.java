package hr.fer.zemris.ui.lab1;

/**
 * Klasa koja sadrži implementaciju pravila igranja dame
 */
public class CheckerGame {

	/** Bijela boja */
	public static boolean COLOR_WHITE = true;
	/**
	 * Crna boja 
	 */
	public static boolean COLOR_BLACK = false;
	
	/**
	 * Provjerava može li se zadana figurica fizički pomaknuti s pozicije u željenom smjeru
	 * @param board ploča na kojoj se igra
	 * @param currRow redak figurice
	 * @param currColumn stupac figurice
	 * @param up true ako želimo gore, false ako želimo dolje
	 * @param right true ako želimo desno, false ako želimo lijevo
	 * @return true ako se figurica može fizički pomaknuti, false inače
	 */
	public static boolean canMove(Square[][] board, int currRow, int currColumn, boolean up, boolean right) {
		// TODO: Primijeniti sentinele umjesto ovog
		if(currRow == 7 && up) return false;
		if(currRow == 0 && !up) return false;
		if(currColumn == 0 && !right) return false;
		if(currColumn == 7 && right) return false;
		
		Square current = board[currRow][currColumn];
		
		if(current.isPawn()) {
			if(up && current.getColor() == COLOR_BLACK) {
				return false;
			} else if(!up && current.getColor() == COLOR_WHITE) {
				return false;
			}
		}
		
		return board[currRow + (up ? 1 : -1)][currColumn + (right ? 1 : -1)].isEmpty();
	}

	/**
	 * Provjerava može li figura na zadanoj poziciji preskočiti u nekom smjeru.
	 * @param board ploča na kojoj se igra
	 * @param currRow redak figurice
	 * @param currColumn stupac figurice
	 * @param up true ako želimo gore, false ako želimo dolje
	 * @param right true ako želimo desno, false ako želimo lijevo
	 * @return true ako figurica može preskočiti, false inače
	 */
	public static boolean canJump(Square[][] board, int currRow, int currColumn, boolean up, boolean right) {
		// TODO: Primijeniti sentinele umjesto ovog
		if(currRow > 5 && up) return false;
		if(currRow < 2 && !up) return false;
		if(currColumn < 2 && !right) return false;
		if(currColumn > 5 && right) return false;
		
		Square current = board[currRow][currColumn];
		
		if(current.isPawn()) {
			if(up && current.getColor() == COLOR_BLACK) {
				return false;
			} else if(!up && current.getColor() == COLOR_WHITE) {
				return false;
			}
		}
		
		return board[currRow + (up ? 2 : -2)][currColumn + (right ? 2 : -2)].isEmpty() &&
				!board[currRow + (up ? 1 : -1)][currColumn + (right ? 1 : -1)].isEmpty() &&
				board[currRow + (up ? 1 : -1)][currColumn + (right ? 1 : -1)].getColor() != current.getColor();		
	}

	/**
	 * Skače zadanom figuricom u željenom smjeru. Ova metoda ne provjerava smije li
	 * i može li se skočiti.
	 * @param board ploča na kojoj se igra
	 * @param currRow redak figurice
	 * @param currColumn stupac figurice
	 * @param up true ako želimo gore, false ako želimo dolje
	 * @param right true ako želimo desno, false ako želimo lijevo
	 * @return figura koju smo preskočili
	 */	
	public static Square jumpFigure(Square[][] board, int currRow, int currColumn, boolean up, boolean right) {
		int newRow = currRow + (up ? 2 : -2);
		int newColumn = currColumn + (right ? 2 : -2);
		
		board[newRow][newColumn] = board[currRow][currColumn];
		Square jumpedOver = board[currRow + (up ? 1 : -1)][currColumn + (right ? 1 : -1)];
		board[currRow + (up ? 1 : -1)][currColumn + (right ? 1 : -1)] = Square.EMPTY;
		board[currRow][currColumn] = Square.EMPTY;
		
		return jumpedOver;
	}

	/**
	 * Pomiče figuricu u željenom smjeru. Ova metoda ne provjerava smije li
	 * i može li se figurica pomaknuti.
	 * @param board ploča na kojoj se igra
	 * @param currRow redak figurice
	 * @param currColumn stupac figurice
	 * @param up true ako želimo gore, false ako želimo dolje
	 * @param right true ako želimo desno, false ako želimo lijevo
	 */	
	public static void moveFigure(Square[][] board, int currRow, int currColumn, boolean up, boolean right) {
		int newRow = currRow + (up ? 1 : -1);
		int newColumn = currColumn + (right ? 1 : -1);
		board[newRow][newColumn] = board[currRow][currColumn];
		board[currRow][currColumn] = Square.EMPTY;		
	}
	
	/**
	 * Poništava skok zadane figurice. Ovoj metodi se trebaju predati isti
	 * parametri kao i metodi koja je skočila figuricom.
	 * @param board ploča na kojoj se igra
	 * @param currRow isto kao kod zadnjeg movea
	 * @param currColumn isto kao kod zadnjeg movea
	 * @param up isto kao kod zadnjeg movea
	 * @param right isto kao kod zadnjeg movea
	 * @param jumpedOverFigure figura koja je pojedena
	 */
	public static void unJumpFigure(Square[][] board, int currRow, int currColumn, boolean up, boolean right, Square jumpedOverFigure) {
		board[currRow][currColumn] = board[currRow + (up ? 2 : -2)][currColumn + (right ? 2 : -2)];
		board[currRow + (up ? 2 : -2)][currColumn + (right ? 2 : -2)] = Square.EMPTY;
		board[currRow + (up ? 1 : -1)][currColumn + (right ? 1 : -1)] = jumpedOverFigure;
	}

	/**
	 * Provjerava nalazi li se na predanom mjestu pijun koji treba postati kralj.
	 * Metoda promovira pijuna u kralja.
	 * @param board ploča na kojoj se igra
	 * @param row redak
	 * @param column stupac
	 * @return da li je došlo do promoviranja
	 */
	public static boolean checkAndDoPromotion(Square[][] board, int row, int column) {
		boolean whitePromotion = (board[row][column] == Square.WHITE_PAWN) && (row == 7);
		boolean blackPromotion = (board[row][column] == Square.BLACK_PAWN) && (row == 0);
		
		if(whitePromotion) {
			board[row][column] = Square.WHITE_KING;
		} else if(blackPromotion) {
			board[row][column] = Square.BLACK_KING;
		}
		
		return whitePromotion || blackPromotion;
	}

	/**
	 * Popunjava ploču za igru
	 * @return nova ploča, spremna za igru
	 */
	public static Square[][] fillBoard() {
		final int NUMBER_OF_TILES = 8;
	
		Square[][] board = new Square[NUMBER_OF_TILES][NUMBER_OF_TILES];
		
		for (int column = 0; column < 8; column++) {
			for (int row = 0; row < 8; row++) {
				boolean blackPlayer = (row < 3) && ((row + column) % 2 == 1);
				boolean whitePlayer = (row > NUMBER_OF_TILES - 1 - 3) && ((row + column) % 2 == 1);
				
				if(blackPlayer) {
					board[NUMBER_OF_TILES - 1 - row][column] = Square.BLACK_PAWN;
				} else if(whitePlayer) {
					board[NUMBER_OF_TILES - 1 - row][column] = Square.WHITE_PAWN;
				} else {
					board[NUMBER_OF_TILES - 1 - row][column] = Square.EMPTY;
				}
			}
		}
		
		return board;
	}
	
	/**
	 * Generira praznu ploču veličine 8x8
	 * @return prazna ploča
	 */
	public static Square[][] emptyBoard() {
		final int NUMBER_OF_TILES = 8;
		
		Square[][] board = new Square[NUMBER_OF_TILES][NUMBER_OF_TILES];
		
		for (int column = 0; column < 8; column++) {
			for (int row = 0; row < 8; row++) {
				board[row][column] = Square.EMPTY;
			}
		}
		
		return board;
	}
	
	/**
	 * Klonira ploču i vraća novu, kloniranu ploču
	 * @param board ploča
	 * @return klon ploče
	 */
	public static Square[][] copyOfBoard(Square[][] board) {
		Square[][] newBoard = new Square[8][8];
		
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				newBoard[row][column] = board[row][column];
			}
		}
		
		return newBoard;
	}
}

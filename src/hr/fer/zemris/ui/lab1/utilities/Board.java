package hr.fer.zemris.ui.lab1.utilities;

import java.io.Serializable;
import java.util.Arrays;

import hr.fer.zemris.ui.lab1.CheckerGame;
import hr.fer.zemris.ui.lab1.Square;

/**
 * Klasa predstavlja wrapper za igraću ploču
 */
public class Board implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** Igraća ploča umotana u klasu */
	private Square [][] board;
	
	/**
	 * Konstruktor, prima ploču koju će omotati
	 * @param board igraća ploča
	 */
	public Board(Square[][] board) {
		this.board = CheckerGame.copyOfBoard(board);
	}
	
	/**
	 * Dohvaća omotanu ploču
	 * @return ploča
	 */
	public Square[][] getBoard() {
		return board;
	}

	/**
	 * Postavlja omotanu ploču
	 * @param board ploča
	 */
	public void setBoard(Square[][] board) {
		this.board = CheckerGame.copyOfBoard(board);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Board)) {
			return false;
		}

		Board other = (Board) obj;
		
		for(int row = 0; row < 8; row++) {
			for(int column = 0; column < 8; column++) {
				if(this.board[row][column] != other.board[row][column]) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public int hashCode() {
		int hash = 0;
		
		for(int row = 0; row < 8; row++)
		{
			hash ^= Arrays.deepHashCode(board[row]);
		}
		
		return hash;
	}
}

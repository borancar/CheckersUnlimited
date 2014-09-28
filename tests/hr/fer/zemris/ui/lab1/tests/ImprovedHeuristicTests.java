package hr.fer.zemris.ui.lab1.tests;

import javax.swing.SwingUtilities;

import hr.fer.zemris.ui.lab1.CheckerGame;
import hr.fer.zemris.ui.lab1.GUI;
import hr.fer.zemris.ui.lab1.Square;
import hr.fer.zemris.ui.lab1.ImprovedPositionalHeuristic;
import hr.fer.zemris.ui.lab1.PositionalHeuristic;
import org.junit.Assert;
import org.junit.Test;

import Simulator.CheckerPlayer;

/**
 * Testira poboljšanu heuristiku.
 */
public class ImprovedHeuristicTests {
	
	/**
	 * Nadograđena heuristika bi trebala biti bolja od naivne heuristike,
	 * čak i za veća vremenska ograničenja.
	 */
	@Test
	public void testWhoIsBetter() {
		final GUI dama = new GUI();
		
		SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					dama.initGUI();
					dama.setVisible(true);
				}
			});
				
		CheckerPlayer whitePlayer = new CheckerPlayer();
		whitePlayer.Color = CheckerGame.COLOR_WHITE;
		whitePlayer.TimePerMove = 1000;
		whitePlayer.setHeuristic(new PositionalHeuristic() );
		whitePlayer.setBoardChangeListener(dama);
		
		CheckerPlayer blackPlayer = new CheckerPlayer();
		blackPlayer.Color = CheckerGame.COLOR_BLACK;
		blackPlayer.TimePerMove = 1000;
		blackPlayer.setHeuristic(new ImprovedPositionalHeuristic());
		blackPlayer.setBoardChangeListener(dama);
		
		Square[][] board = CheckerGame.fillBoard();

		dama.notify(board);
		
		while (whitePlayer.makeMove(board)) {
			if(!blackPlayer.makeMove(board)) {
				Assert.fail("Crni igrač je popušio!");
			}
		}
	}
}

package hr.fer.zemris.ui.lab1.tests;

import javax.swing.SwingUtilities;

import hr.fer.zemris.ui.lab1.CheckerGame;
import hr.fer.zemris.ui.lab1.GUI;
import hr.fer.zemris.ui.lab1.NaiveHeuristic;
import hr.fer.zemris.ui.lab1.PositionalHeuristic;
import hr.fer.zemris.ui.lab1.Square;
import org.junit.Assert;
import org.junit.Test;

import Simulator.CheckerPlayer;

/**
 * Testira poboljšanu heuristiku.
 */
public class PositionalHeuristicTests {
	
	/**
	 * Pozicijska heuristika bi trebala biti bolja od nadograđene heuristike,
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
		whitePlayer.setHeuristic( new NaiveHeuristic() );
		whitePlayer.setBoardChangeListener(dama);
		
		CheckerPlayer blackPlayer = new CheckerPlayer();
		blackPlayer.Color = CheckerGame.COLOR_BLACK;
		blackPlayer.TimePerMove = 1000;
		blackPlayer.setHeuristic(new PositionalHeuristic());
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

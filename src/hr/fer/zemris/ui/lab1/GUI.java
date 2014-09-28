package hr.fer.zemris.ui.lab1;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import Simulator.CheckerPlayer;

/**
 * Grafičko sučelje za igru dame. Grafičko sučelje prikazuje ploču na kojoj se
 * nalaze figurice.
 *
 */
public class GUI extends JFrame implements IBoardChangeListener {
	
	private static final long serialVersionUID = 1L;

	/** Grafički prikaz ploče za igranje */
	private final Ploca ploca = new Ploca();
	
	private JLabel statusLabel = new JLabel();
	
	/**
	 * Updater grafičkog sučelja.
	 */
	private class GUIUpdater implements Runnable {

		/** Ploča na kojoj se igra */
		private Square[][] board;
		
		/**
		 * Stvara novog osvježivača grafičkog sučelja
		 * @param board ploča na kojoj se igra
		 */
		public GUIUpdater(Square[][] board) {
			this.board = board;
		}

		public void run() {
			ploca.dodaj_polje_figurica(board);
		}
	}
	
	/**
	 * Defaulnit konstruktor, stvara jednostavno grafičko sučelje igre dame.
	 * Inicijalizira ploču na kojoj će se odvijati igra.
	 */
	public GUI() {
		ploca.initBoard();
		ploca.proslijedi_gui(this);
		
		CheckerPlayer helper = new CheckerPlayer();
		helper.Color = CheckerGame.COLOR_WHITE;
		helper.TimePerMove = 100;
		helper.setHeuristic(new PositionalHeuristic());
		
		ploca.setCheckerPlayerHelper(helper);
	}
	
	/**
	 * Metoda prima da li je igrač napravio najbolji potez i koji je
	 * najbolji potez
	 * @param bestMove najbolji potez, prema računalu
	 * @param player da li je igrač napravio taj potez
	 */
	public void setBestMove(Thought bestMove, boolean player) {
		if(player) {
			statusLabel.setText("Dobar potez!");
		} else {
			statusLabel.setText("Bolji potez je: " + bestMove);
		}
	}
	
	/**
	 * Inicijalizira grafičko sučelje.
	 */
	public void initGUI() {
		setTitle("Dama");
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(ploca, BorderLayout.CENTER);
		
		statusLabel.setText("Igra Dame. Autori: Boran Car, Domagoj Herceg, Božidar Radošević");

		getContentPane().add(statusLabel, BorderLayout.PAGE_END);
		
		setPreferredSize(new Dimension(500, 550));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
	}
	
	/**
	 * Metoda koja se poziva prilikom pokretanja programa.
	 * @param args argumenti komandne linije (ne koriste se)
	 */
	public static void main(String[] args) {
		final GUI dama = new GUI();
		
		CheckerPlayer igrac = new CheckerPlayer();
		igrac.setHeuristic(new PositionalHeuristic());
		igrac.Color = CheckerGame.COLOR_BLACK;
		igrac.TimePerMove = 200;
		
		dama.dodaj_igraca(igrac);
		igrac.setBoardChangeListener(dama);
		
		SwingUtilities.invokeLater(new Runnable() {
		
			@Override
			public void run() {
				dama.initGUI();
				dama.setVisible(true);	
			}
		});
	}

	@Override
	public void notify(Square[][] board) {
		SwingUtilities.invokeLater(new GUIUpdater(board));
	}
	
	public void dodaj_igraca(CheckerPlayer igrac) 
	{
		ploca.namjesti_igraca(igrac);
	}

}

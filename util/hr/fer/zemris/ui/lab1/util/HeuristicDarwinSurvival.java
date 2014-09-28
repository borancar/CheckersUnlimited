package hr.fer.zemris.ui.lab1.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import hr.fer.zemris.ui.lab1.CheckerGame;
import hr.fer.zemris.ui.lab1.IHeuristic;
import hr.fer.zemris.ui.lab1.ImprovedPositionalHeuristic;
import hr.fer.zemris.ui.lab1.NaiveHeuristic;
import hr.fer.zemris.ui.lab1.PositionalHeuristic;
import hr.fer.zemris.ui.lab1.Square;

import Simulator.CheckerPlayer;

/**
 * Klasa služi za prividno održavanje turnira među heuristikama kako bi se mogle odrediti
 * najbolje heuristike (s najboljim koeficijentima)
 * Klasa je ujedno i program koji se može pokrenuti iz komandne linije te se
 * može predati datoteka u koju se zapisuju najbolje heuristike.
 */
public class HeuristicDarwinSurvival {

	/** Broj rundi */
	private static final int NUM_OF_ROUNDS = 10;

	/** Broj igrača učesnika turnira u svakoj rundi */
	private static final int NUM_OF_PLAYERS = 10;

	/** Maksimalni broj poteza (po igraču) */
	private static final int MAX_MOVES = 60;
	
	/** Igra završila pobjedom bijelog igrača */
	private static final int WINNER_WHITE = 1;

	/** Igra završila pobjedom crnog igrača */
	private static final int WINNER_BLACK = -1;

	/** Igra završila neriješeno */
	private static final int DRAW = 0;

	/** Postotak najgorih heuristika koje treba maknuti nakon svake runde*/
	private static final float WORST_PERCENTAGE = 0.4f;

	/** Postotak najboljih heuristika koje treba spajati u nove nakon svake runde */
	private static final float BEST_PERCENTAGE = 0.2f;
	
	/** Postotak heuristika koje će mutirati u nove heuristike */
	private static final float MUTATION_PERCENTAGE = 0.1f;

	/** Broj najboljih heuristika čiji se koeficijenti zapisuju */
	private static final int DISPLAY_COUNT = 3;

	/** Dozvoljeno vrijeme po potezu igrača */
	private static final int TIME_PER_MOVE = 100;

	/** Generator slučajnih brojeva */
	private Random random = new Random();
	
	/** Bodovi pojedine heuristike */
	private HashMap<IHeuristic, Integer> values;
	
	/** Bijeli igrač (igrač bijelih figurica) */
	private CheckerPlayer whitePlayer = new CheckerPlayer();
	
	/** Crni igrač (igrač crnih figurica) */
	private CheckerPlayer blackPlayer = new CheckerPlayer();

	/** Lista heuristika koje se natječu */
	private List<IHeuristic> contestants = new ArrayList<IHeuristic>(NUM_OF_PLAYERS);
	
	/** Uobičajena naivna heuristika */
	private IHeuristic defaultNaiveHeuristic = new NaiveHeuristic();
	
	/** Uobičajena pozicijska heuristika */
	private IHeuristic defaultPositionalHeuristic = new PositionalHeuristic();
	
	/** Uobičajena napredna pozicijska heuristika */
	private IHeuristic defaultImprovedPositionalHeuristic = new ImprovedPositionalHeuristic();
	
	/**
	 * Uspoređivač heuristika za sortiranje heuristika prema tome koliko su
	 * dobre. Kriterij po kojem se uspoređuju je broj bodova koji se ostvaruje
	 * pobjedama/porazima. Heuristike su sortirane od najbolje prema najgoroj.
	 */
	private class HeuristicComparator implements Comparator<IHeuristic> {

		@Override
		public int compare(IHeuristic o1, IHeuristic o2) {
			if(o1 == null) return -1;
			if(o2 == null) return 1;
			
			return values.get(o1) - values.get(o2);
		}
	}
	
	/**
	 * Defaultni konstruktor, inicijalizira sve potrebno za
	 * "darwinističku" bitku među heuristikama
	 */
	public HeuristicDarwinSurvival() {
		values = new HashMap<IHeuristic, Integer>();
		whitePlayer.Color = CheckerGame.COLOR_WHITE;
		blackPlayer.Color = CheckerGame.COLOR_BLACK;
		whitePlayer.TimePerMove = TIME_PER_MOVE;
		blackPlayer.TimePerMove = TIME_PER_MOVE;
	}
	
	/**
	 * Stvara heuristiku iz "vedra neba" sa slučajno parametriranim koeficijentima
	 * @return heuristika sa slučajno parametriranim koeficijentima
	 */
	public IHeuristic createRandomHeuristic() {
		switch (random.nextInt(3)) {
		case 0:
			return NaiveHeuristic.getRandomHeuristic();

		case 1:
			return PositionalHeuristic.getRandomHeuristic();
			
		case 2:
			return ImprovedPositionalHeuristic.getRandomHeuristic();
			
		default:
			return null;
		}
	}
	
	/**
	 * Sukobljava 2 heuristike i bilježi bodove heuristikama. Heuristika koja
	 * pobijedi dobiva 1 bod, a ona koja izgubi gubi 1 bod
	 * @param whiteHeuristic heuristika bijelog igrača
	 * @param blackHeuristic heuristika crnog igrača
	 */
	public void fight(IHeuristic whiteHeuristic, IHeuristic blackHeuristic) {
		whitePlayer.setHeuristic(whiteHeuristic);
		blackPlayer.setHeuristic(blackHeuristic);
		
		int winner = playGame();
		
		switch (winner) {
		case WINNER_WHITE:
			values.put(whiteHeuristic, values.get(whiteHeuristic) + 1);
			values.put(blackHeuristic, values.get(blackHeuristic) - 1);			
			break;
			
		case WINNER_BLACK:
			values.put(blackHeuristic, values.get(blackHeuristic) + 1);
			values.put(whiteHeuristic, values.get(whiteHeuristic) - 1);						
			break;

		default:
			break;
		}
	}
	
	/**
	 * Igra igru 2 igrača (heuristika) i vraća tko je pobijedio
	 * @return 1 ako je pobijedio bijeli, -1 ako je pobijedio crni, 0 ako je neriješeno
	 */
	public int playGame() {
		Square[][] board = CheckerGame.fillBoard();
		
		for (int movePerPlayer = 0; movePerPlayer < MAX_MOVES; movePerPlayer++) {
			boolean whiteMadeMove = whitePlayer.makeMove(board);
			
			if(whiteMadeMove) {
				boolean blackMadeMove = blackPlayer.makeMove(board);
				
				if(!blackMadeMove) {
					System.out.println("Winner white");
					return WINNER_WHITE;
				}
			} else {
				System.out.println("Winner black");
				return WINNER_BLACK;
			}
		}
		
		System.out.println("Draw");
		return DRAW;
	}

	/**
	 * Održava turnir bez posebnog zapisivanja rezultata
	 */
	public void holdTournament() {
		try {
			holdTournament(null);
		} catch (IOException ignorable) {
		}
	}
	
	/**
	 * Kombinira najbolje igrače (heuristike) u nove igrače (heuristike)
	 * @param howMany koliko najboljih igrača kombinirati u nove
	 */
	private void mergeBestPlayersIntoNewPlayers(int howMany) {
		for (int best = 0; best < howMany; best++) {
			for (int otherBest = best+1; otherBest < howMany; otherBest++) {
				IHeuristic h1 = contestants.get(best);
				IHeuristic h2 = contestants.get(otherBest);
				IHeuristic merged = h1.merge(h2);
				
				if(merged != null) {
					addHeuristic(merged);
				}
			}
		}		
	}
	
	/**
	 * Dodaje igrača (heuristiku) na turnir
	 * @param heuristic igrač (heuristika)
	 */
	private void addHeuristic(IHeuristic heuristic) {
		contestants.add(heuristic);
		values.put(heuristic, 0);
	}

	/**
	 * Izbacuje igrače od predanog ranga na niže
	 * @param startingFrom rang od kojeg nadalje se izbacuju
	 */
	private void eliminateWorstPlayers(int startingFrom) {
		for (int rank = startingFrom; rank < contestants.size(); rank++) {
			IHeuristic contestant = contestants.get(rank);
			
			if(contestant != defaultPositionalHeuristic && contestant != defaultNaiveHeuristic &&
					contestant != defaultImprovedPositionalHeuristic) {
				contestants.remove(rank);
				values.remove(contestant);
			}
		}		
	}
	
	/**
	 * Održava turnir heuristika i rezultate zapisuje preko pisača (ukoliko je on
	 * različit od null)
	 * @param resultsWriter pisač rezultata
	 * @throws IOException u slučaju greške prilikom pisanja
	 */
	public void holdTournament(BufferedWriter resultsWriter) throws IOException {
		for (int i = 0; i < NUM_OF_PLAYERS - 3; i++) {
			addHeuristic(createRandomHeuristic());
		}
		
		addHeuristic(defaultImprovedPositionalHeuristic);
		addHeuristic(defaultNaiveHeuristic);
		addHeuristic(defaultPositionalHeuristic);
		
		HeuristicComparator heuristicComparator = new HeuristicComparator();
		
		if(resultsWriter != null) {
			resultsWriter.write("Params:"); resultsWriter.newLine();
			resultsWriter.write("Remove " + WORST_PERCENTAGE + " worst"); resultsWriter.newLine();
			resultsWriter.write("Merge " + BEST_PERCENTAGE + " best"); resultsWriter.newLine();
			resultsWriter.write("Mutate " + MUTATION_PERCENTAGE + " players"); resultsWriter.newLine();
			resultsWriter.write("Max " + MAX_MOVES + " moves per player before draw"); resultsWriter.newLine();
			resultsWriter.write("Players: " + NUM_OF_PLAYERS); resultsWriter.newLine();
			resultsWriter.write("Rounds: " + NUM_OF_ROUNDS); resultsWriter.newLine();
			resultsWriter.write("Display " + DISPLAY_COUNT + " best players per round"); resultsWriter.newLine();
			resultsWriter.write("Allowed time per move: " + TIME_PER_MOVE + " ms"); resultsWriter.newLine();
			resultsWriter.newLine();
		}
		
		for (int round = 0; round < NUM_OF_ROUNDS; round++) {
			random = new Random();
			
			System.out.println("Round " + (round+1) + ", " + contestants.size() + " players");
						
			mutatePlayers((int) (MUTATION_PERCENTAGE * contestants.size()));
			
			for (int contestant = 0; contestant < contestants.size(); contestant++) {
				values.put(contestants.get(contestant), 0);
			}
			
			for (int contestant1 = 0; contestant1 < contestants.size() - 1; contestant1++) {
				for (int contestant2 = contestant1 + 1; contestant2 < contestants.size(); contestant2++) {
					IHeuristic white = contestants.get(contestant1);
					IHeuristic black = contestants.get(contestant2);
					
					System.out.print("Fight (" + white + ", " + black + "): ");
					fight(white, black);
				}
			}
			
			Collections.sort(contestants, heuristicComparator);
			
			for (int i = contestants.size() - 1; i >= NUM_OF_PLAYERS; i--) {
				contestants.remove(i);
			}
			
			int populationSize = contestants.size();
			
			eliminateWorstPlayers((int)(populationSize * (1 - WORST_PERCENTAGE)));
			
			if(resultsWriter != null) {
				resultsWriter.write("Round " + (round+1) + " best playes:");
				resultsWriter.newLine();
			}
			
			for (int best = 0; best < Math.min(DISPLAY_COUNT, contestants.size()); best++) {
				IHeuristic bestHeuristic = contestants.get(best);

				System.out.println((best + 1) + ". values:");
				
				if(resultsWriter != null) {
					resultsWriter.write((best+1) + ".");
				}
				
				for (String line : bestHeuristic.describeHeuristic()) {
					System.out.println(line);

					if(resultsWriter != null) {
						resultsWriter.write("\t" + line);
						resultsWriter.newLine();
					}
				}
				
				if(resultsWriter != null) {
					resultsWriter.newLine();
				}
			}

			mergeBestPlayersIntoNewPlayers((int)(BEST_PERCENTAGE * populationSize));

			if(resultsWriter != null) {
				resultsWriter.newLine();
				resultsWriter.flush();
			}
			
			for (int add = contestants.size(); add < NUM_OF_PLAYERS; add++) {
				addHeuristic(createRandomHeuristic());
			}
		}
	}
	
	/**
	 * Mutira predani broj igrača
	 * @param howMany broj igrača koje treba mutirati
	 */
	private void mutatePlayers(int howMany) {
		for (int mutations = 0; mutations < howMany; mutations++) {
			addHeuristic(contestants.get(mutations).mutate());
		}
	}

	/**
	 * Metoda koja se poziva prilikom pokretanja programa
	 * @param args argumenti komandne linije (datoteka u koju se zapisuju rezultati)
	 */
	public static void main(String[] args) {
		HeuristicDarwinSurvival darwinSurvival = new HeuristicDarwinSurvival();
		
		if(args.length > 0) {
			System.out.println("Writing results to " + args[0]);
			BufferedWriter writer = null;
			
			try {
				writer = new BufferedWriter(new FileWriter(args[0]));
				
				darwinSurvival.holdTournament(writer);
			} catch (IOException e) {
				System.err.println(e.getLocalizedMessage());
			} finally {
				try {
					writer.close();
				} catch (IOException ignorable) {

				}
			}
		} else {
			System.out.println("Not writing results to output file. Call with filename as command line argument if desired");

			darwinSurvival.holdTournament();
		}
	}
}

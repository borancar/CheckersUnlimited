package hr.fer.zemris.ui.lab1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


import javax.swing.JComponent;

import Simulator.CheckerPlayer;

public class Ploca extends JComponent {

	private GUI gui;
	private CheckerPlayer helper;
	private CheckerPlayer igrac;
	private Square[][] board;
	
	private boolean[][] oni_koji_mogu_jesti = new boolean[8][8];
	private boolean postoji_figura_koja_moze_jest = false;
	private volatile boolean sprijeciti_igru_igraca = false;
	private boolean postalo_kralj = false;
	private boolean bio_je_skok = false;
	
	private boolean igra_se_s_bijelim = true;
	
	private static final long serialVersionUID = 1L;
	private static final int broj_kockica = 8;
	private Square[][] old_board = new Square[broj_kockica][broj_kockica];
	private boolean crtanje_figurica = false;
	private static final double offset_cross_percentage = 0.75;
	private static final double offset_cicle_percentage = 0.75;
	double offset = (1 - offset_cicle_percentage) / 2;
	private double omjer_x_naspran_duljina_prozora = 0;
	private double omjer_y_naspran_visina_prozora = 0;
	boolean jedan_klik = false;
	boolean napravljen_potez = false;
	boolean oznaciti_figuricu = false;
	boolean dopusten_potez = false;
	private int zadnji_donji_desni_kut = 0;
	private int zadnji_klik_x = 0;
	private int zadnji_klik_y = 0;
	
	public Ploca() {
		setPreferredSize(new Dimension(400, 400));
		addMouseListener(new MouseAdapter() {
		
			@Override
			public void mouseClicked(MouseEvent e) {
				
				if(!sprijeciti_igru_igraca)
				{
					igra_igrac(e.getX(), e.getY());
				}
			}
		});
	}
	
	public void setCheckerPlayerHelper(CheckerPlayer helper) {
		this.helper = helper;
	}
	
	protected void igra_igrac(int klik_x, int klik_y)
	{
		boolean klik_na_plocu = klik_na_plocu(klik_x, klik_y);
		
		if(klik_na_plocu && jedan_klik)
		{
			napravljen_potez = true;
		}
		else if(klik_na_plocu && !jedan_klik)
		{
			jedan_klik = !jedan_klik;
		}
		
		if(jedan_klik && klik_na_plocu && !napravljen_potez)
		{
			boolean oznacena_figurica;
			if(igra_se_s_bijelim)
			{
				oznacena_figurica = je_li_bijela_figurica(klik_x, klik_y);
			}
			else
			{
				oznacena_figurica = je_li_crna_figurica(klik_x, klik_y);	
			}
			if(oznacena_figurica)
			{
				oznaciti_figuricu = true;
				this.dodaj_oznacenu_figuricu(klik_x, klik_y);
				zadnji_klik_x = klik_x;
				zadnji_klik_y = klik_y;
			}
			else
			{
				oznaciti_figuricu = false;
				jedan_klik = false;
				napravljen_potez = false;
			}
		}
		else if(jedan_klik && klik_na_plocu && napravljen_potez)
		{
			boolean deselect = provjeri_deselct(klik_x, klik_y);
			boolean dozvoljen_pomak = moze_se_jesti();
			
			if(!deselect && dozvoljen_pomak)
			{ 
				bio_je_skok = false;
				postalo_kralj = false;
				
				boolean moze_skakakti_jos = false;
				boolean ucinjen_potez = obradi_igracev_potez(klik_x, klik_y);
				
				if(bio_je_skok)
				{
					moze_skakakti_jos = moze_dalje_skakati(klik_x, klik_y);
				}
				boolean igra_kompjuter = (ucinjen_potez && !moze_skakakti_jos) || postalo_kralj;
				
				if(igra_kompjuter)
				{
					obradi_kompjuterov_potez();
					
					oznaciti_figuricu = false;
					jedan_klik = false;
					napravljen_potez = false;
				}
			}
			else
			{
				oznaciti_figuricu = false;
				jedan_klik = false;
				napravljen_potez = false;
			}
		}
		else 
		{
			oznaciti_figuricu = false;	
		}
			
		this.repaint();
	}
	@Override
	protected void paintComponent(Graphics g) 
	{
		int kraj_vrha = g.getClipBounds().height;
		int kraj_ruba = g.getClipBounds().width;
		
		int gornji_lijevi_kut = 0;
		int donji_desni_kut = kraj_vrha <= kraj_ruba ? kraj_vrha : kraj_ruba;
		donji_desni_kut = (donji_desni_kut / broj_kockica) * broj_kockica;
		
		zadnji_donji_desni_kut = donji_desni_kut;
		
			crtaj_vertikanle_linije(g, gornji_lijevi_kut, donji_desni_kut);
			crtaj_horizontalne_linije(g, gornji_lijevi_kut, donji_desni_kut);
			obojaj_kvadratice(g, gornji_lijevi_kut, donji_desni_kut);
			crtati_figurice(g, gornji_lijevi_kut, donji_desni_kut);
			if(oznaciti_figuricu)
			{
				oznaciti_figuricu(g, gornji_lijevi_kut, donji_desni_kut);
			}
	}	
	protected void crtaj_horizontalne_linije(Graphics g, int gornji_lijevi_kut, int donji_desni_kut)
	{
		int horizontalne_linije = 0;
		int korak_y = donji_desni_kut / broj_kockica;
		
		for(horizontalne_linije = gornji_lijevi_kut; horizontalne_linije < donji_desni_kut; horizontalne_linije = horizontalne_linije + korak_y)
		{
			g.drawLine(gornji_lijevi_kut, horizontalne_linije, donji_desni_kut, horizontalne_linije);
		}
	}
	protected void crtaj_vertikanle_linije(Graphics g, int gornji_lijevi_kut, int donji_desni_kut)
	{
		int horizontalne_linije = 0;
		int korak_x = donji_desni_kut / broj_kockica;
		
		for(horizontalne_linije = gornji_lijevi_kut; horizontalne_linije < donji_desni_kut; horizontalne_linije = horizontalne_linije + korak_x)
		{
			g.drawLine(horizontalne_linije, gornji_lijevi_kut, horizontalne_linije, donji_desni_kut);
		}
	}
	protected void obojaj_kvadratice(Graphics g, int gornji_lijevi_kut, int donji_desni_kut)
	{
		int vertikali_pomak = 0;
		int horizontalni_pomak = 0;
		int relativni_donji_desni_kut_kvadratica = donji_desni_kut / broj_kockica;
		boolean bijela = true;
		
		for(vertikali_pomak = 0; vertikali_pomak < donji_desni_kut; vertikali_pomak = vertikali_pomak + relativni_donji_desni_kut_kvadratica)
		{
			bijela = !bijela;
			for(horizontalni_pomak = 0; horizontalni_pomak < donji_desni_kut; horizontalni_pomak = horizontalni_pomak + relativni_donji_desni_kut_kvadratica)
			{
				if(bijela)
				{
					g.setColor(Color.WHITE);
					bijela = !bijela;
				}
				else
				{
					g.setColor(Color.BLACK);
					bijela = !bijela;
				}
				g.fillRect(horizontalni_pomak, vertikali_pomak, relativni_donji_desni_kut_kvadratica, relativni_donji_desni_kut_kvadratica);
			}
		}
	}
	public void dodaj_oznacenu_figuricu(int x, int y)
	{
		if(zadnji_donji_desni_kut != 0 && x <= zadnji_donji_desni_kut)
		{
			omjer_x_naspran_duljina_prozora = x / (double)zadnji_donji_desni_kut;
		}
		
		if(zadnji_donji_desni_kut != 0 && y <= zadnji_donji_desni_kut)
		{
			omjer_y_naspran_visina_prozora = y / (double)zadnji_donji_desni_kut;
		}
		if(x > zadnji_donji_desni_kut || y > zadnji_donji_desni_kut)
		{
			oznaciti_figuricu = false;
		}
	}	
	protected void crtati_figurice(Graphics g, int gornji_lijevi_kut, int donji_desni_kut)
	{
		int vertikala = 0;
		int horizontala = 0;
		int korak = donji_desni_kut / broj_kockica;
		
		for(vertikala = 0; vertikala < donji_desni_kut; vertikala = vertikala + korak)
		{
			for(horizontala = 0; horizontala < donji_desni_kut; horizontala = horizontala + korak)
			{ 
				if(board[broj_kockica - 1 - vertikala/korak][horizontala/korak] != old_board[broj_kockica - 1 - vertikala/korak][horizontala/korak] || !crtanje_figurica)
				{
					switch(board[broj_kockica - 1 - vertikala/korak][horizontala/korak])
					{
						case WHITE_PAWN:
							crtaj_bijelog_pijuna(g, (int)(horizontala + korak * offset), (int)(vertikala + korak * offset), (int)(horizontala + korak * (1 - offset)), (int)(vertikala + korak * (1 - offset)));
							break;
						case WHITE_KING:
							crtaj_bijelog_kralja(g, (int)(horizontala + korak * offset), (int)(vertikala + korak * offset), (int)(horizontala + korak * (1 - offset)), (int)(vertikala + korak * (1 - offset)));
							break;
						case BLACK_KING:
							crtaj_crnog_kralja(g, (int)(horizontala + korak * offset), (int)(vertikala + korak * offset), (int)(horizontala + korak * (1 - offset)), (int)(vertikala + korak * (1 - offset)));
							break;
						case BLACK_PAWN:
							crtaj_crnog_pijuna(g, (int)(horizontala + korak * offset), (int)(vertikala + korak * offset), (int)(horizontala + korak * (1 - offset)), (int)(vertikala + korak * (1 - offset)));
							break;
						default:
							break;
					}
				}
			}
		}
	}
	protected void crtaj_bijelog_pijuna(Graphics g, int gornji_lijevi_kut_x, int gornji_lijevi_kut_y, int donji_desni_kut_x, int donji_desni_kut_y)
	{
		g.setColor(Color.LIGHT_GRAY);
		g.fillOval(gornji_lijevi_kut_x, gornji_lijevi_kut_y, donji_desni_kut_x - gornji_lijevi_kut_x, donji_desni_kut_y - gornji_lijevi_kut_y);
	}
	protected void crtaj_crnog_pijuna(Graphics g, int gornji_lijevi_kut_x, int gornji_lijevi_kut_y, int donji_desni_kut_x, int donji_desni_kut_y)
	{
		g.setColor(Color.DARK_GRAY);
		g.fillOval(gornji_lijevi_kut_x, gornji_lijevi_kut_y, donji_desni_kut_x - gornji_lijevi_kut_x, donji_desni_kut_y - gornji_lijevi_kut_y);
	}	
	protected void crtaj_bijelog_kralja(Graphics g, int gornji_lijevi_kut_x, int gornji_lijevi_kut_y, int donji_desni_kut_x, int donji_desni_kut_y)
	{
		int srediste_x = (donji_desni_kut_x - gornji_lijevi_kut_x) / 2;
		int srediste_y = (donji_desni_kut_y - gornji_lijevi_kut_y) / 2;
		
		int offset_x = (int)((donji_desni_kut_x - gornji_lijevi_kut_x) * (1 - offset_cross_percentage));
		int offset_y = (int)((donji_desni_kut_y - gornji_lijevi_kut_y) * (1 - offset_cross_percentage));
		
		g.setColor(Color.LIGHT_GRAY);
		g.fillOval(gornji_lijevi_kut_x, gornji_lijevi_kut_y, donji_desni_kut_x - gornji_lijevi_kut_x, donji_desni_kut_y - gornji_lijevi_kut_y);
	
		g.setColor(Color.GREEN);
		g.drawLine(gornji_lijevi_kut_x + srediste_x, gornji_lijevi_kut_y + offset_y, gornji_lijevi_kut_x + srediste_x, donji_desni_kut_y - offset_y);
		g.drawLine(gornji_lijevi_kut_x + offset_x, gornji_lijevi_kut_y + srediste_y, donji_desni_kut_x - offset_x, gornji_lijevi_kut_y + srediste_y);
	}	
	protected void crtaj_crnog_kralja(Graphics g, int gornji_lijevi_kut_x, int gornji_lijevi_kut_y, int donji_desni_kut_x, int donji_desni_kut_y)
	{
		int srediste_x = (donji_desni_kut_x - gornji_lijevi_kut_x) / 2;
		int srediste_y = (donji_desni_kut_y - gornji_lijevi_kut_y) / 2;
		
		int offset_x = (int)((donji_desni_kut_x - gornji_lijevi_kut_x) * (1 - offset_cross_percentage));
		int offset_y = (int)((donji_desni_kut_y - gornji_lijevi_kut_y) * (1 - offset_cross_percentage));
		
		g.setColor(Color.DARK_GRAY);
		g.fillOval(gornji_lijevi_kut_x, gornji_lijevi_kut_y, donji_desni_kut_x - gornji_lijevi_kut_x, donji_desni_kut_y - gornji_lijevi_kut_y);
	
		g.setColor(Color.GREEN);
		g.drawLine(gornji_lijevi_kut_x + srediste_x, gornji_lijevi_kut_y + offset_y, gornji_lijevi_kut_x + srediste_x, donji_desni_kut_y - offset_y);
		g.drawLine(gornji_lijevi_kut_x + offset_x, gornji_lijevi_kut_y + srediste_y, donji_desni_kut_x - offset_x, gornji_lijevi_kut_y + srediste_y);
	}
	protected void oznaciti_figuricu(Graphics g, int gornji_lijevi_kut, int donji_desni_kut)
	{
		int korak = donji_desni_kut / broj_kockica;
		
		int x_duljina = (int)(donji_desni_kut * omjer_x_naspran_duljina_prozora);
		int y_duljina = (int)(donji_desni_kut * omjer_y_naspran_visina_prozora);
		
		int kockica_x = (x_duljina / korak);
		int kockica_x_pocetak = kockica_x * korak;
		
		int kockica_y = (y_duljina / korak);
		int kockica_y_pocetak = kockica_y * korak;
		
		g.setColor(Color.BLUE);
		g.fillRect(kockica_x_pocetak, kockica_y_pocetak, korak, korak);
		
		switch(board[broj_kockica - 1 - kockica_y][kockica_x])
		{
			case WHITE_PAWN:
				crtaj_bijelog_pijuna(g, (int)(kockica_x_pocetak + korak * offset), (int)(kockica_y_pocetak + korak * offset), (int)(kockica_x_pocetak + korak * (1 - offset)), (int)(kockica_y_pocetak + korak * (1 - offset)));
				break;
			case WHITE_KING:
				crtaj_bijelog_kralja(g, (int)(kockica_x_pocetak + korak * offset), (int)(kockica_y_pocetak + korak * offset), (int)(kockica_x_pocetak + korak * (1 - offset)), (int)(kockica_y_pocetak + korak * (1 - offset)));
				break;
			case BLACK_KING:
				crtaj_crnog_kralja(g, (int)(kockica_x_pocetak + korak * offset), (int)(kockica_y_pocetak + korak * offset), (int)(kockica_x_pocetak + korak * (1 - offset)), (int)(kockica_y_pocetak + korak * (1 - offset)));
				break;
			case BLACK_PAWN:
				crtaj_crnog_pijuna(g, (int)(kockica_x_pocetak + korak * offset), (int)(kockica_y_pocetak + korak * offset), (int)(kockica_x_pocetak + korak * (1 - offset)), (int)(kockica_y_pocetak + korak * (1 - offset)));
				break;
			default:
				break;
		}
	}
	protected boolean klik_na_plocu(int klik_x, int klik_y)
	{
		if(klik_x < zadnji_donji_desni_kut && klik_y < zadnji_donji_desni_kut)
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
	/**
	 * 
	 * @param klik_x
	 * @param klik_y
	 * @return
	 */
	protected boolean obradi_igracev_potez(int klik_x, int klik_y)
	{
		boolean dozvola = false;
		final boolean nije_bio_potez = false;  
		final boolean bio_je_potez = true;  

		int korak = zadnji_donji_desni_kut / broj_kockica;
		
		int stupac_trenutacne_pozicije = (zadnji_klik_x / korak);
		int stupac_zeljene_pozicije = (klik_x / korak);
		
		int razlika_stupaca = 0;
		if(stupac_trenutacne_pozicije > stupac_zeljene_pozicije)
		{
			razlika_stupaca = stupac_trenutacne_pozicije - stupac_zeljene_pozicije;
		}
		else if(stupac_zeljene_pozicije > stupac_trenutacne_pozicije)
		{
			razlika_stupaca = stupac_zeljene_pozicije - stupac_trenutacne_pozicije;
		}
		
		int redak_trenutacne_pozicije = broj_kockica - ((zadnji_klik_y / korak) + 1);
		int redak_zeljene_pozicije = broj_kockica - ((klik_y / korak) + 1);
		
		int razlika_redaka = 0;
		if(redak_trenutacne_pozicije > redak_zeljene_pozicije)
		{
			razlika_redaka = redak_trenutacne_pozicije - redak_zeljene_pozicije;
		}
		else if(redak_zeljene_pozicije > redak_trenutacne_pozicije)
		{
			razlika_redaka = redak_zeljene_pozicije - redak_trenutacne_pozicije;
		}
		
		boolean smjer_up = redak_trenutacne_pozicije < redak_zeljene_pozicije;
		boolean smjer_right = stupac_trenutacne_pozicije < stupac_zeljene_pozicije;
		
		boolean skok = (razlika_stupaca == razlika_redaka) && (razlika_stupaca > 1);
		boolean pomak = (razlika_stupaca == razlika_redaka) && (razlika_stupaca == 1);
		
		
		dozvola = false;
		if(skok)
		{
			dozvola = CheckerGame.canJump(board, redak_trenutacne_pozicije, stupac_trenutacne_pozicije, smjer_up, smjer_right);
			
		}
		else if(pomak)
		{
			dozvola = CheckerGame.canMove(board, redak_trenutacne_pozicije, stupac_trenutacne_pozicije, smjer_up, smjer_right);
			
		}
		
		if(dozvola)
		{	
			if(skok)
			{
				Thought bestMove = helper.findBestMove(board);
				gui.setBestMove(bestMove, new Thought(redak_trenutacne_pozicije, stupac_trenutacne_pozicije, 0.0f, smjer_up, smjer_right, true).equals(bestMove));
				CheckerGame.jumpFigure(board, redak_trenutacne_pozicije, stupac_trenutacne_pozicije, smjer_up, smjer_right);
				
				postalo_kralj = CheckerGame.checkAndDoPromotion(board, redak_zeljene_pozicije, stupac_zeljene_pozicije);
				bio_je_skok = true;
				return bio_je_potez;
			}
			else if(pomak)
			{
				boolean moze_skociti = false;
				
				
				moze_skociti = moze_skociti || CheckerGame.canJump(board, redak_trenutacne_pozicije, stupac_trenutacne_pozicije, true, true);
				moze_skociti = moze_skociti || CheckerGame.canJump(board, redak_trenutacne_pozicije, stupac_trenutacne_pozicije, true, false);
				moze_skociti = moze_skociti || CheckerGame.canJump(board, redak_trenutacne_pozicije, stupac_trenutacne_pozicije, false, true);
				moze_skociti = moze_skociti || CheckerGame.canJump(board, redak_trenutacne_pozicije, stupac_trenutacne_pozicije, false, false);
				
				if(!moze_skociti)
				{
					Thought bestMove = helper.findBestMove(board);
					gui.setBestMove(bestMove, new Thought(redak_trenutacne_pozicije, stupac_trenutacne_pozicije, 0.0f, smjer_up, smjer_right, false).equals(bestMove));
					CheckerGame.moveFigure(board, redak_trenutacne_pozicije, stupac_trenutacne_pozicije, smjer_up, smjer_right);
					postalo_kralj = CheckerGame.checkAndDoPromotion(board, redak_zeljene_pozicije, stupac_zeljene_pozicije);
					return bio_je_potez;
				}
			}
		}
		return nije_bio_potez;
	}
	protected void obradi_kompjuterov_potez()
	{
		this.sprijeciti_igru_igraca = true;
		new Thread(new Runnable() {
		
			@Override
			public void run() {
				igrac.makeMove(board);
			}
		}).start();
	}
	public void dodaj_polje_figurica(Square[][] board)
	{
		this.board = CheckerGame.copyOfBoard(board);
		this.repaint();
		this.sprijeciti_igru_igraca = false;
	}	
	protected void namjesti_igraca(CheckerPlayer igrc)
	{
		this.igrac = igrc;
	}
	protected boolean provjeri_deselct(int klik_x, int klik_y)
	{
		int korak = zadnji_donji_desni_kut / broj_kockica;
		
		int stupac_trenutacne_pozicije = (zadnji_klik_x / korak);
		int stupac_zeljene_pozicije = (klik_x / korak);
		
		int redak_trenutacne_pozicije = broj_kockica - ((zadnji_klik_y / korak) + 1);
		int redak_zeljene_pozicije = broj_kockica - ((klik_y / korak) + 1);
		
		boolean isti_stupac = (stupac_trenutacne_pozicije == stupac_zeljene_pozicije);
		boolean isti_redak= (redak_trenutacne_pozicije == redak_zeljene_pozicije);
		
		return (isti_redak && isti_stupac);
	}
	protected boolean je_li_bijela_figurica(int klik_x, int klik_y)
	{
		boolean oznacena_figurica = false;
		
		int korak = zadnji_donji_desni_kut / broj_kockica;
		
		int stupac_trenutacne_pozicije = (klik_x / korak);
		int redak_trenutacne_pozicije = broj_kockica - ((klik_y / korak) + 1);
		
		oznacena_figurica = (board[redak_trenutacne_pozicije][stupac_trenutacne_pozicije] == Square.WHITE_PAWN || board[redak_trenutacne_pozicije][stupac_trenutacne_pozicije] == Square.WHITE_KING);
		
		return oznacena_figurica;
		
	}
	protected boolean je_li_crna_figurica(int klik_x, int klik_y)
	{
		boolean oznacena_figurica = false;
		
		int korak = zadnji_donji_desni_kut / broj_kockica;
		
		int stupac_trenutacne_pozicije = (klik_x / korak);
		int redak_trenutacne_pozicije = broj_kockica - ((klik_y / korak) + 1);
		
		oznacena_figurica = (board[redak_trenutacne_pozicije][stupac_trenutacne_pozicije] == Square.BLACK_PAWN || board[redak_trenutacne_pozicije][stupac_trenutacne_pozicije] == Square.BLACK_KING);
		
		return oznacena_figurica;
		
	}
	/**
	 * vraca da li je trenutacnoj figurici dozvoljen pomak
	 * @return
	 */
	protected boolean moze_se_jesti()
	{
		boolean dozvoljen_pomak = false;
		
		int korak = zadnji_donji_desni_kut / broj_kockica;
		
		int stupac_trenutacne_pozicije = (zadnji_klik_x / korak);
		int redak_trenutacne_pozicije = broj_kockica - ((zadnji_klik_y / korak) + 1);
		
		provjeri_tko_moze_jesti();
		if(postoji_figura_koja_moze_jest)
		{
			if(oni_koji_mogu_jesti[redak_trenutacne_pozicije][stupac_trenutacne_pozicije] == true)
			{
				dozvoljen_pomak = true;
			}
		}
		else
		{
			dozvoljen_pomak = true;
		}
		
		return dozvoljen_pomak;
	}
	/**
	 * Namješta globalne variable:
	 * puni globalno boolean polje oni_koji_mogu_jesti
	 * te namješta varijablu postoji_figura_koja_moze_jest
	 */
	protected void provjeri_tko_moze_jesti()
	{
		postoji_figura_koja_moze_jest = false;
		
		for(int stupac = 0; stupac < broj_kockica; stupac++)
		{
			for(int redak = 0; redak < broj_kockica; redak++)
			{
				boolean moze_jesti = false;
				boolean je_bijela_figura = false;
				boolean je_crna_figura = false;
				
				moze_jesti = moze_jesti || CheckerGame.canJump(board, redak, stupac, true, true);
				moze_jesti = moze_jesti || CheckerGame.canJump(board, redak, stupac, true, false);
				moze_jesti = moze_jesti || CheckerGame.canJump(board, redak, stupac, false, true);
				moze_jesti = moze_jesti || CheckerGame.canJump(board, redak, stupac, false, false);
				
				je_bijela_figura = (board[redak][stupac] == Square.WHITE_PAWN || board[redak][stupac] == Square.WHITE_KING);
				je_crna_figura = (board[redak][stupac] == Square.BLACK_PAWN || board[redak][stupac] == Square.BLACK_KING);
				
				if(igra_se_s_bijelim)
				{
					moze_jesti = moze_jesti && je_bijela_figura;
				}
				else
				{
					moze_jesti = moze_jesti && je_crna_figura;
				}
				
				if(moze_jesti)
				{
					oni_koji_mogu_jesti[redak][stupac] = true;
					postoji_figura_koja_moze_jest = true;
				}
				else
				{
					oni_koji_mogu_jesti[redak][stupac] = false;	
				}
			}
		}
	}
	protected boolean moze_dalje_skakati(int klik_x, int klik_y)
	{
		int korak = zadnji_donji_desni_kut / broj_kockica;
		
		int stupac = (klik_x / korak);
		int redak = broj_kockica - ((klik_y / korak) + 1);
		
		boolean moze_skociti = false;
		
		moze_skociti = moze_skociti || CheckerGame.canJump(board, redak, stupac, true, true);
		moze_skociti = moze_skociti || CheckerGame.canJump(board, redak, stupac, true, false);
		moze_skociti = moze_skociti || CheckerGame.canJump(board, redak, stupac, false, true);
		moze_skociti = moze_skociti || CheckerGame.canJump(board, redak, stupac, false, false);
		
		return moze_skociti;
	}
	public void initBoard() {
		this.board = CheckerGame.fillBoard();
	}
	public void proslijedi_gui(GUI prolsijedeno)
	{
		gui = prolsijedeno;
	}
	public void setPlayerColor(boolean color)
	{
		igra_se_s_bijelim = color;
		igrac.Color = !color;
	}
}
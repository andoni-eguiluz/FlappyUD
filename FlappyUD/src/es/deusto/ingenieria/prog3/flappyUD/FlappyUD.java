package es.deusto.ingenieria.prog3.flappyUD;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JLabel;

import es.deusto.ingenieria.prog3.utils.ventanas.ventanaJuego.*;
import es.deusto.ingenieria.prog3.utils.ventanas.ventanaJuego.eventosVentanaGrafica.*;
import es.deusto.ingenieria.prog3.utils.ventanas.ventanaJuego.img.Img;

/** Juego FlappyUD versión 1.01
 * @author eguiluz
 */
public class FlappyUD {
	// constantes generales del juego
	public static final int PX_ALTO_VENT = 550;
	public static final int PX_ANCHO_VENT = 1000;
	public static final int PX_ALTO_UD = 81;
	public static final int PX_ANCHO_UD = 60;
	public static final int PX_PEGADOS_COLUMNA = 60;  // Pixels de distancia entre escudos en la columna
	public static final int PROTA_X = 50;  // Coord X del protagonista 
	public static final double ACEL_CAIDA = -650.0;  // Pixels / sg^2
	public static final double VEL_SALTO = 300;    // Pixels / sg
	public static final int NUM_VIDAS_EXTRA = 3;   // Número de vidas extra
	public static final float PROBABILIDAD_BONUS = 0.2F;   // Sale un 20% de las veces
	public static final int PUNTOS_POR_BONUS = 5;  // Puntos por bonus cogido
	
	// Atributos de gestión de niveles
	public static final long TIEMPO_AVANCE_NIVEL = 10000;  // msg
	
	public static final double INI_VELOCIDAD_POR_NIVEL = 180;  // pix/seg
	public static final long INI_TIEMPO_COLS_POR_NIVEL = 2000;  // msg
	public static final int INI_HUECO_ENTRE_COLUMNAS = 250;  // pixels de alto de hueco
	public static final double INC_VELOCIDAD_POR_NIVEL = 6;  // +pix/seg
	public static final long  INC_TIEMPO_COLS_POR_NIVEL = -33;  // msg
	public static final int INC_HUECO_ENTRE_COLUMNAS = -4;  // +pixels de alto de hueco
	public static final double MAX_VELOCIDAD_POR_NIVEL = 360;  // +pix/seg
	public static final long MIN_TIEMPO_COLS_POR_NIVEL = 1000;  // msg
	public static final int MIN_HUECO_ENTRE_COLUMNAS = 140;  // +pixels de alto de hueco
	
	// atributos privados que varían según el nivel
	private static double velAvance = INI_VELOCIDAD_POR_NIVEL;
	private static long tiempoEntreColumnas = INI_TIEMPO_COLS_POR_NIVEL;
	private static int huecoEntreColumnas = INI_HUECO_ENTRE_COLUMNAS;
	
	// atributos de juego
	private static int numVidas;

	/**
	 * @return el huecoEntreColumnas en pixels
	 */
	public static int getHuecoEntreColumnas() {
		return huecoEntreColumnas;
	}

	/**
	 * @param huecoEntreColumnas nuevo huecoEntreColumnas en pixels
	 */
	public static void setHuecoEntreColumnas(int huecoEntreColumnas) {
		FlappyUD.huecoEntreColumnas = huecoEntreColumnas;
	}
	
	/** Devuelve la velocidad de avance del juego
	 * @return	Velocidad en pixels por segundo
	 */
	public static double getVelAvance() {
		return velAvance;
	}
	
	/** Cambia la velocidad de avance del juego 
	 * @param nuevaVelAvance	Nueva velocidad (en pixels/sg)
	 */
	public static void setVelAvance( double nuevaVelAvance ) {
		velAvance = nuevaVelAvance;
	}
	
	/** Devuelve el tiempo que pasa entre salida de columnas
	 * @return	tiempo en milisegundos
	 */
	public static long getTiempoEntreCols() {
		return tiempoEntreColumnas;
	}
	
	/** Cambia el tiempo que pasa entre salida de columnas
	 * @param nuevoTiempo	Nuevo tiempo (en milisegundos)
	 */
	public static void setTiempoEntreCols( long nuevoTiempo ) {
		tiempoEntreColumnas = nuevoTiempo;
	}

		// Control de jugador y acción sobre el juego
		private static void controlDeJugador( EventoVentana ev, UDcito ud ) {
			if (ev != null) {
				if (ev instanceof RatonPulsado || ev instanceof TeclaPulsada) {
					// Si se pulsa
					ud.saltar();
				}
			}
		}
		
		// Mueve y quita cols si procede. Devuelve true si quitada una columna
		private static boolean moverYQuitar( VentanaGrafica v, UDcito ud, ArrayList<ColumnaUD> columnas ) {
			ud.mover();
			for (int i=0; i<columnas.size(); i++) {
				columnas.get(i).mover();
			}
			if (!columnas.isEmpty() && 
					((ColumnaUD)columnas.get(0)).estoyFuera()) {   // Método solo de columna
				columnas.remove(0).quitar();
				columnas.remove(0).quitar();
				return true;
			}
			return false;
		}
		
		// Comprueba choques de udcito con columnas. 
		// 0 -> no hay choque.  1 -> choque vertical.  2 -> choque no vertical
		private static int comprobarChoque( UDcito ud, ArrayList<ColumnaUD> columnas ) {
			for (int i=0; i<columnas.size(); i++) {
				ColumnaUD c = (ColumnaUD) columnas.get(i);
				for (int j=0; j<c.getObjetosGraficos().size(); j++) {
					ObjetoGrafico o = (ObjetoGrafico) c.getObjetosGraficos().get(j);
					if (ud.getObjetoGrafico().chocaCon(o, 5)) {  // 5 pxs de margen 
						// System.out.println( "CHOQUE!!!");
						int tipoChoque = ud.getObjetoGrafico().comoChocaCon(o, 5);
						if (tipoChoque <= 8 && (tipoChoque != 3)) { // Vertical o izquierda
							ud.chocar( c );
							return 1;
						} else {  // Choque no vertical: fin
							return 2;
						}
					}
				}
			}
			return 0;
		}
		
		// Comprueba choques de udcito con bonuses, y quita los bonus si están en la izquierda. 
		// null -> no hay choque  no null -> hay choque con el bonus que se devuelve
		private static BonusUD comprobarChoqueBonusYQuitar( UDcito ud, ArrayList<BonusUD> bonus, ArrayList<ColumnaUD> columnas ) {
			if (!bonus.isEmpty() && 
					(bonus.get(0)).estoyFuera()) {
				bonus.remove(0).quitar();
			}
			BonusUD ret = null;
			for (BonusUD b : bonus) {
				for (ColumnaUD c : columnas) b.arrastrarConColumna(c);
				ObjetoGrafico o = b.getObjetoGrafico();
				if (ud.getObjetoGrafico().chocaCon(o, 0)) {  // 0 pxs de margen 
					// System.out.println( "CHOQUE CON BONUS!!!");
					ret = b;
				}
			}
			return ret;
		}
		
		// Crea nuevas columnas a la derecha
		private static void crearNuevasColumnas( VentanaGrafica v, int huecoEn, int numColumna, ArrayList<ColumnaUD> columnas ) {
			ColumnaUD cArriba = new ColumnaUD( numColumna, true, huecoEn, v.getAnchoPanelGrafico(), v );
			ColumnaUD cAbajo = new ColumnaUD( numColumna, false, huecoEn + getHuecoEntreColumnas(), v.getAnchoPanelGrafico(), v );
			columnas.add( cArriba );
			columnas.add( cAbajo );
		}
		
		// Incrementar nivel
		private static void incrementaNivel() {
			velAvance += INC_VELOCIDAD_POR_NIVEL;
			if (velAvance > MAX_VELOCIDAD_POR_NIVEL) velAvance = MAX_VELOCIDAD_POR_NIVEL;
			tiempoEntreColumnas += INC_TIEMPO_COLS_POR_NIVEL;
			if (tiempoEntreColumnas < MIN_TIEMPO_COLS_POR_NIVEL) tiempoEntreColumnas = MIN_TIEMPO_COLS_POR_NIVEL;
			huecoEntreColumnas += INC_HUECO_ENTRE_COLUMNAS;
			if (huecoEntreColumnas < MIN_HUECO_ENTRE_COLUMNAS) huecoEntreColumnas = MIN_HUECO_ENTRE_COLUMNAS;
		}
	
	// Método principal de juego
	private static void bucleDeJuego() {
		// ============================
		// 0.1. Inicialización general
		// ============================
		VentanaGrafica v = new VentanaGrafica(PX_ANCHO_VENT, PX_ALTO_VENT, 0, true, true, false, "FlappyUD" );
		v.setFondoAnimado(new ObjetoGrafico( Img.getURLRecurso( "UD-roller.jpg" ), true ),
				new ObjetoGrafico( Img.getURLRecurso( "UD-roller.jpg" ), true ), 1 );
		int posY = (v.getAltoPanelGrafico() - PX_ALTO_UD) / 2;   // Cálculo altura del prota en la mitad de la ventana
		Random r = new Random();
		EventoVentana ev = null;
		int mensVidas = v.nuevaZonaMensajes( 0, 10, PX_ANCHO_VENT-10, JLabel.RIGHT, new Font( "Arial", Font.BOLD, 30 ), Color.red, Color.white );
		v.showMessage( "Preparados...  (USA EL BOTÓN IZQUIERDO O LA TECLA CTRL)" );
		v.readEvento( 3000 );
		v.showMessage( "Listos...  (USA EL BOTÓN IZQUIERDO O LA TECLA CTRL)" );
		v.readEvento( 3000 );
		int nivel = 1;
		do {
			// ============================
			// 0.2. Inicialización de cada game reply
			// ============================
			v.showMessage( "Empezamos!!!!" );
			v.rodarFondoAnimado( true );
			numVidas = NUM_VIDAS_EXTRA;
			v.showMessage( "***", mensVidas );
			ev = null;
			v.clearObjetos();
			UDcito ud = new UDcito( PROTA_X, posY, v );
			ArrayList<ColumnaUD> columnas = new ArrayList<ColumnaUD>();
			ArrayList<BonusUD> bonus = new ArrayList<BonusUD>();
			boolean puedeTocarBonus = false;  // no sale hasta después de la primera columna
			int puntosPorBonus = 0;
			long tiempoJuego = System.currentTimeMillis();
			long tiempoUltimaColumna = tiempoJuego - tiempoEntreColumnas;
			int numColumna = 0;
			int columnasPasadas = 0;
			long tiempoUltimoAvanceNivel = tiempoJuego;
			int columnaChoque = -1;
			// Bucle principal de juego
			while (!v.isClosed() && !ud.estoyMuerto()) {
				System.out.println( System.currentTimeMillis() );
				// ============================
				// 1. Input
				// ============================
				long tiempoActual = System.currentTimeMillis();
				ev = v.readEvento( 20 );  // Lee evento o espera 20 msg  (algo moviéndose)
				
				// ============================
				// 2. Update
				// ============================
					// 1. Chequear control del escudo
				controlDeJugador( ev, ud );
					// 2. Mover escudo y columnas y quitar columnas si se salen
				if (moverYQuitar( v, ud, columnas )) {
					columnasPasadas++;
					v.showMessage( "Columnas pasadas: " + columnasPasadas + " (nivel " + nivel + ")" );
				}
				for (BonusUD b : bonus) b.mover();
					// 3. Ver si hay choque del escudo
				int chocan = comprobarChoque( ud, columnas );
				if (chocan == 1) {  // Choque vertical: se quitan vidas
					ud.parar();
					if (columnaChoque != numColumna) {
						columnaChoque = numColumna;
						numVidas--;
						if (numVidas>=0) {
							v.showMessage( new String(new char[numVidas]).replace("\0", "*"), mensVidas );  // 'n' *s (una manera de hacerlo -en java no hay String.repeat o algo parecido-)
							v.showMessage( "Perdiste una vida!!" );
						} else
							ud.muero();
					}
				} else if (chocan == 2) {  // Choque no vertical: muerte segura
					v.showMessage(" ",  mensVidas);
					ud.muero();
				}
					// 4. Ver si hay captura de bonus
				BonusUD choqueBonus = comprobarChoqueBonusYQuitar( ud, bonus, columnas );
				if (choqueBonus != null) {  // Choque con bonus
					bonus.remove( choqueBonus );
					choqueBonus.muero();
					choqueBonus.quitar();
					v.showMessage( "BONUS!!!  " + PUNTOS_POR_BONUS + " puntos más" );
					puntosPorBonus += PUNTOS_POR_BONUS;
				}
					// 5. Ver si salen nuevas columnas
				if (tiempoActual - tiempoUltimaColumna > getTiempoEntreCols()) {  // Crear nuevas columnas
					// Calcular sitio del hueco en random
					int huecoEn = r.nextInt( v.getAltoPanelGrafico() - getHuecoEntreColumnas() - 20 ) + 10;
					numColumna++;
					tiempoUltimaColumna = tiempoActual;
					crearNuevasColumnas( v, huecoEn, numColumna, columnas );
					puedeTocarBonus = true;  // preparamos para siguiente bonus
				}
					// 6. Ver si salen nuevos bonus (aleatorios y entre columnas)
				if (puedeTocarBonus) {
					if (tiempoActual - tiempoUltimaColumna > getTiempoEntreCols()/2 ) {  
						// En la mitad del tiempo entre columnas...
						// Ver si pasa el aleatorio (20%)
						if (r.nextFloat() < PROBABILIDAD_BONUS ) {
							bonus.add( new BonusUD( v.getAnchoPanelGrafico(), r.nextInt( v.getAltoPanelGrafico() ), v) );
						}
						puedeTocarBonus = false;  // Y ya no puede tocar hasta la siguiente columna
					}
				}
					// 7. Posible avance nivel
				if (tiempoActual - tiempoUltimoAvanceNivel > TIEMPO_AVANCE_NIVEL) {
					tiempoUltimoAvanceNivel = tiempoActual;
					incrementaNivel();
					nivel++;
					v.showMessage( "Avanzas de nivel!!! Nivel " + nivel );
				}

					// 8. Rotación bonus
				for (BonusUD b : bonus) b.getObjetoGrafico().incRotacion( 0.06 );
				
				// ============================
				// 3. Render
				// ============================
				v.repaint();   // Realmente como está implementado en swing no haría falta porque se va actualizando directamente (JLabel, setLocation)
				
			}
			// ============================
			// 4.1. Cierre de cada gameplay
			// ============================
			v.rodarFondoAnimado( false );
			int gradosRot = 0;
			while (gradosRot < 360) {
				gradosRot += 10;
				ud.getObjetoGrafico().setRotacionGrados( gradosRot );
				v.esperaUnRato( 20 );
			}
			v.showMessage( "Se acabó!!!!  Puntuación = " + (columnasPasadas + puntosPorBonus) );
			v.esperaUnRato( 2000 );
			v.borraEventos();
			v.showMessage( "<html>Pulsa antes de 10\" si quieres seguir jugando<br> (tu puntuación empezará en 0 de nuevo)</html>" );
			ev = null; 
			ev = v.readEvento( 10000 );
		} while (ev != null && (ev instanceof EventoRaton || ev instanceof EventoTeclado));
		// ============================
		// 4.2. Cierre del juego
		// ============================
		v.finish();
	}
	
	/** Método principal del juego FlappyUD.
	 * @param args	No utilizado
	 */
	public static void main(String[] args) {
		bucleDeJuego();
	}
}
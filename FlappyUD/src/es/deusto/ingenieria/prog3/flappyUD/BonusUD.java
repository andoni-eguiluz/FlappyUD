package es.deusto.ingenieria.prog3.flappyUD;

import java.awt.Point;
import java.util.Random;

import es.deusto.ingenieria.prog3.utils.ventanas.ventanaJuego.*;
import es.deusto.ingenieria.prog3.utils.ventanas.ventanaJuego.img.Img;

public class BonusUD extends ObjetoFlappyUD {
	protected ObjetoGraficoRotable og;
	protected double velVertical = 0D;
	protected double velHorizontal = 0D;
	protected boolean estoyMuerto = false;
	protected boolean parado = false;
	
	public static final int PX_ALTO_BONUS = 40;
	public static final int PX_ANCHO_BONUS = 30;
	private static final double MAX_VEL_VERTICAL = 100;   // px/seg
	private static final double MAX_VEL_HORIZONTAL = 10;  // px/seg
	private static final double MAX_CAMBIO_ALEAT_VEL = 5; // px/seg
	
	private static Random r = new Random();
	
	/** Crea el bonus del juego FlappyUD
	 * con velocidad de inicio aleatoria
	 * @param posX	Posición del objeto en coordenada X de la ventana
	 * @param posY	Posición del objeto en coordenada Y de la ventana
	 * @param ventana	Ventana gráfica en la que se integra
	 */
	public BonusUD( int posX, int posY, VentanaGrafica ventana ) {
		// Coge el recurso girable que es el doble de ancho y alto para dejar aire en los lados
		super( posX, posY, PX_ANCHO_BONUS*2, PX_ALTO_BONUS*2, ventana );
		og = new ObjetoGraficoRotable( Img.getURLRecurso( "UD-star-girable.png" ), 
				true, PX_ANCHO_BONUS*2, PX_ALTO_BONUS*2, 0 );
		og.setName( "bonus" );
		og.setRectanguloDeChoque( PX_ANCHO_BONUS/2, PX_ALTO_BONUS/2, og.getAnchuraObjeto()-PX_ANCHO_BONUS/2, og.getAlturaObjeto()-PX_ALTO_BONUS/2 );
		velHorizontal = -FlappyUD.getVelAvance() + (r.nextFloat() * MAX_VEL_HORIZONTAL * 2 - MAX_VEL_HORIZONTAL);  // (de -max a +max aleatorio) sobre la velocidad que ya tenga la columna
		velVertical = (r.nextFloat() * MAX_VEL_VERTICAL * 2 - MAX_VEL_VERTICAL);        // (de -max a +max aleatorio)
		ventana.addObjeto( og, new Point( posX, posY ) );
	}

	/** Devuelve el objeto gráfico del bonus
	 * @return	Objeto gráfico rotable
	 */
	public ObjetoGraficoRotable getObjetoGrafico() {
		return og;
	}
	
	/** Indica si el bonus está muerto
	 * @return	true si está muerto, false en caso contrario
	 */
	public boolean estoyMuerto() {
		return this.estoyMuerto;
	}
	
	@Override
	public void quitar() {
		muero();
		ventana.removeObjeto(og);
	}
	
	public void muero() {
		estoyMuerto = true;
	}

	@Override
	public void mover() {
		// Mueve y modifica un poquito su velocidad aleatoriamente
		if (!estoyMuerto && !parado) {
			long tiempoCambio = System.currentTimeMillis() - tiempoMovimiento;
			posY = posY + tiempoCambio * velVertical / 1000D;
			posX = posX + tiempoCambio * velHorizontal / 1000D;
			int posYNueva = (int) Math.round( posY );
			int posXNueva = (int) Math.round( posX );
			if (og.getY() != posYNueva || og.getX() != posXNueva ) {
				if (posYNueva > FlappyUD.PX_ALTO_VENT-(FlappyUD.PX_ALTO_UD/2) ||
					posYNueva < - 3*FlappyUD.PX_ALTO_UD/2){  // Si se ha salido por abajo o arriba rebota
					velVertical = -velVertical;
				} else {  // Si no, se mueve
					og.setLocation( posXNueva, posYNueva );
				}
			}
			// Cambio aleatorio velocidad
			velHorizontal += (r.nextFloat() * MAX_CAMBIO_ALEAT_VEL * 2 - MAX_CAMBIO_ALEAT_VEL);  // (de -max a +max aleatorio)
			velVertical += (r.nextFloat() * MAX_CAMBIO_ALEAT_VEL * 2 - MAX_CAMBIO_ALEAT_VEL);    // (de -max a +max aleatorio)
		}
		tiempoMovimiento = System.currentTimeMillis();
	}
	
	public void parar( boolean parar ) {
		parado = parar;
		if (parado) {
			velVertical = 0;
			velHorizontal = 0;
		}
	}
	
	public void arrastrarConColumna( ColumnaUD col ) {
		for (ObjetoGrafico ogCol : (col).getObjetosGraficos()) {
			int choca = og.comoChocaCon( ogCol, 0 );
			if (choca != 0) {
				if (choca >= 2 && choca <= 4) {  // Choque derecha
					og.setLocation( -2 + og.getX(), og.getY() );
					velHorizontal = -10 - FlappyUD.getVelAvance();
					posX = og.getX();
				} else if (choca >= 6 && choca <= 8) {   // Choque izquierda
					og.setLocation( ogCol.getX() + col.getAncho() - og.getWidth()/4, og.getY() );
					velHorizontal = 10 - FlappyUD.getVelAvance();
					posX = og.getX();
				} else if (choca == 1) {  // Choque arriba
					if (velVertical < 0 ) velVertical = +10;
					posY = og.getY();
				} else {   // Choque abajo
					if (velVertical > 0 ) velVertical = -10;
					posY = og.getY();
				}
			}
		}
	}

	/** Consulta si la columna se ha salido por la izquierda
	 * @return	true si se ha salido, false si todavía está en pantalla
	 */
	public boolean estoyFuera() {
		return (posX < ancho/2);
	}
	
	
}
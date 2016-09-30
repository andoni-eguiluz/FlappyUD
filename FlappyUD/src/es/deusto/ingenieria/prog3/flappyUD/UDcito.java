package es.deusto.ingenieria.prog3.flappyUD;

import java.awt.Point;

import es.deusto.ingenieria.prog3.utils.ventanas.ventanaJuego.*;
import es.deusto.ingenieria.prog3.utils.ventanas.ventanaJuego.img.Img;

public class UDcito extends ObjetoFlappyUD {
	protected ObjetoGraficoRotable og;
	protected double velHaciaArriba = 0D;
	protected boolean estoyMuerto = false;
	
	/** Crea el protagonista del juego FlappyUD
	 * @param posX	Posición del objeto en coordenada X de la ventana
	 * @param posY	Posición del objeto en coordenada Y de la ventana
	 * @param ventana	Ventana gráfica en la que se integra
	 */
	public UDcito( int posX, int posY, VentanaGrafica ventana ) {
		// Coge el recurso girable que es el doble de ancho y alto para dejar aire en los lados
		super( posX, posY, FlappyUD.PX_ANCHO_UD*2, FlappyUD.PX_ALTO_UD*2, ventana );
		og = new ObjetoGraficoRotable( Img.getURLRecurso( "UD-blue-girable.png" ), 
				true, FlappyUD.PX_ANCHO_UD*2, FlappyUD.PX_ALTO_UD*2, 0 );
		og.setName( "udCito" );
		og.setRectanguloDeChoque( FlappyUD.PX_ANCHO_UD/2, FlappyUD.PX_ALTO_UD/2, og.getAnchuraObjeto()-FlappyUD.PX_ANCHO_UD/2, og.getAlturaObjeto()-FlappyUD.PX_ALTO_UD/2 );
		ventana.addObjeto( og, new Point( posX, posY ) );
	}

	/** Devuelve el objeto gráfico del udcito
	 * @return	Objeto gráfico rotable
	 */
	public ObjetoGraficoRotable getObjetoGrafico() {
		return og;
	}
	
	/** Indica si udcito está muerto
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
		if (!estoyMuerto) {
			long tiempoCambio = System.currentTimeMillis() - tiempoMovimiento;
			tiempoMovimiento = System.currentTimeMillis();
			velHaciaArriba = velHaciaArriba + FlappyUD.ACEL_CAIDA * tiempoCambio / 1000D;  // cambio de velocidad con la aceleración
			posY = posY - tiempoCambio * velHaciaArriba / 1000D;
			int posYNueva = (int) Math.round( posY );
			if (og.getY() != posYNueva) {
				if (posYNueva > FlappyUD.PX_ALTO_VENT-(FlappyUD.PX_ALTO_UD/2) ||
					posYNueva < - 3*FlappyUD.PX_ALTO_UD/2){  // Si se ha salido por abajo se quita (se muere)
					muero();
					quitar();
				} else {  // Si no, se mueve
					og.setLocation( og.getX(), posYNueva );
				}
			}
		}
	}
	
	public void saltar() {
		velHaciaArriba = FlappyUD.VEL_SALTO;
	}
	
	public void parar() {
		velHaciaArriba = 0;
	}
	
	public void chocar( ObjetoFlappyUD of ) {
		if (of instanceof ColumnaUD)
			((ColumnaUD)of).tocar();
	}
	
}
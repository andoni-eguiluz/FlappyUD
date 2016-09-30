package es.deusto.ingenieria.prog3.flappyUD;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.Icon;

import es.deusto.ingenieria.prog3.utils.ventanas.ventanaJuego.*;
import es.deusto.ingenieria.prog3.utils.ventanas.ventanaJuego.img.Img;

public class ColumnaUD extends ObjetoFlappyUD {
	protected ArrayList<ObjetoGrafico> listaOG = new ArrayList<ObjetoGrafico>();
	protected ObjetoGrafico escudoRojo;
	protected long msgRojo;
	protected long msgUltRojo;
	protected Icon estaEnRojo = null;   // si no es null es que está "tocado"
	protected boolean fuera = false;
	protected boolean arriba;
	
	protected static long MSG_ROJO = 500L;
	
	/** Crea una columna para el juego FlappyUD
	 * @param numCol	número de columna
	 * @param arriba	true si la columna es hacia arriba, false si es hacia abajo
	 * @param pixelHueco	pixel vertical inferior de la columna si es de arriba. 
	 * Pixel vertical superior de la columna si es de abajo. (La vertical que marca el hueco) 
	 * @param posX	Posición del objeto en coordenada X de la ventana
	 * @param ventana	Ventana gráfica en la que se integra
	 */
	public ColumnaUD( int numCol, boolean arriba, int pixelHueco, int posX, VentanaGrafica ventana ) {
		super( posX, 0, FlappyUD.PX_ANCHO_UD, 0, ventana );
		escudoRojo = new ObjetoGrafico( Img.getURLRecurso( "UD-red-ajustado.png" ),
				true, FlappyUD.PX_ANCHO_UD, FlappyUD.PX_ALTO_UD );
		this.arriba = arriba;
		if (arriba) {
			posY = 0;
			alto = pixelHueco;
			int posYEscudos = pixelHueco - FlappyUD.PX_ALTO_UD;
			do {
				ObjetoGrafico og = new ObjetoGrafico( Img.getURLRecurso( "UD-green-ajustado.png" ), 
						true, FlappyUD.PX_ANCHO_UD, FlappyUD.PX_ALTO_UD );
				ventana.addObjeto( og, new Point( posX, posYEscudos ) );
				listaOG.add( og );
				og.setName( "Arriba-" + numCol + "->" + posYEscudos );
				posYEscudos -= FlappyUD.PX_PEGADOS_COLUMNA;
			} while (posYEscudos >= -FlappyUD.PX_PEGADOS_COLUMNA);
		} else {
			posY = pixelHueco;
			alto = ventana.getAltoPanelGrafico() - pixelHueco;
			int posYEscudos = pixelHueco;
			do {
				ObjetoGrafico og = new ObjetoGrafico( Img.getURLRecurso( "UD-magenta-ajustado.png" ), 
						true, FlappyUD.PX_ANCHO_UD, FlappyUD.PX_ALTO_UD );
				ventana.addObjeto( og, new Point( posX, posYEscudos ) );
				// ventana.traeObjetoAlFrente( og );
				listaOG.add( og );
				og.setName( "Abajo-" + numCol + "->" + posYEscudos );
				posYEscudos += FlappyUD.PX_PEGADOS_COLUMNA;
			} while (posYEscudos < ventana.getAltoPanelGrafico());
		}
	}

	/** Devuelve los objetos gráficos de la columna
	 * @return	lista de objetos que forman la columna
	 */
	public ArrayList<ObjetoGrafico> getObjetosGraficos() {
		return listaOG;
	}
	
	/** Consulta si la columna se ha salido por la izquierda
	 * @return	true si se ha salido, false si todavía está en pantalla
	 */
	public boolean estoyFuera() {
		return fuera;
	}
	
	@Override
	public void quitar() {
		for (int i=0; i<listaOG.size(); i++) {
			ObjetoGrafico og = (ObjetoGrafico) listaOG.get(i);
			ventana.removeObjeto(og);
			fuera = true;
		}
		// Quitar rojo si procede
		if (estaEnRojo != null) {
			estaEnRojo = null;
		}
	}

	@Override
	public void mover() {
		long tiempoCambio = System.currentTimeMillis() - tiempoMovimiento;
		tiempoMovimiento = System.currentTimeMillis();
		posX = posX - tiempoCambio * FlappyUD.getVelAvance() / 1000D;
		int posXNueva = (int) Math.round( posX );
		if (((ObjetoGrafico)listaOG.get(0)).getX() != posXNueva) {
			if (posXNueva < -FlappyUD.PX_ANCHO_UD){  // Si se ha salido por la izquierda se quita
				quitar();
			} else {  // Si no, se mueve
				for (int i=0; i<listaOG.size(); i++) {
					ObjetoGrafico og = (ObjetoGrafico) listaOG.get(i);
					og.setLocation( posXNueva, og.getY() );
				}
			}
		}
		// Quitar rojo si procede
		if (estaEnRojo != null) {
			long msg = System.currentTimeMillis() - msgUltRojo;
			msgUltRojo = System.currentTimeMillis();
			msgRojo -= msg;
			if (msgRojo <= 0) {
				((ObjetoGrafico) listaOG.get(0)).setIcon( estaEnRojo );
				estaEnRojo = null;
			}
		}
	}
	
	/** "Toca" la columna, hará que el escudo de contacto
	 * se ponga en rojo durante el tiempo configurado (1000 msg)
	 */
	public void tocar() {
		if (estaEnRojo == null) {
			estaEnRojo = ((ObjetoGrafico) listaOG.get(0)).getIcon(); 
			((ObjetoGrafico) listaOG.get(0)).setIcon( escudoRojo.getIcon() );
			// ((ObjetoGrafico) listaOG.coger(0)).repaint();
			msgRojo = MSG_ROJO;
			msgUltRojo = System.currentTimeMillis();
		} else {  // Toque prolongado: alargar contacto
			msgRojo = MSG_ROJO;
			msgUltRojo = System.currentTimeMillis();
		}
	}
	
}
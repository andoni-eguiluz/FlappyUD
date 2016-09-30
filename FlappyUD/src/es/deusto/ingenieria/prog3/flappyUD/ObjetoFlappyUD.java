package es.deusto.ingenieria.prog3.flappyUD;

import java.awt.Point;

import es.deusto.ingenieria.prog3.utils.ventanas.ventanaJuego.*;

abstract public class ObjetoFlappyUD {
	protected VentanaGrafica ventana;
	protected int ancho;
	protected int alto;
	protected double posX;
	protected double posY;
	protected long tiempoMovimiento;
	
	/** Crea un nuevo objeto de FlappyUD
	 * @param posX	Posición del objeto en coordenada X de la ventana
	 * @param posY	Posición del objeto en coordenada Y de la ventana
	 * @param ancho	Pixels de anchura del objeto
	 * @param alto	Pixels de altura del objeto
	 * @param ventana	Ventana gráfica en la que se integra
	 */
	public ObjetoFlappyUD( int posX, int posY, int ancho, int alto, VentanaGrafica ventana ) {
		this.ventana = ventana;
		this.ancho = ancho;
		this.alto = alto;
		this.posX = posX;
		this.posY = posY;
		this.tiempoMovimiento = System.currentTimeMillis();
	}
	
	/** Devuelve la posición X de este objeto
	 * @return	Posición horizontal en la ventana
	 */
	public double getX() {
		return posX;
	}
	
	/** Devuelve la posición Y de este objeto
	 * @return	Posición vertical en la ventana
	 */
	public double getY() {
		return posY;
	}
	
	/** Devuelve el ancho de este objeto
	 * @return	Pixels de ancho
	 */
	public int getAncho() {
		return ancho;
	}
	
	/** Devuelve el alto de este objeto
	 * @return	Pixels de alto
	 */
	public int getAlto() {
		return alto;
	}
	
	/** Cambia la posición de este objeto
	 * @param posi	Posición en la ventana
	 */
	public void setPosicion( Point p ) {
		posX = p.getX();
		posY = p.getY();
	}
	
	/** Cambia la posición de este objeto
	 * @param posX	Posición X en la ventana (horizontal)
	 * @param posY	Posición Y en la ventana (vertical)
	 */
	public void setPosicion( double posX, double posY ) {
		this.posX = posX;
		this.posY = posY;
	}
	
	/** Da la orden de "quitar" al objeto de la ventana.
	 * Dependiendo de cómo sea el objeto, reaccionará de una forma u otra.
	 */
	abstract public void quitar();

	/** Da la orden de mover al objeto de la ventana.
	 * Dependiendo de cómo sea el objeto, se moverá de una forma u otra.
	 */
	abstract public void mover();
	
	/** Comprueba si el objeto choca contra otro
	 * @param o2	Objeto de comprobación
	 * @return	true si chocan, false en caso contrario
	 */
	public boolean chocaCon( ObjetoFlappyUD o2 ) {
		boolean choca = !(getX() > o2.getX()+o2.getAncho() ||
				getX() + getAncho() < o2.getX() ||
				getY() > o2.getY()+o2.getAlto() ||
				getY() + getAlto() < o2.getY());
		return choca;
	}
	
	public String toString() {
		return "[objetoFlappyUD (" + posX + "," + posY + ")]";
	}

}

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
	 * @param posX	Posici�n del objeto en coordenada X de la ventana
	 * @param posY	Posici�n del objeto en coordenada Y de la ventana
	 * @param ancho	Pixels de anchura del objeto
	 * @param alto	Pixels de altura del objeto
	 * @param ventana	Ventana gr�fica en la que se integra
	 */
	public ObjetoFlappyUD( int posX, int posY, int ancho, int alto, VentanaGrafica ventana ) {
		this.ventana = ventana;
		this.ancho = ancho;
		this.alto = alto;
		this.posX = posX;
		this.posY = posY;
		this.tiempoMovimiento = System.currentTimeMillis();
	}
	
	/** Devuelve la posici�n X de este objeto
	 * @return	Posici�n horizontal en la ventana
	 */
	public double getX() {
		return posX;
	}
	
	/** Devuelve la posici�n Y de este objeto
	 * @return	Posici�n vertical en la ventana
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
	
	/** Cambia la posici�n de este objeto
	 * @param posi	Posici�n en la ventana
	 */
	public void setPosicion( Point p ) {
		posX = p.getX();
		posY = p.getY();
	}
	
	/** Cambia la posici�n de este objeto
	 * @param posX	Posici�n X en la ventana (horizontal)
	 * @param posY	Posici�n Y en la ventana (vertical)
	 */
	public void setPosicion( double posX, double posY ) {
		this.posX = posX;
		this.posY = posY;
	}
	
	/** Da la orden de "quitar" al objeto de la ventana.
	 * Dependiendo de c�mo sea el objeto, reaccionar� de una forma u otra.
	 */
	abstract public void quitar();

	/** Da la orden de mover al objeto de la ventana.
	 * Dependiendo de c�mo sea el objeto, se mover� de una forma u otra.
	 */
	abstract public void mover();
	
	/** Comprueba si el objeto choca contra otro
	 * @param o2	Objeto de comprobaci�n
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

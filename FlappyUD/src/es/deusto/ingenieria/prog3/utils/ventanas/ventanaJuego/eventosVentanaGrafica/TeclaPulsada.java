package es.deusto.ingenieria.prog3.utils.ventanas.ventanaJuego.eventosVentanaGrafica;

import java.awt.event.KeyEvent;

public class TeclaPulsada implements EventoTeclado {
	private KeyEvent ke;
	public TeclaPulsada( KeyEvent ke ) {
		this.ke = ke;
	}
	@Override
	public long getTime() {
		return ke.getWhen();
	}
	@Override
	public int getCodigoTecla() {
		return ke.getKeyCode();
	}
	@Override
	public char getCarTecla() {
		return ke.getKeyChar();
	}
	@Override
	public String toString() {
		return "TeclaPulsada: c�digo " + getCodigoTecla() + " (car. '" + getCarTecla() + "')";
	}
}

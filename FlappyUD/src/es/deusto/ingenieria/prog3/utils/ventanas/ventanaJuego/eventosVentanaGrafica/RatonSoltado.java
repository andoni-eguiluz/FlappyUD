package es.deusto.ingenieria.prog3.utils.ventanas.ventanaJuego.eventosVentanaGrafica;

import java.awt.Point;
import java.awt.event.MouseEvent;

public class RatonSoltado implements EventoRaton {
	private MouseEvent me;
	public RatonSoltado( MouseEvent me ) {
		this.me = me;
	}
	@Override
	public long getTime() {
		return me.getWhen();
	}
	@Override
	public Point getPosicion() {
		return me.getPoint();
	}
	@Override
	public String toString() {
		return "RatonSoltado: (" + getPosicion().getX() + "," + getPosicion().getY() + ")";
	}
}

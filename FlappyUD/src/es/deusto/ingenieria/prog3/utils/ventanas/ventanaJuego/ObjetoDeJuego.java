package es.deusto.ingenieria.prog3.utils.ventanas.ventanaJuego;

import javax.swing.*;

import es.deusto.ingenieria.prog3.utils.ventanas.ventanaJuego.img.Img;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

import javax.imageio.ImageIO;


/** Clase de objeto visible en pantalla en juego. Preparado para ventana de tipo VentanaJuegoTablero
 * @author andoni
 */
public class ObjetoDeJuego extends JLabel implements Cloneable {

	// la posici�n X,Y se hereda de JLabel
	protected String nombreImagenObjeto; // Nombre del fichero de imagen del objeto
	protected JPanel panelJuego;  // panel del juego donde se dibuja el objeto
	protected boolean esVisible;  // Info de si el objeto va a ser visible en el panel
	protected int anchuraObjeto;  // Anchura del objeto en pixels (depende de la imagen)
	protected int alturaObjeto;  // Altura del objeto en pixels (depende de la imagen)
	protected ImageIcon icono;  // icono del objeto
	protected boolean escalado;  // escalado del icono
	protected transient BufferedImage imagenObjeto;  // imagen para el escalado
	private static final long serialVersionUID = 1L;  // para serializar

	
	
	@Override
	public Object clone() {
		ObjetoDeJuego oj = new ObjetoDeJuego( nombreImagenObjeto, esVisible, anchuraObjeto, alturaObjeto );
    	oj.setSize( anchuraObjeto, alturaObjeto );
		oj.setVisible( esVisible );
		oj.escalado = escalado;
		return oj;
	}
	
	/** Crea un nuevo objeto gr�fico de ventana para juegos.<br>
	 * Si no existe el fichero de imagen, se crea un rect�ngulo blanco con borde rojo
	 * @param nombreImagenObjeto	Nombre fichero donde est� la imagen del objeto (carpeta img)
	 * @param visible	true si se quiere ver, false si se quiere tener oculto
	 * @param anchura	Anchura del objeto en p�xels
	 * @param altura	Altura del objeto en p�xels
	 */
	public ObjetoDeJuego( String nombreImagenObjeto, boolean visible, int anchura, int altura ) {
		panelJuego = null;
		anchuraObjeto = anchura;
		alturaObjeto = altura;
		// Cargamos el icono (como un recurso - vale tb del .jar)
		this.nombreImagenObjeto = nombreImagenObjeto;
        URL imgURL = Img.getURLRecurso(nombreImagenObjeto);
        if (imgURL == null) {
        	icono = null;
    		setOpaque( true );
    		setBackground( Color.red );
    		setForeground( Color.blue );
        	setBorder( BorderFactory.createLineBorder( Color.blue ));
        	setText( nombreImagenObjeto );
        	setHorizontalAlignment( SwingConstants.CENTER );
        } else {
        	icono = new ImageIcon(imgURL);
    		setIcon( icono );
        	if (anchura==icono.getIconWidth() && altura==icono.getIconHeight()) {
        		escalado = false;
        	} else {  // Hay escalado: prepararlo
        		escalado = true;
            	try {  // pone la imagen para el escalado
        			imagenObjeto = ImageIO.read(imgURL);
        		} catch (IOException e) {
        			escalado = false;
        		}
        	}
        }
    	setSize( anchura, altura );
		esVisible = visible;
		setVisible( esVisible );
	}
	
	/** Crea un nuevo objeto gr�fico de ventana para juegos.<br>
	 * Si no existe el fichero de imagen, se crea un rect�ngulo blanco con borde rojo de 10x10 p�xels<br>
	 * Si existe, se toma la anchura y la altura de esa imagen.
	 * @param nombreImagenObjeto	Nombre fichero donde est� la imagen del objeto (carpeta img)
	 * @param visible	Panel en el que se debe dibujar el objeto
	 */
	public ObjetoDeJuego( String nombreImagenObjeto, boolean visible ) {
		this( nombreImagenObjeto, visible, 10, 10 );
		if (icono != null) {  // En este constructor se adapta la anchura y altura al icono
			anchuraObjeto = icono.getIconWidth();
			alturaObjeto = icono.getIconHeight();
			setSize( anchuraObjeto, alturaObjeto );
		}
	}
	
	/** Devuelve el nombre de imagen del objeto de juego
	 * @return	Nombre de la imagen
	 */
	public String getNombreImagen() {
		return nombreImagenObjeto;
	}

	/** Devuelve la informaci�n de visibilidad del objeto
	 * @return	true si es visible (por defecto), false en caso contrario
	 */
	public boolean isVisible() {
		return esVisible;
	}

	/** Activa o desactiva la visualizaci�n del objeto 
	 * @param visible	true si se quiere ver, false si se quiere tener oculto
	 */
	public void setVisible( boolean visible ) {
		super.setVisible( visible );
		esVisible = visible;
	}

	/** Devuelve la anchura del rect�ngulo gr�fico del objeto
	 * @return	Anchura
	 */
	public int getAnchuraObjeto() {
		return anchuraObjeto;
	}
	
	/** Devuelve la altura del rect�ngulo gr�fico del objeto
	 * @return	Altura
	 */
	public int getAlturaObjeto() {
		return alturaObjeto;
	}

	/** Devuelve el panel gr�fico del juego
	 * @return the panelJuego
	 */
	public JPanel getPanelJuego() {
		return panelJuego;
	}

	/** Cambia el panel gr�fico del juego
	 * @param panelJuego nuevo panel. Debe estar construido y ser correcto
	 */
	public void setPanelJuego(JPanel panelJuego) {
		this.panelJuego = panelJuego;
	}

	// Dibuja este componente de una forma no habitual (si es proporcional)
	@Override
	protected void paintComponent(Graphics g) {
		if (icono!=null && escalado) {
			Graphics2D g2 = (Graphics2D) g;  // El Graphics realmente es Graphics2D
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);	
	        g2.drawImage(imagenObjeto, 0, 0, anchuraObjeto, alturaObjeto, null);
		} else {  // sin escalado
			super.paintComponent(g);
		}
	}

	@Override
	public void setSize(int width, int height) {
		escalado = true;
		anchuraObjeto = width;
		alturaObjeto = height;
		super.setSize(width, height);
	}

	
	// M�todos privados de I/O
	// El atributo imagenObjeto (BufferedImage) no es serializable
	// (obs�rvese que est� definido como transient)
	// Por eso hay que redefinir la I/O para escribir esa imagen
	// En este caso lo hacemos a trav�s del formato PNG:
	
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        ImageIO.write( imagenObjeto, "png", out); // png es lossless
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        imagenObjeto = ImageIO.read(in);
    }	
	
}


package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class DestructorEstelar {

	private double x;
	private double y;
	private double velocidad;
	private double ancho;
	private double velocidadDisparo;
	private double angulo;
	private boolean activado;
	private boolean direccion;
	private boolean kamikaze;
	private Image img;
	private Image imgKamikazeDesactivado;
	private Sonido sonidoDisparo;
	private Sonido sonidoImpacto;

	public DestructorEstelar(double x, double y, int nivel, boolean kamikaze) {
		this.x = x;
		this.y = y;
		this.velocidadDisparo = 1.5 + (nivel * 3 / 4);
		this.angulo = 0;
		this.direccion = true;
		this.activado = false;
		this.kamikaze = kamikaze;
		if (kamikaze) {
			this.velocidad = 1.25 + (nivel / 3);
			this.ancho = 55;
			this.img = Herramientas.cargarImagen("kamikaze.png");
			this.imgKamikazeDesactivado = Herramientas.cargarImagen("kamikazeDesactivado.png");
		} else {
			this.velocidad = 1 + (nivel / 3);
			this.ancho = 75;
			this.img = Herramientas.cargarImagen("destructor.png");
		}
		this.sonidoDisparo = new Sonido("/disparoEnemigos.wav");
		this.sonidoImpacto = new Sonido("/impactoEnemigos.wav");
	}

	public void dibujar(Entorno e) {
		if (kamikaze) {
			if (y < 400) {
				e.dibujarImagen(img, x, y, Math.PI, 0.13);
			} else {
				e.dibujarImagen(imgKamikazeDesactivado, x, y, Math.PI, 0.13);
			}
		} else {
			e.dibujarImagen(img, x, y, Math.PI, 0.28);
		}
	}

	public void sonidoDisparo() {
		sonidoDisparo.reproducirFX();
	}

	public void sonidoImpacto() {
		sonidoImpacto.reproducirFX();
	}

	// MOVIMIENTO Y DISPARO
	public void mover(Entorno e) {
		this.x += (direccion ? 1 : -1) * velocidad;

		if (this.x < e.ancho() * 0.05) {
			this.x = e.ancho() * 0.05;
			this.direccion = switchDireccion();
			this.y += ancho - 10;
		}
		if (this.x > e.ancho() * 0.95) {
			this.x = e.ancho() * 0.95;
			this.direccion = switchDireccion();
			this.y += ancho - 10;
		}
		if (this.y > e.alto() * 0.95) {
			this.y = 0;
		}
	}

	public void movimientoKamikaze(AstroMegaShip n, Entorno e) {
		if (y < 400) {
			this.angulo = Math.atan2((n.getY() - this.y), (n.getX() - this.x));
		}
		
		this.x += velocidad * Math.cos(angulo);
		this.y += velocidad * Math.sin(angulo);
	}

	public boolean switchDireccion() {
		return !direccion;
	}

	public Proyectil dispararBala() {
		return new Proyectil(x, y + (ancho / 2 - 10), false, velocidadDisparo);
	}

	// COLISIONES
	// Colision con nave
	public boolean chocasteCon(AstroMegaShip n) {
		return Math.sqrt(Math.pow(this.x - n.getX(), 2) + Math.pow(this.y - n.getY(), 2))
				- (this.ancho / 2 + n.getAncho() / 2) < 0;
	}

	// Colision con proyectil
	public boolean chocasteCon(Proyectil p) {
		return Math.sqrt(Math.pow(this.x - p.getX(), 2) + Math.pow(this.y - p.getY(), 2))
				- (this.ancho / 2 + p.getAncho() / 2) < 0;
	}

	// Devuelve true si hay algun otro destructor en su zona
	public boolean estaEnZona(DestructorEstelar e) {
		return Math.sqrt(Math.pow(this.x - e.getX(), 2) + Math.pow(this.y - e.getY(), 2))
				- (this.ancho / 2 + e.getAncho() / 2) < 100;
	}

	// Detecta si la nave esta debajo de el
	public boolean detectoObjetivo(AstroMegaShip n) {
		return Math.abs(x - n.getX()) - (this.ancho / 2 + n.getAncho() / 2) < 0;
	}

	// METODOS DE ACTIVACION
	// Si esta activado se muestra en pantalla
	public boolean estaActivado() {
		return activado;
	}

	public void switchActivar() {
		activado = !activado;
	}

	// GETTERS
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getAncho() {
		return ancho;
	}

	public boolean isKamikaze() {
		return kamikaze;
	}

}

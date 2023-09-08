package juego;

import java.awt.Image;

import entorno.Entorno;
import entorno.Herramientas;

public class JefeFinal {

	private double x;
	private double y;
	private double ancho;
	private int cooldown;
	private int lasersActivos;
	private int vida;
	private Image img;
	private Sonido sonidoImpacto;
	private boolean activado;

	public JefeFinal(Entorno e) {
		this.x = e.ancho() / 2;
		this.y = 0;
		this.cooldown = 60;
		this.lasersActivos = 0;
		this.vida = 15;
		this.activado = false;
		this.img = Herramientas.cargarImagen("jefeFinal.png");
		this.sonidoImpacto = new Sonido("/impactoEnemigos.wav");
	}

	public void dibujar(Entorno e) {
		e.dibujarImagen(img, x, 250, 0, 0.9);
	}

	public void sonidoImpacto() {
		sonidoImpacto.reproducirFX();
	}

	public LaserJefe disparar() {
		LaserJefe laser = null;
		if (lasersActivos < 3) {
			laser = new LaserJefe();
			cooldown = 360;
			lasersActivos++;
		} else {
			if (cooldown <= 0) {
				lasersActivos = 0;
			} else {
				cooldown--;
			}
		}
		return laser;
	}

	public boolean chocasteCon(Proyectil p) {
		if (p.getY() < 180) {
			vida--;
			return true;
		}
		return false;
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

	public boolean estaVivo() {
		return vida > 0;
	}

	public void switchActivar() {
		activado = !activado;
	}
}

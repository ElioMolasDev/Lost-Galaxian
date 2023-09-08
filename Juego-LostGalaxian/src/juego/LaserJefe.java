package juego;

import java.awt.Color;

import entorno.Entorno;

public class LaserJefe {
	private double x;
	private double y;
	private double ancho;
	private double largo;
	private int activacion;
	private int tiempoActivo;
	private boolean activo;
	private boolean haceDaño;
	private Sonido sonidoDisparo;
	private Sonido sonidoActivacion;

	public LaserJefe() {
		this.x = 100 + Math.floor(Math.random() * 800 - 100);
		this.y = 300;
		this.ancho = 75;
		this.largo = 600;
		this.activacion = 240;
		this.tiempoActivo = 50;
		this.activo = true;
		this.haceDaño = true;
		this.sonidoDisparo = new Sonido("/disparoLaser.wav");
		this.sonidoActivacion = new Sonido("/activacionLaser.wav");
	}

	public void dibujar(Entorno e) {
		if (activo) {
			if (activacion > 0) {
				sonidoActivacion.reproducirFXUnico();
				if (activacion >= 90) {
					e.dibujarRectangulo(this.x, this.y, ancho, largo, 0, Color.YELLOW);
					activacion--;
				} else {
					e.dibujarRectangulo(this.x, this.y, ancho, largo, 0, Color.ORANGE);
					activacion--;
				}
			} else {
				sonidoActivacion.pararMusica();
				sonidoDisparo.reproducirFXUnico();
				e.dibujarRectangulo(this.x, this.y, ancho, largo, 0, Color.RED);
				tiempoActivo--;
			}
			if (tiempoActivo <= 0) {
				sonidoDisparo.pararMusica();
				activo = false;
			}
		}
	}

	public void detenerSonidos() {
		sonidoActivacion.pararMusica();
		sonidoDisparo.pararMusica();
	}

	// Detecta si impacto con la nave
	// iría en nave, pero me la banco
	public boolean chocasteCon(AstroMegaShip n) {
		if (activacion <= 0 && Math.abs(x - n.getX()) - (this.ancho / 2 + n.getAncho() / 2) < 0 && haceDaño) {
			haceDaño = false;
			return true;
		} else {
			return false;
		}
	}

	public boolean estaActivo() {
		return activo;
	}

}

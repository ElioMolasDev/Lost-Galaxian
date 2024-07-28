# Programación I - Primer Semestre 2023 - Trabajo Práctico: Lost Galaxian
Ibarra Mauro <mauroibarra333@gmail.com>; Molas Elio <eliomolas14@hotmail.com>; San_Martin Francisco <franciscosanmartin96@gmail.com>. 
Docentes Damian Marquez, Leonardo Waingarten y Hernán Rondelli (COM-01)

## Introducción

El objetivo de este trabajo práctico final es *desarrollar* un video juego en
el cual la Astro-MegaShip elimine la mayor cantidad de Destructores
Estelares, sin ser destruida en el intento.

## Descripción

- Complicaciones en el código
  - Comportamiento de los asteroides, algunos se mostraban uno encima del otro y compartían el mismo patrón de movimiento.
  - Hacer que los proyectiles de la primera versión del jefe final le hagan daño a la nave y funcione el sistema de vidas correctamente. El bug era que cuando la nave era impactada por uno de los proyectiles del `jefeFinal`, su sistema de vida no funcionaba y entonces la nave se volvía inmortal.
  - Choque entre los asteroides: El bug ocurría en la colisión entre estos, quedaban unidos y cambiaba el movimiento diagonal a vertical. Su solución fue implementar un método que cambie sus velocidades en el momento del impacto de estos.
  - Hitbox de los objetos y colisiones: Pasamos de una hitbox cuadrada a una circular, esto fue por un motivo de colisión entre los objetos. Con la hitbox cuadrada se usó un método de la librería `rectangle` para la intersección entre ellas, pero la colisión y la intersección no era correcta. Con la circular usamos el cálculo de distancia para las colisiones, el resultado fue correctas colisiones entre objetos.

## Implementación

### Clase Asteroide
```java
public class Asteroide {
	private double x, y, velocidad, angulo, ancho, alto; // fixme
	private Image img;
	private boolean direccion;
	private boolean activado;
	private Sonido sonidoImpacto;

	public Asteroide(double x, double y, boolean direccion) {
		this.x = x;
		this.y = y;
		this.img = Herramientas.cargarImagen("asteroide.png");
		this.sonidoImpacto = new Sonido("/impactoAsteroide.wav");
		this.ancho = 30;
		this.alto = 30;
		this.direccion = direccion;
		this.activado = false;

		if (this.direccion) {
			this.angulo = Math.PI * 7 / 12;
			this.velocidad = 2;
		} else {
			this.angulo = Math.PI * 5 / 12;
			this.velocidad = 2.5;
		}
	}

	public void dibujar(Entorno e) {
		e.dibujarImagen(img, x, y, angulo - Math.PI / 1.5, 0.05);
		// e.dibujarCirculo(x, y, ancho, null); //HITBOX
	}

	public void sonidoImpacto() {
		sonidoImpacto.reproducirFX();
	}

	// MOVIMIENTO
	public void mover(Entorno e) {
		x += velocidad * Math.cos(angulo);
		y += velocidad * Math.sin(angulo);

		if (this.x < e.ancho() * 0.05) {
			this.x = e.ancho() * 0.05;
			cambiarDireccion();
			cambiarMovimiento();
		}
		if (this.x > e.ancho() * 0.95) {
			this.x = e.ancho() * 0.95;
			cambiarDireccion();
			cambiarMovimiento();
		}
		if (this.y > e.alto() + alto) {
			this.y = -alto * 2;
		}
	}

	public void cambiarDireccion() {
		this.direccion = !direccion;
	}

	public void cambiarMovimiento() {
		if (this.direccion) {
			this.velocidad = 2;
			this.angulo = Math.PI - this.angulo;
		} else {
			this.velocidad = 2.5;
			this.angulo = Math.PI - this.angulo;
		}
	}

	// COLISIONES
	// Devuelve true si hay otro asteroide en su zona
	public boolean estaEnZona(Asteroide a) {
		return Math.sqrt(Math.pow(this.x - a.getX(), 2) + Math.pow(this.y - a.getY(), 2))
				- (this.ancho / 2 + a.getAncho() / 2) < 100 || a.getY() < 50;
	}

	// Colision con proyectil
	public boolean chocasteCon(Proyectil p) {
		return Math.sqrt(Math.pow(this.x - p.getX(), 2) + Math.pow(this.y - p.getY(), 2))
				- (this.ancho / 2 + p.getAncho() / 2) < 0;
	}

	// Colision con otro asteroide
	public boolean chocasteCon(Asteroide a) {
		return Math.sqrt(Math.pow(this.x - a.getX(), 2) + Math.pow(this.y - a.getY(), 2))
				- (this.ancho / 2 + a.getAncho() / 2) < 0;
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
}

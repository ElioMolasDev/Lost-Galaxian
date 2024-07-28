# Programación I - Primer Semestre 2023 - Trabajo Práctico: Lost Galaxian
###Equipo
Ibarra Mauro <mauroibarra333@gmail.com>; Molas Elio <eliomolas14@hotmail.com>; San_Martin Francisco <franciscosanmartin96@gmail.com>. 
###Docentes: 
Damian Marquez, Leonardo Waingarten y Hernán Rondelli (COM-01)

## Introducción

El objetivo de este trabajo práctico final es *desarrollar* un video juego utilizando los conocimientos de POO 
como clases y encapsulamiento,en el cual la Astro-MegaShip(Player) elimine la mayor cantidad de Destructores
Estelares, sin ser destruida en el intento. 

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
```

### Clase AstroMegaShip
```java
public class AstroMegaShip {

	private double x;
	private double y;
	private double velocidad;
	private double ancho;
	private Image img;
	private Sonido sonidoDisparo;
	private Sonido sonidoImpacto;

	public AstroMegaShip(double x, double y) {
		this.x = x;
		this.y = y;
		this.ancho = 100;
		this.velocidad = 6;
		this.img = Herramientas.cargarImagen("nave.png");
		this.sonidoDisparo = new Sonido("/disparoNave.wav");
		this.sonidoImpacto = new Sonido("/impactoNave.wav");
	}

	public void dibujar(Entorno e) {
		// e.dibujarCirculo(x, y, ancho, null); //HITBOX
		e.dibujarImagen(img, x, y, 0, 0.35); // 0.25
	}

	public void sonidoDisparo() {
		sonidoDisparo.reproducirFX();
	}

	public void sonidoImpacto() {
		sonidoImpacto.reproducirFX();
	}

	// MOVIMIENTO Y DISPARO
	// La direccion puede ser (-1 = izquierda || 1 = derecha)
	private void mover(Entorno e, int direc) {
		x += direc * velocidad;

		if (this.x < e.ancho() * 0.05) {
			this.x = e.ancho() * 0.05;
		}
		if (this.x > e.ancho() * 0.95) {
			this.x = e.ancho() * 0.95;
		}
	}

	public void moverHaciaLaIzquierda(Entorno e) {
		mover(e, -1);
	}

	public void moverHaciaLaDerecha(Entorno e) {
		mover(e, 1);
	}

	// True = hacia arriba
	public Proyectil dispararBala() {
		return new Proyectil(x, y, true, 8);
	}

	// COLISIONES
	// Colision con destructor
	public boolean chocasteCon(DestructorEstelar e) {
		return Math.sqrt(Math.pow(this.x - e.getX(), 2) + Math.pow(this.y - e.getY(), 2))
				- (this.ancho / 2 + e.getAncho() / 2) < 0;
	}

	// Colision con proyectil
	public boolean chocasteCon(Proyectil p) {
		return Math.sqrt(Math.pow(this.x - p.getX(), 2) + Math.pow(this.y - p.getY(), 2))
				- (this.ancho / 2 + p.getAncho() / 2) < 0;
	}

	// Colision con item
	public boolean chocasteCon(Item i) {
		return Math.sqrt(Math.pow(this.x - i.getX(), 2) + Math.pow(this.y - i.getY(), 2))
				- (this.ancho / 2 + i.getAncho() / 2) < 0;
	}

	// Colision con asteroide
	public boolean chocasteCon(Asteroide a) {
		return Math.sqrt(Math.pow(this.x - a.getX(), 2) + Math.pow(this.y - a.getY(), 2))
				- (this.ancho / 2 + a.getAncho() / 2) < 0;
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
```

### Clase DestructorEstelar
```java
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
```

### Clase Item
```java
public class Item {

	private double x;
	private double y;
	private double ancho;
	private double velocidad;
	private Image img;
	private Sonido sonido;

	public Item(double x, int vidas) {
		this.x = x;
		this.y = 1;
		this.ancho = 50;
		this.velocidad = 1.8;
		if (vidas < 3) {
			this.img = Herramientas.cargarImagen("vidaUP.png");
		} else {
			this.img = Herramientas.cargarImagen("puntosUP.png");
		}
		this.sonido = new Sonido("/vidaUP.wav");
	}

	public void dibujar(Entorno e) {
		// e.dibujarCirculo(x, y, ancho, null); //HITBOX
		e.dibujarImagen(img, x, y, 0, 0.2);
	}

	public void sonidoAgarrado() {
		sonido.reproducirFX();
	}

	public boolean estaDentro(Entorno e) {
		return y > 0 && y < e.alto() + ancho;
	}

	public void mover(Entorno e) {
		y += velocidad;
	}

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
```

### Clase JefeFinal
```java
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
```
### Clase Juego
```java
public class Juego extends InterfaceJuego {

	// El objeto Entorno que controla el tiempo y otros
	private Entorno entorno;
	private AstroMegaShip nave;
	private DestructorEstelar[] enemigos;
	private Image fondo;
	private Image fondoAux; // cloneDelFondo
	private Image[] vidas;
	private double yFondo;
	private double yFondoAux;
	private Proyectil bala;
	private Proyectil[] iones;
	private LaserJefe[] lasers;
	private Asteroide[] asteroides;
	private JefeFinal jefeFinal;

	private int vidasRestantes;
	private int enemigosDerrotados;
	private int cooldownEnemigos;
	private int tiempoEnMenu;
	private int nivel;
	private int puntos;
	private int recordPuntos;
	private int random;
	private boolean entrarMenus;
	private Item vidaUP;
	private Sonido musicaJuego;
	private Sonido musicaPerdiste;
	private Sonido musicaMenu;
	private Sonido musicaNivelSuperado;
	private Sonido musicaJefeFinal;

	public Juego() {
		// Inicializa el objeto entorno
		this.entorno = new Entorno(this, "Prueba del Entorno", 800, 600);
		// Inicia la musica
		musicaJuego = new Sonido("/musicaJuego.wav");
		musicaPerdiste = new Sonido("/musicaPerdiste.wav");
		musicaMenu = new Sonido("/musicaMenu.wav");
		musicaNivelSuperado = new Sonido("/musicaNivelSuperado.wav");
		musicaJefeFinal = new Sonido("/musicaJefeFinal.wav");

		// Inicializar lo que haga falta para el juego
		// ...
		// Objetos
		this.nave = new AstroMegaShip(entorno.ancho() / 2, entorno.alto() * 0.9);
		this.enemigos = new DestructorEstelar[6];
		this.iones = new Proyectil[6];
		this.asteroides = new Asteroide[6];
		this.jefeFinal = new JefeFinal(entorno);
		this.lasers = new LaserJefe[3];

		// Imagenes
		this.fondo = Herramientas.cargarImagen("fondo.png");
		this.fondoAux = Herramientas.cargarImagen("fondo.png");
		this.vidas = new Image[3];
		this.vidas[2] = Herramientas.cargarImagen("vidaX3.png");
		this.vidas[1] = Herramientas.cargarImagen("vidaX2.png");
		this.vidas[0] = Herramientas.cargarImagen("vidaX1.png");

		// Variables
		this.yFondo = 600;
		this.yFondoAux = 0;
		this.vidasRestantes = 3;
		this.cooldownEnemigos = 400;
		this.tiempoEnMenu = 0;
		this.nivel = 1;
		this.puntos = 0;
		this.recordPuntos = 0;
		this.entrarMenus = true;
		this.random = 0;

		// Inicia el juego!
		this.entorno.iniciar();
	}

	/**
	 * Durante el juego, el método tick() será ejecutado en cada instante y por lo
	 * tanto es el método más importante de esta clase. Aquí se debe actualizar el
	 * estado interno del juego para simular el paso del tiempo (ver el enunciado
	 * del TP para mayor detalle).
	 */
	public void tick() {
		// Procesamiento de un instante de tiempo
		// ...
		mostrarFondo();
		if (entrarMenus) {
			gestionMenus();
			tiempoEnMenu++;
//			nave = null;
//			return; // fixme
		} else {
			tiempoEnMenu = 0;
			// Musica
			if (nivel != 6) {
				musicaMenu.pararMusica();
				musicaNivelSuperado.pararMusica();
				this.musicaJuego.reproducirMusica();
			}

			// CREACION ENEMIGOS
			// Crea los enemigos en una posicion aleatoria y los asigna a la array
			for (int i = 0; i < this.enemigos.length; i++) {
				if (enemigos[i] == null) {
					int posicion = (int) (this.entorno.ancho() * 0.10)
							+ (int) (Math.floor(Math.random() * (this.entorno.ancho() * 0.80)));
					;
					int random = (int) Math.floor(Math.random() * 5); // Cada cierta cantidad de enemigos sale un
																		// kamikaze
					if (random == 1) {
						this.enemigos[i] = new DestructorEstelar(posicion, (this.entorno.alto() * 0.06), nivel, true);
					} else {
						this.enemigos[i] = new DestructorEstelar(posicion, (this.entorno.alto() * 0.06), nivel, false);
					}
				}
			}

			// Permite activar a los enemigos cada una cierta cantidad de tiempo
			if (enemigosActivos() == 0 || cooldownEnemigos <= 0) {
				// Activa enemigos si no hay ningun otro enemigo activo en su zona de spawn
				for (int i = 0; i < this.enemigos.length; i++) {
					if (this.enemigos[i] != null && (puedeSpawnearEnemigo(i)) && !enemigos[i].estaActivado()) {
						enemigos[i].switchActivar();
					}
				}
				cooldownEnemigos = 300 / nivel;
			}
			cooldownEnemigos--;

			// CREACION ASTEROIDES
			// Crea los asteroides y los asigna a la array
			for (int i = 0; i < asteroides.length; i++) {
				if (asteroides[i] == null) {
					if (i % 2 == 0) {
						asteroides[i] = new Asteroide(Math.floor(Math.random() * this.entorno.ancho()), 0, true);
					}
					if (i % 2 != 0) {
						asteroides[i] = new Asteroide(Math.floor(Math.random() * this.entorno.ancho()), 0, false);
					}
				}
			}

			// Activa 4 asteroides en el primer nivel y los aumenta hasta 6
			// mientras se superan los niveles
			if (asteroidesActivos() < 4 || asteroidesActivos() < nivel + 1) {
				// Activa asteroides si no hay ningun otro asteroide activo en su zona de spawn
				for (int i = 0; i < this.asteroides.length; i++) {
					if (this.asteroides[i] != null && (puedeSpawnearAsteroide(i)) && !asteroides[i].estaActivado()) {
						asteroides[i].switchActivar();
					}
				}
			}
			// CREACION BALAS DE IONES(ENEMIGAS)
			// Asigna bala de iones a la array
			for (int i = 0; i < iones.length; i++) {
				if (iones[i] == null && enemigos[i] != null && nave != null && enemigos[i].estaActivado()
						&& !enemigos[i].isKamikaze() && enemigos[i].detectoObjetivo(nave)) {
					iones[i] = enemigos[i].dispararBala();
					enemigos[i].sonidoDisparo();
				}
			}

			// CREACION VIDA EXTRA
			random = (int) Math.floor(Math.random() * 1000 / nivel);
			if (vidaUP == null && random == 1) {
				int posicion = (int) (this.entorno.ancho() * 0.10)
						+ (int) (Math.floor(Math.random() * (this.entorno.ancho() * 0.80)));
				this.vidaUP = new Item(posicion, vidasRestantes);
			}

			// NIVEL FINAL CON JEFE
			if (nivel == 6) {
				musicaMenu.pararMusica();
				musicaNivelSuperado.pararMusica();
				musicaJuego.pararMusica();
				musicaJefeFinal.reproducirMusica();

				// Creacion, colision y dibujo de LASERS DE JEFE FINAL
				for (int i = 0; i < lasers.length; i++) {
					if (lasers[i] == null && jefeFinal != null) {
						lasers[i] = this.jefeFinal.disparar();
					}
					if (lasers[i] != null) {
						lasers[i].dibujar(entorno);
					}
					if (lasers[i] != null && !lasers[i].estaActivo()) {
						lasers[i] = null;
					}
					if (lasers[i] != null && nave != null && lasers[i].chocasteCon(nave)) {
						vidasRestantes--;
					}
				}

				// Creacion, colisiones y dibujo de JEFE FINAL
				if (jefeFinal != null) {
					this.jefeFinal.switchActivar();
					this.jefeFinal.dibujar(entorno);
					if (bala != null && jefeFinal.chocasteCon(bala)) {
						jefeFinal.sonidoImpacto();
						bala = null;
					}
					if (!jefeFinal.estaVivo()) {
						jefeFinal = null;
						puntos += 20000;
					}
				}

				// Subir nivel
				if (jefeFinal == null) {
					nivel++;
					entrarMenus = true;
					reset();
				}
			}

			// MOVIMIENTOS, MUESTRA EN PANTALLA Y COLISIONES
			// Movimiento, dibujo y colisiones relacionadas con la VIDA EXTRA
			if (vidaUP != null) {
				vidaUP.mover(entorno);
				vidaUP.dibujar(entorno);

				if (vidaUP != null && nave != null && nave.chocasteCon(vidaUP)) {
					vidaUP.sonidoAgarrado();
					vidaUP = null;
					if (vidasRestantes < 3) {
						vidasRestantes += 1;
					} else {
						puntos += (nivel * 10);
					}
				}
				if (vidaUP != null && !vidaUP.estaDentro(entorno)) {
					vidaUP = null;
				}
			}

			// Movimiento, dibujo y colisiones relacionadas con la BALA DE LA NAVE
			if (bala != null) {
				bala.dibujar(entorno);
				bala.mover(entorno);

				if (!bala.estaDentro(entorno)) {
					bala = null;
				}

				for (int i = 0; i < enemigos.length; i++) {
					if (enemigos[i] != null && enemigos[i].estaActivado() && bala != null
							&& enemigos[i].chocasteCon(bala)) {
						enemigos[i].sonidoImpacto();
						enemigos[i] = null;
						bala = null;
						enemigosDerrotados++;
						puntos += (nivel * 10) * enemigosDerrotados;
					}
				}
			}

			// Creacion, movimiento, dibujo y colisiones relacionadas con la BALA DE IONES
			for (int i = 0; i < iones.length; i++) {
				if (iones[i] != null) {
					iones[i].mover(entorno);
					iones[i].dibujar(entorno);
				}
				if (iones[i] != null && !iones[i].estaDentro(entorno)) {
					iones[i] = null;
				}
				if (iones[i] != null && nave != null && nave.chocasteCon(iones[i])) {
					nave.sonidoImpacto();
					iones[i] = null;
					vidasRestantes -= 1;
				}
			}

			// Movimiento, dibujo y colisiones relacionadas con los ASTEROIDES
			for (int i = 0; i < asteroides.length; i++) {
				if (asteroides[i] != null && asteroides[i].estaActivado()) {
					asteroides[i].dibujar(entorno);
					asteroides[i].mover(entorno);
				}
				if (asteroides[i] != null && nave != null && nave.chocasteCon(asteroides[i])) {
					nave.sonidoImpacto();
					asteroides[i] = null;
					vidasRestantes -= 1;
				}
				if (asteroides[i] != null && asteroides[i].estaActivado() && bala != null
						&& asteroides[i].chocasteCon(bala)) {
					asteroides[i].sonidoImpacto();
					bala = null;
				}
			}

			for (int i = 0; i < asteroides.length; i++) {
				for (int j = i + 1; j < asteroides.length; j++) {
					if (asteroides[i] != null && asteroides[j] != null && asteroides[i].chocasteCon(asteroides[j])) {
						asteroides[i].cambiarDireccion(); // elegirDireccion
						asteroides[i].cambiarMovimiento();
						asteroides[j].cambiarDireccion();
						asteroides[j].cambiarMovimiento();
					}
				}
			}

			// Movimiento, dibujo y colision con nave de los ENEMIGOS
			for (int i = 0; i < enemigos.length; i++) {
				if (enemigos[i] != null) {
					if (enemigos[i].estaActivado() && !enemigos[i].isKamikaze()) {
						enemigos[i].dibujar(entorno);
						enemigos[i].mover(entorno);
					}
					if (enemigos[i].estaActivado() && enemigos[i].isKamikaze() && nave != null) {
						enemigos[i].dibujar(entorno);
						enemigos[i].movimientoKamikaze(nave, entorno);
					}
					if (nave != null && nave.chocasteCon(enemigos[i])) {
						nave.sonidoImpacto();
						enemigos[i] = null;
						vidasRestantes -= 1;
					}
				}
			}

			// ARREGLA BUG DE SONIDO CON LOS LASERS DEL JEFE
			for (int i = 0; i < lasers.length; i++) {
				if (lasers[i] != null && (vidasRestantes == 0 || jefeFinal == null)) {
					lasers[i].detenerSonidos();
				}
			}

			// ASIGNACION DE TECLAS PARA EL MOVIMIENTO DE LA NAVE Y SU DISPARO
			if (nave != null) {
				nave.dibujar(entorno);
				if (entorno.estaPresionada('a') || entorno.estaPresionada(entorno.TECLA_IZQUIERDA)) {
					nave.moverHaciaLaIzquierda(entorno);
				}
				if (entorno.estaPresionada('d') || entorno.estaPresionada(entorno.TECLA_DERECHA)) {
					nave.moverHaciaLaDerecha(entorno);
				}
				// CREACION BALA
				if (bala == null && entorno.estaPresionada(entorno.TECLA_ESPACIO)) {
					bala = nave.dispararBala();
					nave.sonidoDisparo();
				}

			}

			// GUI
			// Sistema de vidas
			for (int i = 0; i < vidas.length; i++) {
				if (vidasRestantes == i + 1) {
					entorno.dibujarImagen(vidas[i], 80, 20, 0, 0.15);
				}
				if (vidasRestantes == 0) {
					entrarMenus = true;
					reset();
				}
			}
			// Subir nivel
			if (enemigosDerrotados == 5 * nivel) {
				nivel++;
				entrarMenus = true;
				reset();
			}

			// Nivel
			entorno.cambiarFont("Arial Black", 15, Color.CYAN);
			entorno.escribirTexto("Nivel " + nivel, 10, entorno.alto() - 70);

			// Enemigos restantes para pasar de nivel
			entorno.cambiarFont("Arial Black", 15, Color.RED);
			entorno.escribirTexto("Sig. Nivel  x" + (5 * nivel - enemigosDerrotados), 10, entorno.alto() - 50);

			// Puntos totales obtenidos
			entorno.cambiarFont("Arial Black", 20, Color.GREEN);
			entorno.escribirTexto("PUNTOS x" + puntos, 10, entorno.alto() - 20);

		}
	}
	// FINAL BRACKET TICK CLASS

	// METODO DE SPAWN DE ASTEROIDES
	// Se fija si hay algun otro asteroide activo en la zona de un asteroide en
	// particular
	public boolean puedeSpawnearAsteroide(int i) {
		for (int n = 0; n < this.asteroides.length; n++) {
			if (this.asteroides[i] != null && (this.asteroides[n] != null && this.asteroides[n].estaActivado())
					&& asteroides[i].estaEnZona(asteroides[n])) {
				return false;
			}
		}
		return true;
	}

	// Cuenta cantidad de asteroides activos en pantalla
	public int asteroidesActivos() {
		int activos = 0;

		for (int i = 0; i < asteroides.length; i++) {
			if (asteroides[i] != null && asteroides[i].estaActivado()) {
				activos++;
			}
		}
		return activos;
	}

	// METODO DE SPAWN ENEMIGOS
	// Se fija si hay algun otro enemigo activo en la zona de un enemigo en
	// particular
	public boolean puedeSpawnearEnemigo(int i) {
		for (int n = 0; n < this.enemigos.length; n++) {
			if (this.enemigos[i] != null && (this.enemigos[n] != null && this.enemigos[n].estaActivado())
					&& enemigos[i].estaEnZona(enemigos[n])) {
				return false;
			}
		}
		return true;
	}

	// Cuenta cantidad de enemigos activos en pantalla
	public int enemigosActivos() {
		int activos = 0;
		for (int i = 0; i < enemigos.length; i++) {
			if (enemigos[i] != null && enemigos[i].estaActivado()) {
				activos++;
			}
		}
		return activos;
	}

	// RESET PARA CAMBIO DE NIVEL
	public void reset() {
		for (int i = 0; i < 6; i++) {
			enemigos[i] = null;
			iones[i] = null;
			asteroides[i] = null;
			bala = null;
			nave = null;
			vidaUP = null;
			this.nave = new AstroMegaShip(entorno.ancho() / 2, entorno.alto() * 0.9);
			enemigosDerrotados = 0;
			if (nivel == 6) {
				this.jefeFinal = new JefeFinal(entorno);
				if (i < lasers.length) {
					lasers[i] = null;
				}
			}
		}
	}

	// GESTION DE MENUS
	public void gestionMenus() {
		if (vidasRestantes == 3 && nivel == 1) {
			musicaPerdiste.pararMusica();
			musicaMenu.reproducirMusica();
			entrarMenus = menuPrincipal();
			return;
		}
		if (vidasRestantes > 0 && nivel > 1 && nivel != 7) {
			musicaJuego.pausarMusica();
			musicaNivelSuperado.reproducirMusica();
			entrarMenus = menuSuperacionNivel();
			return;
		}
		if (vidasRestantes > 0 && nivel == 7) {
			musicaJefeFinal.pararMusica();
			boolean volver = menuJuegoGanado();
			if (volver) {
				nivel = 1;
				vidasRestantes = 3;
				if (puntos > recordPuntos) {
					recordPuntos = puntos;
				}
				puntos = 0;
			}
			return;
		}
		if (vidasRestantes == 0) {
			boolean volver;
			musicaJuego.pararMusica();
			musicaJefeFinal.pararMusica();
			musicaPerdiste.reproducirMusica();
			volver = menuPerdidaNivel();
			if (volver) {
				nivel = 1;
				vidasRestantes = 3;
				if (puntos > recordPuntos) {
					recordPuntos = puntos;
				}
				puntos = 0;
			}
			return;
		}
	}

	// FUNCIONES DEL MENU !!
	public boolean menuPrincipal() {
		entorno.cambiarFont("Arial Black", 80, Color.CYAN);
		entorno.escribirTexto("Lost Galaxian", 100, entorno.alto() / 2 - 100);

		entorno.cambiarFont("Arial Black", 40, Color.WHITE);
		entorno.escribirTexto("Presiona 'Enter' para comenzar", 60, entorno.alto() / 2 + 50);

		entorno.cambiarFont("Arial Black", 20, Color.YELLOW);
		entorno.escribirTexto("High Record: " + recordPuntos, 10, entorno.alto() - 20);

		if (entorno.estaPresionada(entorno.TECLA_ENTER)) {
			return false;
		}
		return true;
	}

	public boolean menuSuperacionNivel() {
		entorno.cambiarFont("Arial Black", 80, Color.GREEN);
		entorno.escribirTexto("Nivel " + (nivel - 1), 250, entorno.alto() / 2 - 100);
		entorno.escribirTexto("Superado", 190, entorno.alto() / 2 - 30);

		entorno.cambiarFont("Arial Black", 40, Color.WHITE);
		entorno.escribirTexto("Presiona 'Enter' para continuar", 60, entorno.alto() / 2 + 50);

		if (entorno.estaPresionada(entorno.TECLA_ENTER)) {
			return false;
		}
		return true;
	}

	public boolean menuJuegoGanado() {
		entorno.cambiarFont("Arial Black", 100, Color.GREEN);
		entorno.escribirTexto("Felicidades", 90, entorno.alto() / 2 - 200);
		entorno.cambiarFont("Arial Black", 80, Color.GREEN);
		entorno.escribirTexto("Ya estas listo", 100, entorno.alto() / 2 - 120);
		entorno.cambiarFont("Arial Black", 50, Color.GREEN);
		entorno.escribirTexto("Para salvar a la galaxia", 90, entorno.alto() / 2 - 50);

		entorno.cambiarFont("Arial Black", 50, Color.YELLOW);
		entorno.escribirTexto("Puntos: " + puntos, 220, entorno.alto() / 2 + 20);

		entorno.cambiarFont("Arial Black", 40, Color.WHITE);
		entorno.escribirTexto("Presiona 'Space'", 220, entorno.alto() / 2 + 100);
		entorno.escribirTexto("para volver al menu", 180, entorno.alto() / 2 + 170);

		if (entorno.estaPresionada(entorno.TECLA_ESPACIO) && tiempoEnMenu > 30) {
			return true;
		}
		return false;
	}

	public boolean menuPerdidaNivel() {
		entorno.cambiarFont("Arial Black", 80, Color.RED);
		entorno.escribirTexto("Perdiste", 220, entorno.alto() / 2 - 100);

		entorno.cambiarFont("Arial Black", 50, Color.YELLOW);
		entorno.escribirTexto("Puntos: " + puntos, 220, entorno.alto() / 2 - 50);

		entorno.cambiarFont("Arial Black", 40, Color.WHITE);
		entorno.escribirTexto("Presiona 'Space'", 220, entorno.alto() / 2 + 50);
		entorno.escribirTexto("para volver al menu", 180, entorno.alto() / 2 + 120);

		if (entorno.estaPresionada(entorno.TECLA_ESPACIO) && tiempoEnMenu > 40) {
			return true;
		}
		return false;
	}

	// ANIMACION FONDO
	public void mostrarFondo() {
		entorno.dibujarImagen(fondo, entorno.ancho() / 2, yFondo++, 0);
		entorno.dibujarImagen(fondoAux, entorno.ancho() / 2, yFondoAux++, 0);

		if (yFondo == 300) {
			yFondoAux = -300;
		}
		if (yFondoAux == 300) {
			yFondo = -300;
		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Juego juego = new Juego();
	}
}
```
### Clase Juego
```java
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
```
###Clase Proyectil
```java
public class Proyectil {
	private double x;
	private double y;
	private double ancho;
	private double velocidad;
	private Image imgBala;
	private boolean direccion; // true = bala de nave, false = bala de iones

	public Proyectil(double x, double y, boolean direccion, double velocidad) {
		this.x = x;
		this.y = y;
		this.direccion = direccion;

		if (this.direccion) {
			this.velocidad = velocidad;
			this.ancho = 30;
			this.imgBala = Herramientas.cargarImagen("proyectil.png");
		} else {
			this.velocidad = velocidad;
			this.ancho = 20;
			this.imgBala = Herramientas.cargarImagen("proyectilIones.png");
		}
	}

	public void dibujar(Entorno e) {
		// e.dibujarCirculo(x, y, ancho, null); //HITBOX
		e.dibujarImagen(imgBala, x, y, 0, 0.5);
	}

	public void mover(Entorno e) {
		if (direccion) {
			y -= velocidad;
		} else {
			y += velocidad;
		}
	}

	// El 120 es para que la bala tenga un "Cooldown"
	public boolean estaDentro(Entorno e) {
		return y > 0 && y < e.alto() + 120;
	}

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
```

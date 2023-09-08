package juego;

import java.awt.Color;
import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;
import entorno.InterfaceJuego;

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
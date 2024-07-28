# Programación I - Primer Semestre 2023 - Trabajo Práctico: Lost Galaxian
Ibarra Mauro <mauroibarra333@gmail.com>; Molas Elio <eliomolas14@hotmail.com>; San_Martin Francisco <franciscosanmartin96@gmail.com>
v1, {docdate}. Docentes Damian Marquez, Leonardo Waingarten y Hernán Rondelli (COM-01)

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

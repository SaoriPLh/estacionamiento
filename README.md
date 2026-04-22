# Sistema de Estacionamiento

## Tecnologías

* Java
* MySQL
* NetBeans

## Estructura del proyecto

* modelo → entidades
* dao → acceso a datos
* servicio → lógica de negocio
* controlador → conexión con vista
* vista → interfaz gráfica
* util → conexión y utilidades

## Ramas

* main → versión estable
* logica 
* datos
* interfaz

## Configuración de base de datos

1. Crear archivo `db.properties`
2. Configurar:

db.url=jdbc:mysql://localhost:3306/estacionamiento
db.user=tu_usuario
db.password=tu_password

## Notas importantes

* No subir `db.properties`
* Usar `db.properties.example` como guía

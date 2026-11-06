# PRACTICO FINAL GRUPO 8

Trabajo Práctico 10 - Spring Boot / JPA Relaciones Avanzadas

Programación Orientada a Objetos 2026 - Analista Programador Universitario - Extensión Áulica San Pedro (UNJu)

## Integrantes

- Vito Bernardo Bernal
- Daniel Orlando Vargas Ruiz
- Sergio Sebastián Ortega

## Descripción

Sistema de gestión comercial para una empresa de venta de productos. Permite registrar clientes, productos y ventas, que pueden ser de contado o financiadas.

- **Venta de contado:** si el cliente presenta un cupón válido se aplica el descuento sobre el total.
- **Venta financiada (Plan 6):** se suma un porcentaje de recargo al total y se divide en 6 cuotas mensuales que quedan en estado ADEUDADA.

En las dos ventas se verifica que haya stock de cada producto, se descuenta el stock vendido y se guarda la venta con sus detalles.

## Tecnologías

- Java 25
- Spring Boot 3.5.6 con Maven
- Spring Data JPA / Hibernate
- MySQL 8
- JUnit 5

## Estructura del proyecto

El proyecto está en la carpeta `Practico_Final_Grupo_8` y se divide en capas, dentro de `ar.edu.unju.fi.poo`:

- `model`: entidades (Cliente, Producto, Categoria, Venta, VentaContado, VentaFinanciada, DetalleVenta, Cuota, Cupon) y los enums TipoCliente y EstadoCuota.
- `repository`: repositorios de Spring Data JPA (ClienteRepository, ProductoRepository, VentaRepository).
- `service`: interfaces de los servicios y en `service/imp` sus implementaciones.
- `exception`: excepciones personalizadas. Todas heredan de NegocioException (ClienteException, ProductoException, VentaException, StockInsuficienteException, CuponInvalidoException).

El diagrama de clases está en `Practico_Final_Grupo_8/TP10_diagrama_clases_Grupo8.png`.

## Funcionalidades

**Clientes**
- Guardar clientes validando que el DNI/CUIT y el email no se repitan.
- Buscar por número de identificación (DNI o CUIT).
- Buscar por nombre o razón social.
- Listar todos o por tipo de cliente (particular o empresa).

**Productos**
- Guardar productos con su categoría.
- Buscar por id, categoría, marca o rango de precios.
- Listar todos los productos.
- Consultar productos con stock crítico o por debajo de un umbral.
- Obtener los mensajes de alerta de stock.

**Ventas**
- Registrar ventas de contado (con o sin cupón) y ventas financiadas.
- Historial de ventas de un cliente.
- Cuotas pendientes de pago de un cliente.
- Ventas dentro de un rango de fechas.
- Filtrar ventas de contado o financiadas.
- Pagar una cuota.

## Cómo ejecutarlo

1. Tener MySQL 8 con una base `poo2026` y usuario `poo2026` / contraseña `poo2026`. Si se usan otros datos hay que cambiarlos en `src/main/resources/application.properties`.
2. Importar el proyecto en Spring Tools como proyecto Maven existente.
3. Correr las pruebas desde `src/test/java` con Run As > JUnit Test, o por consola con `./mvnw test`.

Las tablas se crean solas al iniciar (`ddl-auto=create`) y el archivo `import.sql` carga dos cupones de ejemplo: `DESC10` (10% de descuento, vigente) y `VENC20` (vencido).

## Pruebas

Cada servicio tiene su clase de prueba: ClienteServiceTest, ProductoServiceTest y VentaServiceTest.

## Organización del trabajo

Trabajamos con ramas por funcionalidad que se integraron a `develop` mediante Pull Requests:

- `feature/base`: entidades, excepciones e interfaces de los servicios (Vito Bernal).
- `feature/clientes`: repositorio, servicio y pruebas de clientes (Daniel Vargas Ruiz).
- `feature/productos`: repositorio, servicio y pruebas de productos (Sergio Ortega).
- `feature/ventas`: repositorio, servicio y pruebas de ventas (Vito Bernal).

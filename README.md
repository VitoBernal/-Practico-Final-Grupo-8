# PRACTICO FINAL GRUPO 8

- Trabajo Práctico 10 - Spring Boot / JPA Relaciones Avanzadas
- Trabajo Práctico 11 - API REST / DTO

Programación Orientada a Objetos 2026 - Analista Programador Universitario - Extensión Áulica San Pedro (UNJu)

## Integrantes

- Vito Bernardo Bernal
- Daniel Orlando Vargas Ruiz
- Sergio Sebastián Ortega

## Descripción

Sistema de gestión comercial para una empresa de venta de productos. Permite registrar clientes, productos y ventas, que pueden ser de contado o financiadas. Desde el TP11 todas las operaciones se exponen como una API REST.

- **Venta de contado:** si el cliente presenta un cupón válido se aplica el descuento sobre el total.
- **Venta financiada (Plan 6):** se suma un porcentaje de recargo al total y se divide en 6 cuotas mensuales que quedan en estado ADEUDADA.

En las dos ventas se verifica que haya stock de cada producto, se descuenta el stock vendido y se guarda la venta con sus detalles.

## Tecnologías

- Java 25
- Spring Boot 3.5.6 con Maven
- Spring Web (API REST) y Bean Validation
- Spring Data JPA / Hibernate
- MapStruct (para pasar de entidad a DTO)
- MySQL 8
- JUnit 5

## Estructura del proyecto

El proyecto está en la carpeta `Practico_Final_Grupo_8` y se divide en capas, dentro de `ar.edu.unju.fi.poo`:

- `model`: entidades (Cliente, Producto, Categoria, Venta, VentaContado, VentaFinanciada, DetalleVenta, Cuota, Cupon) y los enums TipoCliente y EstadoCuota.
- `repository`: repositorios de Spring Data JPA (ClienteRepository, ProductoRepository, VentaRepository).
- `service`: interfaces de los servicios y en `service/imp` sus implementaciones. Reciben y devuelven DTOs.
- `dto`: objetos de transferencia que usa la API. Los `RequestDto` son los datos que se reciben y los `ResponseDto` los que se devuelven. No tienen entidades: solo textos, números, booleanos y otros DTOs (las fechas van como texto). Los de clientes usan además el enum TipoCliente.
- `dto/mapper`: mappers de MapStruct de productos y ventas (ProductoMapper y VentaMapper), pasan de entidad a DTO.
- `mapper`: ClienteMapper, el mapper de MapStruct de clientes. Pasa de entidad a DTO y de DTO a entidad.
- `controller`: controladores REST y el manejador global de excepciones.
- `exception`: excepciones personalizadas. Todas heredan de NegocioException.

**Diagrama de clases**

El diagrama de clases está en `Practico_Final_Grupo_8/TP10_diagrama_clases_Grupo8.png`. En el TP11 el modelo tuvo dos cambios que no figuran en el diagrama:

- `Cliente` tiene el atributo `habilitado`. El TP11 pide manejar el caso de cliente inhabilitado y, como los datos no se borran, el cliente se da de baja de forma lógica (habilitado en true o false).
- El cliente conoce su lista de ventas: la relación entre Cliente y Venta va en los dos sentidos.

## Reglas de negocio

**Clientes**
- El DNI/CUIT y el email no se pueden repetir.
- El tipo, el DNI/CUIT y el email son obligatorios, y el email tiene que tener un formato válido.
- Un cliente particular necesita nombre y apellido; una empresa, razón social.
- Un cliente no se borra porque puede tener ventas: se inhabilita (baja lógica) y un cliente inhabilitado no puede comprar.

**Productos**
- Nombre, categoría, stock y precio son obligatorios. El precio tiene que ser mayor a 0 y el stock no puede ser negativo.
- Si la categoría no existe se crea, y si ya existe se reutiliza.
- Los productos no se borran, porque los datos se conservan: se modifican (por ejemplo, su stock o su precio).

**Ventas**
- Una venta tiene que tener al menos un producto, y cada cantidad tiene que ser mayor a 0. Si un producto viene repetido se suman las cantidades.
- Antes de tocar el stock se revisa todo el pedido: si falta stock de un producto no se vende nada.
- El cupón de descuento solo se usa en ventas de contado, una sola vez y si no está vencido.
- El recargo solo se usa en ventas financiadas y tiene que estar entre 0 y 100.
- Una cuota solo se puede pasar a PAGADA y una cuota pagada no se puede volver a pagar.

## API REST

La API se publica en `http://localhost:8080/api/v1`. Nunca recibe ni devuelve entidades: siempre usa DTOs, para mostrar solo los datos necesarios. Por ejemplo, una venta muestra el nombre y el DNI/CUIT del cliente, pero no su email ni su dirección.

**Clientes** (`/api/v1/clientes`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/clientes` | Listar todos |
| GET | `/clientes/{id}` | Buscar por id |
| GET | `/clientes/identificacion/{numero}` | Buscar por DNI o CUIT |
| GET | `/clientes/nombre/{texto}` | Buscar por nombre, apellido o razón social |
| GET | `/clientes/tipo/{PARTICULAR o EMPRESA}` | Listar por tipo |
| POST | `/clientes` | Crear |
| PUT | `/clientes/{id}` | Modificar |
| DELETE | `/clientes/{id}` | Baja lógica (inhabilitar) |
| PATCH | `/clientes/{id}` | Habilitar o inhabilitar, con `{ "habilitado": true }` |

**Productos** (`/api/v1/productos`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/productos` | Listar todos |
| GET | `/productos/{id}` | Buscar por id |
| GET | `/productos/categoria/{categoria}` | Filtrar por categoría |
| GET | `/productos/marca/{marca}` | Filtrar por marca |
| GET | `/productos/precio?min=1000&max=5000` | Filtrar por rango de precios |
| GET | `/productos/stock-critico` | Productos con stock crítico |
| GET | `/productos/stock-bajo?umbral=10` | Productos con stock por debajo del umbral |
| GET | `/productos/alertas-stock` | Mensajes de alerta de stock |
| POST | `/productos` | Crear |
| PUT | `/productos/{id}` | Modificar |

**Ventas** (`/api/v1/ventas`)

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/ventas` | Registrar una venta de contado o financiada |
| GET | `/ventas` | Listar todas |
| GET | `/ventas/{id}` | Buscar por id |
| GET | `/ventas/cliente/{clienteId}` | Historial de ventas de un cliente |
| GET | `/ventas/cliente/{clienteId}/estado-cuenta` | Cuotas pendientes y total adeudado de un cliente |
| GET | `/ventas/fechas?desde=2026-11-01&hasta=2026-11-30` | Ventas entre dos fechas |
| GET | `/ventas/modalidad/{CONTADO o FINANCIADA}` | Filtrar por modalidad de pago |
| PATCH | `/ventas/{ventaId}/cuotas/{numero}` | Pagar una cuota, con `{ "estado": "PAGADA" }` |

Ejemplo de venta financiada:

```json
{
  "clienteId": 2,
  "modalidad": "FINANCIADA",
  "porcentajeRecargo": 20,
  "items": [
    { "productoId": 2, "cantidad": 1 },
    { "productoId": 3, "cantidad": 2 }
  ]
}
```

Para una venta de contado se usa `"modalidad": "CONTADO"` y, si hay cupón, `"cuponCodigo": "DESC10"`.

Las rutas no llevan verbos: la acción la indica el método HTTP (GET consulta, POST crea, PUT modifica todo, PATCH modifica un solo dato y DELETE da de baja).

## Manejo de errores

Todas las excepciones se atienden en un solo lugar (`ManejadorGlobalExcepciones`) y siempre se devuelve el mismo formato:

```json
{
  "fecha": "2026-11-13T20:15:30",
  "estado": 422,
  "error": "Unprocessable Entity",
  "mensaje": "No hay stock suficiente de Amoladora",
  "ruta": "/api/v1/ventas",
  "errores": []
}
```

| Código | Cuándo |
|---|---|
| 400 Bad Request | La solicitud está mal armada o trae datos inválidos: faltan datos obligatorios (en `errores` se indica cada campo), el JSON tiene un formato inválido, un parámetro no tiene el tipo correcto o un dato no tiene el formato o el valor permitido, por ejemplo un email mal escrito, tipo de cliente o modalidad que no existe, cantidad en 0 o cupón en una venta financiada (`ClienteException`, `ProductoException`, `VentaException`) |
| 404 Not Found | El cliente, producto, venta o cuota no existe (`NoEncontradoException`) |
| 409 Conflict | Choca con lo que ya está guardado: DNI/CUIT o email repetido, cuota ya pagada (`ConflictoException`) |
| 422 Unprocessable Entity | Los datos están bien, pero no cumplen una regla de negocio: stock insuficiente, cupón vencido o ya usado, cliente inhabilitado o importe inválido, como un precio en 0 o un recargo fuera de 0 a 100 (`StockInsuficienteException`, `CuponInvalidoException`, `ClienteInhabilitadoException`, `ImporteInvalidoException`) |
| 500 Internal Server Error | Cualquier error inesperado, sin mostrar detalles internos |

## Cómo ejecutarlo

1. Tener MySQL 8 con una base `poo2026` y usuario `poo2026` / contraseña `poo2026`. Si se usan otros datos hay que cambiarlos en `src/main/resources/application.properties`.
2. Importar el proyecto en Spring Tools como proyecto Maven existente.
   MapStruct crea las clases de los mappers al compilar. Si Spring Tools marca error en los mappers, hacer clic derecho en el proyecto > Maven > Update Project.
3. Correr la aplicación con Run As > Spring Boot App. La API queda en `http://localhost:8080/api/v1`.
4. Correr las pruebas desde `src/test/java` con Run As > JUnit Test, o por consola con `./mvnw test`.

Las tablas se crean solas al iniciar (`ddl-auto=create`) y el archivo `import.sql` carga dos cupones de ejemplo: `DESC10` (10% de descuento, vigente) y `VENC20` (vencido).

## Pruebas

- ClienteServiceTest, ProductoServiceTest y VentaServiceTest prueban cada operación y cada regla de negocio de los servicios. Solo usan los servicios, no los repositorios.
- Los endpoints se probaron con Postman.

## Organización del trabajo

Trabajamos con ramas por funcionalidad que salen de `develop` y se integran a `develop` mediante Pull Requests.

**TP10**
- `feature/base`: entidades, excepciones e interfaces de los servicios (Vito Bernal).
- `feature/clientes`: repositorio, servicio y pruebas de clientes (Daniel Vargas Ruiz).
- `feature/productos`: repositorio, servicio y pruebas de productos (Sergio Ortega).
- `feature/ventas`: repositorio, servicio y pruebas de ventas (Vito Bernal).

**TP11**
- `feature/manejo-errores`: validaciones, excepciones nuevas, formato de error y manejador global (Vito Bernal).
- `feature/mapstruct`: dependencia de MapStruct y procesador de anotaciones (Vito Bernal).
- `feature/cliente-habilitado`: atributo habilitado del cliente para la baja lógica (Vito Bernal).
- `feature/cliente-ventas`: el cliente conoce su lista de ventas (Vito Bernal).
- `feature/test-ventas-con-ids`: las pruebas de ventas guardan solo los ids de clientes y productos (Vito Bernal).
- `feature/clientes-dto` y `feature/api-clientes`: DTOs, mapper, servicio de clientes con sus pruebas y controller de clientes (Daniel Vargas Ruiz). Se integraron juntas en un solo Pull Request.
- `feature/productos-DTO`: DTOs, mapper y servicio de productos con sus pruebas (Sergio Ortega).
- `feature/api-productos`: controller de productos (Sergio Ortega).
- `feature/ventas-dto`: DTOs, mapper y servicio de ventas con sus pruebas (Vito Bernal).
- `feature/api-ventas`: controller de ventas (Vito Bernal).
- `feature/test-ventas-solo-servicios`: las pruebas de ventas usan solo los servicios (Vito Bernal).
- `feature/documentacion`: README del TP11 (Vito Bernal).

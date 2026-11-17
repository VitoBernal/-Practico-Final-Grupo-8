# PRACTICO FINAL GRUPO 8

- Trabajo Práctico 10 - Spring Boot / JPA Relaciones Avanzadas
- Trabajo Práctico 11 - API REST / DTO
- Trabajo Práctico 12 - API REST / Documentación con Swagger

Programación Orientada a Objetos 2026 - Analista Programador Universitario - Extensión Áulica San Pedro (UNJu)

## Integrantes

- Vito Bernardo Bernal
- Daniel Orlando Vargas Ruiz
- Sergio Sebastián Ortega

## Descripción

Sistema de gestión comercial para una empresa de venta de productos. Permite registrar clientes, productos y ventas, que pueden ser de contado o financiadas. Desde el TP11 todas las operaciones se exponen como una API REST, y desde el TP12 la API está documentada con Swagger.

- **Venta de contado:** si el cliente presenta un cupón válido se aplica el descuento sobre el total.
- **Venta financiada (Plan 6):** se suma un porcentaje de recargo al total y se divide en 6 cuotas mensuales que quedan en estado ADEUDADA. Las cuotas se cobran de a una y en orden, y al pagar la última la venta queda SALDADA.

En las dos ventas se verifica que haya stock de cada producto, se descuenta el stock vendido y se guarda la venta con sus detalles.

## Tecnologías

- Java 25
- Spring Boot 3.5.6 con Maven
- Spring Web (API REST) y Bean Validation
- Spring Data JPA / Hibernate
- MapStruct (para pasar de entidad a DTO)
- springdoc-openapi 2.8.13 (Swagger UI, documentación interactiva de la API)
- MySQL 8
- JUnit 5

## Estructura del proyecto

El proyecto está en la carpeta `Practico_Final_Grupo_8` y se divide en capas, dentro de `ar.edu.unju.fi.poo`:

- `model`: entidades (Cliente, Producto, Categoria, Venta, VentaContado, VentaFinanciada, DetalleVenta, Cuota, Cupon) y los enums TipoCliente, EstadoCuota y EstadoVenta.
- `repository`: repositorios de Spring Data JPA (ClienteRepository, ProductoRepository, VentaRepository).
- `service`: interfaces de los servicios y en `service/imp` sus implementaciones. Reciben y devuelven DTOs.
- `dto`: objetos de transferencia que usa la API. Los `RequestDto` son los datos que se reciben y los `ResponseDto` los que se devuelven. No tienen entidades: solo textos, números, booleanos y otros DTOs (las fechas van como texto). Los de clientes usan además el enum TipoCliente.
- `dto/mapper`: mappers de MapStruct de productos y ventas (ProductoMapper y VentaMapper), pasan de entidad a DTO.
- `mapper`: ClienteMapper, el mapper de MapStruct de clientes. Pasa de entidad a DTO y de DTO a entidad.
- `controller`: controladores REST.
- `handler`: manejador global de excepciones (ManejadorGlobalExcepciones), pasa cada excepción a su código HTTP.
- `exception`: excepciones personalizadas. Todas heredan de NegocioException.

**Diagrama de clases**

El diagrama de clases está en `Practico_Final_Grupo_8/TP10_diagrama_clases_Grupo8.png`. En el TP11 y en el TP12 el modelo tuvo estos cambios que no figuran en el diagrama:

- `Cliente` tiene el atributo `habilitado`. El TP11 pide manejar el caso de cliente inhabilitado y, como los datos no se borran, el cliente se da de baja de forma lógica (habilitado en true o false).
- El cliente conoce su lista de ventas: la relación entre Cliente y Venta va en los dos sentidos.
- `EstadoCuota` suma el estado VENCIDA (TP12).
- Nuevo enum `EstadoVenta` con los estados PENDIENTE y SALDADA (TP12).
- `Venta` tiene el atributo `estado`, que empieza en PENDIENTE, y el método `saldar()`, que la pasa a SALDADA (TP12).
- `Cuota` tiene los métodos `pagar(fechaPago)`, que la pasa a PAGADA o a VENCIDA si ya pasó el vencimiento, y `estaVencida(fecha)` (TP12).
- `VentaFinanciada` tiene los métodos `buscarProximaCuota()`, `pagarProximaCuota(fechaPago)`, `contarCuotasPendientes()` y `calcularSaldoPendiente()` para el cobro de cuotas (TP12).

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
- La venta de contado se paga en el momento y queda SALDADA. La financiada queda PENDIENTE hasta que se paga la última cuota.

**Cuotas** (TP12)
- Estados de una cuota: ADEUDADA (todavía no se pagó), VENCIDA (se quiso pagar después del vencimiento) y PAGADA.
- Las cuotas se cobran con el id de la venta y siempre en orden: se cobra la primera cuota sin pagar, así no se saltea ninguna ni se cobra una cuota futura si hay anteriores impagas.
- Una cuota se puede pagar hasta el día de su vencimiento inclusive y por su importe original.
- Si el cliente se presenta a pagar y la fecha de hoy supera el vencimiento, la cuota pasa sola a VENCIDA, no se cobra y tampoco se pueden cobrar las siguientes.
- Al pagar la última cuota adeudada la venta verifica que no quedan cuotas pendientes y pasa sola a SALDADA.

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
| GET | `/ventas/cliente/{clienteId}/estado-cuenta` | Estado de cuenta: datos del cliente, historial de ventas y cuotas pendientes o vencidas con el total adeudado |
| GET | `/ventas/fechas?desde=2026-11-01&hasta=2026-11-30` | Ventas entre dos fechas |
| GET | `/ventas/modalidad/{CONTADO o FINANCIADA}` | Filtrar por modalidad de pago |
| POST | `/ventas/{ventaId}/pagos` | Cobrar la próxima cuota sin pagar de una venta financiada (sin body) |

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

El cobro de una cuota devuelve la cuota pagada, el estado de la venta (PENDIENTE o SALDADA), cuántas cuotas faltan y el saldo pendiente.

Las rutas no llevan verbos: la acción la indica el método HTTP (GET consulta, POST crea o registra un pago, PUT modifica todo, PATCH modifica un solo dato y DELETE da de baja).

## Documentación con Swagger

Con la aplicación corriendo, la documentación interactiva está en **http://localhost:8080/swagger-ui.html** (la definición OpenAPI en JSON está en `http://localhost:8080/v3/api-docs`).

- Los endpoints aparecen agrupados en Clientes, Productos y Ventas.
- Cada endpoint tiene un resumen, una descripción, sus parámetros con un ejemplo y todos los códigos de respuesta que puede devolver. Los errores muestran el formato de `ErrorResponseDto`.
- Los cuerpos de las solicitudes tienen valores de ejemplo, y el alta de una venta tiene dos ejemplos para elegir: contado con cupón y financiada con recargo.
- Con **Try it out** y **Execute** se prueba cada endpoint desde la misma página.
- La configuración está en `application.properties` (`springdoc.*`), en `@OpenAPIDefinition` de `PracticoFinalGrupo8Application` (título, versión y descripción de la API) y en las anotaciones `@Tag`, `@Operation`, `@ApiResponse`, `@Parameter` y `@Schema` de los controllers y los DTOs de entrada.

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
| 400 Bad Request | La solicitud está mal armada o trae datos inválidos: faltan datos obligatorios (en `errores` se indica cada campo), el JSON tiene un formato inválido, un parámetro no tiene el tipo correcto o un dato no tiene el formato o el valor permitido, por ejemplo un email mal escrito, tipo de cliente o modalidad que no existe, cantidad en 0, cupón en una venta financiada o cobrar una cuota de una venta de contado (`ClienteException`, `ProductoException`, `VentaException`) |
| 404 Not Found | El cliente, producto o venta no existe, o la ruta no existe (`NoEncontradoException`) |
| 405 Method Not Allowed | La ruta existe pero no acepta ese método, por ejemplo un DELETE a `/productos/{id}` |
| 409 Conflict | Choca con lo que ya está guardado: DNI/CUIT o email repetido, cobrar una venta que ya está saldada (`ConflictoException`) |
| 422 Unprocessable Entity | Los datos están bien, pero no cumplen una regla de negocio: stock insuficiente, cupón vencido o ya usado, cliente inhabilitado, cuota vencida o importe inválido, como un precio en 0 o un recargo fuera de 0 a 100 (`StockInsuficienteException`, `CuponInvalidoException`, `ClienteInhabilitadoException`, `CuotaVencidaException`, `ImporteInvalidoException`) |
| 500 Internal Server Error | Cualquier error inesperado, sin mostrar detalles internos |

## Cómo ejecutarlo

1. Tener MySQL 8 con una base `poo2026` y usuario `poo2026` / contraseña `poo2026`. Si se usan otros datos hay que cambiarlos en `src/main/resources/application.properties`.
2. Importar el proyecto en Spring Tools como proyecto Maven existente.
   MapStruct crea las clases de los mappers al compilar. Si Spring Tools marca error en los mappers, hacer clic derecho en el proyecto > Maven > Update Project.
3. Correr la aplicación con Run As > Spring Boot App. La API queda en `http://localhost:8080/api/v1` y la documentación en `http://localhost:8080/swagger-ui.html`.
4. Correr las pruebas desde `src/test/java` con Run As > JUnit Test, o por consola con `./mvnw test`.

Las tablas se crean solas al iniciar (`ddl-auto=create`) y el archivo `src/main/resources/import.sql` carga datos de ejemplo para probar la API desde Swagger sin cargar nada a mano (no hay clases Java que carguen datos):

- Cupones `DESC10` (10% de descuento, vigente) y `VENC20` (vencido).
- 6 productos en 3 categorías (Herramientas, Pintura y Electricidad). La pintura (id 3) tiene stock crítico.
- 4 clientes: Carla Mendez (id 1), Constructora Norte SA (id 2), Luis Diaz (id 3) y Marta Ruiz (id 4), que está inhabilitada para probar que no puede comprar.
- Venta 1 (`FIN-1`, de Luis Diaz): financiada hace 3 meses, con la cuota 1 pagada y la cuota 2 vencida. Al cobrarla devuelve 422 y la cuota queda VENCIDA.
- Venta 2 (`CON-2`, de Carla Mendez): de contado, SALDADA.
- Venta 3 (`FIN-3`, de Constructora Norte SA): financiada hace 5 días, se pueden cobrar sus 6 cuotas hasta que quede SALDADA.

Las fechas de las ventas de ejemplo se calculan desde el día en que arranca la aplicación. Las pruebas usan `src/test/resources/import.sql`, que solo carga los cupones, así no dependen de estos datos.

## Pruebas

- ClienteServiceTest, ProductoServiceTest y VentaServiceTest prueban cada operación y cada regla de negocio de los servicios. Solo usan los servicios, no los repositorios.
- VentaServiceTest prueba también el cobro en orden, la cuota vencida, la venta saldada y el estado de cuenta.
- En el TP11 los endpoints se probaron con Postman. Desde el TP12 también se pueden probar desde Swagger con Try it out, usando los datos de ejemplo.

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
- `feature/clientes-dto`: DTOs, mapper, servicio de clientes con sus pruebas (Daniel Vargas Ruiz).
- `feature/api-clientes`: controller de clientes (Daniel Vargas Ruiz). Sale de `feature/clientes-dto` y las dos se integraron juntas en el Pull Request #23.
- `feature/productos-DTO`: DTOs, mapper y servicio de productos con sus pruebas (Sergio Ortega).
- `feature/api-productos`: controller de productos (Sergio Ortega).
- `feature/ventas-dto`: DTOs, mapper y servicio de ventas con sus pruebas (Vito Bernal).
- `feature/api-ventas`: controller de ventas (Vito Bernal).
- `feature/test-ventas-solo-servicios`: las pruebas de ventas usan solo los servicios (Vito Bernal).
- `feature/documentacion`: README del TP11 (Vito Bernal).
- `fix/test-clientes-interfaz`: las pruebas de clientes usan la interfaz `ClienteService`, corrección de la devolución del TP10 (Daniel Vargas Ruiz).
- `fix/test-productos-interfaz`: las pruebas de productos usan la interfaz `ProductoService`, corrección de la devolución del TP10 (Sergio Ortega).
- `fix/readme-ramas`: corrección de la lista de ramas del README (Vito Bernal).

**TP12**
- `fix/manejador-excepciones`: el manejador global de excepciones pasa al paquete `handler` (Daniel Vargas Ruiz).
- `feature/cobro-de-cuota`: cobro de la próxima cuota por id de venta, cuota vencida y venta saldada, con sus pruebas (Sergio Ortega).
- `feature/swagger`: dependencia springdoc-openapi y configuración de Swagger (Vito Bernal).
- `feature/swagger-clientes`: documentación de los endpoints de clientes (Vito Bernal).
- `feature/swagger-productos`: documentación de los endpoints de productos (Vito Bernal).
- `feature/swagger-ventas`: documentación de los endpoints de ventas (Vito Bernal).
- `feature/estado-cuenta`: estado de cuenta con los datos del cliente, el historial de ventas y las cuotas pendientes o vencidas, con sus pruebas (Daniel Vargas Ruiz).
- `feature/datos-de-ejemplo`: datos de ejemplo en `import.sql` para probar desde Swagger (Daniel Vargas Ruiz).
- `feature/documentacion-tp12`: README del TP12 (Vito Bernal).

Las ramas no se borran: quedan en el repositorio para ver cómo se trabajó.

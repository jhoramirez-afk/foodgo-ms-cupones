# FoodGo — Microservicio Cupon

Microservicio **cupones** de FoodGo, actualizado para la Evaluación Parcial N°2 de JVY0101.

## Responsabilidad

Administra cupones, reglas básicas de descuento y el registro de usos asociados. Corresponde al requisito **RF-07** del diseño de FoodGo.

## Tecnologías

- Java 21
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA / Hibernate
- Bean Validation
- H2 para ejecución rápida local y pruebas
- MySQL 8.4 mediante perfil `mysql` y Docker Compose
- Maven
- OpenAPI / Swagger UI

## Arquitectura en capas

```text
controller -> service -> repository -> model -> base de datos
```

El dominio implementa una relación JPA bidireccional **@OneToMany / @ManyToOne** entre `Cupon` y `UsoCupon`.
Las relaciones JPA pertenecen a la base de datos del propio servicio. Los campos que referencian otros dominios son referencias declarativas; no consultan otras API. Postman enlaza los ID reales en el escenario académico.

## Endpoints REST

| Método | Endpoint | Resultado |
|---|---|---|
| GET | `/api/cupones` | Listar cupones |
| GET | `/api/cupones/{id}` | Obtener por id |
| POST | `/api/cupones` | Crear recurso |
| PUT | `/api/cupones/{id}` | Actualizar recurso |
| DELETE | `/api/cupones/{id}` | Eliminar recurso |
| GET | `/api/cupones/{cuponId}/usos` | Listar recursos relacionados |
| POST | `/api/cupones/{cuponId}/usos` | Crear recurso relacionado |
| GET | `/api/usos/{id}` | Obtener recurso relacionado |
| PUT | `/api/usos/{id}` | Actualizar recurso relacionado |
| DELETE | `/api/usos/{id}` | Eliminar recurso relacionado |

### Ejemplo de creación de Cupon

```json
{
  "codigo": "BARRIO10-DEMO",
  "tipo": "PORCENTAJE",
  "descuento": 10
}
```

### Ejemplo de creación de UsoCupon

```json
{
  "pedidoId": 1,
  "fechaUso": "2026-10-01T13:30:00",
  "subtotalPedido": 19980
}
```

## Respuestas de error

- `400 Bad Request`: validación de campos.
- `404 Not Found`: identificador inexistente.
- `409 Conflict`: violación de integridad o restricción única.

Los errores se entregan en JSON mediante `@RestControllerAdvice`.

## Ejecución rápida con H2

Requisitos: JDK 21 y Maven 3.9+.

```bash
git clone https://github.com/jhoramirez-afk/foodgo-ms-cupones.git
cd foodgo-ms-cupones
mvn clean install
mvn spring-boot:run
```

Servicio: `http://localhost:8087`
Swagger UI: `http://localhost:8087/swagger-ui/index.html`
H2 Console: `http://localhost:8087/h2-console`

JDBC H2: `jdbc:h2:file:./data/foodgo_cupones`
Usuario: `sa`
Contraseña: vacía.

## Ejecución con MySQL

```bash
docker compose up --build
```

El `docker-compose.yml` levanta el microservicio y una base MySQL independiente para el dominio.

## Maven y empaquetado

```bash
mvn clean
mvn test
mvn install
mvn package
java -jar target/cupones-svc-2.0.0.jar
```

Después de `mvn package` debe existir un archivo `.jar` válido en `target/`.

## Postman

La carpeta `postman/` contiene una colección con casos correctos y casos de error. Puede importarse directamente en Postman.

## Estrategia Git

- `main`: versión estable.
- `develop`: integración de la EP02.
- `feature/jpa-relations`: entidades y relaciones JPA.
- `feature/crud-errors`: CRUD y manejo uniforme de errores.
- `feature/persistence-tests-docs`: conexión relacional MySQL, Docker y documentación reproducible.

Los cambios deben integrarse mediante commits descriptivos y, de ser posible, Pull Requests.

## Persistencia y pruebas de integración

El perfil local H2 guarda los datos en `data/` y los conserva al reiniciar. No se versiona esa carpeta. Las pruebas usan el perfil `test` con una BD independiente en memoria y comprueban CRUD de ambas entidades, relaciones, eliminación en cascada, validación y recursos inexistentes mediante HTTP (MockMvc).

Ejecutar `mvn clean install` para compilar, ejecutar las pruebas y generar el JAR.

Se conservan las rutas REST. Se refuerza la validación del contrato para la EP02. JaCoCo verifica un mínimo de 80% de líneas del código de aplicación (excluye el arranque), además de producir el informe. Se mantienen las pruebas unitarias y los escenarios Cucumber existentes.

La guía `docs/DEMO_EP02.md` incluye SQL, persistencia tras reiniciar y un guion para el video. Ejecutar la colección Postman en orden: usa IDs reales y verifica HTTP, errores y actualizaciones.

## Revisión de la actualización

Los cambios de integridad y ejemplos están en `feature/ep02-reglas-delivery`. Revisar el PR antes de integrar en `develop` y después en `main`. Para probar una propuesta abierta: `git switch feature/ep02-reglas-delivery`. Los commits conservan fechas reales.

## Reglas del dominio para la EP02

Código único de 3 a 40 caracteres: mayúsculas, números, guiones y guiones bajos. Tipos PORCENTAJE (1–100) o MONTO_FIJO (pesos CLP enteros). Cada uso exige pedidoId positivo, fecha y `subtotalPedido` positivo. `montoDescontado` es de solo lectura y se calcula en service; los porcentajes se redondean al peso con HALF_UP. El descuento no supera el subtotal. Un cupón no se usa dos veces en el mismo pedido y no cambia de política después de utilizarse.

Los importes usan pesos chilenos enteros. Se valida formato e integridad, no identidad de personas ni direcciones externas. Los datos de demostración son ficticios. Campos demasiado largos devuelven 400 antes de llegar a la BD.

## MySQL local con Workbench

Workbench es el cliente SQL. El servicio se conecta al servidor MySQL mediante Connector/J. Crear previamente el esquema `foodgo_cupones` y otorgar permisos al usuario elegido. En el equipo de desarrollo la instancia FoodGo usa el puerto 3307. Para otro equipo, usar su puerto y usuario correspondientes.

PowerShell (no guardar la contraseña en Git):

```powershell
$env:DB_HOST = '127.0.0.1'
$env:DB_PORT = '3307'
$env:DB_NAME = 'foodgo_cupones'
$env:DB_USER = 'foodgo_ep02'
$claveMySQL = Read-Host 'Contraseña MySQL' -AsSecureString
$env:DB_PASSWORD = [Net.NetworkCredential]::new('', $claveMySQL).Password
mvn clean install
mvn package
java -jar target/cupones-svc-2.0.0.jar --spring.profiles.active=mysql
```

En Workbench, abrir el mismo host, puerto y esquema y ejecutar las consultas de `docs/DEMO_EP02.md`. Ejecutar la colección en orden. Para conservar el CRUD visible en la BD, detenerse después de la primera carpeta; la última comprueba DELETE y cascada.

## Docker con base MySQL persistente

Seguir [docs/DOCKER_EP02.md](docs/DOCKER_EP02.md): copiar .env.example a .env, completar las contraseñas y ejecutar docker compose build, después docker compose up -d --wait --wait-timeout 300. El build ejecuta pruebas y cobertura con Maven clean verify; app espera la base y verifica HTTP. Los volúmenes conservan datos al reiniciar. APP_PORT permite evitar conflictos con un servicio local.

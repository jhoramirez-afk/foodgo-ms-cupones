# API REST: cupones

Base local: http://localhost:8087/api. Swagger UI: http://localhost:8087/swagger-ui/index.html.

| Método | Ruta | HTTP de éxito |
|---|---|---:|
| POST | /cupones | 201 |
| GET | /cupones | 200 |
| GET | /cupones/{id} | 200 |
| PUT | /cupones/{id} | 200 |
| DELETE | /cupones/{id} | 204 |
| POST | /cupones/{id}/usos | 201 |
| GET | /cupones/{id}/usos | 200 |
| GET | /usos/{id} | 200 |
| PUT | /usos/{id} | 200 |
| DELETE | /usos/{id} | 204 |

## Crear entidad principal

```json
{
  "codigo": "BARRIO10-DEMO",
  "tipo": "PORCENTAJE",
  "descuento": 10
}
```

## Crear entidad relacionada

```json
{
  "pedidoId": 1,
  "fechaUso": "2026-10-01T13:30:00",
  "subtotalPedido": 19980
}
```

Usar el ID retornado por la creación del padre. Los ID son generados por la BD. Editar los hijos mediante sus propias rutas. Ver las reglas y los campos calculados en REGLAS_EP02.md.

Errores: 400 para datos o JSON inválidos; 404 para recurso/relación local inexistente; 409 para conflictos de integridad o unicidad cuando corresponda. Un campo demasiado largo devuelve 400. Los mensajes y validationErrors se entregan mediante ApiExceptionHandler.

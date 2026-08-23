# Cupon — Contrato de la API REST

## Base

- **Base path**: `/api/cupones`
- **Formato**: JSON — **Puerto**: 8087 (configurable con `PORT`)

## Recursos

| Método | Ruta | Códigos de estado | Descripción |
|--------|------|-------------------|-------------|
| GET | `/api/cupones` | 200 | Lista todos los recursos |
| GET | `/api/cupones/{id}` | 200 / 404 | Obtiene un recurso por id |
| POST | `/api/cupones` | 201 / 400 | Crea un recurso |
| PUT | `/api/cupones/{id}` | 200 / 404 / 400 | Actualiza un recurso |
| DELETE | `/api/cupones/{id}` | 204 / 404 | Elimina un recurso |

## Atributos de un recurso

| Campo | Tipo | Obligatorio | Descripción |
|-------|------|-------------|-------------|
| id | Long | - | Identificador autogenerado |
| codigo | String | Sí | Campo principal del recurso |
| tipo | String | No | Campo del dominio |
| descuento | BigDecimal | No | Campo del dominio |

## Ejemplos con curl

```bash
# Listar
curl http://localhost:8087/api/cupones

# Crear
curl -X POST http://localhost:8087/api/cupones \
  -H "Content-Type: application/json" \
  -d '{"codigo":"Demo"}'

# Obtener por id
curl http://localhost:8087/api/cupones/1

# Actualizar
curl -X PUT http://localhost:8087/api/cupones/1 \
  -H "Content-Type: application/json" \
  -d '{"codigo":"Actualizado"}'

# Eliminar
curl -X DELETE http://localhost:8087/api/cupones/1
```

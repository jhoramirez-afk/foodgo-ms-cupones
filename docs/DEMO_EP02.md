# Demostración EP02: cupones

## CRUD, relación y persistencia

1. Compilar con mvn clean install y mostrar el JAR generado.
2. Ejecutar con el perfil mysql según README.
3. En Postman, ejecutar Crear, consultar y actualizar. Explicar los HTTP 201/200 y los ID retornados.
4. En Workbench, usar el esquema foodgo_cupones y ejecutar:

```sql
USE foodgo_cupones;
SELECT * FROM cupones;
SELECT * FROM usos_cupon;
SELECT p.id AS padre_id, h.*
FROM cupones p JOIN usos_cupon h ON h.cupon_id = p.id;
```

5. Reiniciar el JAR y repetir GET/SELECT para demostrar conservación de los datos.
6. Ejecutar los casos de error y mostrar message/validationErrors.
7. Ejecutar Eliminar y comprobar cascada. Verificar 204, luego 404, y ausencia de las filas correspondientes.

## Hilo conductor

El escenario Pedido de Camila enlaza los ocho servicios desde Postman, usando ID reales y datos ficticios. Dos hamburguesas a 9990 suman 19980. Un cupón de 10% descuenta 1998 y el pago registra 17982. El tracking llega a ENTREGADO antes de la reseña. Las reglas se detallan en REGLAS_EP02.md.

La presentación de 3–8 minutos debe mostrar código, configuración relacional, CRUD/errores, Maven/JAR y reproducción del README, con las voces de los dos estudiantes. El video se prepara al final.

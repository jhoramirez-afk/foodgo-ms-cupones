# Integridad del dominio cupones

Código único de 3 a 40 caracteres: mayúsculas, números, guiones y guiones bajos. Tipos PORCENTAJE (1–100) o MONTO_FIJO (pesos CLP enteros). Cada uso exige pedidoId positivo, fecha y `subtotalPedido` positivo. `montoDescontado` es de solo lectura y se calcula en service; los porcentajes se redondean al peso con HALF_UP. El descuento no supera el subtotal. Un cupón no se usa dos veces en el mismo pedido y no cambia de política después de utilizarse.

## Alcance de la evaluación

Se mantienen controller/service/repository/model, CRUD REST, relaciones OneToMany/ManyToOne, MySQL, Maven y Git. Las reglas hacen coherentes los datos retornados y las pruebas de éxito/error (IE1, IE2, IE3, IE5, IE6). No se agregan componentes externos. README, Postman y consultas SQL respaldan IE4, IE7 e IE10.

La colección incluye 9 peticiones de CRUD/lectura, 14 casos de error y 6 peticiones de eliminación/cascada. `mvn clean install` ejecuta pruebas unitarias, MockMvc con JPA/H2 y escenarios Cucumber. MySQL se demuestra con el perfil mysql y la colección.

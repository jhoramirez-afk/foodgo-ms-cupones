# language: es
Característica: Servicio Cupon (microservicio cupones del caso FoodGo)
  Los escenarios validan el contrato REST del microservicio alineado a sus endpoints.

  Escenario: el listado del recurso responde 200
    Dado el servicio "Cupon" está disponible
    Cuando consulto el listado de "cupones"
    Entonces el listado responde con código 200

  Escenario: ciclo de vida completo del recurso
    Dado un nuevo "cupon" con codigo "BARRIO10-CUCUMBER"
    Cuando consulto el "cupon" recién creado
    Entonces el recurso tiene codigo "BARRIO10-CUCUMBER" y código 200
    Cuando actualizo el "cupon" con codigo "BARRIO15-CUCUMBER"
    Entonces el recurso queda con codigo "BARRIO15-CUCUMBER" y código 200
    Cuando elimino el "cupon"
    Entonces la eliminación responde con código 204
    Y al consultar el "cupon" eliminado responde 404

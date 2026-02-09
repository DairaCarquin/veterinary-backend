# Pet Service

Microservicio para la gestión de mascotas en el sistema veterinario.

## Funcionalidades

- **CRUD Mascota**: Crear, leer, actualizar y eliminar mascotas
- **Asociación mascota ↔ cliente**: Las mascotas están asociadas a clientes
- **Estado de mascota**: Control del estado activo/inactivo (borrado lógico)
- **Eventos**: Publica eventos de PET_REGISTERED cuando se registra una mascota

## Tecnología

- Spring Boot 3.2.0
- Spring Data JPA
- MySQL
- Apache Kafka
- Spring Security

## Configuración

### Base de datos

La aplicación se conecta a MySQL. Configurar en `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/veterinary_db
spring.datasource.username=root
spring.datasource.password=root
```

### Kafka

Configurar los servidores de Kafka en `application.properties`:

```properties
spring.kafka.bootstrap-servers=localhost:9092
```

## Endpoints

### Crear mascota
```
POST /api/pets
Content-Type: application/json

{
  "client_id": 10,
  "name": "Firulais",
  "species": "DOG",
  "breed": "Golden Retriever",
  "age": 3
}
```

### Obtener mascota por ID
```
GET /api/pets/{id}
```

### Obtener todas las mascotas
```
GET /api/pets
```

### Actualizar mascota (campos: name, breed, age)
```
PUT /api/pets/{id}
Content-Type: application/json

{
  "name": "Firulais",
  "breed": "Golden Retriever",
  "age": 4
}
```

### Eliminar mascota (borrado lógico)
```
DELETE /api/pets/{id}
```

## Permisos

- Solo usuarios con rol ADMIN o VETERINARIAN pueden crear mascotas
- client_id es obligatorio
- Al crear una mascota se publica el evento PET_REGISTERED

## Ejecutar

```bash
./mvnw spring-boot:run
```

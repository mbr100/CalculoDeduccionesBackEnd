# CalculoDeduccionesBackEnd

API para el cálculo de deducciones I+D+i y gestión de empresas, personal y proyectos asociados.

---

## Tabla de Contenidos

- [Descripción](#descripción)
- [Tecnologías](#tecnologías)
- [Instalación](#instalación)
- [Uso](#uso)
- [Endpoints](#endpoints)
    - [Economicos](#economicos)
    - [Personal](#personal)
    - [Proyectos](#proyectos)
- [Estructura de la API](#estructura-de-la-api)
- [Licencia](#licencia)

---

## Descripción

`CalculoDeduccionesBackEnd` es un backend desarrollado en **Spring Boot** que permite:

- Gestionar empresas/económicos.
- Gestionar personal económico, incluyendo altas, bajas, bonificaciones y retribuciones.
- Gestionar proyectos asociados a cada económico.
- Calcular deducciones por proyectos y resúmenes económicos.

La API está documentada con **OpenAPI 3.1**.

---

## Tecnologías

- Java 17+ con Spring Boot
- Maven
- JPA / Hibernate
- Base de datos (H2, MySQL, o PostgreSQL según configuración)
- OpenAPI / Swagger
- JSON para intercambio de datos

---

## Instalación

### 1. Clonar el repositorio

```bash
git clone https://tu-repositorio.git
cd CalculoDeduccionesBackEnd
```

### 2. Configurar la base de datos

Editar el archivo `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/calculo_deducciones
spring.datasource.username=usuario
spring.datasource.password=contraseña
spring.jpa.hibernate.ddl-auto=update
```

### 3. Compilar y ejecutar

```bash
mvn clean install
mvn spring-boot:run
```

La API estará disponible en:

```
http://localhost:8080
```

---

## Uso

La API expone endpoints REST para **economicos**, **personal** y **proyectos**.

Se recomienda usar Swagger UI para explorar la documentación:

```
http://localhost:8080/swagger-ui/index.html
```

---

## Endpoints

### Economicos

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/economicos` | Crear nuevo económico |
| GET | `/api/economicos/` | Listar todos los económicos |
| GET | `/api/economicos/{idEconomico}` | Obtener info general de un económico |
| PUT | `/api/economicos/actualizar` | Actualizar datos de un económico |
| DELETE | `/api/economicos/` | Eliminar económico |

### Personal

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/personal/economico/crear` | Crear nuevo personal económico |
| PUT | `/api/personal/actualizar` | Actualizar datos personal |
| GET | `/api/personal/{idEconomico}/resumen-coste-personal` | Obtener resumen coste personal |
| PUT | `/api/personal/bonificacion` | Actualizar bonificación |
| DELETE | `/api/personal/{idEconomico}/{id}` | Eliminar personal económico |

### Proyectos

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/proyectos` | Crear nuevo proyecto |
| PUT | `/api/proyectos` | Actualizar proyecto existente |
| DELETE | `/api/proyectos/{idProyecto}` | Eliminar proyecto |
| GET | `/api/proyectos/economico/{idEconomico}` | Listar proyectos de un económico |

---


## Licencia

MIT License
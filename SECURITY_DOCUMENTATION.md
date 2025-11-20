# Documentación del Sistema de Gestión de Usuarios, Roles y Permisos

## Índice
1. [Resumen del Sistema](#resumen-del-sistema)
2. [Roles Definidos](#roles-definidos)
3. [Permisos por Módulo](#permisos-por-módulo)
4. [Matriz de Permisos por Rol](#matriz-de-permisos-por-rol)
5. [Endpoints de Autenticación](#endpoints-de-autenticación)
6. [Endpoints de Gestión de Usuarios](#endpoints-de-gestión-de-usuarios)
7. [Endpoints de Gestión de Roles](#endpoints-de-gestión-de-roles)
8. [Endpoints de Gestión de Permisos](#endpoints-de-gestión-de-permisos)
9. [Endpoints de Módulos Existentes](#endpoints-de-módulos-existentes)
10. [Cómo Aplicar Permisos](#cómo-aplicar-permisos)
11. [Usuario Administrador por Defecto](#usuario-administrador-por-defecto)

---

## Resumen del Sistema

Se ha implementado un sistema completo de gestión de usuarios, roles y permisos usando **Spring Security** con **JWT (JSON Web Tokens)** para autenticación stateless.

### Tecnologías Utilizadas:
- Spring Boot 3.5.4
- Spring Security
- JWT (jjwt 0.12.3)
- JPA/Hibernate
- MySQL

### Arquitectura:
- **Modelos**: `User`, `Role`, `Permission`
- **Enums**: `RoleType`, `PermissionType`
- **Autenticación**: JWT con tokens Bearer
- **Autorización**: Basada en permisos granulares usando `@PreAuthorize`

---

## Roles Definidos

| Rol | Código | Descripción |
|-----|--------|-------------|
| Super Administrador | `SUPER_ADMIN` | Acceso total al sistema incluyendo gestión de usuarios, roles y permisos |
| Administrador | `ADMIN` | Gestión de usuarios y datos del sistema (sin modificar roles/permisos del sistema) |
| Gestor | `GESTOR` | CRUD completo en económicos, proyectos y personal |
| Consultor | `CONSULTOR` | Solo lectura de datos |
| Usuario | `USUARIO` | Acceso básico de lectura |

---

## Permisos por Módulo

### Módulo Económicos
- `CREATE_ECONOMICO`: Crear nuevos económicos
- `READ_ECONOMICO`: Visualizar económicos
- `UPDATE_ECONOMICO`: Modificar económicos existentes
- `DELETE_ECONOMICO`: Eliminar económicos

### Módulo Proyectos
- `CREATE_PROYECTO`: Crear nuevos proyectos
- `READ_PROYECTO`: Visualizar proyectos
- `UPDATE_PROYECTO`: Modificar proyectos existentes
- `DELETE_PROYECTO`: Eliminar proyectos

### Módulo Personal
- `CREATE_PERSONAL`: Crear nuevos registros de personal
- `READ_PERSONAL`: Visualizar personal
- `UPDATE_PERSONAL`: Modificar registros de personal
- `DELETE_PERSONAL`: Eliminar registros de personal

### Módulo Usuarios
- `CREATE_USER`: Crear nuevos usuarios
- `READ_USER`: Visualizar usuarios
- `UPDATE_USER`: Modificar usuarios existentes
- `DELETE_USER`: Eliminar usuarios

### Módulo Roles
- `CREATE_ROLE`: Crear nuevos roles
- `READ_ROLE`: Visualizar roles
- `UPDATE_ROLE`: Modificar roles existentes
- `DELETE_ROLE`: Eliminar roles
- `ASSIGN_ROLE`: Asignar roles a usuarios

### Módulo Permisos
- `READ_PERMISSION`: Visualizar permisos
- `ASSIGN_PERMISSION`: Asignar permisos a roles

---

## Matriz de Permisos por Rol

| Permiso | SUPER_ADMIN | ADMIN | GESTOR | CONSULTOR | USUARIO |
|---------|-------------|-------|--------|-----------|---------|
| **Económicos** |||||
| CREATE_ECONOMICO | ✅ | ✅ | ✅ | ❌ | ❌ |
| READ_ECONOMICO | ✅ | ✅ | ✅ | ✅ | ✅ |
| UPDATE_ECONOMICO | ✅ | ✅ | ✅ | ❌ | ❌ |
| DELETE_ECONOMICO | ✅ | ✅ | ✅ | ❌ | ❌ |
| **Proyectos** |||||
| CREATE_PROYECTO | ✅ | ✅ | ✅ | ❌ | ❌ |
| READ_PROYECTO | ✅ | ✅ | ✅ | ✅ | ✅ |
| UPDATE_PROYECTO | ✅ | ✅ | ✅ | ❌ | ❌ |
| DELETE_PROYECTO | ✅ | ✅ | ✅ | ❌ | ❌ |
| **Personal** |||||
| CREATE_PERSONAL | ✅ | ✅ | ✅ | ❌ | ❌ |
| READ_PERSONAL | ✅ | ✅ | ✅ | ✅ | ✅ |
| UPDATE_PERSONAL | ✅ | ✅ | ✅ | ❌ | ❌ |
| DELETE_PERSONAL | ✅ | ✅ | ✅ | ❌ | ❌ |
| **Usuarios** |||||
| CREATE_USER | ✅ | ✅ | ❌ | ❌ | ❌ |
| READ_USER | ✅ | ✅ | ✅ | ✅ | ❌ |
| UPDATE_USER | ✅ | ✅ | ❌ | ❌ | ❌ |
| DELETE_USER | ✅ | ✅ | ❌ | ❌ | ❌ |
| **Roles** |||||
| CREATE_ROLE | ✅ | ❌ | ❌ | ❌ | ❌ |
| READ_ROLE | ✅ | ✅ | ❌ | ✅ | ❌ |
| UPDATE_ROLE | ✅ | ❌ | ❌ | ❌ | ❌ |
| DELETE_ROLE | ✅ | ❌ | ❌ | ❌ | ❌ |
| ASSIGN_ROLE | ✅ | ✅ | ❌ | ❌ | ❌ |
| **Permisos** |||||
| READ_PERMISSION | ✅ | ✅ | ❌ | ✅ | ❌ |
| ASSIGN_PERMISSION | ✅ | ✅ | ❌ | ❌ | ❌ |

---

## Endpoints de Autenticación

### POST /api/auth/login
**Acceso**: Público (sin autenticación)

**Descripción**: Autentica un usuario y devuelve un token JWT

**Request Body**:
```json
{
  "username": "string",
  "password": "string"
}
```

**Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "userId": 1,
  "username": "admin",
  "email": "admin@calculodeducciones.com",
  "roles": ["SUPER_ADMIN"],
  "permissions": ["CREATE_ECONOMICO", "READ_ECONOMICO", ...]
}
```

### POST /api/auth/register
**Acceso**: Público (sin autenticación)

**Descripción**: Registra un nuevo usuario con rol USUARIO por defecto

**Request Body**:
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "firstName": "string",
  "lastName": "string"
}
```

**Response** (201 Created): Mismo formato que login

---

## Endpoints de Gestión de Usuarios

### GET /api/users
**Permiso Requerido**: `READ_USER`

**Descripción**: Obtiene todos los usuarios paginados

**Query Params**:
- `page` (default: 0)
- `size` (default: 20)
- `sort` (default: username)

**Response** (200 OK): Page de UserDTO

### GET /api/users/search?query={search}
**Permiso Requerido**: `READ_USER`

**Descripción**: Busca usuarios por nombre, apellido, username o email

### GET /api/users/{id}
**Permiso Requerido**: `READ_USER`

**Descripción**: Obtiene un usuario por ID

**Response** (200 OK):
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@calculodeducciones.com",
  "firstName": "Administrador",
  "lastName": "Sistema",
  "active": true,
  "lastLoginDate": "2025-01-20T10:30:00",
  "createdAt": "2025-01-01T00:00:00",
  "roles": [
    {
      "id": 1,
      "name": "SUPER_ADMIN",
      "displayName": "Super Administrador",
      "description": "Acceso total al sistema",
      "active": true,
      "isSystemRole": true
    }
  ],
  "permissions": ["CREATE_ECONOMICO", "READ_ECONOMICO", ...]
}
```

### POST /api/users
**Permiso Requerido**: `CREATE_USER`

**Descripción**: Crea un nuevo usuario

**Request Body**:
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "firstName": "string",
  "lastName": "string",
  "roles": ["GESTOR", "CONSULTOR"]
}
```

### PUT /api/users/{id}
**Permiso Requerido**: `UPDATE_USER`

**Descripción**: Actualiza un usuario existente

**Request Body**:
```json
{
  "email": "string",
  "firstName": "string",
  "lastName": "string",
  "active": true,
  "roles": ["GESTOR"]
}
```

### DELETE /api/users/{id}
**Permiso Requerido**: `DELETE_USER`

**Descripción**: Elimina un usuario

### POST /api/users/assign-role
**Permiso Requerido**: `ASSIGN_ROLE`

**Descripción**: Asigna un rol a un usuario

**Request Body**:
```json
{
  "userId": 1,
  "roleName": "GESTOR"
}
```

### PATCH /api/users/{id}/activate
**Permiso Requerido**: `UPDATE_USER`

**Descripción**: Activa un usuario desactivado

### PATCH /api/users/{id}/deactivate
**Permiso Requerido**: `UPDATE_USER`

**Descripción**: Desactiva un usuario activo

---

## Endpoints de Gestión de Roles

### GET /api/roles
**Permiso Requerido**: `READ_ROLE`

**Descripción**: Obtiene todos los roles del sistema

**Response** (200 OK):
```json
[
  {
    "id": 1,
    "name": "SUPER_ADMIN",
    "displayName": "Super Administrador",
    "description": "Acceso total al sistema",
    "active": true,
    "isSystemRole": true,
    "permissions": [
      {
        "id": 1,
        "name": "CREATE_ECONOMICO",
        "displayName": "Crear Económico",
        "description": "Permite crear nuevos económicos",
        "active": true
      }
    ]
  }
]
```

### GET /api/roles/{id}
**Permiso Requerido**: `READ_ROLE`

**Descripción**: Obtiene un rol por ID

### POST /api/roles/assign-permission
**Permiso Requerido**: `ASSIGN_PERMISSION`

**Descripción**: Asigna un permiso a un rol

**Request Body**:
```json
{
  "roleId": 1,
  "permissionName": "CREATE_ECONOMICO"
}
```

### DELETE /api/roles/{roleId}/permissions/{permissionName}
**Permiso Requerido**: `ASSIGN_PERMISSION`

**Descripción**: Remueve un permiso de un rol

---

## Endpoints de Gestión de Permisos

### GET /api/permissions
**Permiso Requerido**: `READ_PERMISSION`

**Descripción**: Obtiene todos los permisos del sistema

**Response** (200 OK):
```json
[
  {
    "id": 1,
    "name": "CREATE_ECONOMICO",
    "displayName": "Crear Económico",
    "description": "Permite crear nuevos económicos",
    "active": true
  }
]
```

### GET /api/permissions/{id}
**Permiso Requerido**: `READ_PERMISSION`

**Descripción**: Obtiene un permiso por ID

---

## Endpoints de Módulos Existentes

### Módulo Económicos (/api/economicos)

| Método | Endpoint | Permiso Necesario | Descripción |
|--------|----------|-------------------|-------------|
| GET | / | `READ_ECONOMICO` | Listar económicos paginados |
| GET | /{id} | `READ_ECONOMICO` | Obtener económico por ID |
| POST | / | `CREATE_ECONOMICO` | Crear nuevo económico |
| PUT | /actualizar | `UPDATE_ECONOMICO` | Actualizar económico |
| DELETE | / | `DELETE_ECONOMICO` | Eliminar económico |
| GET | /{id}/resumen | `READ_ECONOMICO` | Obtener resumen económico |

**NOTA**: Para aplicar estos permisos a los controllers existentes, debe añadir la anotación `@PreAuthorize` a cada método. Ejemplo:

```java
@GetMapping("")
@PreAuthorize("hasAuthority('READ_ECONOMICO')")
public ResponseEntity<Page<EconomicoListadoGeneralDto>> listadoDeEconomicosPaginado(...) {
    // código existente
}
```

### Módulo Proyectos (/api/proyectos)

| Método | Endpoint | Permiso Necesario | Descripción |
|--------|----------|-------------------|-------------|
| GET | / | `READ_PROYECTO` | Listar proyectos |
| GET | /{id} | `READ_PROYECTO` | Obtener proyecto por ID |
| POST | / | `CREATE_PROYECTO` | Crear nuevo proyecto |
| PUT | /{id} | `UPDATE_PROYECTO` | Actualizar proyecto |
| DELETE | /{id} | `DELETE_PROYECTO` | Eliminar proyecto |

### Módulo Personal (/api/personal)

| Método | Endpoint | Permiso Necesario | Descripción |
|--------|----------|-------------------|-------------|
| GET | / | `READ_PERSONAL` | Listar personal |
| GET | /{id} | `READ_PERSONAL` | Obtener personal por ID |
| POST | / | `CREATE_PERSONAL` | Crear nuevo personal |
| PUT | /{id} | `UPDATE_PERSONAL` | Actualizar personal |
| DELETE | /{id} | `DELETE_PERSONAL` | Eliminar personal |

---

## Cómo Aplicar Permisos

Para proteger un endpoint con permisos, siga estos pasos:

1. **Importar la anotación**:
```java
import org.springframework.security.access.prepost.PreAuthorize;
```

2. **Añadir la anotación al método**:
```java
@GetMapping("/mi-endpoint")
@PreAuthorize("hasAuthority('NOMBRE_DEL_PERMISO')")
public ResponseEntity<?> miMetodo() {
    // código
}
```

3. **Para múltiples permisos** (requiere CUALQUIERA):
```java
@PreAuthorize("hasAnyAuthority('CREATE_ECONOMICO', 'UPDATE_ECONOMICO')")
```

4. **Para múltiples permisos** (requiere TODOS):
```java
@PreAuthorize("hasAuthority('CREATE_ECONOMICO') and hasAuthority('UPDATE_ECONOMICO')")
```

5. **Verificar roles**:
```java
@PreAuthorize("hasRole('SUPER_ADMIN')")
```

---

## Usuario Administrador por Defecto

Al iniciar la aplicación por primera vez, se crea automáticamente un usuario administrador:

- **Username**: `admin`
- **Password**: `admin123`
- **Email**: `admin@calculodeducciones.com`
- **Rol**: `SUPER_ADMIN` (todos los permisos)

**⚠️ IMPORTANTE**: Cambie esta contraseña inmediatamente en producción.

---

## Cómo Usar el Sistema

### 1. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### 2. Usar el Token
Una vez que obtenga el token, inclúyalo en el header `Authorization` de todas las peticiones:

```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### 3. Swagger UI
Puede acceder a la documentación interactiva en:
```
http://localhost:8080/swagger-ui.html
```

Para autenticarse en Swagger:
1. Click en el botón "Authorize"
2. Ingrese: `Bearer {su-token-jwt}`
3. Click en "Authorize"

---

## Configuración JWT

La configuración JWT se encuentra en `application.yml`:

```yaml
jwt:
  secret: "tu-clave-secreta-aqui" # Cambiar en producción
  expiration: 86400000 # 24 horas en milisegundos
```

---

## CORS

La configuración CORS permite solicitudes desde:
- `http://localhost:3000` (React)
- `http://localhost:4200` (Angular)

Para añadir más orígenes, modifique `SecurityConfiguration.java`:

```java
configuration.setAllowedOrigins(List.of(
    "http://localhost:3000",
    "http://localhost:4200",
    "https://tu-dominio.com"
));
```

---

## Estructura de Base de Datos

El sistema crea las siguientes tablas:

- `users`: Información de usuarios
- `roles`: Roles del sistema
- `permissions`: Permisos disponibles
- `user_roles`: Relación usuarios-roles (many-to-many)
- `role_permissions`: Relación roles-permisos (many-to-many)

---

## Próximos Pasos

1. **Aplicar permisos a endpoints existentes**: Añadir `@PreAuthorize` a los controllers de Económicos, Proyectos y Personal
2. **Cambiar contraseña del admin**: Inmediatamente después del despliegue
3. **Configurar JWT secret**: Usar una clave secreta segura en producción
4. **Personalizar CORS**: Añadir los dominios de producción
5. **Crear usuarios iniciales**: Según las necesidades de su organización

---

## Soporte y Contacto

Para dudas o problemas con el sistema de autenticación y autorización, contacte al equipo de desarrollo.

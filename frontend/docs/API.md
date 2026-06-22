# API — Sistema de Biblioteca

> Backend: Spring Boot — Puerto: `8080`  
> Base URL: `http://localhost:8080/api`

---

## Índice

- [Autenticación](#autenticación)
- [Libros](#libros)
- [Préstamos](#préstamos)
- [Usuarios](#usuarios)
- [Paginación](#paginación)
- [Errores](#errores)
- [Reglas de Negocio](#reglas-de-negocio)

---

## Autenticación

Todas las rutas excepto `/api/auth/**` requieren JWT.

**Header:**
```
Authorization: Bearer <token>
```

### POST `/api/auth/register`

Registrar un nuevo usuario.

**Request body:**
```json
{
  "username": "jperez",
  "password": "miPassword123",
  "email": "jperez@mail.com",
  "fullName": "Juan Pérez",
  "role": "USER"
}
```

| Campo    | Tipo   | Obligatorio | Notas                           |
|----------|--------|-------------|----------------------------------|
| username | String | sí          |                                  |
| password | String | sí          | 5-11 caracteres                  |
| email    | String | sí          | Formato email                    |
| fullName | String | sí          |                                  |
| role     | String | no          | `USER` por defecto, también `ADMIN` |

**Response `201 Created`:**
```json
{
  "id": 1,
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "jperez",
  "email": "jperez@mail.com",
  "fullName": "Juan Pérez",
  "role": "USER"
}
```

### POST `/api/auth/login`

Iniciar sesión.

**Request body:**
```json
{
  "username": "jperez",
  "password": "miPassword123"
}
```

**Response `200 OK`:**
```json
{
  "id": 1,
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "jperez",
  "email": "jperez@mail.com",
  "fullName": "Juan Pérez",
  "role": "USER"
}
```

### GET `/api/auth/me`

Obtener datos del usuario autenticado (requiere JWT).

**Response `200 OK`:**
```json
{
  "id": 1,
  "token": null,
  "username": "jperez",
  "email": "jperez@mail.com",
  "fullName": "Juan Pérez",
  "role": "USER"
}
```

> `token` siempre es `null` en este endpoint.

---

## Libros

Ruta base: `/api/libros`

### GET `/api/libros`

Listar todos los libros (paginado).

**Query params opcionales:** `?page=0&size=10&sort=titulo,asc`

**Response `200 OK`:**
```json
{
  "content": [
    {
      "id": 1,
      "titulo": "Cien años de soledad",
      "autor": "Gabriel García Márquez",
      "isbn": "978-84-376-0494-7",
      "codigoQr": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "genero": "Realismo mágico",
      "anioPublicacion": 1967,
      "ejemplaresDisponibles": 3,
      "createdAt": "2026-06-20T10:30:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

### GET `/api/libros/{id}`

Obtener detalle de un libro.

**Response `200 OK`:**
```json
{
  "id": 1,
  "titulo": "Cien años de soledad",
  "autor": "Gabriel García Márquez",
  "isbn": "978-84-376-0494-7",
  "codigoQr": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "genero": "Realismo mágico",
  "anioPublicacion": 1967,
  "ejemplaresDisponibles": 3,
  "createdAt": "2026-06-20T10:30:00"
}
```

### GET `/api/libros/qr/{codigo}`

Buscar libro por su código QR.

**Response `200 OK`:**
```json
{
  "id": 1,
  "titulo": "Cien años de soledad",
  "autor": "Gabriel García Márquez",
  "isbn": "978-84-376-0494-7",
  "codigoQr": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "genero": "Realismo mágico",
  "anioPublicacion": 1967,
  "ejemplaresDisponibles": 3,
  "createdAt": "2026-06-20T10:30:00"
}
```

### GET `/api/libros/{id}/qr`

Obtener la imagen QR del libro (formato PNG).

**Response `200 OK`:** Content-Type `image/png`, body binario.

### POST `/api/libros` **[ADMIN]**

Crear un nuevo libro.

**Request body:**
```json
{
  "titulo": "Cien años de soledad",
  "autor": "Gabriel García Márquez",
  "isbn": "978-84-376-0494-7",
  "genero": "Realismo mágico",
  "anioPublicacion": 1967,
  "ejemplaresDisponibles": 3
}
```

| Campo                 | Tipo    | Obligatorio | Notas          |
|-----------------------|---------|-------------|-----------------|
| titulo                | String  | sí          |                 |
| autor                 | String  | sí          |                 |
| isbn                  | String  | sí          | Único           |
| genero                | String  | no          |                 |
| anioPublicacion       | Integer | no          |                 |
| ejemplaresDisponibles | Integer | sí          | >= 0            |

**Response `201 Created`:**
```json
{
  "id": 1,
  "titulo": "Cien años de soledad",
  "autor": "Gabriel García Márquez",
  "isbn": "978-84-376-0494-7",
  "codigoQr": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "genero": "Realismo mágico",
  "anioPublicacion": 1967,
  "ejemplaresDisponibles": 3,
  "createdAt": "2026-06-21T10:30:00"
}
```

> `codigoQr` se genera automáticamente (UUID).

### PUT `/api/libros/{id}` **[ADMIN]**

Actualizar un libro existente.

**Request body:** Mismo formato que POST.

**Response `200 OK`:** Mismo formato que GET.

### DELETE `/api/libros/{id}` **[ADMIN]**

Eliminar un libro.

**Response `204 No Content`**

> No se puede eliminar si tiene préstamos activos.

---

## Préstamos

Ruta base: `/api/prestamos`

### GET `/api/prestamos`

Listar préstamos. ADMIN ve todos; USER ve solo los propios.

**Query params opcionales:** `?page=0&size=10`

**Response `200 OK`:**
```json
{
  "content": [
    {
      "id": 1,
      "userId": 1,
      "username": "jperez",
      "bookId": 1,
      "bookTitulo": "Cien años de soledad",
      "codigoQr": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "fechaPrestamo": "2026-06-15T10:00:00",
      "fechaDevolucion": null,
      "estado": "ACTIVO",
      "vencido": false,
      "createdAt": "2026-06-15T10:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

| Campo             | Tipo    | Notas                                          |
|-------------------|---------|-------------------------------------------------|
| id                | Long    |                                                 |
| userId            | Long    | ID del usuario que tomó el préstamo             |
| username          | String  | Nombre de usuario                               |
| bookId            | Long    | ID del libro                                    |
| bookTitulo        | String  | Título del libro                                |
| codigoQr          | String  | Código QR del libro                             |
| fechaPrestamo     | String  | ISO datetime                                    |
| fechaDevolucion   | String  | `null` si no se devolvió aún                    |
| estado            | String  | `ACTIVO` / `DEVUELTO` / `VENCIDO`              |
| vencido           | boolean | Calculado dinámicamente si ACTIVO y vencido     |
| createdAt         | String  | ISO datetime                                    |

### GET `/api/prestamos/{id}`

Obtener detalle de un préstamo. USER solo ve sus propios.

**Response `200 OK`:** Mismo formato que un elemento del listado.

### POST `/api/prestamos`

Crear un préstamo por ID de libro.

**Request body:**
```json
{
  "bookId": 1
}
```

**Response `201 Created`:** Mismo formato que GET.

> Error si `ejemplaresDisponibles == 0`.

### POST `/api/prestamos/scan`

Crear un préstamo escaneando código QR.

**Request body:**
```json
{
  "codigoQr": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

**Response `201 Created`:** Mismo formato que GET.

### POST `/api/prestamos/scan-devolver`

Devolver un libro escaneando su código QR.

**Request body:**
```json
{
  "codigoQr": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

**Response `200 OK`:** Loan con `estado: "DEVUELTO"` y `fechaDevolucion` asignada.

### PUT `/api/prestamos/{id}/devolver`

Devolver un libro por ID de préstamo.

**Response `200 OK`:** Loan actualizado con `estado: "DEVUELTO"`.

### PUT `/api/prestamos/{id}/extender`

Extender préstamo 7 días más.

**Response `200 OK`:** Loan actualizado con nueva `fechaMaximaDevolucion`.

> Reglas de extensión:
> - Solo se puede pedir en el último día (desde las 00:00 hasta `fechaMaximaDevolucion`).
> - Si hay copias disponibles → extensiones ilimitadas.
> - Si no hay copias disponibles → máximo 1 extensión.

---

## Usuarios

Ruta base: `/api/usuarios`

### GET `/api/usuarios` **[ADMIN]**

Listar todos los usuarios.

**Response `200 OK`:**
```json
[
  {
    "id": 1,
    "username": "jperez",
    "email": "jperez@mail.com",
    "fullName": "Juan Pérez",
    "role": "USER",
    "createdAt": "2026-06-20T10:00:00"
  }
]
```

### GET `/api/usuarios/{id}` **[ADMIN]**

Obtener un usuario por ID.

**Response `200 OK`:**
```json
{
  "id": 1,
  "username": "jperez",
  "email": "jperez@mail.com",
  "fullName": "Juan Pérez",
  "role": "USER",
  "createdAt": "2026-06-20T10:00:00"
}
```

### PUT `/api/usuarios/{id}` **[ADMIN]**

Actualizar datos de un usuario.

**Request body:**
```json
{
  "fullName": "Juan Pérez Actualizado",
  "email": "jperez@nuevo.com"
}
```

**Response `200 OK`:** Mismo formato que GET.

### DELETE `/api/usuarios/{id}` **[ADMIN]**

Eliminar un usuario.

**Response `204 No Content`**

> No se puede eliminar si tiene préstamos activos.

---

## Paginación

Los endpoints GET de libros (`/api/libros`) y préstamos (`/api/prestamos`) devuelven páginas.

**Parámetros query:**
```
?page=0&size=10&sort=titulo,asc
```

| Parámetro | Tipo   | Default        | Notas                         |
|-----------|--------|----------------|--------------------------------|
| page      | int    | 0              | Página (0-indexed)             |
| size      | int    | 10             | Elementos por página           |
| sort      | String | `titulo,asc`   | `campo,dirección` (varias permitidas) |

**Respuesta paginada:**
```json
{
  "content": [ ... ],
  "page": 0,
  "size": 10,
  "totalElements": 42,
  "totalPages": 5
}
```

---

## Errores

### Formato general
```json
{
  "timestamp": "2026-06-21T10:30:00",
  "status": 400,
  "error": "Mensaje descriptivo del error"
}
```

### Errores de validación (400)
```json
{
  "timestamp": "2026-06-21T10:30:00",
  "status": 400,
  "error": "Validation failed",
  "fieldErrors": {
    "password": "size must be between 5 and 11",
    "email": "must be a well-formed email address"
  }
}
```

### Códigos de estado

| Código | Significado          | Causas comunes                              |
|--------|----------------------|---------------------------------------------|
| 200    | OK                   | Éxito                                       |
| 201    | Created              | Recurso creado                              |
| 204    | No Content           | Eliminación exitosa                         |
| 400    | Bad Request          | Validación fallida, argumento inválido      |
| 401    | Unauthorized         | JWT faltante, inválido o expirado           |
| 403    | Forbidden            | No tiene el rol requerido (ADMIN)           |
| 404    | Not Found            | Recurso no encontrado                       |
| 500    | Internal Server Error| Error inesperado                            |

---

## Reglas de Negocio

- **JWT**: todas las rutas protegidas excepto `POST /api/auth/register` y `POST /api/auth/login`. El JWT se envía como `Authorization: Bearer <token>`.
- **Roles**: `USER` puede ver libros y gestionar sus propios préstamos. `ADMIN` puede crear/editar/eliminar libros y gestionar usuarios.
- **Stock**: no se puede prestar un libro si `ejemplaresDisponibles == 0`.
- **Devolución**: al devolver, el libro incrementa `ejemplaresDisponibles` y el préstamo pasa a `DEVUELTO`.
- **Vencido**: se calcula dinámicamente. Si `estado == ACTIVO` y la fecha actual supera `fechaMaximaDevolucion`, se considera vencido.
- **Extensión**: solo el último día hábil. Si hay copias disponibles → extensiones ilimitadas. Si no → máximo 1.
- **QR**: se genera un UUID automáticamente al crear cada libro. La imagen QR se genera on-demand.
- **CORS**: habilitado exclusivamente para `http://localhost:4200`.

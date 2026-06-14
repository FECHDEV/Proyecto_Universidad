# Contexto del Proyecto — Biblioteca

## Resumen
Aplicación Spring Boot para gestión de biblioteca: catálogo de libros y control de préstamos, con autenticación por usuario/contraseña y JWT.

## Stack
- Java 21
- Spring Boot 4.0.6
  - spring-boot-starter-data-jpa
  - spring-boot-starter-security
  - spring-boot-starter-validation
  - spring-boot-starter-web
- Maven
- MySQL (mysql-connector-j)
- Spring DevTools

## Estructura
```
demo/
├── src/main/java/com/java/prueba_ia/demo/
│   ├── DemoApplication.java
│   ├── config/
│   │   ├── JpaAuditingConfig.java
│   │   ├── JwtAuthFilter.java
│   │   ├── JwtUtil.java
│   │   ├── SecurityConfig.java
│   │   └── WebConfig.java
│   ├── controllers/
│   │   ├── AuthController.java
│   │   ├── BookController.java
│   │   ├── LoanController.java
│   │   └── UserController.java
│   ├── dto/
│   │   ├── auth/
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   └── AuthResponse.java
│   │   ├── book/
│   │   │   ├── BookRequest.java
│   │   │   └── BookResponse.java
│   │   └── loan/
│   │       ├── LoanRequest.java
│   │       └── LoanResponse.java
│   ├── entity/
│   │   ├── User.java
│   │   ├── Book.java
│   │   ├── Loan.java
│   │   └── EstadoPrestamo.java
│   ├── exception/
│   │   └── GlobalExceptionHandler.java
│   ├── mapper/
│   │   ├── LoanMapper.java
│   │   └── BookMapper.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── BookRepository.java
│   │   └── LoanRepository.java
│   └── service/
│       ├── AuthService.java
│       ├── AuthServiceImpl.java
│       ├── BookService.java
│       ├── BookServiceImpl.java
│       ├── LoanService.java
│       └── LoanServiceImpl.java
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

## Entidades

### User
| Campo     | Tipo           | Notas                          |
|-----------|----------------|--------------------------------|
| id        | Long           | PK, autoincrement              |
| username  | String         | Único, not null                |
| password  | String         | BCrypt hashed                  |
| email     | String         | Único                          |
| fullName  | String         |                                |
| role      | Role (Enum)    | `ADMIN` / `USER`               |
| createdAt | LocalDateTime  | `@CreatedDate`                 |

### Book
| Campo                | Tipo           | Notas             |
|----------------------|----------------|-------------------|
| id                   | Long           | PK, autoincrement |
| titulo               | String         |                   |
| autor                | String         |                   |
| isbn                 | String         | Único             |
| genero               | String         |                   |
| anioPublicacion      | Integer        |                   |
| ejemplaresDisponibles| Integer        | >= 0              |
| createdAt            | LocalDateTime  | `@CreatedDate`    |

### Loan
| Campo                  | Tipo           | Notas                                       |
|------------------------|----------------|---------------------------------------------|
| id                     | Long           | PK, autoincrement                           |
| user                   | User           | `@ManyToOne`, not null                      |
| book                   | Book           | `@ManyToOne`, not null                      |
| fechaPrestamo          | LocalDateTime  | Se setea al crear (now)                     |
| fechaDevolucion        | LocalDateTime  | Null al crear, se setea al devolver         |
| fechaMaximaDevolucion  | LocalDateTime  | = fechaPrestamo + 7d (se extiende +7d)      |
| extensiones            | int            | Contador de extensiones solicitadas         |
| estado                 | EstadoPrestamo (Enum) | `ACTIVO` / `DEVUELTO` / `VENCIDO`          |
| createdAt              | LocalDateTime  | `@CreatedDate`                              |

### LoanResponse (DTO)
| Campo            | Tipo        | Notas                                    |
|------------------|-------------|------------------------------------------|
| ...              | ...         | (mismos campos que Loan salvo relaciones) |
| vencido          | boolean     | Calculado dinámicamente (no se persiste)  |

### Enums
- **Role**: `ADMIN`, `USER`
- **EstadoPrestamo**: `ACTIVO`, `DEVUELTO`, `VENCIDO`

## Endpoints
| Método | Ruta                            | Auth     | Rol   | Descripción                |
|--------|---------------------------------|----------|-------|----------------------------|
| POST   | `/api/auth/register`            | No       | -     | Crear usuario              |
| POST   | `/api/auth/login`               | No       | -     | Login, devuelve JWT        |
| GET    | `/api/libros`                   | JWT      | -     | Listar libros (paginado)   |
| GET    | `/api/libros/{id}`              | JWT      | -     | Detalle libro              |
| POST   | `/api/libros`                   | JWT      | ADMIN | Crear libro                |
| PUT    | `/api/libros/{id}`              | JWT      | ADMIN | Actualizar libro           |
| DELETE | `/api/libros/{id}`              | JWT      | ADMIN | Eliminar libro             |
| GET    | `/api/prestamos`                | JWT      | -     | Listar préstamos (ADMIN: todos, USER: solo propios) |
| POST   | `/api/prestamos`                | JWT      | -     | Crear préstamo             |
| PUT    | `/api/prestamos/{id}/devolver`  | JWT      | -     | Devolver libro             |
| PUT    | `/api/prestamos/{id}/extender`  | JWT      | -     | Extender préstamo 7d más   |
| DELETE | `/api/usuarios/{id}`            | JWT      | ADMIN | Eliminar usuario (bloqueado si tiene préstamos activos) |

Parámetros de paginación: `?page=0&size=10&sort=titulo,asc`

## Reglas importantes
- Contraseñas hasheadas con BCrypt (`PasswordEncoder`).
- Endpoints protegidos con JWT (excepto `/api/auth/**`). El JWT es auto-contenido (no consulta DB por request).
- Solo ADMIN puede crear/editar/eliminar libros.
- Un préstamo descuenta `ejemplaresDisponibles` del libro.
- No se permite prestar un libro si `ejemplaresDisponibles == 0`.
- No se puede eliminar un libro si tiene préstamos activos.
- Al devolver un libro, se incrementa `ejemplaresDisponibles` y el estado pasa a `DEVUELTO`.
- `VENCIDO` es dinámico (no se persiste): se calcula en el mapper si `estado == ACTIVO && now > fechaMaximaDevolucion`.
- **Extensión**: solo se puede pedir en el último día (desde el inicio del día hasta `fechaMaximaDevolucion`). Suma 7 días. Si `ejemplaresDisponibles == 0` → máximo 1 extensión hasta que devuelvan más copias. Si `ejemplaresDisponibles > 0` → extensiones ilimitadas.
- Auditoría con `@CreatedDate` + `JpaAuditing`.
- Manejo global de excepciones con `@RestControllerAdvice` (incluye `IllegalArgumentException`, `IllegalStateException`, `AccessDeniedException`, `MethodArgumentNotValidException`).
- Las entidades JPA usan `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` sobre el campo `id` para evitar problemas con proxies de Hibernate.

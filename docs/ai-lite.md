# AI-Lite: Biblioteca API

## Stack
Java 21, Spring Boot 4.0.6, Spring Security, JPA, MySQL, Maven, ZXing 3.5.3

## Entidades
- **User** (roles: ADMIN, USER)
- **Book** (stock: ejemplaresDisponibles, uuid único: codigoQr)
- **Loan** (estados: ACTIVO / DEVUELTO / VENCIDO — VENCIDO es dinámico, no se persiste)

## Reglas clave
- JWT obligatorio (excepto `/api/auth/**`)
- ADMIN gestiona libros (USER solo lectura)
- No préstamo si stock = 0
- Préstamo decrementa stock con `@Lock(PESSIMISTIC_WRITE)` para evitar race conditions; devolución lo incrementa
- Extensión: último día, +7d, ilimitada si hay copias, sino 1 vez
- CORS habilitado para `http://localhost:4200`

## Arquitectura
Controller → Service → Repository → Entity
DTOs separados por dominio

## Endpoints
- `POST /auth/register`, `POST /auth/login`, `GET /auth/me`
- CRUD `/libros` (ADMIN write, USER read), `GET /libros/{id}/qr`
- `POST /prestamos`, `PUT /prestamos/{id}/devolver`, `PUT /prestamos/{id}/extender`
- `POST /prestamos/scan`, `POST /prestamos/scan-devolver`
- `GET /prestamos` (ADMIN ve todos, USER ve solo sus préstamos)
- `GET /usuarios`, `DELETE /usuarios/{id}` (solo ADMIN)

## Notas
- Mantener lógica simple, sin sobre-ingeniería
- Priorizar claridad sobre abstracción
- Código en inglés, endpoints y DB en español

## Uso
Este archivo es solo para contexto rápido. No contiene reglas de desarrollo ni comportamiento del agente.

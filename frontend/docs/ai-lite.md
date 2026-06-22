# AI-Lite: Frontend Biblioteca

## Stack
Angular 19.2.25, NgRx 19.2.1, TypeScript 5.7, RxJS 7.8, Standalone components

## Estructura
```
src/app/
├── core/          # Modelos, servicios, guards, interceptors, store NgRx
├── features/      # Componentes smart (lazy loaded por ruta)
└── shared/        # Componentes dumb, layouts reutilizables
```

## Store NgRx (4 slices)
- **auth** — login, register, currentUser, token
- **books** — listado paginado, detalle, CRUD (admin)
- **loans** — listado paginado, crear, devolver, extender, scan QR
- **users** — listado, CRUD (admin)

## Endpoints clave
| Recurso     | Base URL |
|-------------|----------|
| Auth        | POST /auth/register, POST /auth/login, GET /auth/me |
| Libros      | GET/POST/PUT/DELETE /libros, GET /libros/{id}/qr, GET /libros/qr/{codigo} |
| Préstamos   | GET/POST /prestamos, POST /prestamos/scan, POST /prestamos/scan-devolver, PUT /prestamos/{id}/devolver, PUT /prestamos/{id}/extender |
| Usuarios    | GET/PUT/DELETE /usuarios (admin) |

Ver docs/API.md para contratos exactos.

## Proxy API
`src/proxy.conf.json`: `/api` → `http://localhost:8080`

## Comandos
| Comando   | Descripción                     |
|-----------|----------------------------------|
| ng serve  | Servidor de desarrollo con proxy |
| ng build  | Build de producción              |

## Reglas de negocio
- JWT en localStorage, enviado via interceptor
- ADMIN ve/gestiona todo; USER solo lectura de libros y sus préstamos
- Préstamo requiere stock > 0
- Escaneo QR para prestar y devolver
- Extensión: último día, +7d, ilimitada si hay copias, sino 1 vez
- Paginación: page (0-indexed), size (default 10), sort (campo,dir)

## Convenciones
- Store en inglés, endpoints y DB en español (coincidir con API)
- Componentes feature = smart (conectados al store)
- Componentes shared = dumb (@Input()/@Output(), sin store)

## Uso
Solo para contexto rápido. No contiene reglas de desarrollo. Ver AGENTS.md.

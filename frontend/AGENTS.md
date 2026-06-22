# AGENTS.md — Angular + NgRx Biblioteca (OpenCode)

Eres un agente senior de frontend especializado en Angular 19 + NgRx + TypeScript.

El proyecto está en fase inicial: la estructura de carpetas está definida pero no hay código implementado. Debes generar el código completo siguiendo la arquitectura establecida.

---

## FLUJO DE TRABAJO

### 1. CONTEXTO INICIAL
Siempre en este orden:

1. Leer docs/ai-lite.md
2. Leer docs/API.md solo si necesitas consultar un endpoint concreto
3. Revisar la estructura de carpetas definida

No asumir memoria entre sesiones.

### 2. ANÁLISIS
Antes de cualquier cambio:
- Revisar la estructura de carpetas existente
- Seguir el patrón definido en el plan
- Identificar impacto del cambio en el proyecto

### 3. MODO PLAN (OBLIGATORIO)
Antes de escribir código:
- Qué se va a hacer
- Por qué es necesario
- Archivos afectados
- Riesgos
- Alternativas si existen

No implementar sin plan.

### 4. IMPLEMENTACIÓN
- Cambios mínimos necesarios
- No modificar estructura existente sin razón fuerte
- Mantener patrón Smart (feature) → Dumb (shared) components
- Seguir convenciones NgRx: Actions → Reducer → Effects → Selectors
- Mantener tipado estricto en todo momento

### 5. VALIDACIÓN

BAJO:
- Solo revisión lógica
- Sin tests ni build

MEDIO:
- Revisión de flujo
- ng build --configuration production

ALTO:
- Cambios en store, interceptors o guards
- Validación completa con tests

---

## REGLAS DEL PROYECTO

- JWT se gestiona desde jwt.interceptor con localStorage
- NgRx para estado global; estado local para UI efímera
- DTOs deben coincidir exactamente con la API (ver docs/API.md)
- No duplicar lógica de negocio en componentes
- Servicios solo llaman HTTP; Effects orquestan side effects
- Componentes shared no importan módulos de features
- Lazy loading en todas las rutas features
- Proxy de desarrollo: `/api` → `http://localhost:8080` (proxy.conf.json)

## RESTRICCIONES

- Prefer incremental improvements over refactors unless explicitly requested
- No generar arquitectura nueva sin motivo
- No eliminar lógica existente sin justificación
- No hacer refactors globales sin requerimiento explícito
- No modificar package.json o angular.json (una vez creados) sin motivo justificado

## SKILLS DISPONIBLES

Hay skills instalados en .agents/skills/. Los más relevantes para este proyecto:

- angular-developer: guías oficiales de Angular (componentes, formularios signals, testing, routing, SSR). Cárgalo cuando necesites referencias actualizadas de Angular.
- frontend-design: guías de diseño UI/UX
- test-driven-development: metodología TDD

Usa el skill loading tool cuando la tarea coincida con la descripción de algún skill.

## Seguridad

- Nunca escribir credenciales reales
- Nunca exponer secretos
- Variables de entorno para: API URL, JWT config
- No loguear tokens ni datos sensibles

## MEMORY USAGE STRATEGY

- Preferir el código fuente sobre documentación cuando haya conflicto
- Evitar cargar archivos grandes innecesariamente
- Cargar skills solo cuando sean necesarios para la tarea actual

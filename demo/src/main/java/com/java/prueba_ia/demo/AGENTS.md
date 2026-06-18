# AGENTS.md — Spring Boot Biblioteca (OpenCode)

Eres un agente senior de backend especializado en Java 21 + Spring Boot.

El proyecto ya está en desarrollo, por lo tanto debes trabajar sobre código existente sin reinventarlo.

---

## 🧠 PRINCIPIO BASE

- Continuidad antes que reescritura
- Cambios pequeños y seguros
- No sobre-ingeniería
- Respetar estructura existente

---

## ⚙️ FLUJO DE TRABAJO

### 1. CONTEXTO INICIAL
Siempre en este orden:

1. Leer demo/docs/ai-lite.md
2. Analizar código relevante
3. Usar demo/docs/context.md solo si es necesario

No asumir memoria entre sesiones.

---

### 2. ANÁLISIS
Antes de cualquier cambio:

- Entender el flujo actual del sistema
- Revisar cómo está implementado realmente
- Identificar impacto del cambio

---

### 3. MODO PLAN (OBLIGATORIO)

Antes de escribir código:

- Qué se va a hacer
- Por qué es necesario
- Archivos afectados
- Riesgos
- Alternativas si existen

No implementar sin plan.

---

### 4. IMPLEMENTACIÓN

- Cambios mínimos necesarios
- No modificar arquitectura existente sin razón fuerte
- Mantener patrón Controller → Service → Repository
- No crear capas innecesarias
- Evitar refactors globales

---

### 5. VALIDACIÓN (LIGERA POR DEFECTO)

Clasificar cambios:

🟢 BAJO:
- Solo revisión lógica
- Sin tests ni build

🟡 MEDIO:
- Revisión de flujo
- Verificación de integración

🔴 ALTO:
- Cambios de seguridad o arquitectura
- Validación completa si es necesario

---

## 📦 REGLAS DEL PROYECTO

- JWT siempre debe respetarse
- Seguridad no debe romperse
- DTOs solo si ya existen o son necesarios
- No duplicar lógica
- No asumir reglas no presentes en el código
- Mantener simplicidad

---

## 🧠 OBJETIVO DEL AGENTE

- Entender el sistema rápidamente
- Modificar solo lo necesario
- Evitar complejidad innecesaria
- Mantener estabilidad del proyecto

---

## 🚫 RESTRICCIONES

- Prefer incremental improvements over refactors unless explicitly requested.
- No generar arquitectura nueva sin motivo
- No eliminar lógica existente sin justificación
- No hacer refactors globales sin requerimiento explícito

## Seguridad

- Nunca escribir credenciales reales.
- Nunca exponer secretos.
- Utilizar variables de entorno para:
    - Base de datos
    - JWT
    - API Keys
    - Tokens

## MEMORY USAGE STRATEGY

- Always read demo/docs/ai-lite.md first for understanding the system quickly.
- Only read demo/docs/context.md when ai-lite.md is not enough to understand domain or structure.
- Avoid loading full context.md unless necessary for implementation.
- Prefer codebase over documentation when there is conflict.
- Before using context.md, explicitly decide if the task requires deep domain knowledge. If not, avoid it.
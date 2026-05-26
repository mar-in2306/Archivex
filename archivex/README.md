# Archivex — Plataforma SaaS de Gestión Documental

## Descripción

Archivex es un backend Spring Boot que implementa una plataforma **multi-tenant** de gestión documental. Cada organización gestiona sus propios usuarios, documentos, tipos documentales y flujos de aprobación de forma completamente aislada.

---

## Stack Tecnológico

| Capa | Tecnología |
|------|-----------|
| Framework | Spring Boot 3.5.5 |
| Lenguaje | Java 21 |
| Seguridad | Spring Security + JWT (JJWT 0.12.6) |
| Persistencia | Spring Data JPA + MySQL |
| Mapeo | MapStruct 1.6.3 |
| Boilerplate | Lombok |
| Documentación | Springdoc OpenAPI (Swagger UI) |
| Email | Spring Mail |

---

## Arquitectura por Capas

```
presentationLayer/controller   ← REST Controllers (Swagger)
        ↓
business/service               ← Interfaces de servicio
business/service/impl          ← Lógica de negocio
        ↓
persistenceLayer/dao           ← Acceso a datos (Entity ↔ DTO)
persistenceLayer/mapper        ← MapStruct mappers
persistenceLayer/repository    ← Spring Data JPA
persistenceLayer/entity        ← Entidades JPA
        ↓
securityLayer                  ← JWT, Filtros, Spring Security
```

---

## Módulos del Sistema

1. **Autenticación** — Login JWT, registro de organización + admin
2. **Organizaciones** — Gestión multi-tenant (CRUD)
3. **Usuarios** — CRUD, roles ADMIN/USER, activar/inactivar
4. **Tipos documentales** — Parametrización por organización
5. **Documentos** — Ciclo de vida completo + subida de archivos
6. **Flujos de trabajo** — Pasos de aprobación + tareas asignadas
7. **Trazabilidad** — Auditoría de todas las acciones
8. **Plantillas de correo** — Notificaciones configurables por evento

---

## Configuración Inicial

### 1. Requisitos
- Java 21+
- MySQL 8+
- Gradle 8+

### 2. Base de datos
```sql
CREATE DATABASE archivex_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Variables de entorno (dev)
Edita `application-dev.properties`:
```properties
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
```

### 4. Ejecutar
```bash
./gradlew bootRun
```

---

## API Endpoints

Una vez arrancado, accede a Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

### Flujo de uso básico

```
1. POST /api/v1/organizations/register   → Crear organización + admin
2. POST /api/v1/auth/login               → Obtener JWT
3. Usar el JWT en el header: Authorization: Bearer <token>
4. POST /api/v1/document-types           → Crear tipo documental
5. POST /api/v1/workflow/steps           → Configurar pasos de aprobación
6. POST /api/v1/documents                → Subir documento
7. PATCH /api/v1/documents/{id}/status   → Cambiar estado (CREATED → UNDER_REVIEW → APPROVED)
8. GET  /api/v1/audit/document/{id}      → Ver trazabilidad completa
```

---

## Roles y Permisos (RBAC)

| Acción | ADMIN | USER |
|--------|-------|------|
| Crear usuarios | ✅ | ❌ |
| Eliminar usuarios | ✅ | ❌ |
| Gestionar tipos documentales | ✅ | ✅ |
| Crear/editar documentos | ✅ | ✅ |
| Cambiar estado documentos | ✅ | ✅ |
| Ver auditoría | ✅ | ✅ |
| Gestionar plantillas email | ✅ | ❌ |
| Eliminar organizaciones | ✅ | ❌ |

---

## Estados del Documento

```
CREATED → UNDER_REVIEW → APPROVED
                      ↘ REJECTED → UNDER_REVIEW (reintentar)
```

---

## Estructura del Proyecto

```
archivex/
├── build.gradle
├── settings.gradle
├── README.md
└── src/
    ├── main/
    │   ├── java/com/archivex/archivex/
    │   │   ├── ArchivexApplication.java
    │   │   ├── config/
    │   │   │   └── OpenApiConfig.java
    │   │   ├── securityLayer/
    │   │   │   ├── JwtUtil.java
    │   │   │   ├── JwtAuthFilter.java
    │   │   │   ├── UserDetailsServiceImpl.java
    │   │   │   └── SecurityConfig.java
    │   │   ├── business/
    │   │   │   ├── dto/           (10 DTOs)
    │   │   │   ├── enums/         (UserRole, DocumentStatus, WorkflowTaskStatus)
    │   │   │   └── service/       (8 interfaces + 8 implementaciones)
    │   │   ├── persistenceLayer/
    │   │   │   ├── entity/        (8 entidades JPA)
    │   │   │   ├── repository/    (8 repositorios)
    │   │   │   ├── dao/           (8 DAOs)
    │   │   │   └── mapper/        (8 mappers MapStruct)
    │   │   └── presentationLayer/
    │   │       └── controller/    (8 controllers REST)
    │   └── resources/
    │       ├── application.properties
    │       ├── application-dev.properties
    │       └── application-prod.properties
    └── test/
        └── java/com/archivex/archivex/
            └── ArchivexApplicationTests.java
```

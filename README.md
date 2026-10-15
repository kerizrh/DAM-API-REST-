# WS API Rest - DAM235 (UES)

Servicio Web RESTful desarrollado con **Spring Boot 4 / Java 21 LTS** y asegurado con **Spring Security** y **JSON Web Tokens (JWT)** para la cátedra de Desarrollo de Aplicaciones Móviles (DAM235) de la Universidad de El Salvador.

---

## 🚀 Características Principales

1. **Autenticación Stateless con JWT:**
   - Inicio de sesión en `/auth/login` con generación de token firmado con algoritmo HS256 y vigencia de 30 minutos.
   - Filtro `JwtFilter` para validar la cabecera `Authorization: Bearer <token>` en endpoints protegidos.
   - Endpoint de registro `/auth/register` con encriptación de contraseñas mediante **BCrypt**.
   - Endpoint de logout `/auth/logout`.

2. **Operaciones CRUD Completas:**
   - Mantenimiento completo de persistencia para la entidad `Product` (`GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`).

3. **Arquitectura con Soporte Dual de Base de Datos (Spring Profiles):**
   - **Perfil `h2` (Por defecto):** Base de datos en memoria para pruebas rápidas y desarrollo ágil sin necesidad de software adicional. Carga automática de tablas y datos iniciales mediante `data.sql`.
   - **Perfil `mariadb`:** Conexión directa a MariaDB local (`DBTmp`) según las especificaciones de la guía práctica.

---

## 🛠️ Requisitos de Ejecución

- **Java JDK:** 21 LTS
- **Maven Wrapper:** Incluido en el repositorio (`./mvnw` o `mvnw.cmd`)

---

## ⚙️ Configuración y Ejecución

### 1. Ejecutar con Base de Datos Embebida (H2 - Inmediato)
Por defecto, la aplicación utiliza el perfil `h2`. Para iniciarla por consola:

```bash
# En Windows:
.\mvnw.cmd spring-boot:run

# En Linux / macOS:
./mvnw spring-boot:run
```

La API estará disponible en:  
`http://localhost:8085/api/v1/demoapirestdam235`

### 2. Ejecutar con MariaDB Local
1. Iniciar el servicio local de MariaDB.
2. Ejecutar el script SQL de creación de base de datos y tablas (`DBTmp`).
3. Cambiar el perfil activo en `src/main/resources/application.properties`:
   ```properties
   spring.profiles.active=mariadb
   ```

---

## 📌 Endpoints Principales

| Módulo | Método | Endpoint | Acceso | Descripción |
| :--- | :--- | :--- | :--- | :--- |
| **Auth** | `POST` | `/auth/login` | Público | Inicia sesión y retorna el token JWT. |
| **Auth** | `POST` | `/auth/register` | Público | Registra una nueva cuenta de usuario. |
| **Auth** | `POST` | `/auth/logout` | Protegido | Cierra la sesión activa. |
| **Productos** | `GET` | `/products` ó `/productos` | Protegido | Retorna la lista de todos los productos. |
| **Productos** | `GET` | `/products/{id}` | Protegido | Retorna un producto específico por ID. |
| **Productos** | `POST` | `/products` | Protegido | Inserta un nuevo producto. |
| **Productos** | `PUT` | `/products/{id}` | Protegido | Modifica un producto existente. |
| **Productos** | `DELETE`| `/products/{id}` | Protegido | Elimina un producto por su ID. |

---

## 🧪 Pruebas Automatizadas (Integration Tests)

El proyecto incluye una suite de pruebas de integración completas basadas en `MockMvc`:
- **Clase:** `edu.sv.ues.dam235.apirestdemo.AuthAndProductCrudIntegrationTests`
- **Cobertura de Pruebas:**
  1. Acceso no autorizado a rutas protegidas sin token (HTTP 401).
  2. Registro exitoso de nuevas cuentas de usuario en `/auth/register` (HTTP 201).
  3. Rechazo de registros duplicados (HTTP 400).
  4. Login con las nuevas credenciales y emisión de token JWT (HTTP 200).
  5. Consulta de catálogo mediante `/products` y el alias oficial en español `/productos` (HTTP 200).
  6. Ciclo CRUD completo de Productos: Creación (POST 201), Consulta por ID (GET 200), Modificación (PUT 200) y Eliminación (DELETE 200).
  7. Logout y cierre de sesión (HTTP 200).

Para ejecutar toda la suite de pruebas automatizadas:
```bash
.\mvnw.cmd test
```

---

## 📮 Pruebas con Postman

En la carpeta `postman/` se encuentra la colección lista para importar:
- `postman/apirestdemo.postman_collection.json`

### Credenciales de Prueba Iniciales:
- **Usuario:** `mmartinez@temp.com`
- **Contraseña:** `Temp123.`


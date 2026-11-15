# Despliegue

Guía para poner el sistema en línea. Está pensada para un **despliegue de demostración**: una URL pública funcionando, con los proveedores externos en modo simulado.

> Lo que esto **no** es: un despliegue de producción. Al final se enumera exactamente qué falta para eso.

---

## 1. Qué se despliega

| Pieza | Dónde | Costo |
|---|---|---|
| MongoDB | Atlas, nivel M0 | Gratis |
| Backend | Railway o Render | Gratis con límites |
| Frontend | Vercel o Netlify | Gratis |

Tres servicios, tres paneles. No hace falta contratar servidor.

---

## 2. Probar la imagen en local

Si Docker funciona en tu equipo, todo el backend arranca con un comando:

```bash
docker compose up --build
```

Eso levanta MongoDB y el backend con el perfil `seed`, que carga el catálogo y los usuarios de demostración. La API queda en `http://localhost:8080`.

Para apuntar a Atlas en lugar del Mongo del contenedor:

```bash
MONGODB_URI="mongodb+srv://usuario:clave@cluster..." docker compose up --build
```

---

## 3. Desplegar el backend

### Railway

1. Entrar a [railway.app](https://railway.app) y crear un proyecto desde el repositorio `BACKEND-TALLER-AUTOMOTRIZ`.
2. Railway detecta el `Dockerfile` solo. No hay que configurar el build.
3. En **Variables**, definir:

| Variable | Valor | Por qué |
|---|---|---|
| `MONGODB_URI` | La cadena de Atlas | Sin esto apunta a un Mongo local que no existe |
| `JWT_SECRETO` | 32+ caracteres aleatorios | **Obligatorio.** Con el valor por defecto cualquiera firma un token de administrador |
| `CORS_ORIGENES` | La URL del frontend desplegado | Sin esto el navegador bloquea las llamadas |
| `SPRING_PROFILES_ACTIVE` | `seed` | Solo para la demostración: carga datos de ejemplo |
| `DIAN_SIMULADA` | `true` (ya es el valor por defecto) | La facturación se simula; no hace falta proveedor contratado |

4. Railway asigna una URL del tipo `https://algo.up.railway.app`.

Para generar un secreto decente:

```bash
openssl rand -base64 48
```

### Render

Igual de directo: **New → Web Service**, elegir el repositorio, entorno **Docker**, y las mismas variables. El plan gratuito duerme el servicio tras 15 minutos sin tráfico, así que la primera petición tarda unos 50 segundos en despertar. Para una sustentación conviene abrir la página unos minutos antes.

### Atlas: permitir la conexión

El backend desplegado sale por una IP que no es la tuya. En Atlas, **Network Access** → agregar `0.0.0.0/0`.

Es abrir la base al mundo, y para una demostración es aceptable porque el usuario y la clave siguen protegiéndola. Para producción se restringe a las IP del proveedor.

---

## 4. Desplegar el frontend

### Vercel

1. Importar el repositorio `FRONTED-TALLER-AUTOMOTRIZ`.
2. Vercel detecta Vite solo; el `vercel.json` ya trae el fallback de rutas.
3. En **Environment Variables**, definir:

```
VITE_API_URL = https://tu-backend.up.railway.app/api/v1
```

**Esta variable se incrusta al compilar, no se lee al ejecutar.** Si la cambias después, hay que volver a desplegar para que tenga efecto.

4. Copiar la URL que asigna Vercel y volver al backend para ponerla en `CORS_ORIGENES`. Luego reiniciar el backend.

Ese ida y vuelta es inevitable: cada servicio necesita la URL del otro.

### Netlify

Mismo procedimiento. El `netlify.toml` ya trae la redirección y las cabeceras de caché.

---

## 5. Comprobar que quedó bien

```bash
API=https://tu-backend.up.railway.app

# 1. El servicio responde y ve la base
curl -s $API/actuator/health

# 2. Sin token debe dar 401
curl -s -o /dev/null -w "%{http_code}\n" $API/api/v1/ordenes

# 3. El login funciona
curl -s -X POST $API/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usuario":"admin","clave":"taller2026"}'
```

Y en el navegador, sobre la URL del frontend:

- Entrar con `recepcion01` / `taller2026`
- Que el tablero cargue órdenes
- **Recargar la página estando en `/recepcion`**: si da 404, falta el fallback de rutas
- Abrir `/portal` y consultar la placa `ABC123` con el documento `10000001`

---

## 6. Qué falta para producción

Esto es lo que separa una demostración de un sistema que un taller puede usar de verdad.

| Pendiente | Por qué importa |
|---|---|
| **Proveedor tecnológico DIAN** | Hoy la facturación está **simulada** (`DIAN_SIMULADA=true`): el ciclo se recorre completo y el CUFE lleva prefijo `SIM-`, pero **no tiene validez fiscal**. Para facturar de verdad hay que contratar el proveedor y poner `DIAN_SIMULADA=false` |
| **WhatsApp Business API** | Igual: sin el número habilitado no sale ni entra ningún mensaje, y con ello se cae la aprobación de cotizaciones, que es la decisión central del diseño |
| **Pasarela de pagos** | Los cobros en línea no funcionan; los pagos se registran a mano |
| **Dominio propio con HTTPS** | El webhook de WhatsApp exige una URL pública estable. Las URL que asignan Railway o Vercel cambian al recrear el servicio |
| **Quitar el perfil `seed`** | En producción no debe haber usuarios con clave conocida |
| **Respaldos verificados** | Atlas hace copias automáticas, pero una copia que nunca se restauró no es un respaldo |
| **Revocación de tokens** | Hoy un token robado vale ocho horas. Ver la deuda registrada en el ADR-011 |
| **Pruebas de integración** | Con Testcontainers contra MongoDB real |

Mientras esos puntos estén abiertos, el despliegue sirve para mostrar el sistema, no para operar un taller.

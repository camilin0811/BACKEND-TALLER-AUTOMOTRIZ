# Despliegue

Guía para poner el sistema en línea. Está pensada para un **despliegue de demostración**: una URL pública funcionando, con los proveedores externos en modo simulado.

> Lo que esto **no** es: un despliegue de producción. Al final se enumera exactamente qué falta para eso.

---

## 1. Qué se despliega

| Pieza | Dónde | Costo |
|---|---|---|
| MongoDB | Atlas, nivel M0 | Gratis |
| Backend | Render | Gratis, sin tarjeta |
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

### Render (recomendado)

El repositorio trae un `render.yaml`, así que el servicio se crea prácticamente solo:

1. Entrar a [dashboard.render.com/blueprints](https://dashboard.render.com/blueprints) → **New Blueprint Instance**.
2. Elegir el repositorio `BACKEND-TALLER-AUTOMOTRIZ`. Render lee el `render.yaml` y propone el servicio ya configurado.
3. Solo pide dos valores, porque el resto está fijado en el Blueprint:

| Variable | Valor | Por qué |
|---|---|---|
| `MONGODB_URI` | La cadena de Atlas | Sin esto apunta a un Mongo local que no existe |
| `CORS_ORIGENES` | La URL del frontend desplegado | Sin esto el navegador bloquea las llamadas |

`JWT_SECRETO` lo **genera Render** solo. `SPRING_PROFILES_ACTIVE=seed`, `DIAN_SIMULADA=true` y el ajuste de memoria de la JVM vienen del archivo.

4. Render asigna una URL del tipo `https://taller-backend.onrender.com`.

> **El plan gratuito duerme el servicio tras 15 minutos sin tráfico.** La primera petición después de dormir tarda cerca de un minuto en responder. Para una sustentación, abre la página unos minutos antes y deja una pestaña cargada.

> El primer despliegue tarda bastante, porque la imagen compila el proyecto con Maven desde cero. Los siguientes aprovechan la caché de capas.

### Railway

Railway ya **no tiene plan gratuito permanente**: agotado el crédito de prueba pide método de pago. Si tienes cuenta con saldo, el procedimiento es crear el proyecto desde el repositorio —detecta el `Dockerfile` solo— y definir en **Variables**:

| Variable | Valor | Por qué |
|---|---|---|
| `MONGODB_URI` | La cadena de Atlas | Sin esto apunta a un Mongo local que no existe |
| `JWT_SECRETO` | 32+ caracteres aleatorios | **Obligatorio.** Con el valor por defecto cualquiera firma un token de administrador |
| `CORS_ORIGENES` | La URL del frontend desplegado | Sin esto el navegador bloquea las llamadas |
| `SPRING_PROFILES_ACTIVE` | `seed` | Solo para la demostración: carga datos de ejemplo |
| `DIAN_SIMULADA` | `true` (ya es el valor por defecto) | La facturación se simula; no hace falta proveedor contratado |

Railway asigna una URL del tipo `https://algo.up.railway.app`.

Para generar un secreto decente:

```bash
openssl rand -base64 48
```

### Otras opciones gratuitas

| Dónde | Nota |
|---|---|
| **Koyeb** | Un servicio web gratis, lee el `Dockerfile`, no duerme. Instancia de 512 MB, igual que Render |
| **Google Cloud Run** | El nivel gratuito es amplio y no duerme, pero exige activar facturación con tarjeta |
| **Fly.io** | Buen rendimiento, pero hoy pide tarjeta para cualquier despliegue |

Todas sirven: el `Dockerfile` es el mismo y las variables también. Lo único propio de Render es el `render.yaml`.

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
VITE_API_URL = https://taller-backend.onrender.com/api/v1
```

**Esta variable se incrusta al compilar, no se lee al ejecutar.** Si la cambias después, hay que volver a desplegar para que tenga efecto.

4. Copiar la URL que asigna Vercel y volver al backend para ponerla en `CORS_ORIGENES`. Render reinicia el servicio solo al guardar la variable.

Ese ida y vuelta es inevitable: cada servicio necesita la URL del otro.

### Netlify

Mismo procedimiento. El `netlify.toml` ya trae la redirección y las cabeceras de caché.

---

## 5. Comprobar que quedó bien

```bash
API=https://taller-backend.onrender.com

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
| **Dominio propio con HTTPS** | El webhook de WhatsApp exige una URL pública estable. Las URL que asignan Render o Vercel cambian al recrear el servicio |
| **Un plan que no duerma** | En el plan gratuito de Render la primera petición tras una pausa tarda cerca de un minuto. Un taller no puede esperar eso |
| **Quitar el perfil `seed`** | En producción no debe haber usuarios con clave conocida |
| **Respaldos verificados** | Atlas hace copias automáticas, pero una copia que nunca se restauró no es un respaldo |
| **Revocación de tokens** | Hoy un token robado vale ocho horas. Ver la deuda registrada en el ADR-011 |
| **Pruebas de integración** | Con Testcontainers contra MongoDB real |

Mientras esos puntos estén abiertos, el despliegue sirve para mostrar el sistema, no para operar un taller.

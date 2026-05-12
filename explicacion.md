# Explicación Técnica de G-Flow

Este documento explica el funcionamiento interno del sistema G-Flow para ayudarte a entender la arquitectura y prepararte para posibles preguntas teóricas.

## 1. Arquitectura del Sistema
El sistema sigue un modelo **Cliente-Servidor** en tiempo real:
- **Cliente (Móvil Android):** Es el productor de datos. Lee el sensor físico y los envía.
- **Servidor (PC - Python):** Es el mediador. Recibe los datos del móvil y los retransmite a la página web (Dashboard).
- **Dashboard (Navegador):** Es el consumidor final que muestra los datos al usuario.

## 2. Comunicación: WebSockets
A diferencia de una petición HTTP normal (donde el cliente pide y el servidor responde), aquí usamos **WebSockets**:
- **Bidireccional y persistente:** Una vez abierta la conexión, los datos fluyen de forma constante sin tener que abrir nuevas conexiones.
- **Baja latencia:** Es ideal para sensores porque permite enviar 20-50 mensajes por segundo sin sobrecargar el sistema.

## 3. Componentes de la App Android
La aplicación se basa en cuatro pilares fundamentales de Android:

### A. Activities (Actividades)
Son las pantallas. Tenemos dos:
1. `MainActivity`: Punto de entrada (Botón Start).
2. `SensorActivity`: Visualización de datos (Botón Stop).
*Dato de examen:* El paso de una actividad a otra se hace mediante un **Intent**.

### B. Service (Servicio: SensorService)
Es el componente más importante.
- **¿Por qué un servicio?** Porque el envío de datos y la lectura del sensor deben continuar aunque el usuario cambie de pantalla o la app no esté en primer plano.
- **Foreground Service:** Es un servicio con "prioridad alta" que muestra una notificación persistente. Android exige esto para procesos que usan sensores o red de forma continua.

### C. SensorManager
Es la API de Android que gestiona los sensores.
- Usamos el `TYPE_ACCELEROMETER`.
- Implementamos `SensorEventListener` para recibir los eventos en el método `onSensorChanged`.

### D. BroadcastReceiver
Es un sistema de "mensajería interna" de la App.
- El **Servicio** envía los datos (X, Y, Z) mediante un **Broadcast**.
- La **Actividad** (`SensorActivity`) tiene un **Receiver** que "escucha" esos mensajes para actualizar los números en la pantalla.

## 4. Flujo del Dato (Paso a paso)
1. El **Sensor** detecta movimiento.
2. `SensorService` recibe el evento en `onSensorChanged`.
3. El Servicio hace dos cosas:
   - Envía el dato por **WebSocket** al servidor (para el PC).
   - Envía el dato por **Broadcast** (para la pantalla del móvil).
4. El **Servidor** recibe el JSON del móvil y lo reenvía a los navegadores conectados.
5. El **JavaScript** del navegador actualiza el HTML.

## 5. Ciclo de Vida y Detención
Al pulsar **Stop**:
1. Se llama a `stopService()`, lo que activa el método `onDestroy()` del servicio.
2. En `onDestroy`, el servicio **deja de escuchar al sensor** (ahorro de batería) y **cierra el WebSocket**.
3. Se llama a `finish()` en la actividad para volver atrás.

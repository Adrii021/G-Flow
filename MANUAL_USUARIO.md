# Manual de Usuario - G-Flow

Este documento describe el procedimiento para ejecutar el sistema G-Flow (Cliente Android y Servidor PC).

## 1. Requisitos Previos
- **PC:** Python 3.10 o superior instalado.
- **Móvil/Emulador:** Android Studio instalado y un dispositivo con Android 8.0 o superior.
- **Red:** Ambos dispositivos deben estar en la misma red Wi-Fi.

## 2. Configuración y Ejecución del Servidor (PC)
1. Abra una terminal en la carpeta `server/`.
2. Instale las dependencias necesarias:
   ```bash
   pip install -r requirements.txt
   ```
3. Inicie el servidor:
   ```bash
   python -m uvicorn server:app --host 0.0.0.0 --port 8000
   ```
4. Abra su navegador web en: `http://localhost:8000`

## 3. Configuración y Ejecución de la App (Android)
1. Abra **Android Studio** y seleccione "Open". Elija la carpeta raíz del proyecto.
2. Localice el archivo `app/src/main/java/com/gflow/app/SensorService.kt`.
3. **IMPORTANTE:** Cambie la variable `SERVER_URL` con la dirección IP de su PC.
   *Ejemplo:* `private val SERVER_URL = "ws://192.168.1.XX:8000/ws/sensor"`
4. Conecte su dispositivo o inicie un emulador y pulse el botón **"Run" (Play)**.

## 4. Funcionamiento de la Aplicación
1. Al abrir la App, pulse el botón **"Start"**.
2. La App pasará a la pantalla de transmisión y empezará a enviar datos del acelerómetro. Podrá ver los valores en tiempo real tanto en el móvil como en el dashboard del navegador del PC.
3. Para finalizar, pulse el botón **"Stop"**. La App dejará de enviar datos y volverá a la pantalla principal.

---
*Desarrollado para la asignatura de RSM.*

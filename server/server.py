from fastapi import FastAPI, WebSocket, WebSocketDisconnect
from fastapi.responses import HTMLResponse
import json

app = FastAPI()

# Lista para guardar los clientes web conectados (el navegador)
html_clients = []

@app.get("/")
async def get():
    with open("index.html", "r", encoding="utf-8") as f:
        html_content = f.read()
    return HTMLResponse(html_content)

# Endpoint para la aplicación web (Dashboard)
@app.websocket("/ws/web")
async def websocket_web(websocket: WebSocket):
    await websocket.accept()
    html_clients.append(websocket)
    try:
        while True:
            # El navegador no envía nada, solo escucha, 
            # pero necesitamos recibir para mantener la conexión viva
            await websocket.receive_text()
    except WebSocketDisconnect:
        html_clients.remove(websocket)

# Endpoint para la App Android
@app.websocket("/ws/sensor")
async def websocket_sensor(websocket: WebSocket):
    await websocket.accept()
    print("App Android conectada")
    try:
        while True:
            # Recibimos datos del sensor en formato JSON
            data = await websocket.receive_text()
            
            # Reenviamos esos datos a todos los navegadores web conectados
            for client in html_clients:
                try:
                    await client.send_text(data)
                except:
                    pass
    except WebSocketDisconnect:
        print("App Android desconectada")

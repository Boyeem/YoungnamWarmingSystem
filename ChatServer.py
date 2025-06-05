# ChatServer.py
from websocket_server import WebsocketServer
import threading
import json
from datetime import datetime

clients = []
CHAT_LOG_FILE = "chat_history.txt"

# 로그 저장
def save_chat_log(message):
    try:
        data = json.loads(message)
        sender = data.get("sender", "알 수 없음")
        content = data.get("content", "")
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        log_line = f"[{timestamp}] {sender}: {content}\n"
        with open(CHAT_LOG_FILE, "a", encoding="utf-8") as f:
            f.write(log_line)
    except Exception as e:
        print("로그 저장 실패:", e)

# 클라이언트 접속
def new_client(client, server):
    print(f"클라이언트 접속: {client['id']}")
    clients.append(client)

# 클라이언트 종료
def client_left(client, server):
    print(f"클라이언트 종료: {client['id']}")
    if client in clients:
        clients.remove(client)

# 메시지 수신 시 전체에게 브로드캐스트
def message_received(client, server, message):
    print(f"수신: {message}")
    # save_chat_log(message)
    for c in clients:
        server.send_message(c, message)

# 서버 실행
def run_server():
    server = WebsocketServer(port=9000, host="0.0.0.0")
    server.set_fn_new_client(new_client)
    server.set_fn_client_left(client_left)
    server.set_fn_message_received(message_received)
    print("✅ WebSocket 서버 실행 중 (ws://localhost:9000)")
    server.run_forever()

if __name__ == "__main__":
    threading.Thread(target=run_server).start()

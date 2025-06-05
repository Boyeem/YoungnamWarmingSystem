import websocket
import json
import threading
import time
import mysql.connector
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException
from webdriver_manager.chrome import ChromeDriverManager
import math
from flask import Flask, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

# === 설정 ===
SPRING_WS_URL = "ws://localhost:8080/ws/disaster/python"
DB_CONFIG = {
    'host': 'localhost',
    'user': 'developer',
    'password': 'P!ssw0rd',
    'database': 'disaster_db',
    'auth_plugin': 'mysql_native_password'
}
YNU_LAT = 35.837143
YNU_LON = 128.756562
COLLECTION_INTERVAL = 600  # 10분

# === 상태 변수 ===
collecting = True
pause_event = threading.Event()
pause_event.set()

# === WebSocket 클라이언트 ===
class DisasterWebSocketClient:
    def __init__(self):
        self.ws = None
        self.connected = False
        self.lock = threading.Lock()

    def on_message(self, ws, message):
        try:
            msg = json.loads(message)
            print(f"[Spring 응답] {msg.get('status', 'NO_STATUS')}")

            if msg.get('status') == 'pause':
                print("[Spring 요청] 수집 일시정지")
                pause_collection()  # Flask 핸들러 직접 호출

            if 'duration' in msg:
                print(f"Spring Boot에서 계산된 수집 소요 시간: {msg['duration']}초")
        except json.JSONDecodeError:
            print(f"[Spring RAW 응답] {message}")

    def on_error(self, ws, error):
        print(f"[WebSocket 오류] {error}")

    def on_close(self, ws, close_status_code, close_msg):
        with self.lock:
            self.connected = False
        print(f"[연결 종료] 코드: {close_status_code}, 이유: {close_msg}")
        self.start()

    def on_open(self, ws):
        with self.lock:
            self.connected = True
        print("Spring Boot 연결 성공")
        self.ws.send(json.dumps({"type": "handshake", "status": "connected"}))

    def send_data(self, data):
        if not self.connected:
            print("연결되지 않음 - 전송 취소")
            return False
        try:
            print(f"[보내는 데이터] {json.dumps(data, ensure_ascii=False)}")
            self.ws.send(json.dumps(data, ensure_ascii=False))
            print(f"전송한 데이터 타입: {data.get('type', 'UNKNOWN')}")
            return True
        except Exception as e:
            print(f"전송 실패: {str(e)}")
            return False

    def start(self):
        print(f"WebSocket 연결 시도: {SPRING_WS_URL}")
        self.ws = websocket.WebSocketApp(
            SPRING_WS_URL,
            on_open=self.on_open,
            on_message=self.on_message,
            on_error=self.on_error,
            on_close=self.on_close
        )
        threading.Thread(target=self.ws.run_forever, daemon=True).start()

# === 거리 계산 ===
def calculate_distance(lat1, lon1, lat2, lon2):
    R = 6371
    phi1 = math.radians(lat1)
    phi2 = math.radians(lat2)
    d_phi = math.radians(lat2 - lat1)
    d_lambda = math.radians(lon2 - lon1)
    a = math.sin(d_phi / 2) ** 2 + math.cos(phi1) * math.cos(phi2) * math.sin(d_lambda / 2) ** 2
    return R * 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))

# === 브라우저 생성 ===
def create_browser():
    chrome_options = Options()
    chrome_options.add_argument('--headless')
    chrome_options.add_argument('--window-size=1920x1080')
    chrome_options.add_argument('lang=ko_KR')
    service = Service(ChromeDriverManager().install())
    return webdriver.Chrome(service=service, options=chrome_options)

# === 위치 정보 조회 ===
def get_location(agency, cursor):
    cursor.execute("""SELECT latitude, longitude FROM locations WHERE location_name = %s ORDER BY id ASC LIMIT 1""", (agency,))
    row = cursor.fetchone()
    if row:
        return float(row['latitude']), float(row['longitude'])
    return None

# === ExcludedCp 목록 조회 ===
def get_excluded_cps(cursor):
    cursor.execute("SELECT ExcludedCp FROM exceptioncp")
    return [row['ExcludedCp'] for row in cursor.fetchall()]

# === 재난 유형별 생존품 조회 ===
def get_essential_items(disaster_type, cursor):
    cursor.execute("SELECT essential_items FROM disaster_info WHERE disaster_type = %s", (disaster_type,))
    row = cursor.fetchone()
    return row['essential_items'] if row else "없음"

# === 재난 유형별 반경 조회 ===
def get_disaster_radius(disaster_type, cursor):
    cursor.execute("SELECT danger_radius FROM disaster_info WHERE disaster_type = %s", (disaster_type,))
    row = cursor.fetchone()
    if row:
        radius_str = row['danger_radius'].replace("반경", "").replace("km", "").strip()
        try:
            return float(radius_str)
        except ValueError:
            return None
    return None

# === 데이터 수집 ===
def collect_data():
    conn = mysql.connector.connect(**DB_CONFIG)
    cursor = conn.cursor(dictionary=True)
    messages = []
    excluded_cps = get_excluded_cps(cursor)
    browser = create_browser()
    start_time = time.time()
    
    excluded_num=0

    try:
        browser.get("https://www.safekorea.go.kr/idsiSFK/neo/sfk/cs/sfc/dis/disasterMsgList.jsp?menuSeq=679")
        time.sleep(2)

        try:
            print(f"[홈페이지 로드 시간] {time.time() - start_time:.2f} 초")
            browser.find_element(By.CSS_SELECTOR, "#apagefirst").click()
            time.sleep(1)
        except Exception:
            pass

        try:
            total_pages = int(browser.find_element(By.CSS_SELECTOR, "#tbpagetotal").text.strip())
        except Exception:
            total_pages = 1

        while True:
            for i in range(10):
                try:
                    id_elem = browser.find_element(By.CSS_SELECTOR, f"#disasterSms_tr_{i}_MD101_SN")
                    type_elem = browser.find_element(By.CSS_SELECTOR, f"#disasterSms_tr_{i}_DSSTR_SE_NM")
                    agency_elem = browser.find_element(By.CSS_SELECTOR, f"#disasterSms_tr_{i}_MSG_LOC")
                    content_elem = browser.find_element(By.CSS_SELECTOR, f"#disasterSms_tr_{i}_MSG_CN")

                    msg_type = type_elem.text.strip()
                    msg = {
                        'id': int(id_elem.text.strip()),
                        'type': msg_type,
                        'agency': agency_elem.text.strip(),
                        'distance': None,
                        'content': content_elem.text.strip(),
                        'essential_items': get_essential_items(msg_type, cursor)
                    }

                    if msg_type in ['긴급재난']:
                        location = get_location(msg['agency'], cursor)
                        if location:
                            lat, lon = location
                            distance = calculate_distance(YNU_LAT, YNU_LON, lat, lon)
                            danger_radius = get_disaster_radius(msg_type, cursor)

                            if danger_radius is None or distance < danger_radius:
                                msg['distance'] = f"{distance:.2f}km"
                                messages.append(msg)
                                
                                
                    if msg_type in ['안전안내'] or msg['agency'] not in excluded_cps:
                        location = get_location(msg['agency'], cursor)
                        if location:
                            lat, lon = location
                            distance = calculate_distance(YNU_LAT, YNU_LON, lat, lon)
                            danger_radius = get_disaster_radius(msg_type, cursor)

                            if danger_radius is None or distance < danger_radius:
                                msg['distance'] = f"{distance:.2f}km"
                                messages.append(msg)
                                
                    if msg_type in ['안전안내'] or msg['agency'] in excluded_cps:
                        excluded_num+=1
                        
                                
                except NoSuchElementException:
                    continue

            try:
                current_page = int(browser.find_element(By.CSS_SELECTOR, "#tbpageindex").text.strip())
                if total_pages < 10 or current_page >= total_pages:
                    break
            except NoSuchElementException:
                break

            try:
                browser.find_element(By.CSS_SELECTOR, "#apagenext").click()
                time.sleep(1)
            except NoSuchElementException:
                break
            
        print("예외된 공공기관: " +  str(excluded_num))
        
    finally:
        browser.quit()
        cursor.close()
        conn.close()

    messages.sort(key=lambda x: x['id'], reverse=True)
    return messages[:10], start_time

# === WebSocket 데이터 전송 (스레드화 지원) ===
def send_data_to_spring(messages, start_time):
    data_payload = {
        "type": "disaster_data",
        "messages": messages,
        "combined_content": messages[0].get('content', '') if messages else "",
        "timestamp": int(time.time()),
        "start_time": start_time
    }
    ws_client.send_data(data_payload)

def send_data_to_spring_threaded(messages, start_time):
    threading.Thread(target=send_data_to_spring, args=(messages, start_time), daemon=True).start()

# === 수집 루프 실행 ===
def run_collection_loop():
    def collection_loop():
        global collecting
        while True:
            if not collecting:
                print("수집 일시정지됨 - 다음 루프 대기 중...")
                time.sleep(3)
                continue
            try:
                messages, start_time = collect_data()
                if messages:
                    send_data_to_spring_threaded(messages, start_time)
                else:
                    print("수집된 메시지 없음")
            except Exception as e:
                print(f"수집 루프 오류: {str(e)}")
            time.sleep(1)
    threading.Thread(target=collection_loop, daemon=True).start()

ws_client = DisasterWebSocketClient()

@app.route('/pause', methods=['POST'])
def pause_collection():
    global collecting
    collecting = False
    print("수집 일시정지 요청됨")
    return jsonify({"status": "수집 일시정지됨", "collecting": collecting})

@app.route('/resume', methods=['GET'])
def resume_collection():
    global collecting
    collecting = True
    print("수집 재개 요청됨")
    return jsonify({"status": "수집 재개됨", "collecting": collecting})

@app.route('/start', methods=['GET'])
def start_collection():
    if not ws_client.connected:
        ws_client.start()
        time.sleep(1)
    run_collection_loop()
    return jsonify({"status": "수집 시작", "connected": ws_client.connected}), 200

@app.route('/status', methods=['GET'])
def get_status():
    return jsonify({
        "websocket_connected": ws_client.connected,
        "collecting": collecting,
        "last_activity": time.ctime()
    })

if __name__ == '__main__':
    ws_client.start()
    run_collection_loop()
    app.run(host='0.0.0.0', port=5000, debug=False, use_reloader=False)

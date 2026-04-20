from fastapi import FastAPI
from pydantic import BaseModel
from rtree import index
from loguru import logger
import math


app = FastAPI()

# 전역 변수로 인덱스 생성
idx = index.Index()


# 가상의 상점 데이터 1,000개 생성 (테스트용)
spatial_nodes = {}
for i in range(1000):
    # 대구역 기준 반경 약 10km 이내 랜덤 좌표
    s_lat = 35.87 + (0.1 * math.sin(i))
    s_lon = 128.59 + (0.1 * math.cos(i))
    spatial_nodes[i] = {"lat": s_lat, "lon": s_lon}

    # R-tree에 삽입 (반드시 사각형 형태인 (left, bottom, right, top)으로 넣어야 함)
    # 점(Point)이므로 left=right, bottom=top으로 설정
    idx.insert(i, (s_lat, s_lon, s_lat, s_lon))






@app.get("/")
async def root():
    return {"message": "Hello World"}


@app.get("/temp/{name}")
async def say_hello(name: str):
    return {"message": f"Hello {name}"}


class Point(BaseModel):
    id: int
    lat: float
    lon: float

class SearchRequest(BaseModel):
    my_lat : float
    my_lon : float
    k: int = 3 # 가장 가까운 몇 개를 찾을 것인가?

class DistanceRequest(BaseModel):
    lat1: float
    lon1: float
    lat2: float
    lon2: float

def calculate_haversine(lat1, lon1, lat2, lon2):
    # 지구 반지름 (미터 단위)
    r = 6371000

    # 라디안 변환
    phi1, phi2 = math.radians(lat1), math.radians(lat2)
    dphi = math.radians(lon2 - lon1)
    dlambda = math.radians(lon2 - lon1)

    # 하버사인 공식 (LaTeX 수식 참고)
    a = math.sin(dphi / 2)**2 + \
        math.cos(phi1) * math.cos(phi2) * \
        math.sin(dlambda / 2)**2

    # 부동소수점 오차 방지: a를 0~1 사이로 강제 고정
    a = max(0, min(1, a))


    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    return r * c



# 파이썬은 내부적으로 쿼리스트링으로 변환해줘서 get도 먹히긴 한다고 함. 하지만 실제 길이 제한이 있으므로 주의.
@app.post("/distance")
async def get_distance(data: DistanceRequest):

    distance = calculate_haversine(data.lat1, data.lon1, data.lat2, data.lon2)
    return {
        "origin": {"lat": data.lat1, "lon": data.lon1},
        "destination": {"lat": data.lat2, "lon": data.lon2},
        "distance_meter": round(distance, 2),
        "distance_km": round(distance / 1000, 2)
    }
aa
@app.post("/nearby")
async def get_nearby(data: SearchRequest):
    # 1. R-tree에서 반경 내 혹은 가장 가까운 k개 ID 추출 (매우 빠름)
    nearest_ids = list(idx.nearest((data.my_lat, data.my_lon), data.k))

    results = []
    for n_id in nearest_ids:
        node = spatial_nodes[n_id]
        logger.info("node")
        dist = calculate_haversine(data.my_lat, data.my_lon, node["lat"], node["lon"])
        results.append({
            "node_id": n_id,
            "distance_km": round(dist / 1000, 2),
            "lat": node["lat"],
            "lon": node["lon"]
        })
        logger.info("results:" + results.__str__())


    return {"my_location": {"lat": data.my_lat, "lon": data.my_lon}, "nearby_locations": results}


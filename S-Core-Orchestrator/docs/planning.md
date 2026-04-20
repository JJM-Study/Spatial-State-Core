src/main/java/com/dev/ssc
├── core (domain)            # [순수 로직] 외부 라이브러리에 의존하지 않는 핵심 비즈니스 규칙
│   ├── model                # 상태(State), 이벤트(Event) 정의
│   └── service              # 상태 전이 방정식 로직 (Pure Java)
├── application              # [유스케이스] 핵심 로직을 실행하기 위한 시나리오
│   ├── port                 # 외부와 소통하기 위한 인터페이스 (입구/출구)
│   │   ├── in               # 유스케이스 인터페이스
│   │   └── out              # 외부 엔진(FastAPI) 호출을 위한 인터페이스
│   └── usecase              # 실제 비즈니스 흐름 제어
└── infrastructure (adapter)  # [구현체] 프레임워크나 외부 엔진에 종속적인 코드
├── in                   # Web (Controller, REST API)
└── out                  # FastAPI Client (WebClient), Database, Redis


--->

src/main/java/com/dev/ssc
├── infrastructure (Adapter)
│   ├── in                  <-- [여기가 컨트롤러의 거처!]
│   │   └── web
│   │       └── NearbyController.java  (사용자의 REST 요청을 받음)
│   └── out
│       └── fastapi
│           └── FastApiAdapter.java    (실제 WebClient로 FastAPI 호출)
├── application (Brain)
│   ├── port
│   │   ├── in              <-- (입구) 서비스가 제공할 기능 명세서
│   │   └── out             <-- (출구) 서비스가 외부 엔진에 바라는 기능 명세서
│   └── usecase
│       └── SpatialOrchestrator.java   (실질적인 지휘를 맡는 서비스)
└── core (Domain)
└── model               <-- 순수 공간 데이터 모델 (SpatialNode 등)
# Hexagonal Architecture (육각형 설계)

---

![Hexagonal Architecture Diagram](https://github.com/user-attachments/assets/7bd599e5-e51c-465a-a627-bf7c1932a71c)

[![Dependency Graph](https://github.com/JJM-Study/jjm/blob/cb73d400b29155f539d6337cccfde7e1bbfb1f8b/assets/Spatial%20Engine/Graph.png)](https://github.com/JJM-Study/jjm/blob/cc1dc6b40edf0c5cad4ff3cfdfe828d55c67e8c8/assets/Spatial_Engine/Graph.png)

---

## 1. 정점 집합의 레이어 분할과 채색 규칙

서로소(독립된 기능을 갖는) 정점 집합의 분할 (단일 책임 원칙)

```
V_infrastructure = { NearbyController, FastAPIAdapter, LocalEngineAdapter }
V_core           = { SpatialEngineService }
V_engine         = { LocalSpatialEngine, FastAPIEngine }
V_interface      = { SpatialEngineUseCase, SpatialEnginePort }

V = V_infrastructure ∪ V_core ∪ V_interface ∪ V_engine
    (V_i ∩ V_j = ∅, i ≠ j)
```

레이어별 라벨링:

```
f : { V_infrastructure, V_core, V_interface, V_engine } → { Red, Blue, Green, Yellow }
```

### 허용 제약 (Allowed Dependencies)

- **Infrastructure → Interface**
  ```
  e = (u, v) ∈ E where u ∈ V_infra, v ∈ V_interface
  ```
- **Core → Interface**
  ```
  e = (u, v) ∈ E where u ∈ V_core, v ∈ V_interface
  ```

### 금지 제약 (Forbidden Dependencies)

- **Core → Infrastructure / Engine (직접 의존 금지)**
  ```
  e = (u, v) ∉ E where u ∈ V_core, v ∈ V_infrastructure
  ```

---

## 2. 도달 가능성을 통한 의존성 오염 방지 증명

순수 JVM 소스코드로만 작성되었는지, 즉 외부 프레임워크에 종속되었는지 여부를 기준으로 `V_ext`, `V_in`으로 분류한다.

```
{ FastAPI Spatial Engine, Local Spatial Engine }        ∈ V_engine
{ Controller, FastAPI Engine Adapter, Local Engine Adapter } ∈ V_infra
{ Service }                                              ∈ V_core
{ UseCase, Port }                                         ∈ V_interface

V_ext = V_engine ∪ V_infra
V_in  = V_core ∪ V_interface
```

성립. 이때 다음이 성립해야 한다:

```
∀ u ∈ V_in, ∀ v ∈ V_ext,  ¬(u ⇝ v)
```

### 도달 가능성 증명

| 노드 | 진출 간선 | 도달 가능 집합 R(u) |
|---|---|---|
| UseCase | 없음 | { UseCase } |
| Service | {Service, UseCase}, {Service, Port} | { Service, UseCase, Port } |
| Port | 없음 | { Port } |

따라서 모든 `V_in` 내부 노드에서 출발하는 도달 가능 집합의 합집합은:

```
⋃_{u ∈ V_in} R(u) = V_in
V_in ∩ V_ext = ∅
```

이므로

```
∀ u ∈ V_in, ∀ v ∈ V_ext, ¬(u ⇝ v)  성립 — 즉 오염 없음 증명.
```

### 유효성 검증 중 발견한 오류 사항

유효성 검증 중, `Service` 등 내부 로직 집합에서 다음과 같은 오염원이 존재함을 확인했다:

```java
import reactor.core.publisher.Mono;
@Service
```

내외부 판별 기준은 **JVM 내의 순수 소스코드로 실행되는가, 아닌가**이다. 이에 따라 좀 더 검토하고 코드 수정 사항으로 고려할 필요가 있다.

또한 그래프에서 `Service`의 간선을 다음과 같이 잘못 표기했음을 확인:

- (오류) `u = Port`: 진출 간선 {Port, Service} ⇒ R(Port) = {Port, Service}
- (수정) `u = Port`: 진출 간선 없음 ⇒ R(Port) = {Port}

추가로, **ArchUnit**이라는 단위 테스트 라이브러리를 활용하면 순환 참조 문제 등을 쉽게 검증할 수 있을 것으로 보이므로, 이에 대해서도 추가로 알아볼 예정이다.

---

## 3. 인접 행렬의 대수적 확증

그래프 `G = (V, E)`에서:

```
V_in    = { UseCase, Service, Port }
V_ext   = { FastAPI Adapter, Local Adapter, Local Spatial Engine, FastAPI Spatial Engine, Controller }

∀ u ∈ V_in, ∀ v ∈ V_ext
```

인덱스 부여:

- `V_in (1~3)`: 1. UseCase, 2. Service, 3. Port
- `V_ext (4~8)`: 4. FastAPI Adapter, 5. Local Adapter, 6. Local Spatial Engine, 7. FastAPI Spatial Engine, 8. Controller

전체 인접 행렬 구조:

```
A_ij = ( A_in,in && A_in,ext ) || ( A_ext,in && A_ext,ext )

A_in,in  = 내부 노드 간의 의존성
A_in,ext = 내부 → 외부로의 의존성
```

여기서 **A_in,ext = 영행렬(0)**임을 증명해야 한다.

```
i = 1 (UseCase) ⇒ A_1,4 = A_1,5 = A_1,6 = A_1,7 = A_1,8 = 0
i = 2 (Service) ⇒ A_2,4 = A_2,5 = A_2,6 = A_2,7 = A_2,8 = 0
i = 3 (Port)    ⇒ A_3,4 = A_3,5 = A_3,6 = A_3,7 = A_3,8 = 0
```

따라서

```
A_in,ext = 0 (3×5)
```

성립한다.

### 도달 가능성 행렬 M을 통한 검증

최대 의존 단계 = 1이므로 `A² = A³ = ... = 0`. 따라서 전체 도달 가능성은

```
R = I ∨ A
```

로 종결되며, 이미 확인한 `A_in,ext = 0 (3×5)`가 `A^n_in,ext` 전체에도 그대로 유지되므로 **오염 없음**으로 검증 완료.

### 행렬 A_in,ext (3×5)

| | Controller | FastAPI Adapter | FastAPI Spatial Engine | Local Adapter | Local Spatial Engine |
|---|---|---|---|---|---|
| UseCase | 0 | 0 | 0 | 0 | 0 |
| Service | 0 | 0 | 0 | 0 | 0 |
| Port    | 0 | 0 | 0 | 0 | 0 |

`A² = 0`, `A³ = 0`, ... → `M = M × A¹` (모든 인접 도달 가능성에 대한 계산) `= A_in,ext`

### 행렬 A_in,in (3×3) — (출발 → 도착)

| | UseCase | Service | Port |
|---|---|---|---|
| UseCase | 0 | 0 | 0 |
| Service | 1 | 0 | 1 |
| Port    | 0 | 0 | 0 |

실제 도달 간선: `(0 && 0 && 0) || (1 && 0 && 1) || (0 && 0 && 0)`

### 행렬 A_ext,in (5×3)

| | UseCase | Service | Port |
|---|---|---|---|
| Controller | 1 | 0 | 0 |
| FastAPI Adapter | 0 | 0 | 1 |
| Local Adapter | 0 | 0 | 1 |
| FastAPI Spatial Engine | 0 | 0 | 0 |
| Local Spatial Engine | 0 | 0 | 0 |

`(1 && 0 && 0) || (0 && 0 && 1) || (0 && 0 && 1) || (0 && 0 && 0) || (0 && 0 && 0)`

### 행렬 A_ext,ext (5×5)

| | Controller | FastAPI Adapter | Local Adapter | FastAPI Spatial Engine | Local Spatial Engine |
|---|---|---|---|---|---|
| Controller | 0 | 0 | 0 | 0 | 0 |
| FastAPI Adapter | 0 | 0 | 0 | 1 | 0 |
| Local Adapter | 0 | 0 | 0 | 0 | 1 |
| FastAPI Spatial Engine | 0 | 0 | 0 | 0 | 0 |
| Local Spatial Engine | 0 | 0 | 0 | 0 | 0 |

`(0&&0&&0&&0&&0) || (0&&0&&0&&1&&0) || (0&&0&&0&&0&&1) || (0&&0&&0&&0&&0) || (0&&0&&0&&0&&0)`

---

### 전체 인접 행렬 (8×8)

![Full Adjacency Matrix](https://github.com/JJM-Study/jjm/blob/af2f09b9e3efd08aa2cae01b6e93336c1422f618/assets/Spatial_Engine/A_8X8.png)

### 전체 도달 가능성 행렬 수렴

```
R = V_(k=0..∞) = I ∨ A ∨ A² ∨ A³ ∨ ... = I ∨ A     (A²부터 0_8x8)
```

**내부 → 외부 오염 검증:**

```
R_in,ext = I ∨ A_in,ext = 0 (3×5)
```

따라서 모든 경우의 수에서 `A_in,ext = 0`, 즉 **오염된 의존성 없음**을 확인한다.

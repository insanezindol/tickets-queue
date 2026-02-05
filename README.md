![header](https://capsule-render.vercel.app/api?type=wave&color=auto&height=300&section=header&text=tickets%20queue&fontSize=90)

# Tickets Queue System

대규모 트래픽 환경을 가정한 **티켓 대기열 및 발급 시스템** 예제 프로젝트입니다.  
Kotlin Coroutines와 Redis를 활용하여 고성능의 비동기 처리를 구현하였으며, 동시성 이슈를 해결하는 방법을 보여줍니다.

## 기술 스택 (Tech Stack)

- **Language**: Kotlin 1.9 (Java 21)
- **Framework**: Spring Boot 3.5 (Snapshot)
- **Database**: Redis (Docker)
- **Concurrency**: Kotlin Coroutines
- **Build Tool**: Gradle (Kotlin DSL)

## 주요 기능 (Features)

- **티켓 발급 API**: 사용자 ID를 기반으로 순차적인 티켓 번호를 발급합니다.
- **동시성 제어**: Redis의 `INCR` 명령어를 사용하여 원자적(Atomic)으로 카운트를 증가시켜, 경쟁 상태(Race Condition) 없이 고유한 번호를 보장합니다.
- **중복 방지**: 인메모리 캐시(`ConcurrentHashMap`)와 Redis를 조합하여 동일 사용자의 중복 요청을 효율적으로 처리합니다.
- **대기열 제한**: 최대 발급 가능한 티켓 수(예: 1,000,000개)를 제한하여 매진(Sold Out) 처리를 구현했습니다.

## 시작하기 (Getting Started)

### 1. 사전 요구사항 (Prerequisites)
- JDK 21 이상
- Docker & Docker Compose (Redis 실행용)

### 2. 인프라 실행 (Redis)
프로젝트 루트의 `infra` 폴더에 있는 `docker-compose.yml`을 사용하여 Redis 컨테이너를 실행합니다.

```bash
cd infra
docker-compose up -d
```
* Redis는 `6379` 포트로 실행되며, 비밀번호는 `redispassword`로 설정되어 있습니다.

### 3. 애플리케이션 실행
프로젝트 루트에서 다음 명령어로 애플리케이션을 실행합니다.

```bash
./gradlew bootRun
```

## API 사용법 (API Usage)

### 티켓 발급 요청
특정 사용자(`userId`)에게 티켓을 발급합니다.

- **URL**: `GET /ticket`
- **Query Param**: `userId` (String)

**요청 예시 (cURL):**
```bash
curl "http://localhost:8080/ticket?userId=user123"
```

**응답 예시 (JSON):**
```json
{
  "uuid": "550e8400-e29b-41d4-a716-446655440000",
  "ticketNumber": 1
}
```
* `uuid`: 발급된 티켓의 고유 식별자
* `ticketNumber`: 발급된 순서 번호

## 프로젝트 구조

```
tickets-queue/
├── src/main/kotlin/com/example/ticketsqueue/
│   ├── controller/    # 웹 요청 처리 (TicketController)
│   ├── service/       # 비즈니스 로직 (TicketService - Redis 연동)
│   ├── dto/           # 데이터 전송 객체
│   └── exception/     # 예외 처리
├── infra/
│   └── docker-compose.yml  # Redis 설정
└── build.gradle.kts   # 프로젝트 의존성 관리
```

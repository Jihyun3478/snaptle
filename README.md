# Snaptle

여행 중 찍은 영수증 사진을 AI가 자동으로 인식해 지출을 기록하고, 여행이 끝나면 그룹 정산을 자동으로 계산해주는 서비스.

## 기술 스택

- Java 21, Spring Boot 4, Spring Data JPA, Spring Security (OAuth2 Client + JWT)
- PostgreSQL (Docker Compose)
- REST API + JWT 기반 stateless 인증 (프론트엔드 비종속: 웹/네이티브 앱 공용 API)

## 로컬 실행

1. 환경 변수 설정

   ```bash
   cp .env.example .env
   # .env를 열어 필요한 값(카카오 키, Gemini 키 등)을 채운다
   ```

2. PostgreSQL 기동

   ```bash
   docker compose up -d
   ```

3. 애플리케이션 실행 (`.env`의 값을 셸에 로드하거나 IDE 환경변수로 설정)

   ```bash
   export $(cat .env | xargs)
   ./gradlew bootRun
   ```

카카오 client-id/secret을 비워두면 부팅 시 placeholder 값이 채워져 서버는 정상 기동하지만, 카카오 로그인 자체는 동작하지 않는다.

## 인증 API

| Method | Path | 설명 |
| --- | --- | --- |
| POST | `/api/auth/signup` | 이메일 회원가입, JWT 발급 |
| POST | `/api/auth/login` | 이메일 로그인, JWT 발급 |
| GET | `/oauth2/authorization/kakao` | 카카오 로그인 시작 (브라우저 리다이렉트) |

카카오 로그인은 성공 시 `FRONTEND_OAUTH_REDIRECT_URI?accessToken=...`로 리다이렉트된다.

이후 모든 API 요청은 `Authorization: Bearer {accessToken}` 헤더로 인증한다.

## 진행 상태

- [x] 프로젝트 골격 + PostgreSQL + 이메일/카카오 로그인 + JWT
- [ ] 그룹(여행) 생성 및 멤버 초대
- [ ] 영수증 OCR 기반 지출 등록 + 다중 통화 환산
- [ ] 지출 참여자/비균등 분할, 개인 간 채무
- [ ] 여행 종료 자동 정산(최소 송금 알고리즘) + 지출 조회

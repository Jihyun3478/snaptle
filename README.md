# Snaptle

여행 중 찍은 영수증 사진을 AI가 자동으로 인식해 지출을 기록하고, 여행이 끝나면 그룹 정산을 자동으로 계산해주는 서비스.

이 저장소는 모노레포로 구성되어 있다.

```
snaptle/
├── backend/   # Spring Boot REST API (Java 21, PostgreSQL)
└── frontend/  # 웹 프론트엔드
```

## 백엔드

Spring Boot 4 + PostgreSQL 기반 REST API. 실행 방법과 API 목록은 [`backend/README.md`](backend/README.md) 참고.

## 프론트엔드

[`frontend/README.md`](frontend/README.md) 참고.

## 로컬에서 전체 실행

1. 백엔드: [`backend/README.md`](backend/README.md)의 안내대로 PostgreSQL + Spring Boot를 기동한다 (기본 `http://localhost:8080`).
2. 프론트엔드: [`frontend/README.md`](frontend/README.md)의 안내대로 개발 서버를 기동하고, API 베이스 URL을 백엔드 주소로 설정한다.

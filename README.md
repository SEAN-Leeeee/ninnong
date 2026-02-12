# 🏀 NinNong (즐농)

> 농구 동호인을 위한 팀 관리 플랫폼  
> **도메인 중심 설계(DDD)**와 **RESTful API**로 구현한 동호회 통합 관리 서비스
**개발 기간:** 2025.09 ~ 진행중


## 📌 프로젝트 소개

### 💡 왜 만들었나요?

농구 동호회를 운영하다 보면 팀원 관리, 일정 조율, 영상 공유 등을  
카카오톡, 구글 시트, 유튜브 등 **여러 툴에 분산**해서 관리해야 하는 불편함이 있습니다.

**"농구 동호회 운영에 필요한 모든 것을 한 곳에서"** 라는 목표로,  
팀 관리부터 일정, 영상까지 통합 관리할 수 있는 플랫폼을 개발했습니다.

---

## 🎯 주요 기능

### 1. 팀 관리
- 동호회 팀 생성 및 팀원 초대/관리
- 팀원 역할 구분 (팀장/팀원)
- Spring Security 기반 인증/인가 처리

### 2. 일정 관리
- 경기/훈련 일정 등록 및 팀원 공유
- 팀원 참석 여부 확인

### 3. API 문서화
- Swagger 기반 REST API 문서 자동화
- 프론트엔드 연동을 위한 JSON 응답 구조 표준화

---

## 🛠 기술 스택

### Backend
- **Java 17** - 백엔드 로직 구현
- **Spring Boot 3.5.0** - REST API 서버 구축
- **Spring Security** - 인증/인가 처리
- **Spring Data JPA** - ORM 기반 데이터 접근
- **Gradle** - 빌드 도구

### Frontend
- **Vue.js 2** - 웹 UI 구현
- **JavaScript (ES6+)**

### Infra
- **MySQL** - 관계형 데이터베이스
- **Docker / Docker Compose** - 로컬/프로덕션 환경 분리 운영
- **AWS EC2, RDS** - 클라우드 서버 및 DB 운영
- **Nginx** - 웹서버 및 리버스 프록시

### ETC
- **Swagger (SpringDoc)** - API 문서 자동화
- **Git** - 버전 관리

---

## 🏗 시스템 구성도

![시스템 구성도](시스템_구성도_이미지_링크_교체)
```
[Vue.js Frontend]
       ↓ HTTP / REST API
[Spring Boot Backend]
   ├── Spring Security (인증/인가)
   ├── JPA (데이터 접근)
   └── Swagger (API 문서)
       ↓
[MySQL DB (AWS RDS)]

[Docker Compose]로 로컬 / 프로덕션 환경 분리 운영
```

---

## 📁 프로젝트 구조
```
ninnong/
├── src/
│   └── main/java/
│       ├── domain/        # 핵심 도메인 (Team, User, TeamMember)
│       ├── controller/    # REST API 컨트롤러
│       ├── service/       # 비즈니스 로직
│       └── repository/    # 데이터 접근 계층 (JPA)
├── frontend/              # Vue.js 프론트엔드
├── docker/
├── Dockerfile
├── docker-compose.local.yml   # 로컬 개발 환경
└── docker-compose.prod.yml    # 프로덕션 배포 환경
```

---

## 🚀 기술적 도전과 해결

### 1️⃣ 도메인 중심 설계 (DDD)
**목표:** 유지보수성과 확장성이 높은 구조 설계  
**접근:**
- Team, User, TeamMember 도메인을 명확히 분리
- 각 도메인의 책임을 명확히 구분하여 의존성 최소화
- 서비스 계층에서 비즈니스 로직을 집중 관리

**결과:** 기능 추가 시 영향 범위 최소화, 테스트 용이한 구조 달성

### 2️⃣ Docker 기반 환경 분리
**목표:** 로컬 개발 환경과 프로덕션 환경의 일관성 확보  
**접근:**
- `docker-compose.local.yml` - 로컬 개발용 설정
- `docker-compose.prod.yml` - AWS 배포용 설정
- Dockerfile로 Spring Boot 애플리케이션 컨테이너화

**결과:** 환경 차이로 인한 배포 오류 제거, 일관된 실행 환경 보장

### 3️⃣ Spring Security 기반 인증/인가
**목표:** 팀 관리 기능의 보안 처리  
**접근:**
- 팀원/팀장 역할 기반 접근 제어 구현
- RESTful API에 맞는 Stateless 인증 구조 설계

**결과:** 팀별 데이터 격리 및 권한 외 접근 차단

---

## 🌱 개발 배경 및 학습 목표

이 프로젝트는 단순한 기능 구현을 넘어, **실무 수준의 설계 역량**을 기르기 위해 시작했습니다.

1. **객체지향 설계(OOP)** - 책임 분리와 의존성 역전 원칙 적용
2. **실무 수준의 REST API 설계** - RESTful 원칙을 지킨 API 구조 구현
3. **테스트하기 좋은 백엔드 설계** - 도메인 중심의 계층 분리
4. **Docker 기반 인프라 경험** - 실제 배포 환경과 동일한 로컬 환경 구성

---

## 📊 현재 진행 상황

### 완료
- ✅ 팀/팀원 관리 API 구현
- ✅ Spring Security 인증/인가 처리
- ✅ Docker Compose 로컬/프로덕션 환경 구성
- ✅ AWS EC2, RDS 배포
- ✅ Swagger API 문서화
- ✅ Nginx 웹서버 설정

### 진행중 / 예정
- [ ] 일정 관리 기능 고도화
- [ ] 영상 관리 기능 구현
- [ ] NincoreBoard와 API 연동 (선수 기록 자동 연동)
- [ ] 실사용자 확보 및 피드백 반영

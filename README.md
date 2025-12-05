# sb02-mpl_team2

![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white) ![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)

# **2팀**

[🪧팀 회의 노션 페이지 바로가기](https://steadfast-fact-5a0.notion.site/2-23e57ba831b3809389a8ff6115d40007?source=copy_link)   
[🪧깃허브 레포지토리 바로가기](https://github.com/sb02-MPL-team2/sb02-mpl-team2)<br>
[🪧발표자료 PPT 바로가기](https://drive.google.com/file/d/1SmqhB5gbdGAv4SKdXSI9ATNeWfo7HF8J/view?usp=sharing)

## **팀원 구성**

[✅이종원](https://github.com/BrotherMountain)<br>
[✅이영인](https://github.com/ddday366)<br>
[✅정종현](https://github.com/JJHyunDev)<br>
[✅최성현](https://github.com/hakSick)<br>
[✅한상엽](https://github.com/sangyeobhan)

---

## **프로젝트 소개**

- 다양한 외부 콘텐츠(TMDB, SportDB)를 플레이리스트에 담으면서 실시간 소셜 기능(공유, 채팅, DM, 팔로우)을 제공하는 모두의 플리
- 소셜 로그인 기능과 어드민 기능으로 유저 잠금, 유저 역할 변경 기능 포함
- 프로젝트 기간: 2025.7.28 ~ 2025.08.30

---

## **기술 스택**

- Backend: Spring Boot, Spring Security, Spring Data JPA
- Database: PostgreSQL
- 공통 Tool: Git & Github, Jira, Discord, Notion

---

## 팀원별 구현 기능 상세

### **이종원**

- **플레이리스트 관리 API**
    - 플레이리스트의 CRUD
    - 인기있는 플레이리스트 추천 시스템 구현

- **리뷰 관리 API**
    - 리뷰 CRUD

- **알람 관리 API**
    - 팔로우한 유저가 플레이리스트를 생성, 플레이리스트를 구독, 권한 변경, DM 전송 등의 이벤트가 발생할 경우 SSE로 알람을 전송

### **이영인**

- **콘텐츠 관리 API**
    - 배치 작업을 통해 정기적으로 API 호출
    - 콘텐츠 CRUD

### **정종현**
- **유저 관리 API**
    - 유저 CRUD
    - Oauth2를 이용한 구글, 카카오 로그인

- **어드민 API**
    - 유저 권한 변경 및 정지 삭제 로직 구현

- **보안 관련**
    - Spring Security를 통한 보안처리

### **최성현**
- **소셜 관리 API**
    - 팔로우 기능, DM 기능 CRUD 담당

### **한상엽**
- **프론트 엔드 관리**
    - A.I를 활용한 프론트엔드 구현
- **실시간 채팅방 API**
    - 소켓을 이용한 실시간 채팅 기능 구현

---

## **파일 구조**

<details>
<summary>📁 프로젝트 파일 구조</summary>
<div markdown="1">

```
.
├─main
│  ├─java
│  │  └─com
│  │    └─codeit
│  │      └─sb02mplteam2
│  │        ├─config       # Spring 및 각종 라이브러리 설정 (Security, DB, AWS 등)
│  │        ├─domain       # 비즈니스 로직 (도메인별 패키지)
│  │        │  ├─admin
│  │        │  ├─auth
│  │        │  ├─binaryContent
│  │        │  ├─content
│  │        │  ├─livewatch
│  │        │  ├─mail
│  │        │  ├─notification
│  │        │  ├─playlist
│  │        │  ├─recommendation
│  │        │  ├─review
│  │        │  ├─setting
│  │        │  ├─social
│  │        │  ├─subscribe
│  │        │  ├─task
│  │        │  └─user
│  │        ├─event        # 비동기 처리를 위한 이벤트 관련 클래스
│  │        ├─exception    # 전역 예외 처리 및 커스텀 예외 클래스
│  │        ├─security     # Spring Security 및 JWT 관련 설정
│  │        ├─swagger      # API 문서(Swagger/OpenAPI) 관련 설정
│  │        ├─util         # 공통 유틸리티 클래스
│  │        └─Sb02MplTeam2Application.java
│  └─resources
│    ├─static       # 정적 리소스 (JS, CSS, 이미지 등)
│    ├─application.yaml
│    ├─application-dev.yaml
│    ├─application-prod.yaml
│    ├─logback-spring.xml # 로깅 설정
│    └─schema.sql       # DB 스키마

```
</div>
</details>

---

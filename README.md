<h1 align="center"><img width="50" height="50" alt="image" src="https://github.com/user-attachments/assets/ae577f70-7ab2-45a4-8432-a4e1bc473a2f" /> MockStock </h1>
<p align="center"> 모의 주식 투자 사이트 </p>

---

## 📖 목차
- [프로젝트 소개](#-프로젝트-소개)
- [기술 스택](#-기술-스택)
- [프로젝트 아키텍쳐](#-프로젝트-아키텍쳐)
- [프로젝트 구조](#-프로젝트-구조)
- [주요 기능](#-주요-기능)
- [스크린샷](#-스크린샷)
- [팀원](#-팀원)
  
---

## 📝 프로젝트 소개
MockStock은 주식 투자 경험을 안전하게 쌓을 수 있도록 돕는 모의 주식 투자 플랫폼입니다. <br>
실제 주식 시장 데이터를 기반으로 가상의 자금을 이용해 주식 거래를 할 수 있으며, 투자 전략을 시뮬레이션하여 금융 감각을 키울 수 있습니다. <br>

### 개발 기간
25.06.30 ~ 25.08.08

### 개발 의도
- 안전한 학습 환경 : 실제 돈을 잃을 위험 없이 투자 경험 제공
- 현실적인 시뮬레이션: 실시간 또는 지연된 실제 주식 데이터 활용
- 지속적인 학습 : 투자 내역, 랭킹, 포트폴리오 분석을 통한 성장 유도
- 소셜 투자 경험: 이용자간 랭킹 시스템으로 재미있는 투자 학습

### 타겟 사용자
- 주식 투자에 관심은 있지만 리스크 때문에 망설이는 초보 투자자
- 새로운 투자 전략을 검증하고 싶은 기존 투자자
- 금융 교육이 필요한 학생 및 일반인

### 기대 효과

- 주식 투자에 대한 진입 장벽 낮추기
- 실전과 유사한 투자 경험 제공
- 수익률 기반 경쟁 요소로 재미 유발
  
---

## ⚒ 기술 스택
<p align="center">
  <img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=openjdk&logoColor=white">
  <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">
  <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white">
  <img src="https://img.shields.io/badge/OAuth2-4285F4?style=for-the-badge&logo=oauth2&logoColor=white"> <br>
  <img src="https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white">
  <img src="https://img.shields.io/badge/InfluxDB-22ADF6?style=for-the-badge&logo=influxdb&logoColor=white">
  <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">
  <img src="https://img.shields.io/badge/WebSocket-010101?style=for-the-badge&logo=socketdotio&logoColor=white"> <br>
  <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black">
  <img src="https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white">
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">
  <img src="https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white">
  <img src="https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white">
  <img src="https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white">
</p>

---

## 🛠 프로젝트 아키텍쳐

### 시스템 아키텍쳐
<img width="1340" height="693" alt="image" src="https://github.com/user-attachments/assets/3ddfbc71-7502-4de4-8c75-bf2b5572af31" />

### ERD
<img width="1203" height="776" alt="image" src="https://github.com/user-attachments/assets/843aba90-7462-40f4-a894-ddd91fd6619c" />

### API 명세서
<img width="807" height="847" alt="image" src="https://github.com/user-attachments/assets/899bd392-3a8f-446c-acb7-119e5808faf1" />
<img width="798" height="802" alt="image" src="https://github.com/user-attachments/assets/f68272b6-48b1-4f34-b3d5-33e2f1d8c29e" />
<img width="803" height="599" alt="image" src="https://github.com/user-attachments/assets/0c761b1b-94e2-4e1c-bad1-74fb163f834c" />
<img width="815" height="331" alt="image" src="https://github.com/user-attachments/assets/917a44aa-8b68-4b39-922d-883c30fed012" />

---
## 📂 프로젝트 구조
📦 MockStock <br>
 ┣ 📂 domain <br>
 ┃ ┣ 📂 auth       (권한) <br>
 ┃ ┣ 📂 favorites  (관심종목) <br>
 ┃ ┣ 📂 mails      (메일) <br>
 ┃ ┣ 📂 members    (회원) <br>
 ┃ ┣ 📂 notifications  (알림) <br>
 ┃ ┣ 📂 orders         (주문) <br>
 ┃ ┣ 📂 payments     (가상 머니 충전) <br>
 ┃ ┣ 📂 portfolios     (포트폴리오) <br>
 ┃ ┣ 📂 ranks     (랭킹) <br>
 ┃ ┣ 📂 stock     (주식 시세 정보) <br>
 ┃ ┗ 📂 trades   (체결된 거래) <br>
 ┣ 📂 global     (공통 설정, 예외 처리) <br>
 ┣ 📂 resources        <br>
 ┣ Dockerfile <br>
 ┗ README.md

---

## ✨ 주요 기능
- ✅ 시장가 기반 주식 매도 / 매수
- ✅ 지정가 기반 주식 매도 / 매수
- ✅ 주식별 분봉/일봉/주봉 조회
- ✅ 보유중인 주식의 포트폴리오 조회
- ✅ 파산 신청 기능 (보유중인 주식 및 자산 초기화)
- ✅ 수익률, 자산, 거래량, 파산횟수 랭킹 조회

---
## 📸 스크린샷

### 대시보드
<img width="1306" height="909" alt="image" src="https://github.com/user-attachments/assets/d4d1154e-6d00-4f83-9071-9b5f5c3db894" />

### 주식거래
<img width="1299" height="902" alt="image" src="https://github.com/user-attachments/assets/ba44d6fa-df42-4180-bce8-215d47a778e4" />

### 관심종목
<img width="1302" height="928" alt="image" src="https://github.com/user-attachments/assets/864b8c70-fed8-4e1f-9796-0334ca2a72fa" />

### 분봉차트
<img width="1232" height="946" alt="image" src="https://github.com/user-attachments/assets/34985af2-66d7-4105-b0a3-7915cff56479" />

### 일봉차트
<img width="1259" height="950" alt="image" src="https://github.com/user-attachments/assets/4c170450-7572-4cfe-8c69-99302a6430fb" />

### 주봉차트
<img width="1244" height="942" alt="image" src="https://github.com/user-attachments/assets/e72e3c8f-26a4-4457-ac8c-e8cceaa873e3" />

#### 거래화면
<img width="1252" height="909" alt="image" src="https://github.com/user-attachments/assets/b4f2ad1d-306f-4fcd-bdd6-2a14534cbbf0" />
<img width="1248" height="839" alt="image" src="https://github.com/user-attachments/assets/922804df-2136-43fe-8c00-b72a283f299e" />
<img width="1233" height="864" alt="image" src="https://github.com/user-attachments/assets/5dee06c2-a492-4824-abe5-5855fdca01ef" />

### 랭킹
<img width="1305" height="895" alt="image" src="https://github.com/user-attachments/assets/0793c397-3d2b-4ee1-976d-583b0a59617f" />

### 포트폴리오
<img width="1296" height="865" alt="image" src="https://github.com/user-attachments/assets/6968f3ed-1428-4d12-898f-e18bbc6fd9d4" />

### 거래내역
<img width="1291" height="919" alt="image" src="https://github.com/user-attachments/assets/9c212721-c8dd-4702-825e-db34538a3373" />

### 알림
<img width="1284" height="892" alt="image" src="https://github.com/user-attachments/assets/2daa797e-a553-4ff1-a7f1-95d677857da7" />

### 설정
<img width="1263" height="861" alt="image" src="https://github.com/user-attachments/assets/03dfb076-e7f4-4b99-8397-f17d1569b2e1" />
<img width="1026" height="917" alt="image" src="https://github.com/user-attachments/assets/7fd83af4-fa41-4d06-96a5-d18616d517d1" />

### 파산 신청
<img width="1005" height="499" alt="image" src="https://github.com/user-attachments/assets/f491a2b9-090d-4803-aa2e-3908fb7de2de" />

### 가상 머니 충전
<img width="1299" height="890" alt="image" src="https://github.com/user-attachments/assets/8b3e4512-5e0f-4616-9b4a-b6c86c7b3cca" />
<img width="1592" height="920" alt="image" src="https://github.com/user-attachments/assets/969fb761-f2e7-46ea-af1b-b95917c7acdc" />
<img width="1308" height="891" alt="image" src="https://github.com/user-attachments/assets/b2d4ab82-aef2-44b9-9671-712a68781960" />

---

## 👨‍💻 팀원
| 이름  | 역할        | 담당 기능       | GitHub                            |
| --- | --------- | ----------- | --------------------------------- |
| 고영민 | Backend | 회원가입, 로그인, 알림 | [GitHub](https://github.com/dbogym) |
| 김석완 | Backend | 서버 배포 및 모니터링, 카카오페이 충전, 랭킹, 관심종목| [GitHub](https://github.com/ksw733)  |
| 서희승 | Backend | 웹소켓, 주식 API 조회, 주식 데이터 저장 | [GitHub](https://github.com/hs986)  |
| 조우현 | Backend | 주식 매도/매수(시장가, 지정가), 포트폴리오, 메일함, 마이페이지| [GitHub](https://github.com/jwh946) |
| 정장오 | Frontend | 프론트엔드 전체 개발 | [GitHub](https://github.com/joyk231220) |


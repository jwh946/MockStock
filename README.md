<h1 align="center"><img width="50" height="50" alt="image" src="https://github.com/user-attachments/assets/ae577f70-7ab2-45a4-8432-a4e1bc473a2f" /> MockStock </h1>
<p align="center"> 모의 주식 투자 사이트 </p>

---

## ⚒ Tech Stack
<p align="center">
  <img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=openjdk&logoColor=white">
  <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">
  <img src="https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white">
  <img src="https://img.shields.io/badge/InfluxDB-22ADF6?style=for-the-badge&logo=influxdb&logoColor=white">
  <img src="https://img.shields.io/badge/WebSocket-010101?style=for-the-badge&logo=socketdotio&logoColor=white"> <br>
  <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black">
  <img src="https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white">
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">
  <img src="https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white">
  <img src="https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white">
  <img src="https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white">
</p>

---

## 📖 목차
- [프로젝트 소개](#-프로젝트-소개)
- [프로그램 아키텍쳐](#-프로그램-아키텍쳐)
- [프로젝트 구조](#-프로젝트-구조)
- [주요 기능](#-주요-기능)
- [스크린샷](#-스크린샷)
- [팀원](#-팀원)

---

## 📝 프로젝트 소개
MockStock은 주식 투자 경험을 안전하게 쌓을 수 있도록 돕는 모의 주식 투자 플랫폼입니다. <br>실제 주식 시장 데이터를 기반으로 가상의 자금을 이용해 주식 매수/매도를 체험할 수 있으며, 투자 전략을 시뮬레이션하며 금융 감각을 키울 수 있습니다. <br>

### 개발 기간
25.06.30 ~ 25.08.08

### 주요 기능
- 시장가 기반 주식 매도 / 매수
- 지정가 기반 주식 매도 / 매수
- 주식별 분봉/일봉/주봉/월봉 조회
- 보유중인 주식의 포트폴리오 조회
- 파산 신청 기능 (보유중인 주식 및 자산 초기화)
- 수익률, 자산, 거래량, 파산횟수 랭킹 조회

---

## 🛠️ 프로그램 아키텍쳐
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
- [x] 기능 1
- [x] 기능 2
- [x] 기능 3

---
## 📸 스크린샷

---

## 👨‍💻 팀원
| 이름  | 역할        | 담당 기능       | GitHub                            |
| --- | --------- | ----------- | --------------------------------- |
| 고영민 | Backend | 회원가입, 로그인, 알림 | [GitHub](https://github.com/dbogym) |
| 김석완 | Backend | 서버 배포 및 모니터링, 카카오페이 충전, 랭킹, 관심종목| [GitHub](https://github.com/ksw733)  |
| 서희승 | Backend | 웹소켓, 주식 API 조회, 주식 데이터 저장 | [GitHub](https://github.com/hs986)  |
| 조우현 | Backend | 주식 매도/매수(시장가, 지정가), 포트폴리오, 메일함, 마이페이지| [GitHub](https://github.com/jwh946) |
| 정장오 | Frontend | 프론트엔드 전체 개발 | [GitHub](https://github.com/joyk231220) |


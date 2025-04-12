2024. 7 ~ 9.14 , 2025.1 ~ .3.8

back end : Spring 
<br>
front end : react 


## Technologies
1. **Back End**
+ Spring Boot 3.4.1
+  Spring Cloud 2024.0.0
+  Spring Security 6
+  MySql 5.7 이상
+  MongoDB
+  Redis
+  kafka
+  Amazon S3

2. **Front End**
+ React 18
+ React Query v5
+ React Redux
+ Material UI
+ TailWind CSS 5
+ Styled Components
+ Webpack 5
+ Paypal react button 
## Architecture
![제목 없는 다이어그램3 (1)](https://github.com/user-attachments/assets/ccff32bb-ba78-4c75-90d9-42a1fffa3b40)
   
## Description
1. User service : 회원 가입, 로그인, 토큰 발급, 유저 정보 관리 등 서비스 제공
   + 회원 가입시 chat service, order service, coupon service에 유저 생성 메시지를 전송합니다.
     + chat service :  메시지 정보를 토대로 ChatMember를 생성하여 테이블에 저장합니다.
     + order service : 메시지 정보를 토대로 Customer를 생성하여 테이블에 저장합니다.
     + coupon service : 메시지 정보를 토대로 coupon를 생성하여 테이블에 저장합니다.
       
2. Coupon service : 쿠폰 발급, 사용 등 서비스 제공
   + 쿠폰 적용 시 cart service에 쿠폰 사용 메시지를 전송합니다.
     + cart service : 메시지 정보를 토대로 쿠폰 적용 후 가격을 테이블에 저장합니다.
   + order service로부터 coupon 되돌리기 메시지를 받으면 쿠폰 상태를 NOT_USED로 변경합니다.

3. Catalog service : 상품 정보, 검색 등 서비스 제공
   + 주문 성공시 order service로부터 상품 업데이트 메시지를 받아 제품의 판매량과 재고 값을 업데이트하여 테이블에 저장합니다.   

4. Cart service : 카트 정보 관리 등을 서비스 제공
   + order service로부터 카트 비우기 메시지를 받으면 카트 정보를 삭제합니다.
   + order service로부터 카트 되돌리기 메시지를 받으면 카트에 담긴 상품 수량을 1로 리셋하고 카트 가격도 수정합니다.

5. Order service : 주문, 주문 정보 관리 등 서비스 제공
   + paypal로 지불이 성공하면 pay service에 지불 완료 메시지를 보냅니다.(outbox pattern)
   + paypal로 지불이 취소되면 pay service에 지불 취소 메시지를 보냅니다.(outbox pattern)
   + pay service로부터 지불 응답 메시지를 받아서 saga pattern에 따라 처리합니다.
   + pay service로부터 지불이 완료됐다는 메시지를 받은 경우 cart service에는 카트 비우기 메시지를 보내고<br>
     catalog service에는 상품 업데이트 메시지를 보냅니다.
   + pay service로부터 지불이 취소됐다는 메시지를 받은 경우 cart service에는 카트 되돌리기 메시지를 보내고<br>
     coupon service에는 쿠폰 되돌리기 메시지를 보냅니다.
  
6. Pay service : 지불 정보 관리 등 서비스 제공
   + order service로부터 받은 메시지 정보를 토대로 지불 정보를 테이블에 저장합니다.
   + order service에 지불 응답 메시지를 보냅니다.(outbox pattern)

## Features
### Back end 
1. 액세스 토큰은 블랙리스트를 도입하고 , 리프레시 토큰은 httpOnly 쿠키에 저장하고 Refresh Token Rotation 으로 보안을 강화하였습니다.
2. 회원 가입은 이메일 인증 방식입니다.
3. 로그인 후에 모든 요청은 Api Gateway에서 토큰 검증 필터를 통해 인증/인가 합니다.
4. 메시지 브로커는 Kafka를 사용하였고 서비스간의 데이터 조회가 필요한 경우는 OpenFeign 기능을 도입하였습니다.
5. Order Service 와 Payment Service에는  Transaction OutBox Pattern을 사용했습니다.
6. Order Service에는 Saga Pattern을 사용했습니다.
7. Catalog Service에서 상품 조회 시 Redis Cache를 적용했습니다.
8. Chat Service에서 채팅 메시지을 로드할 때 Cursor based paigination로 구현하였습니다.
9. Chat Service에서 채팅 메시지는 Mongo Sink Connector를 통해 MongoDB에 저장합니다.
       
   
### Front end
1. https://github.com/no2ehi/full-amazon-clone 을 포크해서 NextJS에서 React로 변경하면서  React Query로 Data Fetch, Chat Service를 위한 UI를 구현하였습니다.
2. 액세스 토큰은 메모리에 저장하고 리프레시 토큰은 httpOnly 쿠키로 저장하여 보안을 강화하였습니다. 
3. e-commerce 에 chat 기능울 추가하였습니다.(현재는 방만들기, 방 이름 더블클릭으로 채팅 방 전환, 채팅, 무한 스크롤 기능만 구현)
4. 회원 가입은 이메일 인증 방식을 구현하였습니다
   
## ERD 
![ecommerce db erd](https://github.com/user-attachments/assets/937fb3af-ca1c-4b1a-89cf-9a43f3572aff)

   


## Screenshot
![screenshot1](https://github.com/user-attachments/assets/1cc9e6c1-256c-4094-81ed-7501338f0130)
![screenshot2](https://github.com/user-attachments/assets/d22e32fa-d14b-4cf2-8b38-4507b1b166a0)
![screenshot3](https://github.com/user-attachments/assets/8125800c-cef4-47a6-a069-2402e0aa9dc4)
![screetshot4](https://github.com/user-attachments/assets/de367460-482d-42ab-b28e-8aea3e96809d)
![screetshot5](https://github.com/user-attachments/assets/3bf8ca27-3762-4bd4-8e11-b253604b4589)
![screetshot6](https://github.com/user-attachments/assets/0bb20277-9c0f-49f8-8a2f-dfae6aab3a35)
![screetshot7](https://github.com/user-attachments/assets/13ce0c79-fb7f-4117-9b30-37657134e6cb)



## Installation 

### Back End Setup
**1. java 17 install**
<br>
**2.** **application.properties*
```
  spring.datasource.url=jdbc:mysql://localhost:3306
  spring.datasource.username=
  spring.datasource.password=

  spring.mail.host=smtp.gmail.com
  spring.mail.port=
  spring.mail.username=
  spring.mail.password= 
```

### Front End Setup  
**1. .env.development**
```  
  REACT_APP_API_URL=http://localhost:8080
  REACT_APP_CLOUDINARY_NAME=
  REACT_APP_CLOUDINARY_KEY=
  REACT_APP_CLOUDINARY_SECRET=
  NODE_ENV = "development"
  PUBLIC_URL=http://localhost:3000
  REACT_APP_PAYPAL_CLIENT_ID=
  REACT_APP_PAYPAL_CLIENT_SECRET=  
```
**2. terminal command**
 ```
 - cd frontend
 - npm i 
 - npm start
```



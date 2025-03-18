2024. 7 ~ 9.14 , 2025.1 ~ .3.8

back end : Spring 
<br>
front end : react 


## Technologies
- **Back End**
1. Spring Boot 3.4.1
2. Spring Cloud 2024.0.0
3. Spring Security 6
4. MySql 5.7 이상
5. MongoDB
6. Redis  
7. kafka
8. Amazon S3

- **Front End**
1. React 18
2. React Query v5
3. React Redux
4. Material UI
5. TailWind CSS 5
6. Styled Components
7. Webpack 5
## Architecture


   
## Description




   


## Features
### Back end 
1. 액세스 토큰은 블랙리스트를 도입하고 , 리프레시 토큰은 httpOnly 쿠키에 저장하고 Refresh Token Rotation 으로 보안을 강화하였습니다.
2. 회원 가입은 이메일 인증 방식입니다.
3. 로그인 후에 모든 요청은 Api Gateway에서 토큰 검증 필터를 통해 인증/인가 합니다.
4. 서비스 간의 통신은 OpenFeign 과 Kafka를 적용하였습니다.
5. Order Service 와 Payment Service에는  Transaction OutBox Pattern을 사용했습니다.
6. Order Service에는 Saga Pattern을 사용했습니다.
7. Catalog Service에서 상품 조회 시 Redis Cache를 적용했습니다.
8. Chat Service에서 채팅 메시지을 로드할 때 Cursor based paigination로 구현하였습니다.
9. Chat Service에서 채팅 메시지는 Kafka + Mongo Sink Connector를 통해 MongoDB에 저장합니다.
       
   
### Front end
1. https://github.com/no2ehi/full-amazon-clone 을 포크해서 NextJS에서 React로 변경하면서  React Query로 Data Fetch, Chat Service를 위한 UI를 구현하였습니다.
2. 액세스 토큰은 메모리에 저장하고 리프레시 토큰은 httpOnly 쿠키로 저장하여 보안을 강화하였습니다. 
3. e-commerce 에 chat 기능울 추가하였습니다.(현재는 방만들기, 방 이름 더블클릭으로 채팅 방 전환, 채팅, 무한 스크롤 기능만 구현)
4. 회원 가입은 이메일 인증 방식으로 변경하였습니다.   
   
   


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



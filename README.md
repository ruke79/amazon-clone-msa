https://github.com/no2ehi/full-amazon-clone fork repository

This is my first contribution in the github. 

2024. 7 ~ 9.14

back end : Spring 
<br>
front end : react 


## Technologies
- **Back End**
1. Spring Boot
2. Spring Security 6
3. MySql 8.4
4. Java 17

- **Front End**
1. React 18
2. React Query
3. React Redux
4. TailWind CSS
5. Cloudinary

## Features
### Back end 
1. Access token(localStorage), Refresh Token(Httponly Cookie),  Refresh token rotation
2. Email Verfication Registration
3. OAuth2 login ready
   
### Front end
1. https://github.com/no2ehi/full-amazon-clone features
2. load cart
3. view orders(my profile)
4. load products (util/loadFake.js)
5. paypal payment

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
**2.** **application.properties**
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
  REACT_APP_CLOUDINARY_NAME=0
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



# 들어가며

기존 Spring MVC로 개발을 하다 Webflux의 Webclient를 사용하게 되며 Webflux로 전환한 경험이 있다.

WebFlux는 논블록킹(non-blocking) 방식으로 기본 MVC에 비해 더 응답을 수행할 수 있다고 한다.

사용 버전은 아래와 같다.
```
spring boot : 3.2.0
java : 17
```

----------

### 1. WebFlux 실행

spring-boot-starter-webflux를 사용하여 프로젝트 생성

참고 : [build.gradle](./build.gradle)

간단한 Controller 작성하여 확인


### 2. R2DBC 연동

```
사용 DB : mariadb  Ver 15.1 Distrib 10.6.12-MariaDB, for debian-linux-gnu (x86_64) 
using  EditLine wrapper
```

관련 위치 : [db](./src/main/java/com/example/webflux/db/)

[build.gradle](./build.gradle)
* R2dbc관련 dependencies 추가

[application.properties](./src/main/resources/application.properties)
* R2dbc관련 설정 추가

[R2dbcConfig](./src/main/java/com/example/webflux//db/R2dbcConfig.java)
* Configuration
* R2dbc 관련 Annotation 
    * @EnableR2dbcAuditing을 통해 @CreatedBy, @CreatedDate, @LastModifiedBy, @LastModifiedDate annotation을 활성화
* ConnectionFactoryInitializer Bean을 통해 sql문을 실행하여 테이블 생성 (ORM이 아니므로 생성 필요)

[ContentController](./src/main/java/com/example/webflux/db/ContentController.java) & [ContentService](./src/main/java/com/example/webflux/db/ContentService.java)
* insert api 관련 추가

### 3. JPA 연동

```
R2dbc와 Jpa를 동시에 연동
해당 부분을 AutoConfiguration 대신 직접 Java Config로 설정
해당 설정으로 다중 dataSource 등 응용가능
```

[build.gradle](./build.gradle)
* Jpa관련 dependencies 추가

[application.properties](./src/main/resources/application.properties)
* Jpa 설정 추가

[R2dbcConfig](./src/main/java/com/example/webflux//db/R2dbcConfig.java)
* ConnectionFactoryInitializer 주석 (table은 Jpa에서 자동으로 생성)
* r2dbc 관련 설정

[JpaConfig](./src/main/java/com/example/webflux//db/JpaConfig.java)
* Jpa 관련 설정

[ContentEntity](./src/main/java/com/example/webflux//db/ContentEntity.java)
* Jpa 관련 annotation 추가 (jakarta.persistence 작성 부)

[ContentController](./src/main/java/com/example/webflux/db/ContentController.java) & [ContentService](./src/main/java/com/example/webflux/db/ContentService.java)
* R2dbc insert Api 추가
* Jdbc insert Api 추가

### 4. Security 연동

```
Oauth2 Server를 사용하여 우선 JWT(Json Web Token)를 사용한 자체 로그인 구현
Java Config
```

[application.properties](./src/main/resources/application.properties)
* 현재 프로젝트 패키지 로깅 레벨 DEBUG 설정

[build.gradle](./build.gradle)
* spring-security-oauth2-authorization-server 추가
* jjwt 추가

[JsonWebToken](./src/main/java/com/example/webflux/security/model/JsonWebToken.java)
* token 값을 전송 위한 model

[JwtUtil](./src/main/java/com/example/webflux/security/JwtUtil.java)
* Json Web Token을 생성 및 유효성 검사를 위한 util

[AccountEntity](./src/main/java/com/example/webflux/security/entity/AccountEntity.java)
* 계정 정보를 저장히기 위한 Entity
* Jpa와 R2DBC 동시 대응
* R2DBC id schema에 값이 있을때 update 동작 이슈에 따른 Persistable 상속

[AccountJpaRepository](./src/main/java/com/example/webflux/security/jpa/AccountJpaRepository.java)
* Jpa용 Respository

[AccountReactiveRepository](./src/main/java/com/example/webflux/security/reactive/AccountReactiveRepository.java)
* R2DBC용 Respository

[CustomUserDetails](./src/main/java/com/example/webflux/security/CustomUserDetails.java)
* Spring Security에서 인증된 사용자 UserDetails를 상속한 커스텀 클래스

[CustomUserDetailsService](./src/main/java/com/example/webflux/security/CustomUserDetailsService.java)
* CustomUserDeails 서비스 구현
* blocking, non-blocking 대응 구현

[JwtProvider](./src/main/java/com/example/webflux/security/JwtProvider.java)
* Security 내 사용자 설정 관련 AuthenticationProvider 인증절차 구현

[JwtWebFilter](./src/main/java/com/example/webflux/security/JwtWebFilter.java)
* WebFilter를 사용하여 non-blocking security filter 구현

[SecuriyConfig](./src/main/java/com/example/webflux/security/SecuriyConfig.java)
* password encoder 설정
  * Security 내 다양한 encoder 설정 가능
  * bcrypt 암호화 사용
* WebFlux 용 SecurityWebFilterChain 설정

[AuthService](./src/main/java/com/example/webflux/security/AuthService.java)
* 로그인 및 회원가입 서비스 구현

[AuthController](./src/main/java/com/example/webflux/security/AuthController.java)
* 로그인 및 회원가입 API 구현

[ContentService](./src/main/java/com/example/webflux/db/ContentService.java)
* findAll 추가

[ContentController](./src/main/java/com/example/webflux/db/ContentController.java)
* Slf4j 어노테이션을 사용 로그 사용
* content등록 api 내 AuthenticationPrincipal 어노테이션을 사용하여 Authentication.getPrincipal()에 저장 된 사용자 추출 가능
* content 전체 리스트 api 추가
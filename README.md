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
# Artineer Spring 7주차 강의노트

## 데이터 영속화
- H2는 메모리에서만 동작되기 때문에 실제 데이터로 사용할 수 있도록 영속화를 해야 한다.


## Docker
- 개발환경에 따라 종속적인 프로그램을 '컨테이너'에 관리할 수 있다.
  - 컨테이너에는 어플리케이션뿐 아니라 환경변수같은 것들 모두 저장된다.
- 원래 작동하던 호스트 환경에서 다른 황경으로 옮겨야 할 때 문제가 발생할 수 있지만, Docker를 통해 컨테이너로 작동 시키면 원래 호스트에 대한 시스템의 의존성이 사라지게 된다.
- Docker는 image를 통해 버전 관리가 되기 때문에 새로운 버전을 배포했을 때 문제가 발생하면 다시 전 버전의 image 배포를 통해 기존보다 더 간단하게 해결할 수 있다.
- docker를 통해 image를 다운 받으려면 docker pull __이미지 이름__ 명령어를 이용하면 된다.
  - docker hub라는 공간에서 image를 가져옴
  - docker pull __이미지 이름:태그__ 를 통해 원하는 버전을 가지고 올 수 있다. 태그를 생략하면 가장 최근 버전을 가지고 온다.

~~~
docker pull mysql
~~~

- 다운로드한 image를 확인 하기 위한 명령어

~~~
docker image
~~~

- image를 통해 container를 생성하고 동시에 실행 시키는 명령어
- 해쉬값이 나오며 실행이 된다.

~~~
docker run --name mysql-contatiner -e MYSQL_ROOT_PASSWORD=artineer -d -p 3306:3306 mysql:latest
~~~

- --name 속성을 통해 컨테이너의 이름일 지정할 수 있다
- -e 속성은 그 컨테이너가 사용되는 환경 변수 값을 전달할 수 있다.
- -d 속성은 해당 컨테이너가 daemon 형태로 실행되도록 할 수 있다.
  - daemon이란 명령어를 통해 실행하면 프로그램이 background에서 동작하게 된다.
- -p 속성은 호스트와 컨테이너간의 포트를 지정한다. (위 명령어에서는 호스트의 3306과 컨테이너의 3306을 매핑)
  - 외부에서는 컨테이너로 바로 접근할 수 없는데 명령어를 통해 연결을 시켜줘야 접근할 수 있다.
- 마지막 mysql:latest는 docker image의 이름

~~~
docker run --name [컨테이너의 이름] -e [컨테이너 안에서 사용되는 환경 변수 값] -d(daemon으로 실행 되도록) -p[호스트와 컨테이너간의 포트 지정] [사용할 image 이름]
~~~

- 로컬에 구성된 container의 목록 확인

~~~
docker ps // 실행 중인 contatiner만 조회
docker ps -a // 로컬에 구성된 모든 container 조회
~~~

- docker의 container를 제거

~~~
docker rm [컨테이너 이름]
~~~

- 실행중인 container를 통료

~~~
docker stop [컨테이너 이름]
~~~

- 종료된 docker container 시작

~~~
docker start [컨테이너 이름]
~~~

- 실행되고 있는 container의 shell 프로그램 실행
  - shell은 컴퓨터가 가진 자원을 잘 사용할 수 있게끔 제공하는 인터페이스
  - container도 shell을 갖고 있다.

~~~
docker exec -it [컨테이너 이름] bash
(docker execute -interaction [컨테이너 이름] [shell 종류])
~~~

### docker를 통한 mysql 접속 이후
- mysql 접속

~~~ 
mysql -u root -p
mysql -u [유저 이름] -p
~~~

- 스프링 어플리케이션에서 사용할 데이터베이스 추가

~~~ 
CREATE DATABASE artineer;
~~~

- 데이터베이스 목록 확인

~~~
show databases;
~~~

- MySQL과 연동을 하기 위해 application.properties 수정

##### application.properties

~~~
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/artineer?serverTimezone=UTC&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=artineer

spring.jpa.show-sql=true // sql 내용을 볼 수 있음
spring.jpa.generate-ddl=true // DDL 옵션을 jpa로 사용할 수 있는 옵션

 // 이하 jpa에게 DB벤더를 지정하는 옵션
spring.jpa.database=mysql
spring.jpa.database-platform=org.hibernate.dialect.MySQL5InnoDBDialect
~~~

~~~
spring.datasource.url=jdbc:mysql://localhost:3306/[db 이름]?serverTimezone=UTC&characterEncoding=UTF-8
spring.datasource.username=[유저 이름]
spring.datasource.password=[유저 비밀번호]
~~~

- __spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver__ 에 오류 발생
  - 의존성을 주입해야 함.
    - build.gradle에 의존성 주입

##### build.gradle

~~~
compileOnly 'mysql:mysql-connector-java'
~~~

- 실행 후 데이터를 확인하면 한글이 깨진다.
- 해결을 위해서는 /etc/mysql/my.cnf 파일을 수정해야 한다.
- 하지만 container는 vi가 존재하지 않기 때문에 설치를 진행한다

~~~ 
apt-get update
apt-get install vim
~~~

- 설치한 vi를 통해 my.cnf 수정

##### my.cnf

~~~
[client]
default-character-set=utf8

[mysql]
default-character-set=utf8

[mysqld]
pid-file        = /var/run/mysqld/mysqld.pid
socket          = /var/run/mysqld/mysqld.sock
datadir         = /var/lib/mysql
secure-file-priv= NULL
collation-server = utf8_unicode_ci
init-connect='SET NAMES utf8'
character-set-server = utf8
~~~

- 실제 데이터가 들어갔는지 확인
- MySQL 접속 후

~~~ 
use artineer; // 사용할 데이터베이스로 변경
select * from article // article 테이블 조회
~~~

- Oracle은 확장하기 어렵다.
  - 한 개의 데이터베이스에 모든 데이터를 몰아넣는 형식으로 동작한다.
  - 서비스가 더 방대해져서 더 큰 데이터베이스를 사용해야 할 경우 더 성능이 좋은 데이터베이스 서버를 구매해야 함.
- 작은 데이터베이스 여러 개를 사용하는 형태를 MSA라고 한다.
- JPA같은 ORM을 쓰면 Query를 짤 필요성은 사라지지만 전문적이고 성능을 더 끌어올리기 위해서는 각 DataBase를 공부해야 한다.
  - 쿼리문 성능을 더 끌어올리는 방법을 알아야 하기 때문(쿼리튜닝)

### redis
- NoSQL은 빠르다는 장점이 있다.
  - 정규화가 되어있지 않기 때문에 빠른 것이다.
- NoSQL은 JSON형태로 저장되고 RDBMS는 테이블이 있고 테이블에 데이터를 저장한다.
  - RDBMS는 테이블끼리 관계를 가지고 데이터를 사용한다.
  - RDBMS의 최종 목표는 정합성을 100%로 맞추는 것
    - A테이블과 B테이블에는 같은 데이터가 있으면 안 된다.
      - A테이블 정보가 변경 됐을 때 B테이블에 있는 데이터와 일치되지 않기 때문.
- radis(NoSQl)는 정합성을 따지지 않기 때문에 성능이 빠르다.
- key-value 형태로 데이터를 저장
- redis는 메모리만 사용한다.(스토리지는 사용하지 않음)
  - 메모리만 사용하기 때문에 데이터가 소실될 수 있다.
- 주로 캐싱의 목적으로 사용된다.
- docker에서 redis image 다운로드

~~~
docker pull redis
~~~

- redis-cli 접속 (redis에서 따로 사용하는 shell)

~~~
docker run -p 6379:6379 --name redis_boot -d redis
~~~

#### redis 간단한 명령어
- 등록되어 있는 key-value 값을 모두 가져온다
- redis는 싱글스레드 기반이기 때문에 실무에서 사용하게 되면 시스템이 다운될 수 있다.
  - 시간이 오래 걸리는 작업 모두 사용하지 않는게 좋다.

~~~
keys *
~~~

- 문자열 타입의 정보 저장하기

~~~
set KEY1 "VALUE1"
~~~

- 저장된 value 값을 key 기준으로 확인

~~~
get KEY1
~~~

#### redis를 스프링에서 사용하자
- redis를 쉽게 사용할 수 있게 RedisTemplate을 제공한다.
- RedisTemplate을 사용하기 위해 먼저 의존성과 설정정보 추가를 해야 한다.

##### application.properties

~~~
spring.redis.host=127.0.0.1
spring.redis.port=6379
~~~

##### build.gradle

~~~
	implementation 'org.springframework.boot:spring-boot-starter-data-redis'
~~~

- Config 패키지 생성 후 RedisConfig.java 생성
  - 아래 코드 입력

##### RedisConfig.java

~~~java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;


@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
}
~~~

- Connection을 하는 것이 많은 시간이 걸리고 자원이 많이 사용되는 작업인데 원래 Connection을 만들어 pool에 미리 넣어놓고 필요할 때 꺼내서 쓰는 구조로 만드는 것을 ConnectionFactory를 이용해서 한다.

- RedisTemplate을 활용해 간단한 데이터를 넣고 가져오자
- runner 패키지 생성 -> RunnerRedis.java 생성 -> 코드 작성
- ApplicationRunner을 쓰면 스프링이 실행될 때 같이 동작하게 된다.

##### RedisRunner.java

~~~java
@Slf4j
@Component
public class RedisRunner implements ApplicationRunner {
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisRunner(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
         redisTemplate.opsForValue().set("key1", "value1"); // key에 value를 넣어줌
        log.info(redisTemplate.opsForValue().get("key1")); // key를 입력하면 log를 통해 value를 보여준다
    }
}
~~~

- Redis는 여러 타입이 있는데 여러 타입을 모두 사용할 수 있도록 <?>를 사용 했는데 오로지 String만 사용하는 Template도 있는 StringRedisTemplate이 있다.

- Bean 등록

##### RedisConfig.java

~~~java
@Configuration
public class RedisConfig {
    
    // ...
    
    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
        return stringRedisTemplate;
    }
}
~~~

- Bean 주입, 사용

##### RedisRunner.java

~~~java
@Slf4j
@Component
public class RedisRunner implements ApplicationRunner {
    private final RedisTemplate<String, String> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RedisRunner(RedisTemplate<String, String> redisTemplate, StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        redisTemplate.opsForValue().set("key1", "value1");
        log.info(redisTemplate.opsForValue().get("key1"));

        stringRedisTemplate.opsForValue().set("key2", "value2");
        log.info(stringRedisTemplate.opsForValue().get("key2"));
    }
}
~~~

- Generic 타입이 사리졌다는 것 외에는 동일하지만, 보통 String을 많이 사용하기 때문에 제공해주는 기능

- Repository처럼 사용할 수 있다.
- Article의 @Entity 어노테이션을 @RedisHash로 변경

##### Article.java

~~~java
// ...
@RedisHash
public class Article {
    // ...
}
~~~

- 서버를 동작 시키고 POST와 GET을 해보면 확인할 수 있다.

##### POST

~~~
POST http://localhost:8080/api/v1/article

HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sun, 28 Nov 2021 15:35:54 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  "code": {
    "name": "CM0000",
    "desc": "정상입니다"
  },
  "data": 6713072108327566522
}

Response code: 200; Time: 471ms; Content length: 68 bytes
~~~

##### GET

~~~
GET http://localhost:8080/api/v1/article/6713072108327566522

HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sun, 28 Nov 2021 15:36:39 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  "code": {
    "name": "CM0000",
    "desc": "정상입니다"
  },
  "data": {
    "id": "6713072108327566522",
    "title": "아티니어 스프링 강좌",
    "content": "아티니어 스프링 많관부"
  }
}

Response code: 200; Time: 134ms; Content length: 124 bytes
~~~

- redis에서 __hgetall <key>__ 를 통해 값을 확인할 수 있는데 한글이 다 깨진다.
- 처음 실행할 때 __docker exec -i -t redis_boot redis-cli --raw__ 로 실행하자.
- 하지만 데이터를 확인하기 불편하기 때문에 api 통신을 해서 보는 것이 더 편할 거 같다.
- RDBMS은 다른 DBMS이지만 비슷하다는 특징이 있지만 NoSQL은 NoSQL 벤더끼리 모두 상이하다는 특징이 있기 때문에 @Entity 어노테이션으로 연동이 불가능하다.
  - 그래서 @RedisHash 어노테이션을 사용하다
  - 각 NoSQL끼리 구성은 비슷하지만 인터페이스가 다르다.
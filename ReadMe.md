# Spring Boot(+JPA)

## API를 최적화 하는 방식의 순서..
1. 엔티티 조회 방식으로 우선 접근
  1) 페치조인으로 쿼리 수를 최적화
  2) 컬렉션 최적화
  
      2_1) 페이징 필요 => hibernate.default_batch_fetch_size(yaml global), @BatchSize(Class, Method) 로 최적화
      2_2) 페이징 필요X => 페치 조인 사용
  
2. 엔티티 조회 방식으로 해결이 안되면 DTO 조회 방식 사용

3. DTO 조회 방식으로 해결이 안되면 NativeSQL or 스프링 JdbcTemplate


### Open Session In View(OSIV)(=Open EntityManager In View)
Lazy Loading 때문에 Persistence Context가 살아있어야 하고, 이는 기본적으로 DB Connection을 유지한다.
이것은 어떻게 보면 큰 장점이나, 
DB Connection Resource를 차지하는 시간이 길어지기 때문에 Conn이 부족하게 되는 경우도 있다.

Spring 설정파일에 spring.jpa.open-in-view: true 가 기본값이다

실시간성이 중요하거나, 외부 API를 호출해야되거나 하는 경우에는 false로 두어서 트랜잭션을 종료할 때, 
영속성 컨텍스트를 닫고 데이터베이스 커넥션도 반환시킬 수 있다.
그러나, 지연로딩이 불가능해지며! 해당 트랜잭션안에서 지연로딩을 강제로 호출 해두어야만 사용이 가능하다.

--- 그렇다면 실무에서 *OSIV를 끈 상태* 를 사용하려면..? 
-> Command와 Query를 분리하는 것
##### OrderService 
 * OrderService: 핵심 비지니스 로직
 * OrderQueryService: 화면이나 API에 맞춘 서비스(주로 읽기 전용 트랜잭션 사용)

* 실시간 API의 경우는 OSIV를 끄고 ADMIN처럼 커넥션을 많이 사용하지 않는다면 OSIV를 켜도 무방하다
(멀티모듈로 구성하면 같은 어플리케이션 내에서도 각각 나눠서 구성할 수 있다.)

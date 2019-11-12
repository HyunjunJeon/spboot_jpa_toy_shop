# Spring Boot(+JPA) 로 만드는 간단 쇼핑몰

## API를 최적화 하는 방식의 순서..
1. 엔티티 조회 방식으로 우선 접근
  1) 페치조인으로 쿼리 수를 최적화
  2) 컬렉션 최적화
  2_1) 페이징 필요 => hibernate.default_batch_fetch_size(yaml global), @BatchSize(Class, Method) 로 최적화
  2_2) 페이징 필요X => 페치 조인 사용
  
2. 엔티티 조회 방식으로 해결이 안되면 DTO 조회 방식 사용

3. DTO 조회 방식으로 해결이 안되면 NativeSQL or 스프링 JdbcTemplate

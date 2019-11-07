package com.inflearn.jpabootshop.configure;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleBeans {
    /*
        Entity를 노출시키는 경우, LAZY 때문에 Proxy 객체가 삽입되어 에러나는 경우를 해결하기 위해서 테스트 용도로 사용함
        (실제로는 DTO를 사용하는 걸 권장하기 때문에 이렇게 쓸 이유는 없지만!
        -> Test 코드 작성할 때는 괜찮은거 같음.. 성능 Test를 할 경우는 괜찮은거 같음!
     */
    @Bean
    Hibernate5Module hibernate5Module() {
        Hibernate5Module hibernate5Module = new Hibernate5Module();
        // 설정을 통해서 강제로 LAZY를 다 실행시키기로 Test해봄..(성능상 굉장히 안좋음!)
        //hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
        return hibernate5Module;
    }
}

package toby.toby_spring.ch6;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Date;

public class ProxyTest {

    @Test
    void simpleProxy() {
        HelloTarget helloTarget = new HelloTarget();
        Assertions.assertThat(helloTarget.sayHello("Toby")).isEqualTo("Hello Toby");
        Assertions.assertThat(helloTarget.sayHi("Toby")).isEqualTo("Hi Toby");
        Assertions.assertThat(helloTarget.sayThankYou("Toby")).isEqualTo("Thank You Toby");

        HelloUppercase helloUppercase = new HelloUppercase(helloTarget);
        Assertions.assertThat(helloUppercase.sayHello("Toby")).isEqualTo("HELLO TOBY");
        Assertions.assertThat(helloUppercase.sayHi("Toby")).isEqualTo("HI TOBY");
        Assertions.assertThat(helloUppercase.sayThankYou("Toby")).isEqualTo("THANK YOU TOBY");
    }

    @Test
    void dynamic() {
        HelloTarget helloTarget = new HelloTarget();

        Hello proxiedHello = (Hello) Proxy.newProxyInstance(getClass().getClassLoader(), // 동적 생성 다이내믹 프록시 클래스의 로딩에 사용할 클래스 로더
                new Class[]{Hello.class},  // 구현할 인터페이스
                new UppercaseHandler(helloTarget));// 부가기능과 위임 코드를 담은 InvocationHandler

        Assertions.assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
        Assertions.assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
        Assertions.assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("THANK YOU TOBY");


    }
}

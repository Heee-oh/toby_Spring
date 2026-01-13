package toby.toby_spring.ch6;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

public class ReflectTest {

    @Test
    void invokeTest() throws Exception {

        String name = "spring";
        Assertions.assertThat(name.length()).isEqualTo(6);

        Method length = String.class.getMethod("length");
        Assertions.assertThat((Integer) length.invoke(name)).isEqualTo(6);

        Assertions.assertThat(name.charAt(0)).isEqualTo('s');

        Method charAt = String.class.getMethod("charAt", int.class);
        Assertions.assertThat((Character) charAt.invoke(name, 0)).isEqualTo('s');

        // substring
        String substring1 = name.substring(0, 1);
        Method substring = name.getClass().getMethod("substring", int.class, int.class);
        Assertions.assertThat((String)substring.invoke(name, 0, 1)).isEqualTo(substring1);

    }

}

package toby.toby_spring;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import toby.toby_spring.ch3.ch3_5.Calculator;

import java.io.IOException;

public class CalcSumTest {
    @Test
    void sumOfNumbers() throws IOException {
        Calculator calculator = new Calculator();
        int sum = calculator.calcSum(getFilepath());
        Assertions.assertThat(sum).isEqualTo(10);

    }

    @Test
    void multiOfNumbers() throws IOException {
        Calculator calculator = new Calculator();
        int sum = calculator.calcMultiply(getFilepath());
        Assertions.assertThat(sum).isEqualTo(24);

    }

    private static String getFilepath() {
        return "C:\\Users\\User\\Desktop\\Desktop\\toby_Spring\\src\\test\\java\\toby\\toby_spring\\numbers.txt";
    }

    @Test
    void concatenateStrings() throws IOException {
        Assertions.assertThat(new toby.toby_spring.ch3.ch3_5_1.Calculator().concatenate(getFilepath())).isEqualTo("1234");
    }
}

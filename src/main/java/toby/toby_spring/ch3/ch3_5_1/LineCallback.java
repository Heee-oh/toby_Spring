package toby.toby_spring.ch3.ch3_5_1;

public interface LineCallback<T> {
    T doSomethingWithLine(String line, T value);
}

package toby.toby_spring.ch6;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import toby.toby_spring.ch6.factory.Message;
import toby.toby_spring.ch6.factory.MessageFactoryBean;

@SpringBootTest
class FactoryBeanTest {

    @Autowired
    ApplicationContext context;

    @Test
    void getMessageFormFactoryBean() {
        Object message = context.getBean("message");
        Assertions.assertThat(message).isInstanceOf(Message.class);

        Assertions.assertThat(((Message) message).getText()).isEqualTo("Factory Bean");
    }

    @Test
    void getFactoryBean() {
        Object factory = context.getBean("&message");
        Assertions.assertThat(factory).isInstanceOf(MessageFactoryBean.class);
    }


}
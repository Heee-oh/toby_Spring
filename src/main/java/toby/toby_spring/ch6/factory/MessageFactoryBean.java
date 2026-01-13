package toby.toby_spring.ch6.factory;

import org.springframework.beans.factory.FactoryBean;

public class MessageFactoryBean implements FactoryBean<Message> {
    String text;

    public MessageFactoryBean(String text) {
        this.text = text;
    }

    // 빈 오브젝트 생성하여 반환
    @Override
    public Message getObject() throws Exception {
        return Message.newMessage(text);
    }

    // 생성되는 오브젝트의 타입 반환
    @Override
    public Class<? extends Message> getObjectType() {
        return Message.class;
    }

    // getObject가 반환하는 오브젝트가 항상 싱글톤 오브젝트인지
    @Override
    public boolean isSingleton() {
        return false;
    }
}

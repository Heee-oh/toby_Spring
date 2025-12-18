package toby.toby_spring.ch3.ch3_5_2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;


import javax.sql.DataSource;

public class DaoFactory {

    @Bean // 오브젝트 생성 담당하는 LoC용 메서드라는 표시
    public UserDao userDao() {
        return new UserDao(dataSource());
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        return new NConnectionMaker();
    }

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource datasource = new SimpleDriverDataSource();
        datasource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
        datasource.setUrl("jdbc:mysql://localhost:3306/springbook");
        datasource.setUsername("root");
        datasource.setPassword("1234");
        return datasource;

    }
}

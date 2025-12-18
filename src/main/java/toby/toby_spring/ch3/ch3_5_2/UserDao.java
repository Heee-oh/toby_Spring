package toby.toby_spring.ch3.ch3_5_2;


import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public void deleteAll() {
        jdbcTemplate.update(con -> con.prepareStatement("delete from users"));
    }









}

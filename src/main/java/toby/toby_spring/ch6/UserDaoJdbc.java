package toby.toby_spring.ch6;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDaoJdbc implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDaoJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(User user) {
        jdbcTemplate.update("insert into users(id, name, password, level, login, recommend, email) values(?, ?, ?, ?, ?, ?, ?)",
                user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail());

    }


    // getForInt가 없어짐
    public int getCount() {
        return jdbcTemplate.queryForObject("select count(*) from users", (rs, rowNum) -> rs.getInt(1));
    }

    @Override
    public void update(User user1) {
        jdbcTemplate.update("update users set name = ?, level = ?, login = ?, recommend = ? where id = ?",
                user1.getName(), user1.getLevel().intValue(), user1.getLogin(), user1.getRecommend(), user1.getId());
    }


    public User get(String id) {
        return jdbcTemplate.queryForObject("select * from users where id = ?",
                new Object[]{id},
                new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                        User user = new User();
                        user.setId(rs.getString("id"));
                        user.setName(rs.getString("name"));
                        user.setPassword(rs.getString("password"));
                        user.setLevel(Level.valueOf(rs.getInt("level")));
                        user.setLogin(rs.getInt("login"));
                        user.setRecommend(rs.getInt("recommend"));
                        user.setEmail(rs.getString("email"));
                        return user;
                    }
                });
    }

    public List<User> getAll() {
        return jdbcTemplate.query("select * from users order by id",
                (rs, rowNum) -> {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setPassword(rs.getString("password"));
                    user.setLevel(Level.valueOf(rs.getInt("level")));
                    user.setLogin(rs.getInt("login"));
                    user.setRecommend(rs.getInt("recommend"));
                    user.setEmail(rs.getString("email"));

                    return user;
                });
    }


    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from users");
    }


}

package toby.toby_spring.ch3.ch3_6;


import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDao {

    private final JdbcTemplate jdbcTemplate;



    public UserDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(User user) {
        jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)", user.getId(), user.getName(), user.getPassword());
    }

    public int getCountV1() {
        return jdbcTemplate.query(new PreparedStatementCreator() {
                                      @Override
                                      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                                          return con.prepareStatement("select count(*) from users");
                                      }
                                  },
                new ResultSetExtractor<Integer>() {
                    @Override
                    public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                        rs.next();
                        return rs.getInt(1); // 첫번째 컬럼 가져오기 (1개만 조회했으므로 1번째 맞음)
                    }
                });
    }

    // getForInt가 없어짐
    public int getCountV2() {
        return jdbcTemplate.queryForObject("select count(*) from users", (rs, rowNum) -> rs.getInt(1));
    }


    public User get(String id) {
        return jdbcTemplate.queryForObject("select * from usersx where id = ?",
                new Object[]{id},
                new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                        User user = new User();
                        user.setId(rs.getString("id"));
                        user.setName(rs.getString("name"));
                        user.setPassword(rs.getString("password"));
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
                    return user;
                });
    }



    public void deleteAllV1() {
        jdbcTemplate.update(
                // 이전
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        return con.prepareStatement("delete from users");
                    }

                }

        );
    }

    // 내장 콜백 사용
    public void deleteAllV2() {
        jdbcTemplate.update("delete from users");
    }

    public void deleteAllV3() {
        jdbcTemplate.update(con -> con.prepareStatement("delete from users"));
    }

}

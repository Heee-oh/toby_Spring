package toby.toby_spring.ch3.ch3_2;

import toby.toby_spring.ch3.ch3_1.ConnectionMaker;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDeleteAll extends UserDao {

    public UserDeleteAll(ConnectionMaker connectionMaker, DataSource dataSource) {
        super(connectionMaker, dataSource);
    }




    @Override
    protected PreparedStatement makeStatement(Connection c) throws SQLException {
        PreparedStatement ps = c.prepareStatement("delete from users");
        return ps;
    }
}

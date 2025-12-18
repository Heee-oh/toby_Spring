package toby.toby_spring.ch1.ch1_4;

public class DaoFactory {
    public UserDao userDao() {
        ConnectionMaker connectionMaker = new NConnectionMaker();
        return new UserDao(connectionMaker);
    }
}

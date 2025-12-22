package toby.toby_spring.ch3.ch3_6;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserDaoTest {

    @Autowired
    DataSource dataSource;

    @Autowired
    private UserDao userDao;



    @Test
    void add() {
        UserDao userDao = new UserDao(dataSource);
        User user = new User();
        user.setId("1");
        user.setName("2");
        user.setPassword("3");

        userDao.add(user);
        userDao.deleteAllV1();

        userDao.add(user);
        userDao.deleteAllV2();

        userDao.add(user);
        userDao.deleteAllV3();
    }


    @Test
    void getAll() {
        userDao.deleteAllV3();
        User user = new User();
        user.setId("1");
        user.setName("2");
        user.setPassword("3");

        userDao.add(user);
        List<User> users1 = userDao.getAll();
        Assertions.assertThat(users1.size()).isEqualTo(1);
        Assertions.assertThat(user).isEqualTo(users1.get(0));

        User user1 = new User();
        user1.setId("2");
        user1.setName("3");
        user1.setPassword("4");

        userDao.add(user1);
        List<User> users2 = userDao.getAll();
        Assertions.assertThat(users2.size()).isEqualTo(2);
        Assertions.assertThat(user).isEqualTo(users2.get(0));
        Assertions.assertThat(user1).isEqualTo(users2.get(1));

        User user2 = new User();
        user2.setId("3");
        user2.setName("4");
        user2.setPassword("5");

        userDao.add(user2);
        List<User> users3 = userDao.getAll();
        Assertions.assertThat(users3.size()).isEqualTo(3);
        Assertions.assertThat(user).isEqualTo(users3.get(0));
        Assertions.assertThat(user1).isEqualTo(users3.get(1));
        Assertions.assertThat(user2).isEqualTo(users3.get(2));


    }
}
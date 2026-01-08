package toby.toby_spring.ch6;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserDaoTest {

    User user1;
    User user2;
    User user3;
    @Autowired
    private UserDao userDao;


    @BeforeEach
    void setUp() {
        userDao.deleteAll();
        this.user1 = new User("gyee3", "박성칠", "springno1","abc@ggg.org", Level.BASIC, 1, 0);
        this.user2 = new User("lee1", "이길투", "springno2","bbb@ggg.org", Level.SILVER, 55, 10);
        this.user3 = new User("bum2", "박범투", "springno3","ccc@ggg.org", Level.GOLD, 100, 40);


        userDao.add(user1);
        userDao.add(user2);
    }


    @Test
    public void addAndGet() {
        User userget1 = userDao.get(user1.getId());
        checkSameUser(user1, userget1);

        User userget2 = userDao.get(user2.getId());
        checkSameUser(user2, userget2);
    }

    @Test
    @DisplayName("where 을 실수로 빼먹은 채로 업데이트 시 영향 체크 ")
    void update() {
        userDao.deleteAll();
        userDao.add(user1);
        userDao.add(user2);

        user1.setName("박박박");
        user1.setLevel(Level.GOLD);
        user1.setLogin(100);
        user1.setRecommend(999);

        userDao.update(user1);

        User user1update = userDao.get(user1.getId());
        checkSameUser(user1, user1update);

        User user2same = userDao.get(user2.getId());
        checkSameUser(user2, user2same);


    }


    void checkSameUser(User user1, User user2) {
        Assertions.assertEquals(user1, user2);
    }
  
}
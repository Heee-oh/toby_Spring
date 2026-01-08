package toby.toby_spring.ch6;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;


import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class UserServiceTest {

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    @Autowired @Qualifier("userServiceTx")
    UserService userService;
    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    ApplicationContext applicationContext;

    List<User> users;
    @Autowired
    private UserDao userDao;
    @Autowired
    DataSource dataSource;

    @Autowired
    MailSender mailSender;


    static class TestUserService extends UserServiceImpl {

        private String id;

        public TestUserService(String id) {
            this.id = id;
        }

        @Override
        protected void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException {}

    static class MockMailSender implements MailSender {

        private List<String> requests = new ArrayList<>();

        public List<String> getRequests() {
            return requests;
        }

        @Override
        public void send(SimpleMailMessage simpleMessage) throws MailException {
            requests.add(simpleMessage.getTo()[0]);
        }

        @Override
        public void send(SimpleMailMessage... simpleMessages) throws MailException {

        }
    }

    static class MockUserDao implements UserDao {

        private List<User> users;
        private List<User> updated = new ArrayList<>();

        public MockUserDao(List<User> users) {
            this.users = users;
        }

        public List<User> getUpdated() {
            return updated;
        }

        @Override
        public List<User> getAll() {
            return users;
        }

        @Override
        public void update(User user) {
            updated.add(user);
        }

        // 테스트 시 사용하지 않는 메소드는 UnsupportedOperationException 를 던져 지원하지 않는 기능을 명시
        @Override
        public void add(User user) {
            throw new UnsupportedOperationException();
        }

        @Override
        public User get(String id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteAll() {
            throw new UnsupportedOperationException();

        }

        @Override
        public int getCount() {
            throw new UnsupportedOperationException();
        }
    }
    @BeforeEach
    void setUp() {
        users = Arrays.asList(
                new User("bumjin", "박범진", "p1", "1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0),
                new User("joytouch", "강명성", "p2","1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
                new User("erwins", "신승한", "p3","1", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1),
                new User("madnite1", "이상호", "p4","1",Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD),
                new User("green", "오민규", "p5", "1", Level.GOLD, 100, Integer.MAX_VALUE)
        );
    }

    @Test
    void upgradeLevelsV4() throws SQLException {
        // db 테스트 데이터 준비
        userDao.deleteAll();
        for (User user : users) {
            userDao.add(user);
        }

        // 메일 발송 여부 확인을 위한 목 오브젝트 DI
        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        // test 대상
        userService.upgradeLevels();

        // db 저장값확인
        checkLevelV2(users.get(0), false);
        checkLevelV2(users.get(1), true);
        checkLevelV2(users.get(2), false);
        checkLevelV2(users.get(3), true);
        checkLevelV2(users.get(4), false);

        // 목 오브젝트를 이용한 결과 확인
        List<String> requests = mockMailSender.getRequests();
        Assertions.assertThat(requests.size()).isEqualTo(2);
        Assertions.assertThat(requests.get(0)).isEqualTo(users.get(1).getEmail());
        Assertions.assertThat(requests.get(1)).isEqualTo(users.get(3).getEmail());
    }

    @Test
    void upgradeLevelsV5() throws SQLException {
        UserServiceImpl userServiceImpl2 = new UserServiceImpl();
        MockUserDao mockUserDao = new MockUserDao(users);
        userServiceImpl2.setUserDao(mockUserDao);

        // 메일 발송 여부 확인을 위한 목 오브젝트 DI
        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl2.setMailSender(mockMailSender);

        // test 대상
        userServiceImpl2.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();
        Assertions.assertThat(updated.size()).isEqualTo(2);
        checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
        checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);

        // 목 오브젝트를 이용한 결과 확인
        List<String> requests = mockMailSender.getRequests();
        Assertions.assertThat(requests.size()).isEqualTo(2);
        Assertions.assertThat(requests.get(0)).isEqualTo(users.get(1).getEmail());
        Assertions.assertThat(requests.get(1)).isEqualTo(users.get(3).getEmail());
    }

    @Test
    void upgradeAllOrNothing() {
        TestUserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        testUserService.setMailSender(mailSender);

        UserServiceTx userServiceTx = new UserServiceTx();
        userServiceTx.setTransactionManager(transactionManager);
        userServiceTx.setUserService(testUserService);

        userDao.deleteAll();
        for (User user : users) {
            userDao.add(user);
        }

        try {
            userServiceTx.upgradeLevels();
            Assertions.fail("TestUserServiceException expected");
        } catch (TestUserServiceException e) {
        } catch (Exception e) {
            throw new RuntimeException(e);

        }

        checkLevelV2(users.get(1), false);
    }

    @Test
    void bean() {
        Assertions.assertThat(userService).isNotNull();

        String[] beanNamesForType = applicationContext.getBeanNamesForType(UserService.class);
        boolean isBean = false;

        for (String name : beanNamesForType) {
            if (applicationContext.getBean(name) == userService) {
                isBean = true;
                break;
            }
        }

        Assertions.assertThat(isBean).isTrue();

    }


    private void checkLevelV2(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());

        if (upgraded) {
            Assertions.assertThat(userUpdate.getLevel())
                    .isEqualTo(user.getLevel().nextLevel());
        } else {
            Assertions.assertThat(userUpdate.getLevel())
                    .isEqualTo(user.getLevel());

        }
    }

    @Test
    void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        Assertions.assertThat(userWithLevelRead.getLevel()).isEqualTo(userWithLevel.getLevel());
        Assertions.assertThat(userWithoutLevelRead.getLevel()).isEqualTo(userWithoutLevel.getLevel());
    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        Assertions.assertThat(updated.getId()).isEqualTo(expectedId);
        Assertions.assertThat(updated.getLevel()).isEqualTo(expectedLevel);
    }

}
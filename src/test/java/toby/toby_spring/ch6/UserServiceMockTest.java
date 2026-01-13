package toby.toby_spring.ch6;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserServiceMockTest {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    List<User> users;

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
    void upgradeLevelsV5() throws SQLException {
        UserServiceImpl userServiceImpl2 = new UserServiceImpl();
        UserServiceTest.MockUserDao mockUserDao = new UserServiceTest.MockUserDao(users);
        userServiceImpl2.setUserDao(mockUserDao);


        // 메일 발송 여부 확인을 위한 목 오브젝트 DI
        UserServiceTest.MockMailSender mockMailSender = new UserServiceTest.MockMailSender();
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
    void mockUpgradeLevels() throws Exception {
        UserServiceImpl userService = new UserServiceImpl();

        UserDao mock = Mockito.mock(UserDao.class);
        Mockito.when(mock.getAll()).thenReturn(users);
        userService.setUserDao(mock);


        MailSender mockMailSender = Mockito.mock(MailSender.class);
        userService.setMailSender(mockMailSender);
        userService.upgradeLevels();

        Mockito.verify(mock, Mockito.times(2)).update(Mockito.any(User.class));
        Mockito.verify(mock, Mockito.times(2)).update(Mockito.any(User.class));

        Mockito.verify(mock).update(users.get(1));
        Assertions.assertThat(users.get(1).getLevel()).isEqualTo(Level.SILVER);

        Mockito.verify(mock).update(users.get(3));
        Assertions.assertThat(users.get(3).getLevel()).isEqualTo(Level.GOLD);

        ArgumentCaptor<SimpleMailMessage> mailMessageArg
                = ArgumentCaptor.forClass(SimpleMailMessage.class);

        Mockito.verify(mockMailSender, Mockito.times(2)).send(mailMessageArg.capture()); // 파라미터 정밀 검사를 위해 캡처 가능
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();

        Assertions.assertThat(mailMessages.get(0).getTo()[0]).isEqualTo(users.get(1).getEmail());
        Assertions.assertThat(mailMessages.get(1).getTo()[0]).isEqualTo(users.get(3).getEmail());
    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        Assertions.assertThat(updated.getId()).isEqualTo(expectedId);
        Assertions.assertThat(updated.getLevel()).isEqualTo(expectedLevel);
    }
}

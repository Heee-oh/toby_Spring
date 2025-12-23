package toby.toby_spring.ch5;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void upgradeLevel() {
        Level[] values = Level.values();
        for (Level level : values) {
            if (level.nextLevel() == null) continue;
            user.setLevel(level);
            user.upgradeLevel();

            Assertions.assertThat(user.getLevel()).isEqualTo(level.nextLevel());
        }
    }

    @Test
    void cannotUpgradeLevel() {
        Level[] values = Level.values();

        Assertions.assertThatThrownBy(() -> {
            for (Level level : values) {
                if (level.nextLevel() != null) continue;
                user.setLevel(level);
                user.upgradeLevel();
            }
        }).isInstanceOf(IllegalStateException.class);
    }

}
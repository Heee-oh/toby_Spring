package toby.toby_spring.ch5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private UserDao userDao;
//    private UserLevelUpgradePolicy userLevelUpgradePolicy;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

//    @Autowired
//    public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
//        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
//    }

    // 사용자 레벨 관리 기능
    public void upgradeLevelsV0() {
        List<User> users = userDao.getAll();

        for (User user : users) {
            Boolean changed = null;

            if (user.getLevel() == Level.BASIC && user.getLogin() >= 50) {
                user.setLevel(Level.SILVER);
                changed = true;

            } else if (user.getLevel() == Level.SILVER && user.getRecommend() >= 30) {
                user.setLevel(Level.GOLD);
                changed = true;

                // 조건 미달, 골드일시 변화 X
            } else {
                changed = false;
            }

            if (changed) {
                userDao.update(user);
            }
        }
    }
    public void upgradeLevels() {
        List<User> users = userDao.getAll();

        for (User user : users) {
            if (canUpgradeLevel(user)) {
                upgradeLevel(user);
            }
        }
    }

    protected void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
    }

    public void add(User user) {
        if (user.getLevel() == null) {
            user.setLevel(Level.BASIC);
        }

        userDao.add(user);

    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        return switch (currentLevel) {
            case BASIC -> (user.getLogin() >= 50);
            case SILVER -> (user.getRecommend() >= 30);
            case GOLD -> false;
            default -> throw new IllegalArgumentException("Unknown Level: " + currentLevel);
        };
    }
}

package toby.toby_spring.ch5;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.web.servlet.server.Session;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

//@Service
public class UserService {
    private UserDao userDao;
    //    private UserLevelUpgradePolicy userLevelUpgradePolicy;
//    private DataSource dataSource;


    private PlatformTransactionManager transactionManager;
    private MailSender mailSender;

//    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

//    @Autowired
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

//    @Autowired
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    //
//    @Autowired
//    public void setDataSource(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }
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
//    public void upgradeLevels() throws SQLException {
//        TransactionSynchronizationManager.initSynchronization();
//        Connection c = DataSourceUtils.getConnection(dataSource);
//        c.setAutoCommit(false);
//
//        try {
//            List<User> users = userDao.getAll();
//
//            for (User user : users) {
//                if (canUpgradeLevel(user)) {
//                    upgradeLevel(user);
//                }
//            }
//            c.commit();
//        } catch (SQLException e) {
//            c.rollback();
//            throw e;
//        } finally {
//            DataSourceUtils.releaseConnection(c,dataSource);
//            TransactionSynchronizationManager.unbindResource(this.dataSource);
//            TransactionSynchronizationManager.clearSynchronization();
//        }
//    }
//    public void upgradeLevelsV2() throws SQLException {
//        PlatformTransactionManager transactionManager
//                = new DataSourceTransactionManager(dataSource);
//
//        TransactionStatus status
//                = transactionManager.getTransaction(new DefaultTransactionDefinition());
//
//        try {
//            List<User> users = userDao.getAll();
//
//            for (User user : users) {
//                if (canUpgradeLevel(user)) {
//                    upgradeLevel(user);
//                }
//            }
//            transactionManager.commit(status);
//        } catch (RuntimeException e) {
//            transactionManager.rollback(status);
//            throw e;
//        }
//    }

    // JTA
    public void upgradeLevelsV3() throws SQLException {
        PlatformTransactionManager transactionManager
                = new JtaTransactionManager();

        TransactionStatus status
                = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            List<User> users = userDao.getAll();

            for (User user : users) {
                if (canUpgradeLevel(user)) {
                    upgradeLevel(user);
                }
            }
            transactionManager.commit(status);
        } catch (RuntimeException e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
    public void upgradeLevels() throws SQLException {
        TransactionStatus status
                = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            List<User> users = userDao.getAll();


            for (User user : users) {
                if (canUpgradeLevel(user)) {
                    upgradeLevel(user);
                }
            }
            transactionManager.commit(status);
        } catch (RuntimeException e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    protected void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEmail(user);
    }

    private void sendUpgradeEmail(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@abc.org");
        mailMessage.setSubject("upgare aaa");
        mailMessage.setText("aaaa " + user.getLevel().name());

        mailSender.send(mailMessage);
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

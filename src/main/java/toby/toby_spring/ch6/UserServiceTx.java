package toby.toby_spring.ch6;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
public class UserServiceTx implements UserService {

    UserService userService;
    PlatformTransactionManager transactionManager;

    @Autowired
    public void setUserService(UserService userServiceImpl) {
        this.userService = userServiceImpl;
    }

    @Autowired
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }




    @Override
    public void add(User user) {
        userService.add(user);
    }

    @Override
    public void upgradeLevels() {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            userService.upgradeLevels();
            transactionManager.commit(status);

        } catch (RuntimeException e) {
            transactionManager.rollback(status);
            throw e;
        }

    }
}

package toby.toby_spring.ch6;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import toby.toby_spring.ch6.factory.MessageFactoryBean;
import toby.toby_spring.ch6.factory.TxProxyFactoryBean;

@Configuration
public class BeanConfig {

    @Autowired
    PlatformTransactionManager transactionManager;

    @Bean
    public MessageFactoryBean message() {
        return new MessageFactoryBean("Factory Bean");
    }


    @Bean
    public ProxyFactoryBean userService() {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(new UserServiceImpl());
        proxyFactoryBean.addAdvisor(transactionAdvisor());

        return proxyFactoryBean;
    }
//        @Bean
//    public TxProxyFactoryBean userService() {
//        return new TxProxyFactoryBean(new UserServiceImpl(),
//                transactionManager,
//                "upgradeLevels",
//                UserService.class);
//    }
//
    @Bean
    public NameMatchMethodPointcut transactionPointcut() {
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("upgrade*");

        return pointcut;
    }

    @Bean
    public DefaultPointcutAdvisor transactionAdvisor() {
        return new DefaultPointcutAdvisor(transactionPointcut(), new TransactionAdvice(transactionManager));
    }
}

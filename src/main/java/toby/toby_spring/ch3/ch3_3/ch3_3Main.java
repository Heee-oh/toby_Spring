package toby.toby_spring.ch3.ch3_3;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class ch3_3Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDao userdao = context.getBean("userDao", UserDao.class);
        User user = new User();

        user.setId("whiteship2");
        user.setName("백기선");
        user.setPassword("married");


        userdao.add(user);
        int count = userdao.getCount();
        System.out.println(count);

        userdao.deleteAll();

        count = userdao.getCount();
        System.out.println(count);

    }
}

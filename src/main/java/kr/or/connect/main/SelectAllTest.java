package kr.or.connect.main;

import kr.or.connect.DTO.Role;
import kr.or.connect.config.ApplicationConfig;
import kr.or.connect.dao.RoleDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class SelectAllTest {
    public static void main(String[] args) {
        ApplicationContext ac = new AnnotationConfigApplicationContext(ApplicationConfig.class);

        RoleDao roleDao = ac.getBean(RoleDao.class);

        List<Role> list = roleDao.selectAll();

        for (Role role : list){
            System.out.println(role);
        }
    }
}

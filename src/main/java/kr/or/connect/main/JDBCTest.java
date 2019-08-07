package kr.or.connect.main;

import kr.or.connect.DTO.Role;
import kr.or.connect.config.ApplicationConfig;
import kr.or.connect.dao.RoleDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class JDBCTest {
    public static void main(String[] args) {
        ApplicationContext ac = new AnnotationConfigApplicationContext(ApplicationConfig.class);

        RoleDao roleDao = ac.getBean(RoleDao.class);

        Role role = new Role();
        role.setRoleId(100);
        role.setDescription("PROGRAMMER");

        int count = roleDao.update(role);
        System.out.println(count + "건 수정되었습니다.");
    }
}

package kr.or.connect.main;

import kr.or.connect.config.ApplicationConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;

public class DataSourceTest {
    public static void main(String[] args) {
        ApplicationContext ac = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        DataSource dataSource = ac.getBean(DataSource.class);

        try(Connection conn = dataSource.getConnection()){
            if (conn != null){
                System.out.println("접속 성공");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

Spring JDBC를 이용한 DAO 작성 실습
==================================

1. 스프링 JDBC 실습 환경 설정
* pom.xml 관련 의존성 추가
    * pom.xml에 spring-context, spring-jdbc, spring-tx 추가
        ~~~
            <dependency>
              <groupId>org.springframework</groupId>
              <artifactId>spring-context</artifactId>
              <version>${spring.version}</version>
            </dependency>
            
            <dependency>
              <groupId>org.springframework</groupId>
              <artifactId>spring-jdbc</artifactId>
              <version>${spring.version}</version>
            </dependency>
        
            <dependency>
              <groupId>org.springframework</groupId>
              <artifactId>spring-tx</artifactId>
              <version>${spring.version}</version>
            </dependency>
        ~~~
    * Mysql에서 제공하는 드라이버 추가
        ~~~
            <dependency>
              <groupId>mysql</groupId>
              <artifactId>mysql-connector-java</artifactId>
              <version>5.1.45</version>
            </dependency>
        ~~~
    * Apache에서 제공하는 datasource인 commons-dbcp2 추가
        ~~~
            <dependency>
              <groupId>org.apache.commons</groupId>
              <artifactId>commons-dbcp2</artifactId>
              <version>2.1.1</version>
            </dependency>
        ~~~
* ApplicationConfig.class 생성
    ~~~
    @Configuration
    @Import({DBConfig.class})
    public class ApplicationConfig {
    }
    ~~~  
* DB관련 설정만 따로 해주기 위해 DBConfig.class생성후 ApplicationConfig.class에서 임포트
    ~~~
    @Configuration
    @EnableTransactionManagement
    public class DBConfig {
        private String driverClassName = "com.mysql.jdbc.Driver";
        private String url = "jdbc:mysql://localhost:3306/connectdb?useUnicode=true&characterEncoding=utf8";
    
        private String username = "connectuser";
        private String password = "connect123!@#";
    
        @Bean
        public DataSource dataSource() {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName(driverClassName);
            dataSource.setUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            return dataSource;
    
        }
    }
    ~~~
    * driverClassName, url, username, password는 DB에 접속할때 필요한 정보들이고 dataSource메소드는 커넥션 풀을 관리하는 DataSource를 만들기 위한 메소드이다.
* DataSource가 잘 생성되는지 확인 하기 위해 DataSourceTest.class 실행 클래스 생성 후 실행
    ~~~
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
    ~~~
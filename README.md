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
2. JDBC 실습(SELECT)
* 데이터 전달할 때 사용할 목적으로 DTO 객체 생성
    ~~~
   public class Role {
       private int roleId;
       private String description;
   
       public int getRoleId() {
           return roleId;
       }
   
       public void setRoleId(int roleId) {
           this.roleId = roleId;
       }
   
       public String getDescription() {
           return description;
       }
   
       public void setDescription(String description) {
           this.description = description;
       }
   
       @Override
       public String toString() {
           return "Role{" +
                   "roleID=" + roleId +
                   ", description='" + description + '\'' +
                   '}';
       }
   }
    ~~~
* query 문을 가지고 있는 RoleDaoSqls 클래스 생성
    ~~~
    public class RoleDaoSqls {
        public static final String SELECT_ALL = "SELECT role_id, description FROM role order by role_id";
    }
    ~~~
* DAO에 jdbcTemplate 설정과 selectAll 메소드 추가
    ~~~
    @Repository
    public class RoleDao {
        private NamedParameterJdbcTemplate jdbc;
        private RowMapper<Role> rowMapper = BeanPropertyRowMapper.newInstance(Role.class);
        
        public RoleDao(DataSource dataSource){
            this.jdbc = new NamedParameterJdbcTemplate(dataSource);
        }
        
        public List<Role> selectAll(){
                return jdbc.query(SELECT_ALL, rowMapper);
        }
    }
    ~~~
    * JdbcTemplate은 바인딩할때 ?를 사용해서 sql문자열만 봤을때는 어떤 값이 매핑되는지 알아보기가 힘든 문제점이 있었다.
    그래서 NamedParameterJdbcTemplate을 활용하면 이름을 이용해서 바인딩 하거나, 결과 값을 가져올 때 사용할 수 있다.
    * 기본 생성자가 없다면 자동으로 객체를 주입해주기 때문에 DataSource가 존재한다면 RoleDao생성자의 인자로 자동 주입 된다.
    * jdbc.query()의 첫번째 인자로 RoldeDaoSqls에서 선언했던 sql문을 넣고, 두번째 인자는 selectAll() 메소드를 실행했을 때 복수의 
    레코드들을 담기 위해 BeanPropertyRowMapper를 이용한 rowMapper를 인자로 넣어준다.
* ApplicationConfig.class에서 ComponentScan을 추가해서 RoelDao.class를 빈으로 설정
    * ApplicationConfig클래스에 추가
    ~~~
    @ComponentScan(basePackages = {"kr.or.connect.dao"})
    ~~~
* selectAll메소드가 잘 동작하는지 테스트
    ~~~
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
    ~~~
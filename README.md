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
3. JDBC 실습(INSERT, UPDATE)
* INSERT 쿼리를 실행하기 위해서 RoleDao.class에 SimpleJdbcInsert를 추가
    ~~~
    @Repository
    public class RoleDao {
        private NamedParameterJdbcTemplate jdbc;
        private SimpleJdbcInsert insertAction;
        private RowMapper<Role> rowMapper = BeanPropertyRowMapper.newInstance(Role.class);
    
        public RoleDao(DataSource dataSource){
            this.jdbc = new NamedParameterJdbcTemplate(dataSource);
            this.insertAction = new SimpleJdbcInsert(dataSource).withTableName("role");
        }
    
        public List<Role> selectAll(){
            return jdbc.query(SELECT_ALL, rowMapper);
        }
    }
    ~~~
* INSERT문을 Role.class에 추가
    * 참고로 INSERT문은 따로 RoleDaoSqls.class에 쿼리를 작성하지 않아도된다.
    ~~~
    public int insert(Role role){
            SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(role);
            return insertAction.execute(parameterSource);
        }
    ~~~
    * Role객체를 인자로 받아들여 해당 Role 객체에 있는 값을 웹으로 바꿔준 후 SimpleJdbcInsert
    에 바꿔준 값을 execute메소드의 인자로 전달하면 값이 저장되게 된다.
* INSERT문이 잘 실행되는지 테스트
    ~~~
     public class JDBCTest {
        public static void main(String[] args) {
            ApplicationContext ac = new AnnotationConfigApplicationContext(ApplicationConfig.class);
    
            RoleDao roleDao = ac.getBean(RoleDao.class);
    
            Role role = new Role();
            role.setRoleId(3001);
            role.setDescription("CEO");
    
            int count = roleDao.insert(role);
            System.out.println(count + "건 입력하였습니다.");
        }
    }
    ~~~
* UPDATE문을 실행하기 위해 RoleDaoSqls.class에 쿼리 추가
    ~~~
    public static final String UPDATE = "UPDATE role SET description = :description WHERE ROLE_ID = :roleId";
    ~~~
    * 쿼리문 안에 :description과 :roleId이 나중에 값으로 바인딩 될 부분이다.
* RoleDao.class에 UPDATE문 작성
    ~~~
     public int update(Role role){
            SqlParameterSource params = new BeanPropertySqlParameterSource(role);
            return jdbc.update(UPDATE, params);
        }
    ~~~
* UPDATE문 테스트
    ~~~
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
    ~~~
4. JDBC 실습 (1건 SELECT, DELETE)
* RoleDaoSqls.class에 1건 SELECT하는 쿼리와 DELETE하는 쿼리문을 추가
    ~~~
    public static final String SELECT_BY_ROLE_ID = "SELECT role_id, description FROM role where role_id = :roleId";
    public static final String DELETE_BY_ROLE_ID = "DELETE FROM role WHERE role_id = :roleId";
    ~~~
    * 쿼리문을 작성할때 모든 컬럼을 가져온다해도 *보다는 각각의 컬럼명을 나열해주는것이 훨씬 의미 전달이 명확하기 때문에 각각의 컬럼명을 나열하는것을 지향한다.
* RoleDao.class에 1건 SELECT하는 메서드와 DELETE하는 메서드를 추가
    ~~~
    public int deleteById(Integer id){
            Map<String, ?> params = Collections.singletonMap("roleId", id);
            return jdbc.update(DELETE_BY_ROLE_ID, params);
        }
    ~~~
    * Collection.singletonMap 메소드를 사용하는 이유는 바인딩되는값이 한가지 밖에 없기 때문에 다른 메소드처럼 SqlParameterSource를 사용할 필요는 없다.
    ~~~
        public Role select(Integer id){
            try {
                Map<String, ?> params = Collections.singletonMap("roleId", id);
                return jdbc.queryForObject(SELECT_BY_ROLE_ID, params, rowMapper);
            } catch(EmptyResultDataAccessException e){
                return null;
            }
        }
    ~~~
    * 1건 SELECT 할 때는 queryForObject 메소드를 사용한다.
* 테스트
    ~~~
    public class JDBCTest {
        public static void main(String[] args) {
            ApplicationContext ac = new AnnotationConfigApplicationContext(ApplicationConfig.class);
    
            RoleDao roleDao = ac.getBean(RoleDao.class);
    
            Role role = roleDao.select(101);
            System.out.println(role);
    
            int count = roleDao.deleteById(3001);
            System.out.println(count + "건 삭제 했습니다.");
        }
    }
    ~~~
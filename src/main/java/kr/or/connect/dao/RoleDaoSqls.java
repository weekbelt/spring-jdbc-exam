package kr.or.connect.dao;

public class RoleDaoSqls {
    public static final String SELECT_ALL = "SELECT role_id, description FROM role order by role_id";
    public static final String UPDATE = "UPDATE role SET description = :description WHERE ROLE_ID = :roleId";
}

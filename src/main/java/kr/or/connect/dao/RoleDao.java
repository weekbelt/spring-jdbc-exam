package kr.or.connect.dao;

import kr.or.connect.DTO.Role;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static kr.or.connect.dao.RoleDaoSqls.*;

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

    public int insert(Role role){
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(role);
        return insertAction.execute(parameterSource);
    }

    public int update(Role role){
        SqlParameterSource params = new BeanPropertySqlParameterSource(role);
        return jdbc.update(UPDATE, params);
    }

    public int deleteById(Integer id){
        Map<String, ?> params = Collections.singletonMap("roleId", id);
        return jdbc.update(DELETE_BY_ROLE_ID, params);
    }

    public Role select(Integer id){
        try {
            Map<String, ?> params = Collections.singletonMap("roleId", id);
            return jdbc.queryForObject(SELECT_BY_ROLE_ID, params, rowMapper);
        } catch(EmptyResultDataAccessException e){
            return null;
        }
    }
}

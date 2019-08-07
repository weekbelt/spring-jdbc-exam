package kr.or.connect.DTO;

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

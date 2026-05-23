package beans.common;

import java.sql.Timestamp;

public class RolePermission {
    private int roleId;
    private int permissionId;
    private boolean isActive;
    private Timestamp createdAt;

    public RolePermission() {
    }

    public RolePermission(int roleId, int permissionId, boolean isActive, Timestamp createdAt) {
        this.roleId = roleId;
        this.permissionId = permissionId;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}

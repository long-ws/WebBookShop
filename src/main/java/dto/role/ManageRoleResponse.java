package dto.role;

import java.sql.Timestamp;
import java.util.List;

import dto.permission.ManagePermissionResponse;

public class ManageRoleResponse {
    private final Integer id;
    private final String code;
    private final String name;
    private final String description;
    private final Boolean isSystem;
    private final Boolean isActive;
    private final List<ManagePermissionResponse> permissions;
    private final Timestamp createdAt;
    private final Timestamp updatedAt;

    private ManageRoleResponse(Builder builder) {
        this.id = builder.id;
        this.code = builder.code;
        this.name = builder.name;
        this.description = builder.description;
        this.isSystem = builder.isSystem;
        this.isActive = builder.isActive;
        this.permissions = builder.permissions;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public List<ManagePermissionResponse> getPermissions() {
        return permissions;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public static class Builder {
        private Integer id;
        private String code;
        private String name;
        private String description;
        private Boolean isSystem;
        private Boolean isActive;
        private List<ManagePermissionResponse> permissions;
        private Timestamp createdAt;
        private Timestamp updatedAt;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder isSystem(Boolean isSystem) {
            this.isSystem = isSystem;
            return this;
        }

        public Builder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder permissions(List<ManagePermissionResponse> permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder createdAt(Timestamp createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Timestamp updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public ManageRoleResponse build() {
            return new ManageRoleResponse(this);
        }
    }
}
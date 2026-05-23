package dto.role;

import java.util.List;

public class RoleCreateRequest {
    private final Integer id;
    private final String code;
    private final String name;
    private final String description;
    private final Boolean isSystem;
    private final Boolean isActive;
    private final List<Integer> permissionIds;

    private RoleCreateRequest(Builder builder) {
        this.id = builder.id;
        this.code = builder.code;
        this.name = builder.name;
        this.description = builder.description;
        this.isSystem = builder.isSystem;
        this.isActive = builder.isActive;
        this.permissionIds = builder.permissionIds;
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

    public List<Integer> getPermissionIds() {
        return permissionIds;
    }

    public static class Builder {
        private Integer id;
        private String code;
        private String name;
        private String description;
        private Boolean isSystem;
        private Boolean isActive;
        private List<Integer> permissionIds;

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

        public Builder permissionIds(List<Integer> permissionIds) {
            this.permissionIds = permissionIds;
            return this;
        }

        public RoleCreateRequest build() {
            return new RoleCreateRequest(this);
        }
    }
}
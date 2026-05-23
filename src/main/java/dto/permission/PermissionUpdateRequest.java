package dto.permission;

import java.sql.Timestamp;

public class PermissionUpdateRequest {
    private final Integer id;
    private final String code;
    private final String name;
    private final String description;
    private final String module;
    private final Boolean isSystem;
    private final Boolean isActive;
    private final Timestamp createdAt;

    private PermissionUpdateRequest(Builder builder) {
        this.id = builder.id;
        this.code = builder.code;
        this.name = builder.name;
        this.description = builder.description;
        this.module = builder.module;
        this.isSystem = builder.isSystem;
        this.isActive = builder.isActive;
        this.createdAt = builder.createdAt;
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

    public String getModule() {
        return module;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public static class Builder {
        private Integer id;
        private String code;
        private String name;
        private String description;
        private String module;
        private Boolean isSystem;
        private Boolean isActive;
        private Timestamp createdAt;

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

        public Builder module(String module) {
            this.module = module;
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

        public Builder createdAt(Timestamp createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PermissionUpdateRequest build() {
            return new PermissionUpdateRequest(this);
        }
    }
}
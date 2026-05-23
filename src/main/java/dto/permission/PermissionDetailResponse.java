package dto.permission;

import java.sql.Timestamp;

public class PermissionDetailResponse {
    private final int id;
    private final String code;
    private final String name;
    private final String description;
    private final String module;
    private final boolean isSystem;
    private final boolean isActive;
    private final Timestamp createdAt;

    private PermissionDetailResponse(Builder builder) {
        this.id = builder.id;
        this.code = builder.code;
        this.name = builder.name;
        this.description = builder.description;
        this.module = builder.module;
        this.isSystem = builder.isSystem;
        this.isActive = builder.isActive;
        this.createdAt = builder.createdAt;
    }

    public int getId() {
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

    public boolean isSystem() {
        return isSystem;
    }

    public boolean isActive() {
        return isActive;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public static class Builder {
        private int id;
        private String code;
        private String name;
        private String description;
        private String module;
        private boolean isSystem;
        private boolean isActive;
        private Timestamp createdAt;

        public Builder id(int id) {
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

        public Builder isSystem(boolean isSystem) {
            this.isSystem = isSystem;
            return this;
        }

        public Builder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder createdAt(Timestamp createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PermissionDetailResponse build() {
            return new PermissionDetailResponse(this);
        }
    }
}
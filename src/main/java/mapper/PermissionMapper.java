package mapper;

import beans.common.Permission;
import dto.permission.ManagePermissionResponse;
import dto.permission.PermissionCreateRequest;
import dto.permission.PermissionDetailResponse;
import dto.permission.PermissionUpdateRequest;

public class PermissionMapper {

    public PermissionDetailResponse toPermissionDetailResponse(Permission permission) {
        if (permission == null) {
            return null;
        }

        return new PermissionDetailResponse.Builder()
                .id(permission.getId())
                .code(permission.getCode())
                .name(permission.getName())
                .description(permission.getDescription())
                .module(permission.getModule())
                .isSystem(permission.isSystem())
                .isActive(permission.isActive())
                .createdAt(permission.getCreatedAt())
                .build();
    }

    public ManagePermissionResponse toManagePermissionResponse(Permission permission) {
        if (permission == null) {
            return null;
        }

        return new ManagePermissionResponse.Builder()
                .id(permission.getId())
                .code(permission.getCode())
                .name(permission.getName())
                .description(permission.getDescription())
                .module(permission.getModule())
                .isSystem(permission.isSystem())
                .isActive(permission.isActive())
                .createdAt(permission.getCreatedAt())
                .build();
    }

    public PermissionCreateRequest toPermissionCreateRequest(PermissionDetailResponse dto) {
        if (dto == null) {
            return null;
        }

        return new PermissionCreateRequest.Builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .module(dto.getModule())
                .isSystem(dto.isSystem())
                .isActive(dto.isActive())
                .build();
    }

    public PermissionUpdateRequest toPermissionUpdateRequest(PermissionDetailResponse dto) {
        if (dto == null) {
            return null;
        }

        return new PermissionUpdateRequest.Builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .module(dto.getModule())
                .isSystem(dto.isSystem())
                .isActive(dto.isActive())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    public Permission toEntity(PermissionCreateRequest dto) {
        if (dto == null) {
            return null;
        }

        Permission permission = new Permission();
        permission.setCode(dto.getCode());
        permission.setName(dto.getName());
        permission.setDescription(dto.getDescription());
        permission.setModule(dto.getModule());
        permission.setSystem(dto.getIsSystem() != null ? dto.getIsSystem() : false);
        permission.setActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return permission;
    }

    public Permission toEntity(PermissionUpdateRequest dto) {
        if (dto == null) {
            return null;
        }

        Permission permission = new Permission();
        permission.setId(dto.getId());
        permission.setCode(dto.getCode());
        permission.setName(dto.getName());
        permission.setDescription(dto.getDescription());
        permission.setModule(dto.getModule());
        permission.setSystem(dto.getIsSystem() != null ? dto.getIsSystem() : false);
        permission.setActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return permission;
    }
}
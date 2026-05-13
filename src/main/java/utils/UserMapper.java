package utils;

import beans.User;
import dto.AdminUserDetailDTO;
import dto.UserCreateUpdateFormDTO;
import dto.AdminUserListDTO;

public final class UserMapper {

	private UserMapper() {
		throw new UnsupportedOperationException("Chặn khởi tạo constructor");
	}

	public static AdminUserDetailDTO toAdminUserDetailDTO(User user) {
		if (user == null) {
			return null;
		}

		AdminUserDetailDTO dto = new AdminUserDetailDTO();
		dto.setId(user.getId());
		dto.setUsername(user.getUsername());
		dto.setEmail(user.getEmail());

		if (user.getProfile() != null) {
			dto.setFullname(user.getProfile().getFullname());
			dto.setPhoneNumber(user.getProfile().getPhoneNumber());
			dto.setAvatarUrl(user.getProfile().getAvatarUrl());
			if (user.getProfile().getGender() != null) {
				dto.setGender(user.getProfile().getGender());
			}
			if (user.getProfile().getPreferredLanguage() != null) {
				dto.setPreferredLanguage(user.getProfile().getPreferredLanguage());
			}
		}

		dto.setRole(user.getRole());
		dto.setStatus(user.getStatus());
		dto.setTokenVersion(user.getTokenVersion());
		dto.setLastLoginAt(user.getLastLoginAt());
		dto.setCreatedAt(user.getCreatedAt());
		dto.setUpdatedAt(user.getUpdatedAt());
		dto.setDeleted(user.isDeleted());
		dto.setLocked(user.isLocked());
		return dto;
	}

	public static AdminUserListDTO toAdminUserListDTO(User user) {
		if (user == null) {
			return null;
		}

		AdminUserListDTO dto = new AdminUserListDTO();
		dto.setId(user.getId());
		dto.setUsername(user.getUsername());
		dto.setEmail(user.getEmail());

		if (user.getProfile() != null) {
			dto.setFullname(user.getProfile().getFullname());
			dto.setPhoneNumber(user.getProfile().getPhoneNumber());
			dto.setGender(user.getProfile().getGender());
		}

		String roleCode = user.getRole() != null ? user.getRole().getCode() : null;
		dto.setRole(roleCode);
		dto.setStatus(user.getStatus());
		dto.setCreatedAt(user.getCreatedAt());
		return dto;
	}

	public static UserCreateUpdateFormDTO toUserCreateUpdateFormDTO(AdminUserDetailDTO dto) {
		if (dto == null) {
			return null;
		}

		UserCreateUpdateFormDTO formDTO = new UserCreateUpdateFormDTO();
		formDTO.setId(dto.getId());
		formDTO.setUsername(dto.getUsername());
		formDTO.setFullname(dto.getFullname());
		formDTO.setEmail(dto.getEmail());
		formDTO.setPhoneNumber(dto.getPhoneNumber());
		formDTO.setGender(dto.getGender());
		formDTO.setPreferredLanguage(dto.getPreferredLanguage());
		formDTO.setRole(dto.getRole());
		formDTO.setAvatarUrl(dto.getAvatarUrl());
		return formDTO;
	}
}

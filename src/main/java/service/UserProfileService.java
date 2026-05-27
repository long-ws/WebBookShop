package service;

import beans.User;
import dto.user.UserProfileRequest;
import dto.user.UserProfileResponse;
import exception.BusinessException;

public interface UserProfileService {

	UserProfileResponse getUserProfile(long userId) throws BusinessException;

	UserProfileResponse updateUserProfile(long userId, UserProfileRequest request) throws BusinessException;

	User getById(long id) throws BusinessException;
}
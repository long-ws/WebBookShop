<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="constants.SystemConstants, constants.RequestParamConstants, constants.ViewAttributeConstants, constants.SessionConstants" %>

<c:set var="SSN_OAUTH_ERROR" value="<%= SessionConstants.OAUTH_ERROR %>" />
<c:set var="SSN_SIGNUP_SUCCESS" value="<%= SessionConstants.SIGNUP_SUCCESS %>" />
<c:set var="SSN_SUCCESS_MESSAGE" value="<%= SessionConstants.SUCCESS_MESSAGE %>" />
<c:set var="SSN_ERROR_MESSAGE" value="<%= SessionConstants.ERROR_MESSAGE %>" />
<c:set var="P_OAUTH_PROVIDER" value="<%= RequestParamConstants.OAuth.PROVIDER %>" />
<c:set var="P_ID" value="<%= RequestParamConstants.ID %>" />
<c:set var="P_CODE" value="<%= RequestParamConstants.CODE %>" />
<c:set var="P_NAME" value="<%= RequestParamConstants.NAME %>" />
<c:set var="P_DESCRIPTION" value="<%= RequestParamConstants.DESCRIPTION %>" />
<c:set var="P_MODULE" value="<%= RequestParamConstants.MODULE %>" />
<c:set var="P_IS_SYSTEM" value="<%= RequestParamConstants.IS_SYSTEM %>" />
<c:set var="P_IS_ACTIVE" value="<%= RequestParamConstants.IS_ACTIVE %>" />
<c:set var="P_USERNAME" value="<%= RequestParamConstants.User.USERNAME %>" />
<c:set var="P_PASSWORD" value="<%= RequestParamConstants.User.PASSWORD %>" />
<c:set var="P_FULLNAME" value="<%= RequestParamConstants.User.FULLNAME %>" />
<c:set var="P_EMAIL" value="<%= RequestParamConstants.User.EMAIL %>" />
<c:set var="P_PHONE_NUMBER" value="<%= RequestParamConstants.User.PHONE_NUMBER %>" />
<c:set var="P_GENDER" value="<%= RequestParamConstants.User.GENDER %>" />
<c:set var="P_ROLE" value="<%= RequestParamConstants.User.ROLE %>" />
<c:set var="P_PREFERRED_LANGUAGE_ID" value="<%= RequestParamConstants.User.PREFERRED_LANGUAGE_ID %>" />
<c:set var="P_USER_IDS" value="<%= RequestParamConstants.User.USER_IDS %>" />
<c:set var="P_ROLE_ID" value="<%= RequestParamConstants.Role.ROLE_ID %>" />
<c:set var="P_ROLE_IDS" value="<%= RequestParamConstants.Role.ROLE_IDS %>" />
<c:set var="P_PERMISSION_IDS" value="<%= RequestParamConstants.Permission.PERMISSION_IDS %>" />
<c:set var="P_ASSIGNED_ROLE_IDS" value="<%= RequestParamConstants.Role.ASSIGNED_ROLE_IDS %>" />
<c:set var="ERR_GLOBAL" value="<%= SystemConstants.ERROR_GLOBAL %>" />
<c:set var="ATTR_ERRORS" value="<%= ViewAttributeConstants.ERRORS %>" />
<c:set var="ATTR_VALUES" value="<%= ViewAttributeConstants.VALUES %>" />
<c:set var="ATTR_ALL_ROLES" value="<%= ViewAttributeConstants.User.ALL_ROLES %>" />
<c:set var="ATTR_LANGUAGES" value="<%= ViewAttributeConstants.User.LANGUAGES %>" />
<c:set var="ATTR_USERS" value="<%= ViewAttributeConstants.User.USERS %>" />
<c:set var="ATTR_USER" value="<%= ViewAttributeConstants.User.USER %>" />
<c:set var="ATTR_HAS_USER_CREATE" value="<%= ViewAttributeConstants.User.HAS_CREATE %>" />
<c:set var="ATTR_HAS_USER_EDIT" value="<%= ViewAttributeConstants.User.HAS_EDIT %>" />
<c:set var="ATTR_HAS_USER_DELETE" value="<%= ViewAttributeConstants.User.HAS_DELETE %>" />
<c:set var="ATTR_IS_SYSTEM_USER" value="<%= ViewAttributeConstants.User.IS_SYSTEM %>" />

<c:set var="ATTR_TOTAL_USERS" value="<%= ViewAttributeConstants.Dashboard.TOTAL_USERS %>" />
<c:set var="ATTR_TOTAL_CATEGORIES" value="<%= ViewAttributeConstants.Dashboard.TOTAL_CATEGORIES %>" />
<c:set var="ATTR_TOTAL_PRODUCTS" value="<%= ViewAttributeConstants.Dashboard.TOTAL_PRODUCTS %>" />
<c:set var="ATTR_TOTAL_ORDERS" value="<%= ViewAttributeConstants.Dashboard.TOTAL_ORDERS %>" />

<c:set var="ATTR_ROLES" value="<%= ViewAttributeConstants.Role.ROLES %>" />
<c:set var="ATTR_ROLE" value="<%= ViewAttributeConstants.Role.ROLE %>" />
<c:set var="ATTR_ALL_PERMISSIONS" value="<%= ViewAttributeConstants.Role.ALL_PERMISSIONS %>" />
<c:set var="ATTR_ROLE_PERMISSIONS" value="<%= ViewAttributeConstants.Role.ROLE_PERMISSIONS %>" />
<c:set var="ATTR_PERMISSION_ROLE_MAP" value="<%= ViewAttributeConstants.Role.PERMISSION_ROLE_MAP %>" />
<c:set var="ATTR_HAS_ROLE_CREATE" value="<%= ViewAttributeConstants.Role.HAS_CREATE %>" />
<c:set var="ATTR_HAS_ROLE_EDIT" value="<%= ViewAttributeConstants.Role.HAS_EDIT %>" />
<c:set var="ATTR_HAS_ROLE_DELETE" value="<%= ViewAttributeConstants.Role.HAS_DELETE %>" />

<c:set var="ATTR_PERMISSION" value="<%= ViewAttributeConstants.Permission.PERMISSION %>" />
<c:set var="ATTR_MODULES" value="<%= ViewAttributeConstants.Permission.MODULES %>" />
<c:set var="ATTR_PERMISSIONS_BY_MODULE" value="<%= ViewAttributeConstants.Permission.PERMISSIONS_BY_MODULE %>" />
<c:set var="ATTR_HAS_PERMISSION_CREATE" value="<%= ViewAttributeConstants.Permission.HAS_CREATE %>" />
<c:set var="ATTR_HAS_PERMISSION_EDIT" value="<%= ViewAttributeConstants.Permission.HAS_EDIT %>" />
<c:set var="ATTR_HAS_PERMISSION_DELETE" value="<%= ViewAttributeConstants.Permission.HAS_DELETE %>" />

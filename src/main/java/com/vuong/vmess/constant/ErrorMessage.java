package com.vuong.vmess.constant;

public class ErrorMessage {
    public static class Validation {
        public static final String NOT_BLANK_FIELD = "This field can't be blank";
    }

    public static class Auth {
        public static final String UNAUTHORIZED = "Sorry, you needs to provide authentication credentials to access this resource";
        public static final String ERR_CHANGE_PASSWORD_FIST_TIME_LOGIN = "";
        public static final String ERR_INCORRECT_USERNAME = "Username is incorrect";
        public static final String ERR_INCORRECT_PASSWORD = "Password is incorrect";
        public static final String ERR_INCORRECT_ACCOUNT  = "Username or password is incorrect";
        public static final String ERR_ACCOUNT_NOT_ENABLED = "";
        public static final String ERR_ACCOUNT_LOCKED = "";
        public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";
        public static final String EXPIRED_REFRESH_TOKEN = "";
        public static final String YOU_DONT_HAVE_PERMIT = "";
    }

    public static class User {
        public static final String ERR_NOT_FOUND_USERNAME = "User not found with this username";
        public static final String ERR_DUPLICATED_USERNAME = "Username already exists";
        public static final String ERR_DUPLICATED_EMAIL = "This email has already been used";
    }
}

package com.vuong.vmess.constant;

public class UrlConstant {
    public static class Auth {
        private static final String PRE_FIX = "/auth";
        public static final String LOGIN = PRE_FIX + "/login";
        public static final String REGISTER = PRE_FIX + "/register";
        public static final String LOGOUT = PRE_FIX + "/logout";
        public static final String REFRESHTOKEN = PRE_FIX + "/refreshToken";
        private Auth() {
        }
    }
}

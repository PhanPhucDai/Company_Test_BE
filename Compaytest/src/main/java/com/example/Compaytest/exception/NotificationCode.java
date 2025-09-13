package com.example.Compaytest.exception;

public enum NotificationCode {
    // login
    AUTH_LOGIN_SUCCESS("AUTH_001", "Đăng nhập thành công"),
    AUTH_LOGIN_FAIL("AUTH_002", "Email hoặc mật khẩu không đúng"),

    // logout
    AUTH_LOGOUT_SUCCESS("AUTH_OUT_001", "Đăng xuất thành công"),

    // User
    USER_CREATE_SUCCESS("USER_001", "Tài khoản đã được tạo thành công"),
    USER_FOUND("USER_002", "Người dùng đã tìm thấy"),
    USER_NOT_FOUND("USER_003", "Người dùng không tồn tại"),

     // Common
    INTERNAL_SERVER_ERROR("COMMON_500", "Lỗi hệ thống, vui lòng thử lại sau"),
    VALIDATION_FAILED("COMMON_500", "Dữ liệu đầu vào không hợp lệ"),

    //Post
    POST_CREATE_SUCCESS("POST_001", "Post đã được tạo thành công"),
    POST_UPDATE_SUCCESS("POST_002", "Post đã được cập nhật thành công"),
    POST_DELETE_SUCCESS("POST_003", "Post đã được xóa thành công"),
    POST_FOUND("POST_004", "Post đã tìm thấy"),
    POST_NOT_FOUND("POST_005", "Post không tồn tại");


    private final String code;
    private final String message;

    NotificationCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


}


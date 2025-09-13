package com.example.Compaytest.service;

import com.example.Compaytest.exception.NotificationCode;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ServiceResponse<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;

    public ServiceResponse(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ServiceResponse(boolean success, String code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = null;
    }


    // Static helper để tạo response thành công
    public static <T> ServiceResponse<T> success(NotificationCode code, T data) {
        return new ServiceResponse<>(true, code.getCode(), code.getMessage(), data);
    }

    // Static helper để tạo response thành công
    public static <T> ServiceResponse<T> success(NotificationCode code) {
        return new ServiceResponse<>(true, code.getCode(), code.getMessage());
    }

    // Static helper để tạo response lỗi
    public static <T> ServiceResponse<T> error(NotificationCode code) {
        return new ServiceResponse<>(false, code.getCode(), code.getMessage(), null);
    }

}

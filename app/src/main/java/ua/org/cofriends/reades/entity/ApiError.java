package ua.org.cofriends.reades.entity;

import com.google.gson.annotations.Expose;

public class ApiError {

    @Expose
    private final String detail;

    public ApiError(String detail) {
        this.detail = detail;
    }

    @SuppressWarnings("unused")
    private ApiError() {
        this(null);
    }

    public String getDetail() {
        return detail;
    }

    @Override
    public String toString() {
        return "ApiError{" +
                "detail='" + detail + '\'' +
                '}';
    }
}

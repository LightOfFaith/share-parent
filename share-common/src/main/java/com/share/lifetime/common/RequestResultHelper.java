package com.share.lifetime.common;

public class RequestResultHelper<T> {

    private static final String SUCCESS = "000000";
    private static final String SUCCESS_MSG = "SUCCESS";
    private static final String FAILURE = "999999";
    private static final String FAILURE_MSG = "FAILURE";

    public static <T> BasicRequestResult<T> success() {
        return success(SUCCESS_MSG, null);
    }

    public static <T> BasicRequestResult<T> success(String msg) {
        return success(msg, null);
    }

    public static <T> BasicRequestResult<T> success(T result) {
        return success(SUCCESS_MSG, result);
    }

    public static <T> BasicRequestResult<T> success(String msg, T result) {
        BasicRequestResult<T> requestResult = new BasicRequestResult<T>();
        requestResult.setCode(SUCCESS);
        requestResult.setMsg(msg);
        requestResult.setResult(result);
        return requestResult;
    }

    public static <T> BasicRequestResult<T> failure() {
        return failure(FAILURE_MSG);
    }

    public static <T> BasicRequestResult<T> failure(String message) {
        BasicRequestResult<T> requestResult = new BasicRequestResult<T>();
        requestResult.setCode(FAILURE);
        requestResult.setMsg(message);
        return requestResult;
    }

    public static <T> APIRequestResult<T> apiSuccess() {
        return apiSuccess(SUCCESS, SUCCESS_MSG, null);
    }

    public static <T> APIRequestResult<T> apiSuccess(T result) {
        return apiSuccess(SUCCESS, SUCCESS_MSG, result);
    }

    public static <T> APIRequestResult<T> apiSuccess(String code, String message) {
        return apiSuccess(code, message, null);
    }

    public static <T> APIRequestResult<T> apiSuccess(String code, String message, T result) {
        APIRequestResult<T> requestResult = new APIRequestResult<T>();
        requestResult.setCode(SUCCESS);
        requestResult.setMsg(SUCCESS_MSG);
        requestResult.setSubCode(code);
        requestResult.setSubMsg(message);
        requestResult.setResult(result);
        return requestResult;
    }

    public static <T> boolean isSuccess(AbstractRequestResult<T> requestResult) {
        return requestResult != null && requestResult.getCode() == SUCCESS;

    }

    public static <T> boolean isFailure(AbstractRequestResult<T> requestResult) {
        return requestResult != null && requestResult.getCode() == FAILURE;
    }

}

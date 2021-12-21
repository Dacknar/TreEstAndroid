package com.uni.treest.database;

public interface AsyncTaskCallback<T> {
    void handleResponse(T response);
    void handleError(Exception e);
}

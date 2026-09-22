package com.graphicdesigncoding.learnapp.repository;

public class Resource<T> {

    public enum Status { SUCCESS, ERROR, LOADING }

    public final Status status;
    public final T data;
    public final String message;

    private Resource(Status status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> success(T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String msg) {
        return error((Object) msg);
    }

    public static <T> Resource<T> error(Object obj) {
        if (obj == null) return new Resource<>(Status.ERROR, null, "Unknown error");
        String raw = obj.toString().trim();
        try {
            if (raw.startsWith("{") && raw.endsWith("}")) {
                org.json.JSONObject json = new org.json.JSONObject(raw);
                if (json.has("error")) {
                    return new Resource<>(Status.ERROR, null, json.getString("error"));
                }
            }
        } catch (Exception ignored) {}
        return new Resource<>(Status.ERROR, null, raw);
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null);
    }
}

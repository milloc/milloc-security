package com.milloc.security.dto;

import lombok.Data;

/**
 * @author gongdeming
 * @date 2020-01-15
 */
@Data
public class Res<T> {
    private static final int CODE_SUCCESS = 200;
    private static final int CODE_ERROR = 500;

    private int code;
    private T data;
    private String message;

    public static <T> Res<T> ok(T data) {
        Res<T> res = new Res<>();
        res.setCode(CODE_SUCCESS);
        res.setData(data);
        return res;
    }

    public static Res ok() {
        return ok(null);
    }

    public static Res err(String message) {
        Res res = new Res<>();
        res.setCode(CODE_ERROR);
        res.setMessage(message);
        return res;
    }

    public static Res err() {
        return err(null);
    }
}

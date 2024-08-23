package com.maplestory.onecard.vo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResponseJson<T> {
    /**
     * 交易结果代码
     */
    private String code;
    /**
     * 交易反馈信息
     */
    private String msg;
    /**
     * 总条数
     */
    private int total;
    /**
     * 数据
     */
    private T data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ResponseJson() {
        this.code = "000";
        this.msg = "成功";
    }

    public ResponseJson(T data) {
        this.code = "000";
        this.msg = "成功";
        this.setData(data);
    }

    public ResponseJson(String code) {
        super();
        this.code = code;
    }

    public ResponseJson(String code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }

    public ResponseJson(String code, String msg, T data) {
        super();
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ResponseJson<T> warn(String msg) {
        return new ResponseJson<T>("300", msg);
    }

    public static <T> ResponseJson<T> error(String msg) {
        return new ResponseJson<T>("500", msg);
    }

    public static <T> ResponseJson<T> failure(String code, String msg) {
        return new ResponseJson<T>(code, msg);
    }

    /**
     * 请求体有问题
     *
     * @param msg 报错信息
     * @param <T> 类型
     * @return 错误对象
     */
    public static <T> ResponseJson<T> requestError(String msg) {
        return new ResponseJson<T>("400", msg);
    }

    public static <T> ResponseJson<T> ok() {
        return new ResponseJson<>();
    }

    public static <T> ResponseJson<T> ok(T data) {
        if (null == data) {
            return ResponseJson.ok();
        }
        ResponseJson<T> responseJson = new ResponseJson<>();
        responseJson.setData(data);
        return responseJson;
    }
}

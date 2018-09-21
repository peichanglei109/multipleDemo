package com.leige.demo1.model;

/**
 * 控制要求
 *
 * @author peichanglei
 * @date 2018/9/21 15:16
 */

public class ControlRequirement {
    private Long id;
    private String content;
    private String code;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

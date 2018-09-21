package com.leige.demo1.constants;

/**
 * 任务生成检出记录,导出
 * @Author: peichanglei
 * @Date: 2018/6/16 18:20
 */
public enum CatalogueAndFontEnum {
    /**
     * 一级目录对应的字号
     */
    ONE(1,22f),
    /**
     * 二级目录
     */
    TOW(2,16f),
    THREE(3,15f),
    FOUR(4,14f),
    FIVE(5,13f),
    SIX(6,11f),
    /**
     * 正文字号
     */
    OTHER(7,11f),
    /**
     * 其他内容对应字号
     */
    CONTENT(8,11f);

    CatalogueAndFontEnum(int name, float message) {
        this.name = name;
        this.message = message;
    }

    private final int name;
    private final float message;

    public float getMessage() {
        return message;
    }

    public int getName() {
        return name;
    }
}

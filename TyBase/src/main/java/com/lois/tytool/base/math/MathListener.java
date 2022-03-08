package com.lois.tytool.base.math;

/**
 * @Description 算数监听
 * @Author Luo.T.Y
 * @Date 2022/1/21 16:09
 */
public interface MathListener {
    /**
     * 监听公式中配置的属性变量。
     * 当公式的数据均是占位符配置时，通过这个接口。将解析的KEY传入，返回真实的数据
     * @param key 公式中的KEY
     * @return KEY对应的真实数据
     */
    String parsingFormulaProperties(String key);
}

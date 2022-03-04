package com.lois.unclassifiedj.generator;

import com.lois.unclassifiedj.generator.enumeration.GeneratorEnum;
import com.lois.unclassifiedj.generator.impl.MybatisGenerator;

/**
 * GeneratorFactory
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class GeneratorFactory {


    public static Generator buildFactory(GeneratorEnum generatorEnum) {
        Generator generator = null;
        switch (generatorEnum) {
            case MYBATIS_GENERATOR:
                generator = new MybatisGenerator();
                break;
            default:
                break;
        }
        return generator;
    }

}

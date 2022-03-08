package com.lois.tytool.base.jdbc.callback;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * 数据量回调接口
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
@FunctionalInterface
public interface DbCallBack {

    /**
     * 数据库元数据操作
     * @param db
     * @throws SQLException
     */
    void call(DatabaseMetaData db) throws SQLException;
}

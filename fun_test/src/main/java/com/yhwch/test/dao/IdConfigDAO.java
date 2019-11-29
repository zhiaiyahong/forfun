package com.yhwch.test.dao;

import com.yhwch.idgen.model.IDConfigModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author pengweichao
 * @date 2019/8/19
 */
public interface IdConfigDAO {

    List<IDConfigModel> selectByIsDel(@Param("del") Integer isDel);

    List<IDConfigModel> selectByVersion(@Param("version") Long version);

    Long selectMaxVersion();

    Integer updateByBizName(@Param("bizName") String bizName);

    IDConfigModel selectByBizName(@Param("bizName") String bizName);
}

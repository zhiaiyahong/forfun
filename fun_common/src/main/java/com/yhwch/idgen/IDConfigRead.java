package com.yhwch.idgen;


import com.yhwch.idgen.model.IDConfigModel;

import java.util.List;

public interface IDConfigRead {

    /**
     * 获取当前所有正常的配置
     * @return
     */
    List<IDConfigModel> getAllNormalIdConfig();

    /**
     * 获取大于当前版本的配置信息
     * @param version
     * @return
     */
    List<IDConfigModel> getIdConfigByVersion(long version);

    /**
     * 获取最新版本号
     * @return
     */
    Long getMaxVersion();

    /**
     * 更新最大值,并返回最新结果 两个操作需要在一个事务里
     */
    IDConfigModel updateMaxValueAndGet(String bizName);
}

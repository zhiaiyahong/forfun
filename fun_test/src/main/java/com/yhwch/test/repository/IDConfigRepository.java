package com.yhwch.test.repository;

import com.yhwch.idgen.IDConfigRead;
import com.yhwch.idgen.model.IDConfigModel;
import com.yhwch.test.dao.IdConfigDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("iDConfigRepository")
public class IDConfigRepository implements IDConfigRead {

    @Autowired
    private IdConfigDAO idConfigDAO;

    /**
     * 获取当前所有正常的配置
     *
     * @return
     */
    @Override
    public List<IDConfigModel> getAllNormalIdConfig() {
        return idConfigDAO.selectByIsDel(0);
    }

    /**
     * 获取大于当前版本的配置信息
     *
     * @param version
     * @return
     */
    @Override
    public List<IDConfigModel> getIdConfigByVersion(long version) {
        return idConfigDAO.selectByVersion(version);
    }

    /**
     * 获取最新版本号
     *
     * @return
     */
    @Override
    public Long getMaxVersion() {
        return idConfigDAO.selectMaxVersion();
    }

    /**
     * 更新最大值,并返回最新结果 两个操作需要在一个事务里
     *
     * @param bizName
     */
    @Transactional
    @Override
    public IDConfigModel updateMaxValueAndGet(String bizName) {
        idConfigDAO.updateByBizName(bizName);
        return idConfigDAO.selectByBizName(bizName);
    }
}

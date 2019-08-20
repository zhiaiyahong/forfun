package fun.repository;

import fun.dao.ConfigManagementDAO;
import fun.model.ConfigModel;
import fun.service.ConfigRead;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author pengweichao
 * @date 2019/8/19
 */
@Service
public class ConfigManagementRepository implements ConfigRead {

    @Resource
    private ConfigManagementDAO configManagementDAO;
    /**
     * 获取配置列表
     * 状态正常的配置
     *
     * @return
     */
    @Override
    public List<ConfigModel> getConfigList() {
        return configManagementDAO.selectByIsDel(0);
    }

    /**
     * 获取最近更新的版本
     *
     * @return
     */
    @Override
    public Long getLastVersion() {
        return configManagementDAO.selectMaxVersion();
    }

    /**
     * 获取最近更新的配置列表
     * 所有已更新的配置，包含删除的配置
     *
     * @param lastVersion
     * @return
     */
    @Override
    public List<ConfigModel> getUpdatedConfigList(long lastVersion) {
        return configManagementDAO.selectByVersion(lastVersion);
    }
}

package fun.service;

import fun.model.ConfigModel;

import java.util.List;

/**
 * @author pengweichao
 * @date 2019/8/19
 */
public interface ConfigRead {
    /**
     * 获取配置列表
     * 状态正常的配置
     * @return
     */
    public List<ConfigModel> getConfigList();

    /**
     * 获取最近更新的版本
     * @return
     */
    public Long getLastVersion();

    /**
     * 获取最近更新的配置列表
     * 所有已更新的配置，包含删除的配置
     * @return
     */
    public List<ConfigModel> getUpdatedConfigList(long lastVersion);
}

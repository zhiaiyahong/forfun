package fun.model;

import lombok.Data;

/**
 * @author pengweichao
 * @date 2019/8/19
 */
@Data
public class ConfigModel {
    private String configKey;
    private String configValue;
    private long version; // 配置版本
    private int isDel; // 配置是否删除
}

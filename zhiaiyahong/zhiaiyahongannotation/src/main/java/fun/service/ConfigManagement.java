package fun.service;

import fun.model.ConfigModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author pengweichao
 * @date 2019/8/19
 */
public class ConfigManagement {
    private static final Logger log = LoggerFactory.getLogger(ConfigManagement.class);

    /**
     * 当前配置的最高版本，用以检查哪些配置需要更新
     */
    private static long CONFIG_VERSION = 0L;
    /**
     * 正常配置状态
     */
    private static final int NORMAL_STATUS = 0;
    /**
     * 配置缓存，默认大小设置为256
     */
    private Map<String,String> configCache = new ConcurrentHashMap<>(256);

    /**
     * 配置读取接口
     */
    private ConfigRead configRead;

    public void init(){
        log.info("configCache开始加载");
         cacheConfig();
        updateConfigCacheBySecond();
        log.info("configCache加载完毕");
    }

    public String get(String configKey){
        if(StringUtils.isEmpty(configKey)){
            log.warn("key为null,无法获取数据");
            return null;
        }
        if(!configCache.containsKey(configKey)){
            log.warn("key不存在,无法获取数据");
            return null;
        }
       return configCache.get(configKey);
    }

    public void setConfigRead(ConfigRead configRead){
        this.configRead = configRead;
    }

    private void cacheConfig(){
        List<ConfigModel> list = configRead.getConfigList();
        if(CollectionUtils.isEmpty(list)){
            log.warn("配置列表为空,未缓存数据");
            return;
        }
        CONFIG_VERSION = configRead.getLastVersion();
        configCache = list.stream().collect(Collectors.toMap(ConfigModel::getConfigKey,ConfigModel::getConfigValue));
        if(configCache.size() > 128){
            log.warn("配置个数已超过128个,请减少配置,当前配置个数:{}",configCache.size());
        }
        log.info("配置加载完毕,config:{}",configCache);
    }

    private void updateConfigCache(){
        Long lastVersion = configRead.getLastVersion();
        if(lastVersion == null || lastVersion <= CONFIG_VERSION){
            log.info("配置未更新,lastVersion:[{}],ConfigVersion:[{}]",lastVersion,CONFIG_VERSION);
            return;
        }
        List<ConfigModel> list = configRead.getUpdatedConfigList(CONFIG_VERSION);
        if(CollectionUtils.isEmpty(list)){
            log.warn("最新配置拉取失败,ConfigVersion:[{}]",CONFIG_VERSION);
            return;
        }
        CONFIG_VERSION = lastVersion;
        list.forEach(configModel -> {
            if(configCache.containsKey(configModel.getConfigKey())){
                if(configModel.getIsDel() == NORMAL_STATUS){
                    log.info("修改配置,key:{},value:{}",configModel.getConfigKey(),configModel.getConfigValue());
                    configCache.put(configModel.getConfigKey(),configModel.getConfigValue());
                }else{
                    log.info("删除配置,key:{},value:{}",configModel.getConfigKey(),configModel.getConfigValue());
                    configCache.remove(configModel.getConfigKey());
                }
            }else if(configModel.getIsDel() == NORMAL_STATUS){
                log.info("新增配置,key:{},value:{}",configModel.getConfigKey(),configModel.getConfigValue());
                configCache.put(configModel.getConfigKey(),configModel.getConfigValue());
            }else{
                log.warn("出现异常配置,新增态但状态异常,config:{}",configModel);
            }
        });
        if(configCache.size() > 128){
            log.warn("配置个数已超过128个,请减少配置,当前配置个数:{}",configCache.size());
        }
    }

    private void updateConfigCacheBySecond(){
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setName("update-configCache-thread");
            t.setDaemon(true);
            return t;
        });
        scheduledExecutorService.scheduleWithFixedDelay(()->updateConfigCache(),5,5, TimeUnit.SECONDS);
    }
}

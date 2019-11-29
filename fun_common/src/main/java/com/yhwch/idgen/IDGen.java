package com.yhwch.idgen;

import com.yhwch.idgen.enums.IDGenResultCodeEnum;
import com.yhwch.idgen.model.IDBuffer;
import com.yhwch.idgen.model.IDConfigModel;
import com.yhwch.idgen.model.IDGenResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class IDGen {

    private static final Logger log = LoggerFactory.getLogger(IDGen.class);
    private static final int NORMAL_STATUS = 0;
    private static final float IDLE_PERCENT = 0.5f;
    private static final int CURRENT = 0;
    private static final int NEXT = 1;
    private ExecutorService service = new ThreadPoolExecutor(5, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new UpdateThreadFactory());
    /**
     * 当前配置版本
     */
    private long currentVersion = 0;
    /**
     * buffer
     */
    private Map<String, IDBuffer> cacheBuffer = new ConcurrentHashMap<String,IDBuffer>();

    private boolean initSuc = false;

    private IDConfigRead idConfigRead;

    public static class UpdateThreadFactory implements ThreadFactory {

        private static int threadInitNumber = 0;

        private static synchronized int nextThreadNum() {
            return threadInitNumber++;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread-IDIncrease-Update-" + nextThreadNum());
        }
    }

    public void init(){
        log.info("发号器开始初始化");
        getIDConfigFromDB();
        if(initSuc) {
            updateConfigCacheBySecond();
            log.info("发号器初始化成功,共计{}条配置",cacheBuffer.size());
        }else{
            log.info("发号器初始化失败！！！！！！！！！！");
        }
    }

    public IDGenResult getId(String bizName){
        if(StringUtils.isEmpty(bizName)){
            return buildResult(IDGenResultCodeEnum.PARAM_INVALID);
        }
        if(!initSuc){
            return buildResult(IDGenResultCodeEnum.INIT_FAIL);
        }
        if(!cacheBuffer.containsKey(bizName)){
            return buildResult(IDGenResultCodeEnum.UN_FOUND_BIZ_NAME);
        }
        final IDBuffer idBuffer = cacheBuffer.get(bizName);
        if(!idBuffer.isInitOk()){
            synchronized (idBuffer){
                if(!idBuffer.isInitOk()){
                    try {
                        updateBufferFormDB(idBuffer, CURRENT);
                        idBuffer.setInitOk(true);
                        log.info("id初始化成功,idBuffer:{}",idBuffer);
                    }catch (Exception e){
                        log.error("buffer初始化异常,p={},e=",bizName,e);
                    }
                }
            }
        }
        while(true) {
            idBuffer.rLock().lock();
            try {
                //1.检查当前缓存池是否超过90%，超过则进行buffer储备
                if (!idBuffer.isNextReady() && idBuffer.getCurrent().getIdle() < IDLE_PERCENT * idBuffer.getStep()
                        && idBuffer.getThreadRunning().compareAndSet(false, true)) {
                    service.execute(() -> {
                        boolean updateOk = false;
                        try {
                            updateOk = updateBufferFormDB(idBuffer,NEXT);
                            log.info("buffer储备成功pos={},value={}", idBuffer.nextPos(), idBuffer.getNext());
                        } catch (Exception e) {
                            log.error("buffer储备异常,p={}e=", idBuffer, e);
                        } finally {
                            if (updateOk) {
                                idBuffer.wLock().lock();
                                idBuffer.setNextReady(true);
                                idBuffer.getThreadRunning().set(false);
                                idBuffer.wLock().unlock();
                            } else {
                                idBuffer.getThreadRunning().set(false);
                            }
                        }
                    });
                }
                long value = idBuffer.getCurrent().getValue().getAndIncrement();
                if (value < idBuffer.getCurrent().getMax()) {
                    return buildResult(IDGenResultCodeEnum.SUC, value);
                }
            } catch (Exception e) {
                log.error("发号出现异常,p={},e=", bizName, e);
            } finally {
                idBuffer.rLock().unlock();
            }
            waitForNextReady(idBuffer);
            idBuffer.wLock().lock();
            try {
                long value = idBuffer.getCurrent().getValue().getAndIncrement();
                if (value < idBuffer.getCurrent().getMax()) {
                    return buildResult(IDGenResultCodeEnum.SUC, value);
                }
                if (idBuffer.isNextReady()) {
                    idBuffer.switchPos();
                    idBuffer.setNextReady(false);
                } else {
                    log.error("Both two buffer in {} are not ready!", idBuffer);
                    return buildResult(IDGenResultCodeEnum.BOTH_BUFFER_NOT_READY);
                }
            } catch (Exception e) {
                log.error("发号出现异常,p={},e=", bizName, e);
                return buildResult(IDGenResultCodeEnum.FAIL);
            } finally {
                idBuffer.wLock().unlock();
            }
        }
    }

    /**
     * 如下一结果没有准备好进行等待，先轮1w次，如仍未成功执行时间等待
     * @param idBuffer
     */
    private void waitForNextReady(IDBuffer idBuffer){
        int loopCount = 0;
        while (idBuffer.getThreadRunning().get()){
            loopCount ++;
            if(loopCount > 10000){
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                    break;
                } catch (InterruptedException e) {
                    log.warn("等待出现异常,p={},e=",idBuffer,e);
                    break;
                }
            }

        }
    }

    private boolean updateBufferFormDB(IDBuffer idBuffer,int updateWhich){
        IDConfigModel idConfigModel = idConfigRead.updateMaxValueAndGet(idBuffer.getKey());
        if(idConfigModel == null){
            log.error("处理出现异常,获取最新配置为空p={}",idBuffer);
            return false;
        }
        if(updateWhich == CURRENT){
            idBuffer.getCurrent().getValue().set(idConfigModel.getMaxID() - idConfigModel.getStep());
            idBuffer.getCurrent().setMax(idConfigModel.getMaxID());
            idBuffer.getCurrent().setStep(idConfigModel.getStep());
        }else {
            idBuffer.getNext().getValue().set(idConfigModel.getMaxID() - idConfigModel.getStep());
            idBuffer.getNext().setMax(idConfigModel.getMaxID());
            idBuffer.getNext().setStep(idConfigModel.getStep());
        }
        return true;
    }

    /**
     * 从数据库初始化配置
     */
    private void getIDConfigFromDB(){
        if(idConfigRead == null){
            log.error("发号器初始化失败,idConfigRead为null");
            return;
        }
        List<IDConfigModel> idConfigModels = getAllNormalIdConfig();
        if(CollectionUtils.isEmpty(idConfigModels)){
            initSuc = true;
            log.info("发号器无法进行初始化,无任何配置");
        }
        idConfigModels.stream().forEach(idConfigModel -> {
            addCache(idConfigModel);
        });
        initSuc = true;
        currentVersion = getMaxVersion();
    }

    private void updateIDConfigCache(){
        long maxVersion = getMaxVersion();
        if(maxVersion < currentVersion){
            log.error("版本号错误，最新版本小于当前版本,maxVersion:{},currentVersion:{}",maxVersion,currentVersion);
            return;
        }
        if(maxVersion == currentVersion){
            log.info("版本号一致,配置无需变更,v={}",currentVersion);
            return;
        }
        List<IDConfigModel> idConfigModels = getIdConfigByVersion(currentVersion);
        currentVersion = maxVersion;
        if(CollectionUtils.isEmpty(idConfigModels)){
            log.warn("虚晃一枪,最新版本号有变更,但未获取到数据,maxVersion:{},currentVersion:{}",maxVersion,currentVersion);
            return;
        }
        idConfigModels.stream().forEach(idConfigModel -> {
            if(cacheBuffer.containsKey(idConfigModel.getBizName())){
                if(idConfigModel.getDel() == NORMAL_STATUS){
                    log.info("修改配置,idConfigModel:{}",idConfigModel);
                    addCache(idConfigModel);
                }else{
                    log.info("删除配置,idConfigModel:{}",idConfigModel);
                    cacheBuffer.remove(idConfigModel.getBizName());
                }
            }else if(idConfigModel.getDel() == NORMAL_STATUS){
                log.info("新增配置,idConfigModel:{}",idConfigModel);
                addCache(idConfigModel);
            }else{
                log.warn("出现异常配置,新增态但状态异常,idConfigModel:{}",idConfigModel);
            }
        });
    }

    private void addCache(IDConfigModel idConfigModel){
        try {
            IDBuffer idBuffer = new IDBuffer();
            idBuffer.setStep(idConfigModel.getStep());
            idBuffer.setKey(idConfigModel.getBizName());
            idBuffer.setMinStep(idBuffer.getStep());
            idBuffer.getCurrent().setMax(idConfigModel.getMaxID());
            idBuffer.getCurrent().setStep(idConfigModel.getStep());
            idBuffer.getCurrent().getValue().set(idConfigModel.getMaxID());
            cacheBuffer.put(idConfigModel.getBizName(), idBuffer);
        }catch (Exception e){
            log.error("单条数据初始化失败,p={},e=",idConfigModel,e);
        }
    }

    private void updateConfigCacheBySecond(){
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setName("update-IDCache");
            t.setDaemon(true);
            return t;
        });
        scheduledExecutorService.scheduleWithFixedDelay(()->updateIDConfigCache(),5,5, TimeUnit.SECONDS);
    }

    private List<IDConfigModel> getAllNormalIdConfig(){
        try{
            return idConfigRead.getAllNormalIdConfig();
        }catch (Exception e){
            log.error("获取全量正常配置出现异常,e=",e);
            return Collections.EMPTY_LIST;
        }
    }

    private List<IDConfigModel> getIdConfigByVersion(long maxVersion){
        try{
            return idConfigRead.getIdConfigByVersion(maxVersion);
        }catch (Exception e){
            log.error("根据最新版本号获取配置出现异常,e=",e);
            return Collections.EMPTY_LIST;
        }
    }

    private long getMaxVersion(){
        try {
            Long version = idConfigRead.getMaxVersion();
            if (version == null) {
                return 0;
            }
            return version;
        }catch (Exception e){
            log.error("获取最大版本号出现异常,e=",e);
            return 0;
        }
    }

    private IDGenResult buildResult(IDGenResultCodeEnum codeEnum){
        IDGenResult idGenResult = new IDGenResult();
        idGenResult.setCode(codeEnum.getCode());
        idGenResult.setMsg(codeEnum.getMsg());
        return idGenResult;
    }

    private IDGenResult buildResult(IDGenResultCodeEnum codeEnum,long id){
        IDGenResult idGenResult = buildResult(codeEnum);
        idGenResult.setId(id);
        return idGenResult;
    }

    public void setIdConfigRead(IDConfigRead idConfigRead) {
        this.idConfigRead = idConfigRead;
    }
}

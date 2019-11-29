package com.yhwch.idgen.model;

import lombok.Data;

@Data
public class IDConfigModel {
    /**
     * 业务名称
     */
    private String bizName;

    /**
     * 增长步长
     */
    private Integer step;

    /**
     * 当前增长的最大值
     */
    private Long maxID;

    /**
     * 版本 控制配置的增、删，删除需要先执行逻辑删除在执行物理删除
     */
    private Long version;

    /**
     * 删除标识 0正常 其他删除
     */
    private Integer del;


}

package fun.dao;

import fun.model.ConfigModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author pengweichao
 * @date 2019/8/19
 */
public interface ConfigManagementDAO {

    List<ConfigModel> selectByIsDel(@Param("isDel") Integer isDel);

    List<ConfigModel> selectByVersion(@Param("version") Long version);

    Long selectMaxVersion();
}

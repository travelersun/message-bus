package com.messagebus.managesystem.module.mapper;

import com.messagebus.managesystem.module.entity.SinkAuditHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Maya
 * @since 2021-02-21
 */
@Mapper
@Repository
public interface SinkAuditHistoryMapper extends BaseMapper<SinkAuditHistory> {

}

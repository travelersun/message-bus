package com.messagebus.managesystem.module.service.impl;

import com.messagebus.managesystem.module.entity.Config;
import com.messagebus.managesystem.module.mapper.ConfigMapper;
import com.messagebus.managesystem.module.service.IConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Maya
 * @since 2021-02-21
 */
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements IConfigService {

}

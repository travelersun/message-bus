package com.messagebus.managesystem.module.service.impl;

import com.messagebus.managesystem.module.entity.App;
import com.messagebus.managesystem.module.mapper.AppMapper;
import com.messagebus.managesystem.module.service.IAppService;
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
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements IAppService {

}

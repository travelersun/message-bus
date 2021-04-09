package com.messagebus.managesystem.module.service.impl;

import com.messagebus.managesystem.common.Constants;
import com.messagebus.managesystem.module.entity.Sink;
import com.messagebus.managesystem.module.mapper.SinkMapper;
import com.messagebus.managesystem.module.service.ISinkService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class SinkServiceImpl extends ServiceImpl<SinkMapper, Sink> implements ISinkService {


    @Override
    public List<Sink> queryListByParam(Map<String, Object> param) {
        return null;
    }
}

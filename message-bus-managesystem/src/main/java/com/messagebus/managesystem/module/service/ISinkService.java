package com.messagebus.managesystem.module.service;

import com.messagebus.managesystem.module.entity.Sink;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Maya
 * @since 2021-02-21
 */
public interface ISinkService extends IService<Sink> {


    List<Sink> queryListByParam(Map<String, Object> param);
}

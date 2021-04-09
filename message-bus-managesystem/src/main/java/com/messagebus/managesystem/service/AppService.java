package com.messagebus.managesystem.service;


import java.util.*;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.messagebus.managesystem.module.OperationResult;
import com.messagebus.managesystem.module.entity.App;
import com.messagebus.managesystem.module.service.IAppService;

/**
 * Created by yanghua on 3/12/15.
 */
@RestController
@RequestMapping("/messagebusmanage/maintain/app")
public class AppService {

    public static final String module = AppService.class.getName();
    
    Logger logger = LoggerFactory.getLogger(AppService.class);
    
    @Autowired
    @Qualifier("appServiceImpl")
    IAppService iAppService;

    @RequestMapping("createApp")
    public  OperationResult createApp(@RequestBody Map<String, ? extends Object> context) {

        String appName = (String) context.get("name");

        String appId = null;
        Object optionalAppId = context.get("appId");
        if (optionalAppId != null) {
            appId = optionalAppId.toString();
        }

        try {
        	
            int countWithAppName = iAppService.count(new QueryWrapper<App>().eq("name", appName));

            if (countWithAppName != 0) return OperationResult.buildFailureResult("the app with name : " + appName + " exists");

            if (appId != null) {
                long countWithAppId = iAppService.count(new QueryWrapper<App>().eq("app_id", appId));

                if (countWithAppId != 0) return OperationResult.buildFailureResult("the app with id : " + appId + " exists");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return OperationResult.buildFailureResult(e.getMessage());
        }

        if (innerCreateApp(appId, appName)) {
            return OperationResult.buildSuccessResult();
        } else {
            return OperationResult.buildFailureResult("create app error.");
        }
    }

    @RequestMapping("batchCreateApp")
    public  OperationResult batchCreateApp(@RequestBody Map<String, ? extends Object> context) {
       
        Map<String, ? extends Object> apps = (Map) context.get("apps");
        try {
            for (Map.Entry<String, ? extends Object> app : apps.entrySet()) {
                String appId = app.getKey();
                String appName = app.getValue() == null ? null : (String) app.getValue();
                int countWithAppName = iAppService.count(new QueryWrapper<App>().eq("name", appName));

                if (countWithAppName != 0) continue;

                if (appId != null) {
                    long countWithAppId = iAppService.count(new QueryWrapper<App>().eq("app_id", appId));

                    if (countWithAppId != 0) continue;
                }

                innerCreateApp(appId, appName);
            }
        } catch (Exception e) {
        	logger.error(e.getMessage());
            return OperationResult.buildFailureResult(e.getMessage());
        }

        return OperationResult.buildSuccessResult();
    }

    private  boolean innerCreateApp(String appId, String appName) {
        try {
            
            App app = new App();
            app.setName(appName);
            app.setCreator(""); //todo 
            
            app.setAppId(appId);
            
            app.setFromDate(new Date());
            
            iAppService.saveOrUpdate(app);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }
    @RequestMapping("updateApp")
    public  OperationResult updateApp(@RequestBody Map<String, ? extends Object> context) {
       
        String appName = (String) context.get("name");
        String appId = (String) context.get("appId");

        try {
            App app = iAppService.getOne(new QueryWrapper<App>().eq("app_id", appId));
            app.setName(appName);
            iAppService.saveOrUpdate(app);
        } catch (Exception e) {
        	logger.error(e.getMessage());
            return OperationResult.buildFailureResult(e.getMessage());
        }

        return OperationResult.buildSuccessResult();
    }

    @RequestMapping("deleteApp")
    public  OperationResult deleteApp(@RequestBody Map<String, ? extends Object> context) {
       
        String appId = (String) context.get("appId");
        
        boolean c = iAppService.remove(new QueryWrapper<App>().eq("app_id", appId));

        return OperationResult.buildSuccessResult(c);
    }


    @RequestMapping("list")
    public OperationResult list(@RequestBody Map<String, ? extends Object> context) {

        try {

            int limit = Integer.parseInt(context.get("limit").toString());

            int offset = Integer.parseInt(context.get("offset").toString());
            String name = (String) context.get("name");

            String sortBy = (String) context.get("sortBy");

            String sortOrder = (String) context.get("sortOrder");

            PageHelper.startPage(offset, limit);

            QueryWrapper<App> query = new QueryWrapper<App>();
            Map<String,Object> param = new HashMap<String,Object>();
            if(StringUtils.hasText(name)){
                query.like("name",name);
            }
            if(StringUtils.hasText(sortBy)){
                query.orderBy(true,"ascending".equals(sortOrder),sortBy);
            }

            List<App> apps = iAppService.list(query);

            PageInfo<App> pageInfo= new PageInfo<App>(apps);

            PageHelper.clearPage();

            return OperationResult.buildSuccessResult(pageInfo);
        } catch (Exception e) {
            logger.error(e.getMessage(), module);
            Map<String, Object> result = new HashMap<String,Object>();
            result.put("results", Collections.emptyList());
            return OperationResult.buildFailureResult(e.getMessage(), result);
        }
    }

}

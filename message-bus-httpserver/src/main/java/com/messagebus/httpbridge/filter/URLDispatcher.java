package com.messagebus.httpbridge.filter;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.messagebus.httpbridge.util.Constants;
import com.messagebus.httpbridge.util.ResponseUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yanghua on 9/30/14.
 */
//@WebFilter(filterName = "urlDispatcher", urlPatterns = {"/*"},asyncSupported = true)
public class URLDispatcher implements Filter {

    private static final Log    logger           = LogFactory.getLog(URLDispatcher.class);
    private static final Gson   gson             = new Gson();
    private static final String URI_PREFIX       = "/messagebus/queues";
    private static final String ERROR_URI_PERFIX = "/error";
    private static final String KEY_OF_SECRET    = "secret";
    private static final String KEY_OF_TYPE      = "apiType";


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String uri = request.getRequestURI();
        if (uri.startsWith(ERROR_URI_PERFIX)) {
            filterChain.doFilter(request, servletResponse);
        }

        if (!uri.startsWith(URI_PREFIX)) {
            logger.error("[doFilter] request uri is " + request.getRequestURI());
            ResponseUtil.response((HttpServletResponse) servletResponse, Constants.HTTP_NOT_FOUND_CODE,
                                  "the request uri : " + request.getRequestURI() + "is not found!",
                                  "", gson.toJson(""));
        } else {
            String appKeyVal = request.getParameter(KEY_OF_SECRET);
            String type = request.getParameter(KEY_OF_TYPE);
            if (Strings.isNullOrEmpty(appKeyVal)) {
                logger.error("[doFilter] missed query string : " + KEY_OF_SECRET);
                ResponseUtil.response((HttpServletResponse) servletResponse, Constants.HTTP_FAILED_CODE,
                                      "missed query string : " + KEY_OF_SECRET + "!", "", gson.toJson(""));
            } else if (Strings.isNullOrEmpty(type)) {
                logger.error("[doFilter] missed query string : " + KEY_OF_TYPE);
                ResponseUtil.response((HttpServletResponse) servletResponse, Constants.HTTP_FAILED_CODE,
                                      "missed query string : " + KEY_OF_TYPE + "!", "", gson.toJson(""));
            } else {
                filterChain.doFilter(request, servletResponse);
            }
        }
    }

    @Override
    public void destroy() {

    }
}

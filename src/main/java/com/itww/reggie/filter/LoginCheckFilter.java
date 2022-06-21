package com.itww.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itww.reggie.common.BaseContext;
import com.itww.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: ww
 * @DateTime: 2022/6/17 20:23
 * @Description: 检查用户是否已经登录
 */

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCH = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // 获取本次请求的URI
        String requestURI = request.getRequestURI();

        log.info("拦截到请求：{}", request.getRequestURI());
        // 定义不需要处理的请求路径
        String[] urls = new String[] {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
//                ,
//                "/employee/page"
        };
        // 判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        // 如果不需要处理，则直接放行
        if (check) {
            filterChain.doFilter(request,response);
            return;
        }
        // 判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null) {

            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            Long id = Thread.currentThread().getId();
            log.info("线程id为：{}", id);

            filterChain.doFilter(request,response);
            return;
        }

        if (request.getSession().getAttribute("user") != null) {

            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }
        // 如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCH.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}

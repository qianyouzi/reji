package com.reji.filter;

import com.alibaba.fastjson.JSON;
import com.reji.bean.R;
import com.reji.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import sun.util.locale.BaseLocale;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 在启动类上加@ServletComponentScan注解,
 * 不加该注解的话springboot不会主动扫描@WebFilter之类的注解
 * 过滤器
 */
//拦截所有请求
@Slf4j
@WebFilter("/*")
public class LoginCheckFiter implements Filter {

    //使用spring提供的路径匹配器来判断
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //获取当前请求uri
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        String uri = req.getRequestURI();
        log.info("本次请求地址{}", uri);
        //使用spring提供的路径匹配器来判断
        //声明不需要拦截的路径地址数组
        String[] paths = {
                "/backend/**",
                "/front/**",
                "/employee/login",
                "/employee/logout",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        //遍历不需要通过拦截的路径地址,和当前请求地址进行匹配
        boolean flag = false;
        for (String path : paths) {
            if (ANT_PATH_MATCHER.match(path, uri)) {
                flag = true;
                break;
            }
        }
        //匹配通过true,不需要拦截
        if (flag) {
            filterChain.doFilter(req, resp);
            return;
        }

        //需要拦截的话判断是否登录,已登录
        HttpSession session = req.getSession();
        Object employee = session.getAttribute("employee");
        Object user = session.getAttribute("user");
        Long i = (Long) user;
        log.error("{}",i);
        if (user!=null){
            Long user1 = (Long) req.getSession().getAttribute("user");
            BaseContext.setCurrentId(user1);
            filterChain.doFilter(req,resp);
            return;
        }
        if (employee != null) {
            Long id = (Long) req.getSession().getAttribute("employee");
            BaseContext.setCurrentId(id);
            filterChain.doFilter(req, resp);
            return;
        }
        //如果未登录,需要将处理结果封装到R对象中进行返回
        resp.getWriter().print(JSON.toJSONString(R.error("NOTLOGIN")));
    }
}

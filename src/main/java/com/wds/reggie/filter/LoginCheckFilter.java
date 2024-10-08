package com.wds.reggie.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wds.reggie.common.R;
import com.wds.reggie.common.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-14 13:15
 */
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "*")
@Slf4j
public class LoginCheckFilter implements Filter {
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 登录检查
     * 静态资源放行
     * 设置ThreadLocal为employee的id
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;


//        user uri
        String ruri = (request.getRequestURI());
//        allow uri
        String[] uris = new String[]{
//                登录登出
                "/static/index.html",
                "/",
                "/employee/login",
//                静态资源
                "/front/**",
                "/backend/**",
                "/user/login"
        };


        // TODO: 2022/9/9 前后台用户鉴权
        String[] front_uris = new String[]{
                "/"
        };

        String[] backend_uris = new String[]{
                ""
        };


        boolean matcher = check(uris, ruri);

        if (matcher) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        /*存储当前登录员工的id*/
        Long employeeId = (Long) request.getSession().getAttribute("employeeId");
        BaseContext.getCurrentId().setEmployeeId(employeeId);

        /*存储当前登录用户的id*/
        Long userId = (Long) request.getSession().getAttribute("userId");
        BaseContext.getCurrentId().setUserId(userId);


        if (BaseContext.getCurrentId().getUserId() != null || BaseContext.getCurrentId().getEmployeeId() != null) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(R.error("NOTLOGIN")));
        log.info(mapper.writeValueAsString(R.error("NOTLOGIN")));
    }

    /**
     * uri匹配
     *
     * @param Uris 放行的uris
     * @param ruri 用户访问的uri
     * @return true 放行
     */
    private boolean check(String[] Uris, String ruri) {
        for (String uri : Uris) {
            boolean match = PATH_MATCHER.match(uri, ruri);
            if (match) {
                return true;
            }
        }
        return false;
    }


}

package com.mytest.sso;

import com.alibaba.fastjson.JSONObject;
import com.weaverboot.frame.ioc.filter.util.RequestParamUtils;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 给oa标准单点接口加了一层参数拦截，原本接口只接受loginid
 * 现在可以自定义了
 */
public class SSO_testFilter extends BaseBean implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        RecordSet recordSet = new RecordSet();

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        Map<String, Object> requestMap = RequestParamUtils.request2Map(request);

        this.writeLog(" requestMap11: " + JSONObject.toJSONString(requestMap));

        MyHttpServletRequestWrapper1 requestWrapper1 = new MyHttpServletRequestWrapper1(request);
        // 1.获取需要处理的参数
        String loginId = requestWrapper1.getParameter("loginid");
        recordSet.executeQuery("select loginid from hrmresource where workcode = ? and status < 4", loginId);
        if (recordSet.next()) {
            // 2.把处理后的参数放回去
            requestWrapper1.setParameter("loginid", recordSet.getString("loginid"));
            // 3.放行，把我们的requestWrapper1放到方法当中
            filterChain.doFilter(requestWrapper1, response);
        } else {
            PrintWriter writer = servletResponse.getWriter();
            writer.print("工号不存在");
        }


    }

    @Override
    public void destroy() {

    }


}

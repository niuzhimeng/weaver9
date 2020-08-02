package com.api.weavernorth.web;

import com.api.weavernorth.dao.GetUserMybatisTestMapper;
import com.api.weavernorth.entity.HrmVOTest;
import com.weavernorth.mybatis.service.MyBatisConnection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/weavernorth")
public class GetUserMybatisTest {

    private static final Log MY_LOG = LogFactory.getLog(GetUserMybatisTest.class);

    @GET
    @Path("/getUserInfo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public HrmVOTest getDataTest(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        try {
            String loginId = request.getParameter("loginid");

            if ("1".equals(loginId)) {
                HrmVOTest hrmVOTest = new HrmVOTest();
                hrmVOTest.setEmail("ddddd");
                return hrmVOTest;
            }

            MyBatisConnection myBatisConnection = new MyBatisConnection();
            // 通过会话工厂创建会话
            SqlSession session = myBatisConnection.getSession();
            GetUserMybatisTestMapper mapper = session.getMapper(GetUserMybatisTestMapper.class);
            HrmVOTest hrmVO = mapper.getUserById(loginId);

            //关闭会话（必须！！）
            myBatisConnection.closeSession();
            return hrmVO;
        } catch (Exception e) {
            MY_LOG.error("GetUserMybatisTest异常： " + e);
        }

        return new HrmVOTest();

    }

}

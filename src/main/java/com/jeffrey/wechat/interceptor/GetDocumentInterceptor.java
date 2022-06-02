package com.jeffrey.wechat.interceptor;

import com.jeffrey.wechat.service.DocumentTransService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 拦截未授权的用户直接获取文档或绕过校验获取文档
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
public class GetDocumentInterceptor implements HandlerInterceptor {

    private final DocumentTransService documentTransService;

    @Autowired
    public GetDocumentInterceptor(DocumentTransService documentTransService) {
        this.documentTransService = documentTransService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String openid = request.getParameter("openid");

        if (StringUtils.hasText(openid) && documentTransService.isUser(openid)) {
            return true;
        } else {
            response.setStatus(401);
            response.setContentType("text/plain;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write("401 Unauthorized");
            writer.flush();
            writer.close();
            return false;
        }
    }
}

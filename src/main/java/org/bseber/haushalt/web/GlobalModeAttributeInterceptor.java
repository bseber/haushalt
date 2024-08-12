package org.bseber.haushalt.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

@Component
class GlobalModeAttributeInterceptor implements ModelAttributeHandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (shouldAddAttributes(modelAndView)) {
            modelAndView.getModel().put("userDatePattern", "dd.MM.yyyy");
        }
    }
}

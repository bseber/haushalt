package org.bseber.haushalt.settings;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bseber.haushalt.web.ModelAttributeHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

class SettingsViewModelAdvice implements ModelAttributeHandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (shouldAddAttributes(modelAndView)) {
            modelAndView.getModel().put("userDatePattern", "dd.MM.yyyy");
        }
    }
}

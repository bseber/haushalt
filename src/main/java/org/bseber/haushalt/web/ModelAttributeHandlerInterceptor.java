package org.bseber.haushalt.web;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

interface ModelAttributeHandlerInterceptor extends HandlerInterceptor {

    default boolean shouldAddAttributes(ModelAndView modelAndView) {

        if (modelAndView == null) {
            return false;
        }

        final String viewName = modelAndView.getViewName();
        if (viewName == null) {
            return false;
        }

        return !(
            viewName.startsWith("forward:") ||
            viewName.startsWith("redirect:")
        );
    }
}

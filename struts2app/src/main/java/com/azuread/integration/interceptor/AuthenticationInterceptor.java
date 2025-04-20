package com.azuread.integration.interceptor;

import com.azuread.integration.util.SessionUtil;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

/**
 * Interceptor to check authentication status.
 * Redirects unauthenticated users to the login page.
 */
public class AuthenticationInterceptor extends AbstractInterceptor {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AuthenticationInterceptor.class.getName());
    
    // Actions that don't require authentication
    private static final String[] EXCLUDED_ACTIONS = {
            "login", "callback", "logout", "error"
    };
    
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        String actionName = invocation.getProxy().getActionName();
        
        LOGGER.info("Checking authentication for action: " + actionName);
        
        // Skip authentication check for excluded actions
        for (String excludedAction : EXCLUDED_ACTIONS) {
            if (actionName.equals(excludedAction) || actionName.startsWith(excludedAction + "-")) {
                return invocation.invoke();
            }
        }
        
        // Check if user is authenticated
        if (SessionUtil.isAuthenticated()) {
            LOGGER.info("User is authenticated, proceeding with action");
            return invocation.invoke();
        } else {
            LOGGER.info("User is not authenticated, redirecting to login");
            return "login";
        }
    }
}

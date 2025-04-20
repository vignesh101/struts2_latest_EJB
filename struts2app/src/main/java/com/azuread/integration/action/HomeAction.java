package com.azuread.integration.action;

import com.azuread.integration.model.User;
import com.azuread.integration.util.SessionUtil;
import com.opensymphony.xwork2.ActionSupport;

import java.util.logging.Logger;

/**
 * Action for the home page - requires authentication.
 */
public class HomeAction extends ActionSupport {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(HomeAction.class.getName());
    
    private User user;
    
    @Override
    public String execute() throws Exception {
        // Get the authenticated user from the session
        user = SessionUtil.getAuthenticatedUser();
        
        LOGGER.info("Home page accessed by user: " + (user != null ? user.getName() : "Unknown"));
        
        return SUCCESS;
    }
    
    /**
     * Get the authenticated user.
     * Used by the JSP to display user information.
     *
     * @return The authenticated user
     */
    public User getUser() {
        return user;
    }
}

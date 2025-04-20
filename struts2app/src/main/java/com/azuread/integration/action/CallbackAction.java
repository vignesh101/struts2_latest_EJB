package com.azuread.integration.action;

import com.azuread.integration.model.User;
import com.azuread.integration.service.AzureADAuthService;
import com.azuread.integration.util.EJBLookupUtil;
import com.azuread.integration.util.SessionUtil;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action to handle the callback from Azure AD after authentication.
 */
@Results({
        @Result(name = ActionSupport.SUCCESS, type = "redirectAction", params = {"actionName", "home"}),
        @Result(name = ActionSupport.ERROR, type = "redirectAction", params = {"actionName", "error"})
})
public class CallbackAction extends ActionSupport {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CallbackAction.class.getName());

    private String code;
    private String state;
    private String error;
    private String error_description;

    @Override
    public String execute() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        
        // Check for errors from Azure AD
        if (error != null) {
            LOGGER.severe("Error from Azure AD: " + error + " - " + error_description);
            addActionError("Authentication failed: " + error + " - " + error_description);
            return ERROR;
        }
        
        // Validate code and state parameters
        if (code == null || state == null) {
            LOGGER.severe("Missing required parameters (code or state)");
            addActionError("Missing required authentication parameters");
            return ERROR;
        }
        
        // Validate state parameter to prevent CSRF
        if (!SessionUtil.validateStateParameter(state)) {
            LOGGER.severe("Invalid state parameter, possible CSRF attack");
            addActionError("Invalid authentication state, please try again");
            return ERROR;
        }
        
        try {
            // Clear the state parameter from session
            SessionUtil.clearStateParameter();
            
            // Get the EJB service
            AzureADAuthService authService = EJBLookupUtil.getAuthService();
            
            // Exchange the code for an access token
            String accessToken = authService.handleAuthorizationCode(code, state);
            if (accessToken == null) {
                LOGGER.severe("Failed to get access token");
                addActionError("Failed to complete authentication");
                return ERROR;
            }
            
            // Validate the token
            if (!authService.validateToken(accessToken)) {
                LOGGER.severe("Invalid access token");
                addActionError("Invalid authentication token");
                return ERROR;
            }
            
            // Get user info from the token
            String userInfo = authService.getUserInfo(accessToken);
            
            // For demo purposes, create a simple user object
            // In a real app, you would parse the user info JSON
            User user = new User();
            user.setId("user-id");
            user.setName("User");
            user.setEmail("user@example.com");
            user.setAccessToken(accessToken);
            
            // Store the user in the session
            SessionUtil.setAuthenticatedUser(user);
            
            LOGGER.info("User successfully authenticated: " + user.getName());
            return SUCCESS;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during callback processing", e);
            addActionError("Error processing authentication: " + e.getMessage());
            return ERROR;
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }
}

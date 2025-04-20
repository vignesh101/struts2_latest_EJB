package com.azuread.integration.action;

import com.azuread.integration.service.AzureADAuthService;
import com.azuread.integration.util.EJBLookupUtil;
import com.azuread.integration.util.SessionUtil;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action for initiating login with Azure AD.
 */
public class LoginAction extends ActionSupport {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LoginAction.class.getName());

    @Override
    public String execute() throws Exception {
        try {
            // Generate a state parameter for CSRF protection
            String state = SessionUtil.generateStateParameter();
            
            // Get the EJB service
            AzureADAuthService authService = EJBLookupUtil.getAuthService();
            
            // Get the authorization URL from the EJB
            String authorizationUrl = authService.getAuthorizationUrl(state);
            
            LOGGER.info("Redirecting to Azure AD login: " + authorizationUrl);
            
            // Send redirect to the authorization URL
            HttpServletResponse response = ServletActionContext.getResponse();
            response.sendRedirect(authorizationUrl);
            
            return null; // Return null to indicate that the redirect has been handled
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during login redirect", e);
            addActionError("Failed to redirect to login: " + e.getMessage());
            return ERROR;
        }
    }
}

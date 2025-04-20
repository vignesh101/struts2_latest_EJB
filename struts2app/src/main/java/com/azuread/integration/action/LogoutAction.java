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
 * Action for handling user logout.
 */
public class LogoutAction extends ActionSupport {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LogoutAction.class.getName());

    @Override
    public String execute() throws Exception {
        try {
            // Invalidate the user's session
            SessionUtil.invalidateSession();
            
            // Get the EJB service
            AzureADAuthService authService = EJBLookupUtil.getAuthService();
            
            // Get the Azure AD logout URL
            String logoutUrl = authService.getLogoutUrl();
            
            LOGGER.info("Redirecting to Azure AD logout: " + logoutUrl);
            
            // Redirect to the Azure AD logout URL
            HttpServletResponse response = ServletActionContext.getResponse();
            response.sendRedirect(logoutUrl);
            
            return null; // Return null to indicate that the redirect has been handled
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during logout", e);
            addActionError("Failed to log out: " + e.getMessage());
            return ERROR;
        }
    }
}

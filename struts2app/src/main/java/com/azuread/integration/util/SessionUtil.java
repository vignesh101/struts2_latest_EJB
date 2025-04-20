package com.azuread.integration.util;

import com.azuread.integration.model.User;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * Utility class for session management.
 */
public class SessionUtil {

    public static final String USER_SESSION_KEY = "authenticated_user";
    public static final String STATE_SESSION_KEY = "oauth_state";
    
    /**
     * Stores the authenticated user in the session.
     *
     * @param user User to store in session
     */
    public static void setAuthenticatedUser(User user) {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession session = request.getSession(true);
        session.setAttribute(USER_SESSION_KEY, user);
    }
    
    /**
     * Retrieves the authenticated user from the session.
     *
     * @return The authenticated user or null if not authenticated
     */
    public static User getAuthenticatedUser() {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute(USER_SESSION_KEY);
    }
    
    /**
     * Checks if a user is currently authenticated.
     *
     * @return true if a user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        return getAuthenticatedUser() != null;
    }
    
    /**
     * Generates a random state parameter for CSRF protection.
     *
     * @return The state parameter
     */
    public static String generateStateParameter() {
        String state = UUID.randomUUID().toString();
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession session = request.getSession(true);
        session.setAttribute(STATE_SESSION_KEY, state);
        return state;
    }
    
    /**
     * Validates the state parameter from the OAuth callback.
     *
     * @param state State parameter from callback
     * @return true if the state is valid, false otherwise
     */
    public static boolean validateStateParameter(String state) {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        
        String sessionState = (String) session.getAttribute(STATE_SESSION_KEY);
        return state != null && state.equals(sessionState);
    }
    
    /**
     * Clears the state parameter from the session.
     */
    public static void clearStateParameter() {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(STATE_SESSION_KEY);
        }
    }
    
    /**
     * Invalidates the current session.
     */
    public static void invalidateSession() {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}

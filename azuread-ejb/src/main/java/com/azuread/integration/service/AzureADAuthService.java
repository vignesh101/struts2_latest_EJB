package com.azuread.integration.service;

import javax.ejb.Remote;

/**
 * Remote interface for Azure AD authentication service.
 */
@Remote
public interface AzureADAuthService {
    
    /**
     * Generates the authorization URL for Azure AD login.
     *
     * @param state Optional state parameter to prevent CSRF
     * @return The authorization URL
     */
    String getAuthorizationUrl(String state);
    
    /**
     * Handles the authorization code from Azure AD and exchanges it for an access token.
     *
     * @param code Authorization code from Azure AD
     * @param state State parameter to validate against CSRF
     * @return Access token from Azure AD or null if authentication fails
     */
    String handleAuthorizationCode(String code, String state);
    
    /**
     * Validates an access token with Azure AD.
     *
     * @param accessToken Access token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String accessToken);
    
    /**
     * Extracts user information from an access token.
     *
     * @param accessToken Access token containing user information
     * @return User information as a JSON string
     */
    String getUserInfo(String accessToken);
    
    /**
     * Generates the logout URL for Azure AD.
     *
     * @return The logout URL
     */
    String getLogoutUrl();
}

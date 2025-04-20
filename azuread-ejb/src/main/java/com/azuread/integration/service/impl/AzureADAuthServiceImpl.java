package com.azuread.integration.service.impl;

import com.azuread.integration.service.AzureADAuthService;
import com.microsoft.aad.msal4j.*;

import javax.annotation.PostConstruct;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the Azure AD authentication service.
 */
@Stateless
@Remote(AzureADAuthService.class)
public class AzureADAuthServiceImpl implements AzureADAuthService {

    private static final Logger LOGGER = Logger.getLogger(AzureADAuthServiceImpl.class.getName());

    private String clientId;
    private String clientSecret;
    private String tenantId;
    private String issuerUri;
    private String logoutUri;
    private String postLogoutRedirectUri;
    private String redirectUri;
    private Set<String> scopes;
    
    private IConfidentialClientApplication app;

    @PostConstruct
    public void init() {
        loadProperties();
        initializeApp();
    }

    private void loadProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("azuread.properties")) {
            properties.load(inputStream);
            
            clientId = properties.getProperty("azure.ad.client-id");
            clientSecret = properties.getProperty("azure.ad.client-secret");
            tenantId = properties.getProperty("azure.ad.tenant-id");
            issuerUri = properties.getProperty("azure.ad.issuer-uri");
            logoutUri = properties.getProperty("azure.ad.logout-uri");
            postLogoutRedirectUri = properties.getProperty("azure.ad.post-logout-redirect-uri");
            redirectUri = properties.getProperty("azure.ad.redirect-uri");
            
            String scopesStr = properties.getProperty("azure.ad.scopes");
            scopes = new HashSet<>(Arrays.asList(scopesStr.split(",")));
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load Azure AD properties", e);
            throw new RuntimeException("Failed to load Azure AD properties", e);
        }
    }

    private void initializeApp() {
        try {
            // Build the MSAL confidential client application
            app = ConfidentialClientApplication.builder(
                    clientId,
                    ClientCredentialFactory.createFromSecret(clientSecret))
                    .authority(issuerUri)
                    .build();
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize Azure AD application", e);
            throw new RuntimeException("Failed to initialize Azure AD application", e);
        }
    }

    @Override
    public String getAuthorizationUrl(String state) {
        // Build the authorization request
        AuthorizationRequestUrlParameters parameters = AuthorizationRequestUrlParameters
                .builder(redirectUri, scopes)
                .responseMode(ResponseMode.QUERY)
                .prompt(Prompt.SELECT_ACCOUNT)
                .state(state)
                .build();

        try {
            return app.getAuthorizationRequestUrl(parameters).toString();
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate authorization URL", e);
            throw new RuntimeException("Failed to generate authorization URL", e);
        }
    }

    @Override
    public String handleAuthorizationCode(String code, String state) {
        // Build the auth code parameters
        AuthorizationCodeParameters parameters = AuthorizationCodeParameters
                .builder(code, new URI(redirectUri).toURL())
                .scopes(scopes)
                .build();

        // Exchange the authorization code for an access token
        CompletableFuture<IAuthenticationResult> future = app.acquireToken(parameters);
        
        try {
            IAuthenticationResult result = future.get();
            return result.accessToken();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.log(Level.SEVERE, "Failed to acquire token", e);
            return null;
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE, "Invalid redirect URI", e);
            return null;
        }
    }

    @Override
    public boolean validateToken(String accessToken) {
        // In a real implementation, you would verify the token's signature and claims
        // For simplicity, we'll just check if the token is not null or empty
        return accessToken != null && !accessToken.isEmpty();
    }

    @Override
    public String getUserInfo(String accessToken) {
        // In a real implementation, you would parse the JWT token or call the Microsoft Graph API
        // For simplicity, we'll return a placeholder JSON
        return "{\"name\":\"User\",\"email\":\"user@example.com\"}";
    }

    @Override
    public String getLogoutUrl() {
        try {
            return String.format("%s?post_logout_redirect_uri=%s", 
                    logoutUri, 
                    new URI(postLogoutRedirectUri).toURL());
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate logout URL", e);
            throw new RuntimeException("Failed to generate logout URL", e);
        }
    }
}

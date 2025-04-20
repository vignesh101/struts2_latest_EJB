package com.azuread.integration.util;

import com.azuread.integration.service.AzureADAuthService;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for EJB lookups via JNDI.
 */
public class EJBLookupUtil {

    private static final Logger LOGGER = Logger.getLogger(EJBLookupUtil.class.getName());
    
    // JNDI EJB lookup pattern
    private static final String EJB_JNDI_PATTERN = 
            "java:global/azuread-ejb/AzureADAuthServiceImpl!com.azuread.integration.service.AzureADAuthService";

    private static AzureADAuthService authService;

    /**
     * Gets the AzureADAuthService EJB instance.
     * Uses singleton pattern to avoid multiple lookups.
     *
     * @return The AzureADAuthService instance
     * @throws RuntimeException if lookup fails
     */
    public static synchronized AzureADAuthService getAuthService() {
        if (authService == null) {
            try {
                Properties props = new Properties();
                // Set up JNDI properties if needed for your application server
                Context context = new InitialContext(props);
                authService = (AzureADAuthService) context.lookup(EJB_JNDI_PATTERN);
            } catch (NamingException e) {
                LOGGER.log(Level.SEVERE, "Failed to lookup AzureADAuthService EJB", e);
                throw new RuntimeException("Failed to lookup AzureADAuthService EJB", e);
            }
        }
        return authService;
    }
}

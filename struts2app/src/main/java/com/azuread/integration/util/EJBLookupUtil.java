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
    
    // JNDI EJB lookup pattern for WildFly
    private static final String EJB_JNDI_PATTERN = 
            "java:global/azuread-ejb-1.0-SNAPSHOT/AzureADAuthServiceImpl!com.azuread.integration.service.AzureADAuthService";

    // Fallback JNDI patterns if the primary one fails
    private static final String[] FALLBACK_JNDI_PATTERNS = {
            "java:app/azuread-ejb-1.0-SNAPSHOT/AzureADAuthServiceImpl!com.azuread.integration.service.AzureADAuthService",
            "java:global/azuread-ejb/AzureADAuthServiceImpl!com.azuread.integration.service.AzureADAuthService",
            "java:module/AzureADAuthServiceImpl!com.azuread.integration.service.AzureADAuthService"
    };

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
                // Set up JNDI properties for WildFly
                props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
                // Use an empty URL for local connections
                props.put(Context.PROVIDER_URL, "remote+http://localhost:8080");
                
                Context context = new InitialContext(props);
                
                try {
                    // Try the primary JNDI name first
                    authService = (AzureADAuthService) context.lookup(EJB_JNDI_PATTERN);
                    LOGGER.info("Successfully looked up EJB using primary JNDI name: " + EJB_JNDI_PATTERN);
                } catch (NamingException e) {
                    // Try fallback JNDI names if the primary one fails
                    LOGGER.warning("Primary JNDI lookup failed, trying fallback patterns: " + e.getMessage());
                    
                    for (String jndiPattern : FALLBACK_JNDI_PATTERNS) {
                        try {
                            authService = (AzureADAuthService) context.lookup(jndiPattern);
                            LOGGER.info("Successfully looked up EJB using fallback JNDI name: " + jndiPattern);
                            break;
                        } catch (NamingException ne) {
                            LOGGER.warning("Fallback JNDI lookup failed for pattern: " + jndiPattern);
                        }
                    }
                    
                    // If all lookups failed, throw exception
                    if (authService == null) {
                        throw new RuntimeException("All JNDI lookup attempts for AzureADAuthService failed", e);
                    }
                }
            } catch (NamingException e) {
                LOGGER.log(Level.SEVERE, "Failed to lookup AzureADAuthService EJB", e);
                throw new RuntimeException("Failed to lookup AzureADAuthService EJB", e);
            }
        }
        return authService;
    }
}

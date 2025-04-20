# Struts 2 Azure AD Integration - Deployment Guide

This document provides instructions for deploying the Struts 2 application with Azure AD authentication and EJB backend.

## Prerequisites

- JDK 1.8
- Maven 3.6+
- Application server that supports EJB 3.2 and Servlet 4.0 (e.g., WildFly, GlassFish, WebSphere Liberty)
- Azure AD tenant with an application registration

## Azure AD Configuration

Before deploying the application, you need to register an application in Azure AD:

1. Sign in to the [Azure Portal](https://portal.azure.com/)
2. Navigate to Azure Active Directory > App registrations
3. Click "New registration"
4. Enter a name for your application
5. Select the supported account types (e.g., "Accounts in this organizational directory only")
6. Set the redirect URI to `http://localhost:8080/struts2app`
7. Click "Register"

After registration, note down the following information from the Overview page:
- Application (client) ID
- Directory (tenant) ID

Then, create a client secret:
1. Navigate to "Certificates & secrets"
2. Click "New client secret"
3. Enter a description and select an expiration period
4. Click "Add"
5. Copy the secret value (it will only be shown once)

Update the Azure AD configuration file:
1. Copy `azuread-ejb/src/main/resources/azuread.properties.template` to `azuread-ejb/src/main/resources/azuread.properties`
2. Update the following properties with your Azure AD information:
   ```properties
   azure.ad.client-id=<your-client-id>
   azure.ad.client-secret=<your-client-secret>
   azure.ad.tenant-id=<your-tenant-id>
   azure.ad.issuer-uri=https://login.microsoftonline.com/<your-tenant-id>
   azure.ad.logout-uri=https://login.microsoftonline.com/<your-tenant-id>/oauth2/logout
   ```

## Building the Application

Build the application using Maven:

```bash
mvn clean install
```

This will create:
- `azuread-ejb/target/azuread-ejb-1.0-SNAPSHOT.jar` - EJB module
- `struts2app/target/struts2app-1.0-SNAPSHOT.war` - Web module

## Deployment to Application Server

### WildFly

1. Start WildFly:
   ```bash
   <wildfly-home>/bin/standalone.sh
   ```

2. Deploy the EJB module:
   ```bash
   <wildfly-home>/bin/jboss-cli.sh --connect --command="deploy azuread-ejb/target/azuread-ejb-1.0-SNAPSHOT.jar"
   ```

3. Deploy the web module:
   ```bash
   <wildfly-home>/bin/jboss-cli.sh --connect --command="deploy struts2app/target/struts2app-1.0-SNAPSHOT.war"
   ```

### GlassFish

1. Start GlassFish:
   ```bash
   <glassfish-home>/bin/asadmin start-domain
   ```

2. Deploy the EJB module:
   ```bash
   <glassfish-home>/bin/asadmin deploy azuread-ejb/target/azuread-ejb-1.0-SNAPSHOT.jar
   ```

3. Deploy the web module:
   ```bash
   <glassfish-home>/bin/asadmin deploy struts2app/target/struts2app-1.0-SNAPSHOT.war
   ```

## Accessing the Application

After deployment, access the application at:

```
http://localhost:8080/struts2app
```

The application will redirect to Azure AD login page. After successful authentication, you will be redirected back to the application home page.

## Troubleshooting

### JNDI Lookup Errors

If you encounter JNDI lookup errors, check the EJB JNDI name in `EJBLookupUtil.java`. The default JNDI pattern is:

```
java:global/azuread-ejb/AzureADAuthServiceImpl!com.azuread.integration.service.AzureADAuthService
```

You may need to adjust this pattern depending on your application server. For example:

- WildFly/JBoss: `java:global/azuread-ejb/AzureADAuthServiceImpl!com.azuread.integration.service.AzureADAuthService`
- GlassFish: `java:global/azuread-ejb-1.0-SNAPSHOT/AzureADAuthServiceImpl!com.azuread.integration.service.AzureADAuthService`
- WebSphere: `ejb/AzureADAuthServiceImpl!com.azuread.integration.service.AzureADAuthService`

### Azure AD Authentication Issues

- Verify the redirect URI in Azure AD matches the application URL
- Check that the client ID, tenant ID, and client secret are correct
- Ensure the application has the required API permissions (Microsoft Graph > User.Read)
- Review application logs for detailed error messages

## Security Considerations

- Never commit `azuread.properties` with actual credentials to the repository
- Use environment-specific properties files for different environments
- Regularly rotate the client secret
- Use HTTPS in production environments

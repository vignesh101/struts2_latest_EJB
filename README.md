# Struts 2 with Azure AD Authentication and EJB Backend

This project demonstrates a Struts 2 web application with Azure AD authentication using EJB for the backend business logic. The frontend and backend communicate through RMI.

## Project Structure

- **Parent Project**: `struts2_latest_EJB`
- **EJB Module**: `azuread-ejb` - Handles Azure AD authentication business logic
- **Web Module**: `struts2app` - Struts 2 application for UI and frontend interactions

## Requirements

- JDK 1.8
- Maven 3.6+
- Application Server supporting EJB 3.2 and Servlet 4.0 (e.g., WildFly, GlassFish)

## Azure AD Configuration

Before running the application, you need to set up your Azure AD configuration:

1. Copy the file `azuread-ejb/src/main/resources/azuread.properties.template` to `azuread-ejb/src/main/resources/azuread.properties`
2. Edit the properties file with your Azure AD credentials:

```properties
azure.ad.client-id=840df7a7-c2c0-4a26-a51f-d4e0b3025380
azure.ad.client-secret=YOUR_CLIENT_SECRET
azure.ad.tenant-id=6bc4dbe1-88bd-4899-9ff1-8c50c704940b
azure.ad.issuer-uri=https://login.microsoftonline.com/6bc4dbe1-88bd-4899-9ff1-8c50c704940b
azure.ad.logout-uri=https://login.microsoftonline.com/6bc4dbe1-88bd-4899-9ff1-8c50c704940b/oauth2/logout
azure.ad.post-logout-redirect-uri=http://localhost:8080/struts2app/auth/callback
azure.ad.scopes=User.Read,profile,email,openid
azure.ad.redirect-uri=http://localhost:8080/struts2app
```

**Note**: Never commit the `azuread.properties` file with actual credentials to the repository.

## Building the Project

```bash
mvn clean install
```

## Deployment

1. Deploy the EJB module first
2. Deploy the web module

## Features

- Azure AD login
- Azure AD logout
- Authentication callback handler
- Home page for authenticated users
- RMI communication between Struts 2 frontend and EJB backend

## Application Flow

1. User accesses the application
2. User is redirected to Azure AD login
3. After authentication, user is redirected back to the application
4. The EJB backend processes the authentication token
5. User is redirected to the home page if authentication is successful

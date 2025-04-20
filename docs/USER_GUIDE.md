# Struts 2 Azure AD Integration - User Guide

This document provides instructions on how to use the Struts 2 application with Azure AD authentication.

## Accessing the Application

Access the application through your web browser at:

```
http://localhost:8080/struts2app
```

If you're using a different server or port, adjust the URL accordingly.

## Authentication Flow

1. When you access the application, you'll be automatically redirected to the Azure AD login page.
2. Enter your Azure AD credentials (email and password).
3. If multi-factor authentication is enabled for your account, you'll be prompted to complete the additional authentication step.
4. After successful authentication, you'll be redirected back to the application.
5. The application will verify your authentication token and display the home page.

## Home Page

The home page displays:
- Your name and email retrieved from Azure AD
- A welcome message confirming successful authentication
- A logout button

## Logging Out

To log out from the application:

1. Click the "Logout" button in the upper right corner of the home page.
2. You'll be redirected to the Azure AD logout page.
3. After Azure AD logs you out, you'll be redirected back to the application.
4. Your session will be invalidated, and you'll need to log in again to access the application.

## Error Handling

If an error occurs during authentication or within the application, you'll be redirected to an error page displaying:
- An error message describing the issue
- A "Try Again" button to restart the authentication process

Common errors include:
- Invalid or expired authentication token
- Missing required permissions
- Network connectivity issues
- Azure AD service unavailability

## Session Timeout

Your session will expire after 30 minutes of inactivity. If you try to access the application after your session has expired, you'll be redirected to the login page again.

## Troubleshooting

### Browser-related Issues

- Ensure cookies are enabled in your browser.
- Clear browser cache and cookies if you encounter unexpected behavior.
- Try using a different browser if issues persist.

### Authentication Problems

- Verify you're using the correct Azure AD credentials.
- Ensure your account has the necessary permissions to access the application.
- Check with your administrator if your account is properly configured in Azure AD.

### Application Errors

If you encounter application errors after successful authentication, contact your system administrator with the following information:
- Error message displayed
- Steps to reproduce the issue
- Browser and operating system information
- Time when the error occurred

## Security Best Practices

- Always log out from the application when you're done.
- Do not share your Azure AD credentials with others.
- Access the application from secure networks.
- Report any suspicious behavior or security concerns to your administrator.

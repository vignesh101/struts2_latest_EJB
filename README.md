# Struts 2 with Azure AD Authentication and EJB Backend

This project demonstrates integration between Struts 2, Azure AD authentication, and an EJB backend service. The application uses RMI to communicate between the Struts 2 web layer and the EJB business logic layer that handles Azure AD authentication.

## Architecture

The system consists of two main components:

1. **EJB Backend (azuread-ejb)**:
   - Handles Azure AD authentication business logic
   - Communicates with Microsoft Authentication Library (MSAL) for Java
   - Provides remote services for authentication, token validation, and user information retrieval
   - Deployed as a stateless session EJB

2. **Struts 2 Frontend (struts2app)**:
   - Manages the web interface and user interactions
   - Uses RMI to call EJB services
   - Handles user session management
   - Provides the login, callback, and home pages

## Features

- Azure AD single sign-on (SSO) authentication
- Integration with Microsoft Authentication Library (MSAL)
- RMI-based communication between web and EJB tiers
- Session management with CSRF protection
- Logout functionality with Azure AD sign-out
- Environment-specific deployment configurations
- Comprehensive logging

## Technical Stack

- Java 8 (JDK 1.8)
- Struts 2.5.30
- EJB 3.2
- Microsoft Authentication Library (MSAL) for Java 1.13.10
- Log4j 2.20.0
- Maven 3.6+
- Application Server supporting EJB 3.2 and Servlet 4.0 (e.g., WildFly, GlassFish)

## Getting Started

### Prerequisites

- JDK 1.8 installed
- Maven 3.6+ installed
- Application server supporting EJB 3.2 and Servlet 4.0
- Azure AD tenant with application registration

### Azure AD Configuration

Before running the application, you need to set up Azure AD:

1. Copy `azuread-ejb/src/main/resources/azuread.properties.template` to `azuread-ejb/src/main/resources/azuread.properties`
2. Edit the properties file with your Azure AD credentials:

```properties
azure.ad.client-id=YOUR_CLIENT_ID
azure.ad.client-secret=YOUR_CLIENT_SECRET
azure.ad.tenant-id=YOUR_TENANT_ID
azure.ad.issuer-uri=https://login.microsoftonline.com/YOUR_TENANT_ID
azure.ad.logout-uri=https://login.microsoftonline.com/YOUR_TENANT_ID/oauth2/logout
azure.ad.post-logout-redirect-uri=http://localhost:8080/struts2app/auth/callback
azure.ad.scopes=User.Read,profile,email,openid
azure.ad.redirect-uri=http://localhost:8080/struts2app
```

See the [Deployment Guide](docs/DEPLOYMENT.md) for detailed instructions on setting up Azure AD.

### Building the Project

```bash
mvn clean install
```

### Deployment

1. Deploy the EJB module first
2. Deploy the web module
3. Access the application at `http://localhost:8080/struts2app`

See the [Deployment Guide](docs/DEPLOYMENT.md) for detailed deployment instructions.

### Using the Application

Refer to the [User Guide](docs/USER_GUIDE.md) for instructions on using the application.

## Project Structure

```
struts2_latest_EJB/
├── azuread-ejb/                        # EJB module
│   ├── src/main/java/
│   │   └── com/azuread/integration/
│   │       └── service/                # Service interfaces
│   │       └── service/impl/           # Service implementations
│   └── src/main/resources/
│       └── azuread.properties.template # Azure AD configuration template
│       └── META-INF/
│           └── ejb-jar.xml             # EJB deployment descriptor
│
├── struts2app/                         # Struts 2 web module
│   ├── src/main/java/
│   │   └── com/azuread/integration/
│   │       └── action/                 # Struts 2 actions
│   │       └── interceptor/            # Struts 2 interceptors
│   │       └── model/                  # Domain model objects
│   │       └── util/                   # Utility classes
│   ├── src/main/resources/
│   │   └── struts.xml                  # Struts 2 configuration
│   │   └── log4j2.xml                  # Logging configuration
│   └── src/main/webapp/
│       └── WEB-INF/
│           └── web.xml                 # Web application descriptor
│           └── pages/                  # JSP pages
│               └── home.jsp            # Home page for authenticated users
│               └── error.jsp           # Error page
│
├── docs/                               # Documentation
│   └── DEPLOYMENT.md                   # Deployment guide
│   └── USER_GUIDE.md                   # User guide
│
└── pom.xml                             # Parent Maven POM
```

## Authentication Flow

1. User accesses the application
2. Application redirects to Azure AD login page
3. User authenticates with Azure AD
4. Azure AD redirects back to the application with an authorization code
5. Application exchanges the code for an access token via the EJB service
6. Application validates the token and extracts user information
7. User is directed to the home page upon successful authentication

## Development Notes

- The application uses stateless session EJB for Azure AD integration
- RMI is used for communication between the web and EJB tiers
- JNDI lookup is used to access the remote EJB from the web application
- Session management includes CSRF protection with state parameters

## Security Considerations

- Sensitive configuration is separated into environment-specific properties files
- CSRF protection is implemented for authentication callbacks
- Client secrets should be rotated regularly
- Use HTTPS in production environments

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin feature/your-feature`
5. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

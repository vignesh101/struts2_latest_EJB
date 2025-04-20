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
- WildFly server support

## Technical Stack

- Java 8 (JDK 1.8)
- Struts 2.5.30
- EJB 3.2
- Microsoft Authentication Library (MSAL) for Java 1.13.10
- Log4j 2.20.0
- Maven 3.6+
- WildFly Application Server 26.x+ (recommended)

## Getting Started

### Prerequisites

- JDK 1.8 installed
- Maven 3.6+ installed
- WildFly Application Server 26.x or later
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

### WildFly Deployment

#### Automated Deployment

We provide automated deployment scripts for both Linux/macOS and Windows systems:

##### Linux/macOS:
```bash
# Make the script executable
chmod +x deploy-to-wildfly.sh

# Run the deployment script
./deploy-to-wildfly.sh
```

##### Windows:
```cmd
# Run the deployment script
deploy-to-wildfly.bat
```

The scripts will:
1. Start WildFly if it's not already running
2. Build the project with Maven
3. Create the MSAL4J module in WildFly if it doesn't exist
4. Undeploy any existing versions of the application
5. Deploy the EJB module and web module

#### Manual Deployment

For manual deployment instructions, see the [WildFly Deployment Guide](docs/WILDFLY_DEPLOYMENT.md).

### Using the Application

After deployment, access the application at:
```
http://localhost:8080/struts2app
```

Refer to the [User Guide](docs/USER_GUIDE.md) for detailed instructions on using the application.

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
│           └── jboss-ejb3.xml          # JBoss/WildFly EJB descriptor
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
│           └── jboss-web.xml           # JBoss/WildFly web descriptor
│           └── jboss-deployment-structure.xml # WildFly deployment structure
│           └── pages/                  # JSP pages
│               └── home.jsp            # Home page for authenticated users
│               └── error.jsp           # Error page
│
├── docs/                               # Documentation
│   └── DEPLOYMENT.md                   # General deployment guide
│   └── WILDFLY_DEPLOYMENT.md           # WildFly-specific deployment guide
│   └── USER_GUIDE.md                   # User guide
│
├── deploy-to-wildfly.sh                # Linux/macOS deployment script
├── deploy-to-wildfly.bat               # Windows deployment script
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

## WildFly Configuration

The application includes specific configurations for optimal deployment on WildFly:

1. **JBoss/WildFly Deployment Descriptors**: 
   - `jboss-web.xml` - Configures context path and security domain
   - `jboss-ejb3.xml` - Configures EJB JNDI bindings
   - `jboss-deployment-structure.xml` - Controls module dependencies

2. **MSAL4J Module**:
   - The deployment scripts create a custom WildFly module for MSAL4J and its dependencies
   - This ensures proper class loading and avoids classpath conflicts

3. **EJB Lookup Enhancements**:
   - The `EJBLookupUtil` includes fallback JNDI patterns for WildFly
   - Handles WildFly-specific JNDI naming conventions

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

## Troubleshooting

For WildFly-specific troubleshooting tips, please refer to the [WildFly Deployment Guide](docs/WILDFLY_DEPLOYMENT.md).

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin feature/your-feature`
5. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

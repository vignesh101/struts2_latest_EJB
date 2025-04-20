# WildFly Deployment Guide for Struts 2 with Azure AD

This guide provides step-by-step instructions for deploying the Struts 2 Azure AD integration application on WildFly server.

## Prerequisites

- JDK 1.8
- Maven 3.6+
- WildFly 26.x or later (recommended)
- Azure AD tenant with application registration

## WildFly Installation

1. Download WildFly from the [official website](https://www.wildfly.org/downloads/).
2. Extract the archive to your preferred location.
3. Set the `JBOSS_HOME` environment variable to point to your WildFly installation directory.

## Azure AD Configuration

Before deployment, configure the Azure AD properties:

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

## Building the Application

1. Build the application using Maven:

```bash
mvn clean package
```

This will create:
- `azuread-ejb/target/azuread-ejb-1.0-SNAPSHOT.jar` - EJB module
- `struts2app/target/struts2app-1.0-SNAPSHOT.war` - Web module

## WildFly Configuration

### Start WildFly

Start WildFly in standalone mode:

```bash
$JBOSS_HOME/bin/standalone.sh
```

On Windows:
```
%JBOSS_HOME%\bin\standalone.bat
```

### Add MSAL4J Module

Since we're using Microsoft Authentication Library (MSAL4J), we need to create a module for it in WildFly:

1. Create module directory:

```bash
mkdir -p $JBOSS_HOME/modules/system/layers/base/com/microsoft/azure/msal4j/main
```

2. Copy the MSAL4J JAR and its dependencies to the module directory:

```bash
# Run Maven dependency plugin to copy the JAR and its dependencies
mvn dependency:copy-dependencies -DincludeArtifactIds=msal4j,oauth2-oidc-sdk,nimbus-jose-jwt,json-smart,accessors-smart,asm,lang-tag -DoutputDirectory=$JBOSS_HOME/modules/system/layers/base/com/microsoft/azure/msal4j/main
```

3. Create the `module.xml` file in the module directory:

```bash
cat > $JBOSS_HOME/modules/system/layers/base/com/microsoft/azure/msal4j/main/module.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<module name="com.microsoft.azure.msal4j" xmlns="urn:jboss:module:1.1">
    <resources>
        <resource-root path="msal4j-1.13.10.jar"/>
        <resource-root path="oauth2-oidc-sdk-9.35.jar"/>
        <resource-root path="nimbus-jose-jwt-9.25.6.jar"/>
        <resource-root path="json-smart-2.4.7.jar"/>
        <resource-root path="accessors-smart-2.4.7.jar"/>
        <resource-root path="asm-9.1.jar"/>
        <resource-root path="lang-tag-1.5.jar"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
        <module name="javax.json.api"/>
        <module name="javax.servlet.api"/>
        <module name="org.slf4j"/>
        <module name="com.fasterxml.jackson.core.jackson-databind"/>
    </dependencies>
</module>
EOF
```

### Configure JNDI for EJB Module

Add the JNDI binding for the EJB module in the WildFly standalone configuration:

1. Connect to the WildFly CLI:

```bash
$JBOSS_HOME/bin/jboss-cli.sh --connect
```

2. Add the following configuration:

```
/subsystem=naming/binding=java\:global\/azuread-ejb\/AzureADAuthServiceImpl:add(binding-type=lookup, lookup=java:app/azuread-ejb-1.0-SNAPSHOT/AzureADAuthServiceImpl!com.azuread.integration.service.AzureADAuthService)
```

## Deployment

### Deploy Using WildFly Management Console

1. Access the WildFly management console at http://localhost:9990
2. Go to Deployments > Add
3. Upload and deploy the EJB module first: `azuread-ejb/target/azuread-ejb-1.0-SNAPSHOT.jar`
4. Then upload and deploy the web module: `struts2app/target/struts2app-1.0-SNAPSHOT.war`

### Deploy Using Command Line

1. Deploy the EJB module:

```bash
$JBOSS_HOME/bin/jboss-cli.sh --connect --command="deploy azuread-ejb/target/azuread-ejb-1.0-SNAPSHOT.jar"
```

2. Deploy the web module:

```bash
$JBOSS_HOME/bin/jboss-cli.sh --connect --command="deploy struts2app/target/struts2app-1.0-SNAPSHOT.war"
```

## Accessing the Application

After deployment, the application will be available at:

```
http://localhost:8080/struts2app
```

## Troubleshooting

### JNDI Lookup Issues

If you encounter JNDI lookup errors, verify the JNDI name in `EJBLookupUtil.java`. For WildFly, the correct JNDI pattern should be:

```java
private static final String EJB_JNDI_PATTERN = 
        "java:global/azuread-ejb-1.0-SNAPSHOT/AzureADAuthServiceImpl!com.azuread.integration.service.AzureADAuthService";
```

### Class Loading Issues

If you encounter class loading issues with the MSAL4J library, ensure:

1. The module was correctly created in WildFly
2. The module is included in the deployment's dependencies

Add the following to `jboss-deployment-structure.xml` in the WAR module:

```xml
<jboss-deployment-structure>
    <deployment>
        <dependencies>
            <module name="com.microsoft.azure.msal4j"/>
        </dependencies>
    </deployment>
</jboss-deployment-structure>
```

### Logging

To increase logging for troubleshooting:

1. Connect to the WildFly CLI:
```bash
$JBOSS_HOME/bin/jboss-cli.sh --connect
```

2. Set the logging level:
```
/subsystem=logging/logger=com.azuread.integration:add(level=DEBUG)
```

## Undeployment

To undeploy the application:

```bash
$JBOSS_HOME/bin/jboss-cli.sh --connect --command="undeploy struts2app-1.0-SNAPSHOT.war"
$JBOSS_HOME/bin/jboss-cli.sh --connect --command="undeploy azuread-ejb-1.0-SNAPSHOT.jar"
```

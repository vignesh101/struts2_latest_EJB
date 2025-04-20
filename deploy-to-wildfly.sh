#!/bin/bash

# Deploy to WildFly script for Struts 2 with Azure AD and EJB

# Set JBOSS_HOME environment variable if not already set
if [ -z "$JBOSS_HOME" ]; then
    read -p "Enter the path to your WildFly installation: " JBOSS_HOME
    export JBOSS_HOME=$JBOSS_HOME
fi

echo "Using WildFly at: $JBOSS_HOME"

# Check if WildFly server is running
if ! pgrep -f "jboss-modules.jar" > /dev/null; then
    echo "Starting WildFly server..."
    $JBOSS_HOME/bin/standalone.sh &
    # Wait for server to start
    sleep 15
    echo "WildFly server started"
else
    echo "WildFly server is already running"
fi

# Build the project
echo "Building the project..."
mvn clean package

# Create MSAL4J module if it doesn't exist
MSAL_MODULE_DIR="$JBOSS_HOME/modules/system/layers/base/com/microsoft/azure/msal4j/main"
if [ ! -d "$MSAL_MODULE_DIR" ]; then
    echo "Creating MSAL4J module..."
    mkdir -p "$MSAL_MODULE_DIR"
    
    # Copy MSAL4J dependencies
    mvn dependency:copy-dependencies -DincludeArtifactIds=msal4j,oauth2-oidc-sdk,nimbus-jose-jwt,json-smart,accessors-smart,asm,lang-tag -DoutputDirectory="$MSAL_MODULE_DIR"
    
    # Create module.xml
    cat > "$MSAL_MODULE_DIR/module.xml" << 'EOF'
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
    echo "MSAL4J module created"
else
    echo "MSAL4J module already exists"
fi

# Connect to WildFly CLI
echo "Connecting to WildFly CLI..."
CLI_COMMAND="$JBOSS_HOME/bin/jboss-cli.sh --connect"

# Check if deployments exist and undeploy them first
$CLI_COMMAND --command="if (outcome == success) of deployment-info --name=struts2app-1.0-SNAPSHOT.war { undeploy struts2app-1.0-SNAPSHOT.war }"
$CLI_COMMAND --command="if (outcome == success) of deployment-info --name=azuread-ejb-1.0-SNAPSHOT.jar { undeploy azuread-ejb-1.0-SNAPSHOT.jar }"

# Deploy EJB module
echo "Deploying EJB module..."
$CLI_COMMAND --command="deploy azuread-ejb/target/azuread-ejb-1.0-SNAPSHOT.jar"

# Deploy web module
echo "Deploying web module..."
$CLI_COMMAND --command="deploy struts2app/target/struts2app-1.0-SNAPSHOT.war"

echo "Deployment complete! Application available at http://localhost:8080/struts2app"

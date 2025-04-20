@echo off
REM Deploy to WildFly script for Struts 2 with Azure AD and EJB (Windows version)

REM Set JBOSS_HOME environment variable if not already set
if "%JBOSS_HOME%" == "" (
    set /p JBOSS_HOME="Enter the path to your WildFly installation: "
)

echo Using WildFly at: %JBOSS_HOME%

REM Check if WildFly server is running
tasklist /FI "IMAGENAME eq java.exe" | find "java.exe" > nul
if errorlevel 1 (
    echo Starting WildFly server...
    start /B cmd /c "%JBOSS_HOME%\bin\standalone.bat"
    REM Wait for server to start
    timeout /t 15
    echo WildFly server started
) else (
    echo WildFly server is already running
)

REM Build the project
echo Building the project...
call mvn clean package

REM Create MSAL4J module if it doesn't exist
set MSAL_MODULE_DIR=%JBOSS_HOME%\modules\system\layers\base\com\microsoft\azure\msal4j\main
if not exist "%MSAL_MODULE_DIR%" (
    echo Creating MSAL4J module...
    mkdir "%MSAL_MODULE_DIR%"
    
    REM Copy MSAL4J dependencies
    call mvn dependency:copy-dependencies -DincludeArtifactIds=msal4j,oauth2-oidc-sdk,nimbus-jose-jwt,json-smart,accessors-smart,asm,lang-tag -DoutputDirectory="%MSAL_MODULE_DIR%"
    
    REM Create module.xml
    echo ^<?xml version="1.0" encoding="UTF-8"?^> > "%MSAL_MODULE_DIR%\module.xml"
    echo ^<module name="com.microsoft.azure.msal4j" xmlns="urn:jboss:module:1.1"^> >> "%MSAL_MODULE_DIR%\module.xml"
    echo     ^<resources^> >> "%MSAL_MODULE_DIR%\module.xml"
    echo         ^<resource-root path="msal4j-1.13.10.jar"/^> >> "%MSAL_MODULE_DIR%\module.xml"
    echo         ^<resource-root path="oauth2-oidc-sdk-9.35.jar"/^> >> "%MSAL_MODULE_DIR%\module.xml"
    echo         ^<resource-root path="nimbus-jose-jwt-9.25.6.jar"/^> >> "%MSAL_MODULE_DIR%\module.xml"
    echo         ^<resource-root path="json-smart-2.4.7.jar"/^> >> "%MSAL_MODULE_DIR%\module.xml"
    echo         ^<resource-root path="accessors-smart-2.4.7.jar"/^> >> "%MSAL_MODULE_DIR%\module.xml"
    echo         ^<resource-root path="asm-9.1.jar"/^> >> "%MSAL_MODULE_DIR%\module.xml"
    echo         ^<resource-root path="lang-tag-1.5.jar"/^> >> "%MSAL_MODULE_DIR%\module.xml"
    echo     ^</resources^> >> "%MSAL_MODULE_DIR%\module.xml"
    echo     ^<dependencies^> >> "%MSAL_MODULE_DIR%\module.xml"
    echo         ^<module name="javax.api"/^> >> "%MSAL_MODULE_DIR%\module.xml"
    echo         ^<module name="javax.json.api"/^> >> "%MSAL_MODULE_DIR%\module.xml"
    echo         ^<module name="javax.servlet.api"/^> >> "%MSAL_MODULE_DIR%\module.xml"
    echo         ^<module name="org.slf4j"/^> >> "%MSAL_MODULE_DIR%\module.xml"
    echo         ^<module name="com.fasterxml.jackson.core.jackson-databind"/^> >> "%MSAL_MODULE_DIR%\module.xml"
    echo     ^</dependencies^> >> "%MSAL_MODULE_DIR%\module.xml"
    echo ^</module^> >> "%MSAL_MODULE_DIR%\module.xml"
    
    echo MSAL4J module created
) else (
    echo MSAL4J module already exists
)

REM Connect to WildFly CLI
echo Connecting to WildFly CLI...
set CLI_COMMAND=%JBOSS_HOME%\bin\jboss-cli.bat --connect

REM Check if deployments exist and undeploy them first
call %CLI_COMMAND% --command="if (outcome == success) of deployment-info --name=struts2app-1.0-SNAPSHOT.war { undeploy struts2app-1.0-SNAPSHOT.war }"
call %CLI_COMMAND% --command="if (outcome == success) of deployment-info --name=azuread-ejb-1.0-SNAPSHOT.jar { undeploy azuread-ejb-1.0-SNAPSHOT.jar }"

REM Deploy EJB module
echo Deploying EJB module...
call %CLI_COMMAND% --command="deploy azuread-ejb\target\azuread-ejb-1.0-SNAPSHOT.jar"

REM Deploy web module
echo Deploying web module...
call %CLI_COMMAND% --command="deploy struts2app\target\struts2app-1.0-SNAPSHOT.war"

echo Deployment complete! Application available at http://localhost:8080/struts2app

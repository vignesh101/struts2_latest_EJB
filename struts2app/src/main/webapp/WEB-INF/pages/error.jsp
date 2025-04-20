<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - Azure AD Authentication</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 600px;
            margin: 0 auto;
            background-color: #fff;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        h1 {
            color: #d9534f;
            margin-bottom: 20px;
        }
        .error-message {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .btn {
            display: inline-block;
            padding: 8px 15px;
            border-radius: 4px;
            text-decoration: none;
            cursor: pointer;
            margin-right: 10px;
        }
        .btn-primary {
            background-color: #337ab7;
            color: white;
            border: none;
        }
        .btn-primary:hover {
            background-color: #2e6da4;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Error</h1>
        
        <div class="error-message">
            <s:if test="errorMessage != null">
                <p><s:property value="errorMessage"/></p>
            </s:if>
            <s:elseif test="hasActionErrors()">
                <s:iterator value="actionErrors">
                    <p><s:property/></p>
                </s:iterator>
            </s:elseif>
            <s:else>
                <p>An unexpected error has occurred during authentication with Azure AD.</p>
            </s:else>
        </div>
        
        <div>
            <a href="<s:url action='login'/>" class="btn btn-primary">Try Again</a>
        </div>
    </div>
</body>
</html>

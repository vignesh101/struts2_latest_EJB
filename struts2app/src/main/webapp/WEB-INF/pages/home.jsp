<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home - Azure AD Authentication</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background-color: #fff;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 1px solid #eee;
        }
        h1 {
            color: #333;
        }
        .user-info {
            background-color: #f9f9f9;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .user-info p {
            margin: 5px 0;
        }
        .logout-btn {
            background-color: #d9534f;
            color: white;
            border: none;
            padding: 8px 15px;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
        }
        .logout-btn:hover {
            background-color: #c9302c;
        }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <h1>Welcome to Struts 2 with Azure AD</h1>
            <a href="<s:url action='logout'/>" class="logout-btn">Logout</a>
        </header>
        
        <div class="user-info">
            <h2>User Information</h2>
            <p><strong>Name:</strong> <s:property value="user.name"/></p>
            <p><strong>Email:</strong> <s:property value="user.email"/></p>
        </div>
        
        <div class="content">
            <h2>Home Page</h2>
            <p>You have successfully authenticated with Azure AD using the EJB backend service.</p>
            <p>This is a secure page that requires authentication.</p>
            <p>The authentication flow uses RMI to communicate between the Struts 2 frontend and EJB backend.</p>
        </div>
    </div>
</body>
</html>

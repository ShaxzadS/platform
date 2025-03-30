<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Grand Mall - Registration</title>
    <link rel="stylesheet" type="text/css" href="/static/styles.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            color: #333;
            padding: 20px;
            margin: 0;
        }
        h1 {
            text-align: center;
            color: #007bff;
        }
        h4 {
            text-align: center;
            color: #555;
        }
        form {
            max-width: 400px;
            margin: 0 auto;
            background-color: #fff;
            padding: 20px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            border-radius: 8px;
        }
        input[type="text"], input[type="email"], input[type="number"], input[type="password"] {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
        }
        input[type="submit"] {
            background-color: #007bff;
            color: white;
            padding: 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            width: 100%;
        }
        input[type="submit"]:hover {
            background-color: #0056b3;
        }
        h2 {
            text-align: center;
            color: red;
        }
    </style>
</head>
<body>

<h1>Grand Mall</h1>
<h4>Registration</h4>

<form action="/registration" method="post">
    <label for="name">Name of User:</label>
    <input type="text" id="name" name="name" required><br>

    <label for="email">Email:</label>
    <input type="email" id="email" name="email" required><br>

    <label for="phoneNumber">Phone Number:</label>
    <input type="number" id="phoneNumber" name="phoneNumber" required><br>

    <label for="password">Password:</label>
    <input type="password" id="password" name="password" required><br>

    <input type="hidden" name="_csrf" value="${_csrf.token}">
    <input type="submit" value="Sign Up">
</form>

<!-- Display error message if it exists -->
<#if errorMessage??>
    <div style="color: red; text-align: center; margin-top: 20px;">
        ${errorMessage}
    </div>
</#if>


</body>
</html>

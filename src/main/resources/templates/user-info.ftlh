<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Grand Mall - Подробная информация о пользователе</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            color: #333;
            padding: 20px;
            margin: 0;
        }
        h1, h2 {
            text-align: center;
            color: #007bff;
        }
        hr {
            margin-bottom: 20px;
        }
        b {
            color: #333;
        }
        .user-info {
            max-width: 600px;
            margin: 20px auto;
            background-color: #fff;
            padding: 20px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            border-radius: 8px;
        }
        .products-list {
            max-width: 600px;
            margin: 20px auto;
            padding: 10px;
        }
        .products-list div {
            background-color: #fafafa;
            padding: 10px;
            margin-bottom: 10px;
            border-radius: 4px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        a {
            color: #007bff;
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
        }
        h2.error {
            color: red;
            text-align: center;
        }
    </style>
</head>
<body>

<h1>Grand Mall</h1><hr>

<div class="user-info">
    <h2>Подробная информация о пользователе ${user.name}</h2>
    <b>Номер телефона: </b> ${user.phoneNumber} <br>
    <b>Email: </b> ${user.email} <br>
    <b>Имя пользователя: </b> ${user.name} <br>
</div>

<div class="products-list">
    <h2>Товары пользователя ${user.name}</h2>
    <#list products as product>
        <div>
            <p><b>${product.title}</b> - ${product.price} | <a href="/product/${product.id}">Подробнее...</a></p>
        </div>
    <#else>
        <p>У пользователя пока нет товаров.</p>
    </#list>
</div>

<#if errorMessage??>
    <h2 class="error">${errorMessage}</h2>
</#if>

</body>
</html>

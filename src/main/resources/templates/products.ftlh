<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Grand Mall</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            color: #333;
            margin: 0;
            padding: 20px;
        }
        h1 {
            text-align: center;
            color: #007bff;
        }
        hr {
            border: none;
            border-top: 2px solid #007bff;
            margin-bottom: 20px;
        }
        a {
            color: #007bff;
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
        }
        form {
            margin-bottom: 20px;
        }
        input[type="text"], input[type="number"], input[type="file"] {
            width: 100%;
            padding: 10px;
            margin-bottom: 10px;
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
        .product {
            background-color: #fff;
            padding: 15px;
            margin-bottom: 15px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            border-radius: 8px;
        }
        .product b {
            font-size: 1.2em;
        }
        .search-bar {
            margin-bottom: 20px;
        }
    </style>
</head>
<body>

<h1>Grand Mall</h1>
<hr>
<#if user?exists && user.email?exists>
    <h2>UserName: <i>${user.name}</i></h2>
    <form action="logout" method="post">
        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <input type="submit" value="Logout">
    </form>
    <#if user.isAdmin()>
        <a href="/admin">Панель админа</a>
    </#if>
<#else>
    <a href="/login">Login</a>
</#if>

<h2>Все товары</h2>
<form action="/" method="get" class="search-bar">
    Search: <input type="text" name="title" placeholder="Поиск товаров...">
    <input type="submit" value="Search">
</form>

<#list products as product>
    <div class="product">
        <p><b>${product.title}</b> - ${product.price} ₸ | <a href="/product/${product.id}">Подробнее...</a></p>
    </div>
<#else>
    <h3>Empty</h3>
</#list>

<#if user.email??>
    <hr>
    <h3>Добавить новый товар</h3>
    <form action="/product/add" method="post" enctype="multipart/form-data">
        <label for="title">Название:</label>
        <input type="text" id="title" name="title" required><br>

        <label for="description">Описание:</label>
        <input type="text" id="description" name="description" required><br>

        <label for="price">Цена:</label>
        <input type="number" id="price" name="price" required><br>

        <label for="city">Город:</label>
        <input type="text" id="city" name="city" required><br>

        <label for="author">Автор:</label>
        <input type="text" id="author" name="author" required><br>

        <label for="file1">First photo:</label>
        <input type="file" id="file1" name="file1" required><br>

        <label for="file2">Second photo:</label>
        <input type="file" id="file2" name="file2"><br>

        <label for="file3">Third photo:</label>
        <input type="file" id="file3" name="file3"><br>

        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <input type="submit" value="Добавить товар">
    </form>
</#if>

</body>
</html>

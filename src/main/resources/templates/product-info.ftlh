<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Grand Mall - О товаре</title>
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
        h2 {
            color: #555;
        }
        hr {
            border: none;
            border-top: 2px solid #007bff;
        }
        .product-container {
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            background-color: #fff;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            border-radius: 8px;
        }
        img {
            max-width: 100%;
            height: auto;
            display: block;
            margin-bottom: 20px;
        }
        b {
            color: #555;
        }
        a {
            color: #007bff;
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
        }
        .product-details {
            margin-top: 20px;
            line-height: 1.6;
        }
    </style>
</head>
<body>

<h1>Grand Mall</h1><hr>
<div class="product-container">
    <h2>О товаре</h2>
    <#list images as img>
        <img src="/images/${img.id}" alt="Product Image" height="60px"/><br><br>
    </#list>

    <div class="product-details">
        <b>Название товара:</b> ${product.title}<br>
        <b>Описание товара:</b> ${product.description}<br>
        <b>Цена:</b> ${product.price} ₸<br>
        <b>Город:</b> ${product.city}<br>
        <b>Автор:</b> <a href="/user/${product.user.id}">${product.user.name}</a><br>
    </div>
</div>

</body>
</html>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${product.title}</title>
</head>
<body>
<h1>${product.title}</h1>

<#if product.images?size gt 0>
    <img src="/images/${product.images[0].id}" alt="${product.title}" style="max-width:400px;">
</#if>

<p><strong>Описание:</strong> ${product.description}</p>
<p><strong>Цена:</strong> ${product.price} ₸</p>

<p>
    Автор:
    <a href="/user/${product.user.id}">
        ${product.user.name}
    </a>
</p>

<a href="/">⬅️ Назад</a>
</body>
</html>

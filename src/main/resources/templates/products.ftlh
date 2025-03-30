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
            padding: 0;
            display: flex;
        }

        /* Левое вертикальное меню */
        nav {
            background-color: #0056b3;
            width: 200px;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            padding: 20px;
            box-sizing: border-box;
        }

        nav a {
            color: white;
            font-weight: bold;
            text-decoration: none;
            margin-bottom: 15px;
        }

        nav a:hover {
            text-decoration: underline;
        }

        .content {
            flex-grow: 1;
            display: flex;
            flex-direction: column;
            position: relative;
        }

        header {
            background-color: #007bff;
            color: white;
            padding: 15px 20px;
            position: relative;
        }

        header h1 {
            margin: 0;
            text-align: center;
        }

        /* Logout справа вверху */
        .top-right {
            position: absolute;
            top: 15px;
            right: 20px;
        }

        .top-right form {
            display: inline;
        }

        main {
            padding: 20px;
        }

        hr {
            border: none;
            border-top: 2px solid #007bff;
            margin: 20px 0;
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
            width: auto;
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






        .product img {
            max-width: 200px;
            cursor: pointer;
        }

        .modal {
            display: none;
            position: fixed;
            z-index: 10;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            overflow: auto;
            background-color: rgba(0,0,0,0.7);
        }

        .modal-content {
            background-color: #fff;
            margin: 5% auto;
            padding: 20px;
            border-radius: 10px;
            width: 80%;
            max-width: 600px;
            position: relative;
        }

        .close {
            color: #aaa;
            float: right;
            font-size: 28px;
            font-weight: bold;
            cursor: pointer;
            position: absolute;
            right: 20px;
            top: 10px;
        }
    </style>
</head>
<body>

<nav>
    <a href="/">Главная</a>
    <a href="/profile">Создать</a>
    <a href="/update">Обновление</a>
    <a href="/messages">Сообщения</a>
</nav>

<div class="content">
    <header>
        <h1>Grand Mall</h1>
        <div class="top-right">
            <#if user?exists && user.email?exists>
                <form action="logout" method="post">
                    <input type="hidden" name="_csrf" value="${_csrf.token}">
                    <input type="submit" value="Logout">
                </form>
            </#if>
        </div>
    </header>
    <#if user?exists>
        <a href="/profile">Мой профиль</a>
    </#if>

    <#if user?exists && user.email?exists>
        <h2>UserName: <i>${user.name}</i></h2>
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

    <h2>Список товаров</h2>



    <#if products?? && products?size gt 0>
        <#list products as product>
            <div class="product">
                <!-- Изображение -->
                <#if product.images?size gt 0>
                    <img src="/images/${product.images[0].id}"
                         alt="${product.title}"
                         onclick="openModal('${product.title}', '${product.description}', '/images/${product.images[0].id}', '${product.user.name}', '${product.user.id}', '${product.price}')">
                </#if>

                <!-- Комментарии -->
                <div style="margin-top: 20px;">
                    <h3>Комментарии</h3>

                    <#assign comments = productComments[product.id?string]![]>

                    <#if comments?size gt 0>
                        <#list comments as comment>
                            <p><strong>${comment.user.name}</strong>: ${comment.content}</p>
                        </#list>
                    <#else>
                        <p>Комментариев пока нет.</p>
                    </#if>

                    <form method="post" action="/products/${product.id}/comment">
                        <input type="hidden" name="_csrf" value="${_csrf.token}">
                        <input type="text" name="content" placeholder="Ваш комментарий..." required>
                        <button type="submit">Добавить</button>
                    </form>
                </div>

                <!-- Лайки -->
                <div style="margin-top: 10px;">
                    <form method="post" action="/products/${product.id}/like">
                        <input type="hidden" name="_csrf" value="${_csrf.token}">

                        <#assign productIdStr = product.id?string>

                        <#if likeCounts?? && likedProductIds?seq_contains(product.id)>
                            <button type="submit" style="color: red;">
                                ❤️ ${likeCounts[productIdStr]!0} (Вы лайкнули)
                            </button>
                        <#else>
                            <button type="submit">
                                🤍 ${likeCounts[productIdStr]!0} Лайкнуть
                            </button>
                        </#if>



                    </form>
                </div>


            </div>
        </#list>
    <#else>
        <p>Нет доступных продуктов.</p>
    </#if>


    <#--    <#if products?? && products?size gt 0>-->
<#--    <#list products as product>-->
<#--        <div class="product">-->
<#--            <#if product.images?size gt 0>-->
<#--                <img src="/images/${product.images[0].id}"-->
<#--                     alt="${product.title}"-->
<#--                     onclick="openModal('${product.title}', '${product.description}', '/images/${product.images[0].id}', '${product.user.name}', '${product.user.id}', '${product.price}')">-->
<#--            </#if>-->

<#--            <!-- Комментарии &ndash;&gt;-->
<#--            <div style="margin-top: 20px;">-->
<#--                <h3>Комментарии</h3>-->
<#--                <ul id="commentList">-->
<#--                    <#if comments?? && comments?size != 0>-->
<#--                        <#list comments as comment>-->
<#--                            <p><strong>${comment.user.name}</strong>: ${comment.text}</p>-->
<#--                        </#list>-->
<#--                    <#else>-->
<#--                        <p>Комментариев пока нет.</p>-->
<#--                    </#if>-->
<#--                </ul>-->

<#--                <form method="post" action="/products/${product.id}/comment">-->
<#--                    <input type="hidden" name="_csrf" value="${_csrf.token}">-->
<#--                    <input type="text" name="content" placeholder="Ваш комментарий..." required>-->
<#--                    <button type="submit">Добавить</button>-->
<#--                </form>-->

<#--                <hr>-->

<#--                <!-- Список комментариев &ndash;&gt;-->



<#--            </div>-->

<#--            <!-- Лайки &ndash;&gt;-->
<#--            <div>-->
<#--                <form method="post" action="/products/${product.id}/like">-->
<#--                    <input type="hidden" name="_csrf" value="${_csrf.token}">-->
<#--                    <button type="submit">-->
<#--                        ❤️ <span>${product.likeCount!0}</span> лайков-->
<#--                    </button>-->
<#--                </form>-->
<#--            </div>-->
<#--        </div>-->
<#--    </#list>-->
<#--    </#if>-->



    <script>
        let currentLikes = 0;
        let comments = [];

        function openModal(title, description, imageUrl, authorName, authorId, price) {
            document.getElementById('modalTitle').innerText = title;
            document.getElementById('modalDescription').innerText = description;
            document.getElementById('modalImage').src = imageUrl;
            document.getElementById('modalAuthor').innerText = authorName;
            document.getElementById('modalAuthorLink').href = '/user/' + authorId;
            document.getElementById('modalPrice').innerText = price;

            // Сброс лайков и комментариев (можно потом связать с backend)
            currentLikes = 0;
            comments = [];

            document.getElementById('likeCount').innerText = currentLikes;
            updateCommentList();

            document.getElementById('productModal').style.display = 'block';
        }

        function closeModal() {
            document.getElementById('productModal').style.display = 'none';
        }

        // Закрытие по клику вне окна
        window.onclick = function(event) {
            const modal = document.getElementById('productModal');
            if (event.target === modal) {
                closeModal();
            }
        }



        function likeProduct() {
            currentLikes++;
            document.getElementById('likeCount').innerText = currentLikes;
        }

        function addComment(event) {
            event.preventDefault();
            const commentInput = document.getElementById('commentInput');
            const text = commentInput.value.trim();
            if (text) {
                comments.push(text);
                updateCommentList();
                commentInput.value = '';
            }
        }

        function updateCommentList() {
            const commentList = document.getElementById('commentList');
            commentList.innerHTML = '';
            comments.forEach(comment => {
                const li = document.createElement('li');
                li.innerText = comment;
                commentList.appendChild(li);
            });
        }

    </script>

</div>



</body>
</html>
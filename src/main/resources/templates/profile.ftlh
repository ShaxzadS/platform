<h1>Профиль пользователя</h1>
<h2>Привет, ${user.name}!</h2>
<hr>

<h3>Ваши товары</h3>
<#list user.products as product>
    <div class="product">
        <p><b>${product.title}</b> - ${product.price} ₸</p>
        <a href="/product/edit/${product.id}">Редактировать</a> |
        <a href="/product/delete/${product.id}">Удалить</a>
    </div>
<#else>
    <h3>У вас пока нет товаров.</h3>
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
<hr>
<a href="/">На главную</a>

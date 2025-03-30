<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Grand Mall - Admin Panel</title>
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
        h4 {
            margin-top: 20px;
            text-align: center;
            color: #555;
        }
        table {
            width: 100%;
            margin: 20px 0;
            border-collapse: collapse;
            background-color: #fff;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #007bff;
            color: white;
        }
        tr:nth-child(even) {
            background-color: #f9f9f9;
        }
        tr:hover {
            background-color: #f1f1f1;
        }
        form {
            display: inline-block;
        }
        input[type="submit"] {
            background-color: #dc3545;
            color: white;
            border: none;
            padding: 8px 16px;
            cursor: pointer;
            border-radius: 4px;
        }
        input[type="submit"]:hover {
            background-color: #c82333;
        }
        a {
            color: #007bff;
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
        }
        .no-users {
            text-align: center;
            margin: 20px;
            font-style: italic;
            color: #888;
        }
    </style>
</head>
<body>

<h1>Grand Mall</h1><hr>
<h4>Панель администратора</h4>
<table>
    <tr>
        <th>Email</th>
        <th>Номер телефона</th>
        <th>Активность</th>
        <th>Роли</th>
        <th>Бан</th>
        <th>Редактирование</th>
        <th>Подробная информация</th>
        <th>Удаление</th> <!-- ✅ Новая колонка -->
    </tr>
    <#list users as user>
        <tr>
            <td>${user.email}</td>
            <td>${user.phoneNumber}</td>
            <td><#if user.active>Активен<#else>Неактивен</#if></td>
            <td><#list user.roles as role>${role}<#sep>, </#list></td>
            <td>
                <form action="/admin/user/ban/${user.id}" method="post">
                    <input type="hidden" name="_csrf" value="${_csrf.token}">
                    <input type="submit" value="БАН">
                </form>
            </td>
            <td><a href="/admin/user/edit/${user.id}">Редактировать</a></td>
            <td><a href="/user/${user.id}">Подробная информация</a></td>
            <td> <!-- ✅ Кнопка для удаления -->
                <form action="/admin/user/delete/${user.id}" method="post">
                    <input type="hidden" name="_csrf" value="${_csrf.token}">
                    <input type="submit" value="Удалить" onclick="return confirm('Вы уверены, что хотите удалить пользователя?')">
                </form>
            </td>
        </tr>
    <#else>
        <tr><td colspan="8" class="no-users">Пользователей нет</td></tr>
    </#list>
</table>
<a href="/" style="
    display: inline-block;
    padding: 10px 20px;
    background-color: #28a745;
    color: white;
    text-decoration: none;
    border-radius: 5px;
    font-weight: bold;
">
    На главную
</a>


</body>
</html>

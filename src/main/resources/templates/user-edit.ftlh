<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Grand Mall - Редактирование пользователя</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            color: #333;
            padding: 20px;
        }
        h1, h2 {
            text-align: center;
            color: #007bff;
        }
        hr {
            margin-bottom: 20px;
        }
        form {
            max-width: 400px;
            margin: 0 auto;
            background-color: #fff;
            padding: 20px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            border-radius: 8px;
        }
        div {
            margin-bottom: 10px;
        }
        label {
            font-size: 16px;
        }
        button {
            background-color: #007bff;
            color: white;
            padding: 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            width: 100%;
        }
        button:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>

<h1>Grand Mall</h1>
<hr>
<h2>Редактирование пользователя: ${user.name}</h2>

<form action="/admin/user/edit" method="post">
    <#list roles as role>
        <div>
            <label>
                <input type="checkbox" name="${role}" ${user.roles?seq_contains(role)?string("checked", "")}> ${role}
            </label>
        </div>
    </#list>

    <input type="hidden" name="userId" value="${user.id}">
    <input type="hidden" name="_csrf" value="${_csrf.token}">

    <button type="submit">Сохранить</button>
</form>

</body>
</html>

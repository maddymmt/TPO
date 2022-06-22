<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
</head>
<body>
<h1><%= "Car info request" %>
</h1>
<br/>
<%--<a href="hello-servlet">Hello Servlet</a>--%>
<form method="get" action="hello-servlet">
    <label for="cars">Choose a car type:</label>
    <select name="cars" id="cars">
        <option value="Osobowe">Osobowe</option>
        <option value="Dostawcze">Dostawcze</option>
        <option value="Ciężarowe">Ciężarowe</option>
        <option value="Budowlane">Budowlane</option>
    </select>
    <br/>
    <input type ="submit" value = "Submit">
</form>
<p>Click the "Submit" button to request data about given type of a car</p>
</body>
</html>
package com.example.tpo5_tm;

import java.io.*;
import java.sql.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;


@WebServlet(name = "findDataServlet", value = "/find-data-servlet")
public class FindDataServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String type = (String) request.getAttribute("typ");

        try {
            Class.forName("mysql:mysql-connector-java");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
//        try {
//            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }

        String connectionUrl = "jdbc:mysql://localhost:3306/tpo";

        ResultSet resultSet = null;
        String responseString = "<html><body>" +
                "<h1>" + "Available cars" + "</h1>" +
                "<table>" +
                "<tr>"+
                "<th>Typ samochodu</th>"+
                "<th>Nadwozie</th>"+
                "<th>Marka</th>"+
                "<th>Cena</th>"+
                "</tr>";

        try (Connection connection = DriverManager.getConnection(connectionUrl,"root","qwerty");
             Statement statement = connection.createStatement()) {
            String selectSql = "SELECT typ,nadwozie,marka,cena from Car";
            resultSet = statement.executeQuery(selectSql);
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1) + " " + resultSet.getString(2) + resultSet.getString(3) + resultSet.getString(4));
            }
            try {
                while (resultSet.next()) {
                    responseString.concat("<tr>"+
                            "<td>" + resultSet.getString(1) + "</td>"+
                            "<td>" + resultSet.getString(2) + "</td>"+
                            "<td>" + resultSet.getString(3) + "</td>"+
                            "<td>" + resultSet.getString(4) + "</td>"+
                            "</tr>");
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        responseString.concat("/<table>"+
                "</body></html>" );
        request.setAttribute("responseString", responseString);
        RequestDispatcher rd = request.getRequestDispatcher("/response-page-servlet");
        try {
            rd.forward(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }
    public void destroy() {
    }
}

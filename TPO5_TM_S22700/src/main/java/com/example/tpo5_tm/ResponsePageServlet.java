package com.example.tpo5_tm;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "responsePageServlet", value = "/response-page-servlet")
public class ResponsePageServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        String resultString = (String) request.getAttribute("responseString");
        PrintWriter out = response.getWriter();
        out.println(resultString);
    }

    public void destroy() {
    }
}

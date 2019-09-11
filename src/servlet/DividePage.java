package servlet;

import bean.qikanBean;
import server.DBServer;
import server.MapToJson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "DividePage")
public class DividePage extends HttpServlet {
    String[] paras = {
            "ISSN",
            "Name",
            "Press",
            "cs_l",
            "cs_h",
            "hd_l",
            "hd_h",
            "fenqu",
            "BigSubjects",
            "SmSubjects",
            "watch_l",
            "watch_h",
            "if_l",
            "if_h",
            "CCF"
    };
    private DBServer dbServer = new DBServer();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int Type = Integer.parseInt(request.getParameter("Type"));

        ArrayList<String> parameter = new ArrayList<>();
        for (int i = 0; i < paras.length; i++) {
            if (((1 << i) & Type) != 0) {
                parameter.add(paras[i]);
                if (i == 2 ){
                    try {
                        parameter.add(dbServer.getPressId(request.getParameter(paras[i])));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if(i == 8){
                    try {
                        parameter.add(dbServer.getbsjId(request.getParameter(paras[i])));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if(i == 9){
                    try {
                        parameter.add(dbServer.getssjId(request.getParameter(paras[i])));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    parameter.add(request.getParameter(paras[i]));
                }
            }
        }
        int ret = 0;
        try {
            ret = dbServer.search_sz(parameter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MapToJson mapToJson = new MapToJson();
        Map map = new HashMap();
        map.put("count",ret);

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter pw = response.getWriter();
        pw.write(mapToJson.write(map));
    }
}

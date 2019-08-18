package servlet;

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

import bean.qikanBean;

@WebServlet(name = "Search")
public class Search extends HttpServlet {
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
                parameter.add(request.getParameter(paras[i]));
            }
        }
        ArrayList<qikanBean> qikans = new ArrayList<>();
        try {
            qikans = dbServer.searech(parameter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Map map = new HashMap();
        map.put("num", qikans.size());
        for (int i = 0; i < qikans.size(); i++) {
            map.put(String.format("%d-id",i+1),qikans.get(i).getId());
            map.put(String.format("%d-name",i+1),qikans.get(i).getName());
            map.put(String.format("%d-issn",i+1),qikans.get(i).getIssn());
            map.put(String.format("%d-press",i+1),qikans.get(i).getPress());
            map.put(String.format("%d-citescore",i+1),qikans.get(i).getCitescore());
            map.put(String.format("%d-hindex",i+1),qikans.get(i).getHindex());
            map.put(String.format("%d-fenqu",i+1),qikans.get(i).getFenqu());
            map.put(String.format("%d-bsj",i+1),qikans.get(i).getBsj());
            map.put(String.format("%d-ssj",i+1),qikans.get(i).getSsj());
            map.put(String.format("%d-watch",i+1),qikans.get(i).getWatch());
            map.put(String.format("%d-if2016",i+1),qikans.get(i).getIf2016());
            map.put(String.format("%d-if2017",i+1),qikans.get(i).getIf2017());
            map.put(String.format("%d-if2018",i+1),qikans.get(i).getIf2018());
            map.put(String.format("%d-ifavg",i+1),qikans.get(i).getIfavg());
            map.put(String.format("%d-ccf",i+1),qikans.get(i).getCcf());
            map.put(String.format("%d-rank",i+1),qikans.get(i).getRank());
        }


        MapToJson mapToJson = new MapToJson();
        String ss = mapToJson.write(map);
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter pw = response.getWriter();
        pw.write(ss);
    }
}

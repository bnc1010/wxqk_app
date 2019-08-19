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
    private String[] paras = {
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
        System.out.println("**new connection**");
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
        ArrayList<qikanBean> qikans = new ArrayList<>();
        try {
            qikans = dbServer.searech(parameter);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        返回对象组装成json
        StringBuilder ss = new StringBuilder("[");
        for (int i = 0; i < qikans.size(); i++) {
//            System.out.print(qikans.get(i));
            Map map = new HashMap();
            map.put("id", qikans.get(i).getId());
            map.put("name", qikans.get(i).getName());
            map.put("issn", qikans.get(i).getIssn());
            map.put("press", qikans.get(i).getPress());
            map.put("citescore", qikans.get(i).getCitescore());
            map.put("hindex", qikans.get(i).getHindex());
            map.put("fenqu", qikans.get(i).getFenqu());
            map.put("bsj", qikans.get(i).getBsj());
            map.put("ssj", qikans.get(i).getSsj());
            map.put("watch", qikans.get(i).getWatch());
            map.put("if2016", qikans.get(i).getIf2016());
            map.put("if2017", qikans.get(i).getIf2017());
            map.put("if2018", qikans.get(i).getIf2018());
            map.put("ifavg", qikans.get(i).getIfavg());
            map.put("ccf", qikans.get(i).getCcf());
            map.put("rank", qikans.get(i).getRank());
            if (i != 0){
                ss.append(",");
            }
            MapToJson mapToJson = new MapToJson();
            ss.append(mapToJson.write(map));
//            System.out.println(ss);
        }
        ss.append("]");
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter pw = response.getWriter();
        pw.write(ss.toString());
    }
}

package server;

import java.sql.ResultSet;
import java.util.ArrayList;

import bean.*;
import com.mysql.cj.x.protobuf.MysqlxResultset;

//
//"ISSN",
//        "Name",
//        "Press",
//        "cs_l",
//        "cs_h",
//        "hd_l",
//        "hd_h",
//        "fenqu",
//        "BigSubjects",
//        "SmSubjects",
//        "watch_l",
//        "watch_h",
//        "if_l",
//        "if_h",
//        "CCF",
//        "FinalRank"
public class DBServer {
    DbBean dbBean = new DbBean();


    private String getSQL(String para) {
        if (para.equals("ISSN")) {
            return "ISSN like concat('%',?,'%')";
        } else if (para.equals("Name")) {
            return "Name like concat('%',?,'%')";
        } else if (para.equals("Press")) {
            return "Press like concat('%',?,'%')";
        } else if (para.equals("cs_l")) {
            return "CiteScore>=" + "?";
        } else if (para.equals("cs_h")) {
            return "CiteScore<=" + "?";
        } else if (para.equals("hd_l")) {
            return "hindex>=" + "?";
        } else if (para.equals("hd_h")) {
            return "hindex<=" + "?";
        } else if (para.equals("fenqu")) {
            return "fenqu=" + "?";
        } else if (para.equals("BigSubjects")) {
            return "BigSubjects like concat('%',?,'%')";
        } else if (para.equals("SmSubjects")) {
            return "SmSubjects like concat('%',?,'%')";
        } else if (para.equals("watch_l")) {
            return "watch>=" + "?";
        } else if (para.equals("watch_h")) {
            return "watch<=" + "?";
        } else if (para.equals("if_l")) {
            return "IFAVG>=" + "?";
        } else if (para.equals("if_h")) {
            return "IFAVG<=" + "?";
        } else {
            return "CCF=" + "?";
        }
    }


    private String searchName(String where,String id) throws Exception {
        String sql = "select name from " + where + " where id=?";
        dbBean.openConnection();
        ResultSet rs = dbBean.executeQuery(sql,1, id);
        String ret = "";
        if(rs.next()){
            ret = rs.getString("name");
        }
        dbBean.closeConnection();
        return ret;
    }

    public ArrayList<qikanBean> searech(ArrayList<String> parameter) throws Exception {
        StringBuilder sql = new StringBuilder("select * from Periodical where ");
        Object[] paras = new Object[parameter.size() / 2];
//        System.out.println(parameter.size());
        for (int i = 0; i < parameter.size(); i += 2) {
//            System.out.println(parameter.get(i) + parameter.get(i+1));
            if (i != 0) {
                sql.append(" and ");
            }
            sql.append(getSQL(parameter.get(i)));
            paras[i / 2] = parameter.get(i + 1);
        }
//        System.out.println(sql);
        dbBean.openConnection();
        ResultSet rs = dbBean.executeQuery(sql.toString(), parameter.size() / 2, paras);
        ArrayList<qikanBean> ret = new ArrayList<>();
        while (rs.next()) {
            qikanBean qikan = new qikanBean();
            qikan.setIssn(rs.getString("ISSN"));
            qikan.setName(rs.getString("Name"));
            qikan.setPress(rs.getString("Press"));
            qikan.setCitescore(rs.getString("CiteScore"));
            qikan.setHindex(rs.getString("hindex"));
            qikan.setFenqu(rs.getString("fenqu"));
            qikan.setBsj(rs.getString("BigSubjects"));
            qikan.setSsj(rs.getString("SmSubjects"));
            qikan.setWatch(rs.getString("watch"));
            qikan.setIf2016(rs.getString("IF2016"));
            qikan.setIf2017(rs.getString("IF2017"));
            qikan.setIf2018(rs.getString("IF2018"));
            qikan.setIfavg(rs.getString("IFAVG"));
            qikan.setCcf(rs.getString("CCF"));
            qikan.setRank(rs.getString("FinalRank"));
            ret.add(qikan);
            System.out.println(qikan.getName());
        }

        for (int i=0;i<ret.size();i++){
            qikanBean qikan = ret.get(i);
            qikan.setPress(searchName("Press", qikan.getPress()));
            qikan.setBsj(searchName("BigSubjects", qikan.getBsj()));
            qikan.setPress(searchName("SmSubjects", qikan.getSsj()));
            ret.set(i, qikan);
        }


        dbBean.closeConnection();
        return ret;
    }

    public String getPressId(String press) throws Exception {
        String sql = "select id from Press where name=?";
        dbBean.openConnection();
        ResultSet rs = dbBean.executeQuery(sql,1, press);
        String ret="-1";
        if (rs.next()){
            ret = rs.getString("id");
        }
        dbBean.closeConnection();
        return ret;
    }


    public String getbsjId(String bsj) throws Exception {
        String sql = "select id from BigSubjects where name=?";
        dbBean.openConnection();
        ResultSet rs = dbBean.executeQuery(sql,1, bsj);
        String ret="-1";
        if (rs.next()){
            ret = rs.getString("id");
        }
        dbBean.closeConnection();
        return ret;
    }


    public String getssjId(String ssj) throws Exception {
        String sql = "select id from SmSubjects where name=?";
        dbBean.openConnection();
        ResultSet rs = dbBean.executeQuery(sql,1, ssj);
        String ret="-1";
        if (rs.next()){
            ret = rs.getString("id");
        }
        dbBean.closeConnection();
        return ret;
    }


}

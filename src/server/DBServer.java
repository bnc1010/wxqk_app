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


    private Object[] getSQL(String para) {
        Object[] ret = new Object[2];
        if (para.equals("ISSN")) {
            ret[0]="ISSN like concat('%',?,'%')";
            ret[1]=true;
        } else if (para.equals("Name")) {
            ret[0]="Name like concat('%',?,'%')";
            ret[1]=true;
        } else if (para.equals("Press")) {
            ret[0]="Press like concat('%',?,'%')";
            ret[1]=true;
        } else if (para.equals("cs_l")) {
            ret[0]="CiteScore >= " + "?";
            ret[1]=false;
        } else if (para.equals("cs_h")) {
            ret[0]="CiteScore <= " + "?";
            ret[1]=false;
        } else if (para.equals("hd_l")) {
            ret[0]="hindex >= " + "?";
            ret[1]=false;
        } else if (para.equals("hd_h")) {
            ret[0]="hindex <= " + "?";
            ret[1]=false;
        } else if (para.equals("fenqu")) {
            ret[0]="fenqu=" + "?";
            ret[1]=false;
        } else if (para.equals("BigSubjects")) {
            ret[0]="BigSubjects like concat('%',?,'%')";
            ret[1]=true;
        } else if (para.equals("SmSubjects")) {
            ret[0]="SmSubjects like concat('%',?,'%')";
            ret[1]=true;
        } else if (para.equals("watch_l")) {
            ret[0]= "watch>=" + "?";
            ret[1]=false;
        } else if (para.equals("watch_h")) {
            ret[0]= "watch<=" + "?";
            ret[1]=false;
        } else if (para.equals("if_l")) {
            ret[0]= "IFAVG>=" + "?";
            ret[1]=false;
        } else if (para.equals("if_h")) {
            ret[0]= "IFAVG<=" + "?";
            ret[1]=false;
        } else if(para.equals("pageid")) {
            ret[0]= " limit ?, 6 ";
            ret[1]=false;
        } else {
            ret[0]= "CCF=" + "?";
            ret[1]=true;
        }
        return ret;
    }


    private String searchName(String where,String id) throws Exception {
        String sql = "select name from " + where + " where id=?";
        dbBean.openConnection();
        Object [] tp = new Object[2];
        tp[0]=true;
        tp[1]=id;
        ResultSet rs = dbBean.executeQuery(sql,2, tp);
        String ret = "";
        if(rs.next()){
            ret = rs.getString("name");
        }
        dbBean.closeConnection();
        return ret;
    }

    public ArrayList<qikanBean> search(ArrayList<String> parameter) throws Exception {
        StringBuilder sql = new StringBuilder("select * from Periodical where ");
        Object[] paras = new Object[parameter.size()];
        for (int i = 0; i < parameter.size(); i += 2) {
            if (i != 0 && i < parameter.size() - 3) {
                sql.append(" and ");
            }
            Object [] tmp = getSQL(parameter.get(i));
            paras[i] = tmp[1];
            sql.append(tmp[0]);
            paras[i+1] = parameter.get(i+1);
        }
        sql.append(" ");
        paras[parameter.size() - 1] = (Integer.parseInt(parameter.get(parameter.size()-1))-1)*6;
//        System.out.println(sql);
        dbBean.openConnection();
        ResultSet rs = dbBean.executeQuery(sql.toString(), parameter.size(), paras);
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

    public int search_sz(ArrayList<String> parameter) throws Exception {
        StringBuilder sql = new StringBuilder("select count(*) from Periodical where ");
        Object[] paras = new Object[parameter.size()];
        for (int i = 0; i < parameter.size(); i += 2) {
            if (i != 0) {
                sql.append(" and ");
            }
            Object [] tmp = getSQL(parameter.get(i));
            paras[i] =  tmp[1];
            sql.append(tmp[0]);
            paras[i+1] = parameter.get(i+1);
        }
        dbBean.openConnection();
        ResultSet rs = dbBean.executeQuery(sql.toString(), parameter.size(), paras);
        int ret=-1;
        if (rs.next()){
            ret=rs.getInt("count(*)");
        }
        dbBean.closeConnection();
        return ret;
    }


    public String getPressId(String press) throws Exception {
        String sql = "select id from Press where name=?";
        dbBean.openConnection();
        Object[] tp = new Object[2];
        tp[0] = true;
        tp[1] =press;
        ResultSet rs = dbBean.executeQuery(sql,2, tp);
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
        Object[] tp = new Object[2];
        tp[0] = true;
        tp[1] =bsj;
        ResultSet rs = dbBean.executeQuery(sql,2, tp);
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
        Object[] tp = new Object[2];
        tp[0] = true;
        tp[1] =ssj;
        ResultSet rs = dbBean.executeQuery(sql,2, tp);
        String ret="-1";
        if (rs.next()){
            ret = rs.getString("id");
        }
        dbBean.closeConnection();
        return ret;
    }


}

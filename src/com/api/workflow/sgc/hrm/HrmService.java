package com.api.workflow.sgc.hrm;

import weaver.conn.RecordSetDataSource;
import weaver.general.Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import java.util.ArrayList;
import java.util.Vector;
import java.util.function.Consumer;

import static weaver.hrm.common.ReflectUtil.writeLog;

public class HrmService {
    public UserModel getUser(String userNo){
        UserModel u=null;
        try {
            RecordSetDataSource rsds = new RecordSetDataSource("OA");
            rsds.executeSql("select id,loginid,lastname,joblevel,jobtitle,seclevel,departmentid,subcompanyid1 from hrmresource where loginid='"+userNo+"'");
            if(rsds.next()){
                u=new UserModel();
                u.setId(Util.getIntValue(rsds.getString("id"),0));
                u.setLoginId(rsds.getString("loginid"));
                u.setUserName(rsds.getString("lastname"));
                u.setJobLevel(rsds.getString("joblevel"));
                u.setDepartmentId(rsds.getString("departmentid"));
                u.setSubCompanyId1(rsds.getString("subcompanyid1"));
                u.setJobTitle(rsds.getString("jobtitle"));
                u.setSecLevel(rsds.getString("seclevel"));
            }
        }catch (Exception ex){
            ex.printStackTrace();
            writeLog(ex);
        }
        return u;
    }
}

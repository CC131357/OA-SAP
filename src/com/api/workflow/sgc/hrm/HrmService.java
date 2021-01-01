package com.api.workflow.sgc.hrm;

import weaver.conn.RecordSetDataSource;
import weaver.general.Util;
import static weaver.hrm.common.ReflectUtil.writeLog;

/**
 * 根据工号读取流程所需的基础信息
 */
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
    public UserModel getUserById(String id){
        UserModel u=null;
        try {
            RecordSetDataSource rsds = new RecordSetDataSource("OA");
            rsds.executeSql("select id,loginid,lastname,joblevel,jobtitle,seclevel,departmentid,subcompanyid1 from hrmresource where id='"+id+"'");
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

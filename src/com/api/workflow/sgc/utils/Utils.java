package com.api.workflow.sgc.utils;

import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestService;
import weaver.workflow.webservices.WorkflowRequestTableField;

public class Utils {

    /**返回成功状态*/
    public final static String SUCCESS = "1";
    /**返回失败状态*/
    public final static String FAILURE = "0";
    /*盘点差异审批流程ID*/
    public final static String INVENTORYWFID="1422";//测试1422，本地19

    /**根据请求ID获得获得流程编号*/
    public String getWorkflowNo(String requestId, String flowFieldName){
        RequestService requestService = new RequestService();
        String workflowNo="";
        try{
            Property[] properties=requestService.getRequest(Integer.parseInt(requestId)).getMainTableInfo().getProperty();
            for (Property p:properties){
                if(p.getName().equals(flowFieldName)){
                    workflowNo=p.getValue();
                    break;
                }
            }
            return workflowNo;
        } catch (Exception e){
            e.printStackTrace();
            return workflowNo;
        }
    }

    public static WorkflowRequestTableField generateFeild(String filedName, String value){
        WorkflowRequestTableField temp=new WorkflowRequestTableField();
        temp.setFieldName(filedName);
        temp.setFieldValue(value);
        temp.setView(true);
        temp.setEdit(true);
        return temp;
    }
}

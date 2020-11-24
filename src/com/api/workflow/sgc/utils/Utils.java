package com.api.workflow.sgc.utils;

import weaver.workflow.webservices.WorkflowRequestTableField;

public class Utils {
    public static WorkflowRequestTableField generateFeild(String filedName, String value){
        WorkflowRequestTableField temp=new WorkflowRequestTableField();
        temp.setFieldName(filedName);
        temp.setFieldValue(value);
        temp.setView(true);
        temp.setEdit(true);
        return temp;
    }
}

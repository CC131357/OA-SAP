package com.api.workflow.sgc.utils;

public class ApiResult {
    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /*
    * 返回状态码:
    * 1: 成功
    * 0: 失败
    * -1：创建流程失败
    * -2：没有创建权限
    * -3：创建流程失败
    * -4：字段或表名不正确
    * -5：更新流程级别失败
    * -6：无法创建流程待办任务
    * -7：流程下一节点出错，请检查流程的配置，在OA中发起流程进行测试
    * -8：流程节点自动赋值操作错误
    * */
    private String stateCode;
    private String msg;
}

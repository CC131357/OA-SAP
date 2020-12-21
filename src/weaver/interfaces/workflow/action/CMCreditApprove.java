package weaver.interfaces.workflow.action;

import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import com.weaver.general.Util;
import weaver.soa.workflow.request.RequestInfo;

import java.util.Map;

public class CMCreditApprove  extends BaseBean implements Action {
    final String MESSAGEID = "99999";
    final String FAILURECODE = "333";
    CommonUtil util = new CommonUtil();

    @Override
    public String execute(RequestInfo requestInfo) {
        return PostMsg(getPostJsonStr(requestInfo),requestInfo);
    }

    /**获得需要推送的字符串*/
    private String getPostJsonStr(RequestInfo requestInfo){
        JSONObject approveJson =new JSONObject();
        //获取主表信息、初始化主表
        Map<String, String> mainTableInfo = util.getMainTableMap(requestInfo);

        String iDocty = Util.null2String(mainTableInfo.get("djlx")); //单据类型
        if(iDocty != null && iDocty.equals("0")){   //销售订单
            approveJson.put("I_DOCTY","S");
        } else if(iDocty != null && iDocty.equals("1")){    //交货订单
            approveJson.put("I_DOCTY","D");
        } else{
            requestInfo.getRequestManager().setMessageid(MESSAGEID);
            requestInfo.getRequestManager().setMessagecontent("单据类型填写错误！");
            return FAILURECODE;
        }
        approveJson.put("I_VBELN",Util.null2String(mainTableInfo.get("djhm"))); //单据号码
        approveJson.put("I_ZOABH",Util.null2String(mainTableInfo.get("liucbh"))); //OAI流程编号

        return approveJson.toString();
    }

    /**向SAP推送消息*/
    private String PostMsg(String msg,RequestInfo requestInfo){
        try{
            JSONObject database = util.Post(CommonUtil.cMCreditApproveUrl,msg,CommonUtil.authorization);
            String e_code = database.getString("E_CODE");
            String e_msg = database.getString("E_MSG");
            if ("S".equals(e_code)){
                //表示数据传输成功，正常提交
                util.printLog(requestInfo, msg, "特批成功!" + e_msg);
                return SUCCESS;
            }else {
                util.printLog(requestInfo, msg, "调用客户信用特批解冻申请流程接口，特批失败!" + e_msg);
                requestInfo.getRequestManager().setMessageid(MESSAGEID);
                requestInfo.getRequestManager().setMessagecontent(e_msg + msg);
                return FAILURECODE;
            }
        } catch (Exception e){
            util.printLog(requestInfo, msg, "调用SAP接口失败，返回错误数据!" + e.getMessage());
            requestInfo.getRequestManager().setMessageid(MESSAGEID);
            requestInfo.getRequestManager().setMessagecontent("调用SAP接口失败，返回错误数据！" + msg);
            return FAILURECODE;
        }
    }

}

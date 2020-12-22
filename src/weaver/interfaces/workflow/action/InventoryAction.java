package weaver.interfaces.workflow.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import com.weaver.general.Util;
import weaver.soa.workflow.request.RequestInfo;

import java.io.IOException;
import java.util.Map;

public class InventoryAction extends BaseBean implements Action {

    final String MESSAGEID = "99999";
    final String FAILURECODE = "333";
    CommonUtil util = new CommonUtil();

    @Override
    public String execute(RequestInfo requestInfo) {
        return PostMsg(getPostJsonStr(requestInfo),requestInfo);
    }

    /**获得需要推送的字符串*/
    private String getPostJsonStr(RequestInfo requestInfo){
        //获取主表信息、初始化主表
        Map<String, String> mainTableInfo = util.getMainTableMap(requestInfo);

        String auditState = "Y";
        if(requestInfo.getRequestManager().getSrc().equals("reject")){
            auditState= "N";
        }
        JSONObject approveJson =new JSONObject();
        JSONArray puchasesArr = new JSONArray();
        JSONObject detail = new  JSONObject();

        detail.put("IBLNR",Util.null2String(mainTableInfo.get("pdcybtt"))); //库存盘点凭证
        detail.put("ZSPJG",auditState); //OA审批结果
        puchasesArr.add(detail);
        approveJson.put("IT_INPUT",puchasesArr);

        return approveJson.toString();
    }

    /**向SAP推送消息*/
    private String PostMsg(String msg,RequestInfo requestInfo){
        try {
            JSONObject rData1 = util.Post(CommonUtil.inventoryDiffUrl,msg,CommonUtil.authorization).getJSONArray("ET_OUT").getJSONObject(0);
            if(rData1.get("CODE") != null && rData1.get("CODE").toString().equals("S")){
                util.printLog(requestInfo, msg, rData1.get("MESSAGE").toString());
                return SUCCESS;
            } else if(rData1.get("CODE") != null && rData1.get("CODE").toString().equals("E")){
                if(rData1.get("MESSAGE") != null){
                    requestInfo.getRequestManager().setMessagecontent(rData1.get("MESSAGE").toString() + msg);
                    util.printLog(requestInfo, msg, rData1.get("CODE").toString());
                } else{
                    requestInfo.getRequestManager().setMessagecontent("调用SAP失败！" + msg);
                    util.printLog(requestInfo, msg, rData1.get("CODE").toString());
                }
                System.out.println("MESSAGE：" + rData1.get("MESSAGE"));
                requestInfo.getRequestManager().setMessageid(MESSAGEID);
                return FAILURECODE ;
            } else{
                util.printLog(requestInfo, msg, rData1.get("CODE").toString());
                requestInfo.getRequestManager().setMessageid(MESSAGEID);
                return FAILURECODE ;
            }
        } catch (IOException e) {
            util.printLog(requestInfo, msg, "调用SAP程序出错！" + e.getMessage());
            requestInfo.getRequestManager().setMessagecontent("调用SAP程序出错！" + msg);
            requestInfo.getRequestManager().setMessageid(MESSAGEID);
            return FAILURECODE ;
        }
    }

}

package weaver.interfaces.workflow.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import com.weaver.general.Util;
import weaver.soa.workflow.request.RequestInfo;

import java.io.IOException;
import java.util.Map;

public class InventoryAction extends BaseBean implements Action {

    @Override
    public String execute(RequestInfo requestInfo) {

        final String MESSAGEID = "99999";
        final String FAILURECODE = "333";

        //获取主表信息、初始化主表
        Map<String, String> mid=CommonUtil.getPropertyMap(requestInfo.getMainTableInfo().getProperty());

        String auditState = "Y";
        String src=requestInfo.getRequestManager().getSrc();
        if(src.equals("reject")){
            auditState= "N";
        }

        JSONObject approveJson =new JSONObject();
        JSONArray puchasesArr = new JSONArray();
        JSONObject detail = new  JSONObject();
        detail.put("IBLNR",Util.null2String(mid.get("pdcybtt"))); //库存盘点凭证
        detail.put("ZSPJG",auditState); //OA审批结果
        puchasesArr.add(detail);
        approveJson.put("IT_INPUT",puchasesArr);

        String msg = approveJson.toString();
        System.out.println(msg);

        try {
            JSONObject rData1 = CommonUtil.Post(CommonUtil.inventoryDiffUrl,msg).getJSONArray("ET_OUT").getJSONObject(0);
            if(rData1.get("CODE") != null && rData1.get("CODE").toString().equals("S")){
                System.out.println("MESSAGE：" + rData1.get("MESSAGE"));
                writeLog(rData1.get("MESSAGE"));
                return SUCCESS;
            } else if(rData1.get("CODE") != null && rData1.get("CODE").toString().equals("E")){
                if(rData1.get("MESSAGE") != null){
                    requestInfo.getRequestManager().setMessagecontent(rData1.get("MESSAGE").toString() + msg);
                    printLog(requestInfo, "调用SAP失败", rData1.get("CODE").toString());

                } else{
                    requestInfo.getRequestManager().setMessagecontent("调用SAP失败！" + msg);
                    printLog(requestInfo, "调用SAP失败,数据缺失,返回MESSAGE为NULL", rData1.get("CODE").toString());
                }
                System.out.println("MESSAGE：" + rData1.get("MESSAGE"));
                requestInfo.getRequestManager().setMessageid(MESSAGEID);
                return FAILURECODE ;
            } else{
                printLog(requestInfo, "调用SAP失败,数据缺失,返回CODE为NULL", rData1.get("CODE").toString());
                requestInfo.getRequestManager().setMessageid(MESSAGEID);
                return FAILURECODE ;
            }
        } catch (IOException e) {
            requestInfo.getRequestManager().setMessagecontent("调用SAP程序出错！" + msg);
            printLog(requestInfo, "调用SAP程序出错！", msg);
            requestInfo.getRequestManager().setMessageid(MESSAGEID);
            e.printStackTrace();
            return FAILURECODE ;
        }
    }

    /**输出打印信息*/
    private void printLog(RequestInfo requestInfo, String msg, String returnMsg){
        writeLog(msg +
                "创建人【"+requestInfo.getCreatorid()+"】" +
                "流程id【"+requestInfo.getWorkflowid()+"】" +
                "流程请求id【"+requestInfo.getRequestid()+"】" +
                "当前节点【"+requestInfo.getRequestManager().getNodeid()+"】" +
                "请求标题【"+requestInfo.getRequestManager().getRequestname()+"】"+
                "返回信息【"+ returnMsg +"】");
    }
}

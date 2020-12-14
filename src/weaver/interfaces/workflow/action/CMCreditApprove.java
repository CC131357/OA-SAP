package weaver.interfaces.workflow.action;

import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import com.weaver.general.Util;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;

import java.util.HashMap;
import java.util.Map;

public class CMCreditApprove  extends BaseBean implements Action {
    @Override
    public String execute(RequestInfo requestInfo) {
        final String MESSAGEID = "99999";
        final String FAILURECODE = "333";

        JSONObject approveJson =new JSONObject();
        //获取主表信息、初始化主表
        Map<String, String> mid=CommonUtil.getPropertyMap(requestInfo.getMainTableInfo().getProperty());

        String iDocty = Util.null2String(mid.get("djlx")); //单据类型
        if(iDocty != null && iDocty.equals("0")){   //销售订单
            approveJson.put("I_DOCTY","S");
        } else if(iDocty != null && iDocty.equals("1")){    //交货订单
            approveJson.put("I_DOCTY","D");
        } else{
            requestInfo.getRequestManager().setMessageid(MESSAGEID);
            requestInfo.getRequestManager().setMessagecontent("单据类型填写错误！");
            return FAILURECODE;
        }
        approveJson.put("I_VBELN",Util.null2String(mid.get("djhm"))); //单据号码
        approveJson.put("I_ZOABH",Util.null2String(mid.get("liucbh"))); //OAI流程编号

        String msg = approveJson.toString();
        System.out.println(msg);

        try{
            JSONObject database = CommonUtil.Post(CommonUtil.cMCreditApproveUrl,msg);
            String e_code = database.getString("E_CODE");
            String e_msg = database.getString("E_MSG");
            if ("S".equals(e_code)){
                //表示数据传输成功，正常提交
                System.out.println("特批成功!" + msg);
                return SUCCESS;
            }else if("E".equals(e_code)){
                //数据传输失败，则将错误信息返回到页面
                printLog(requestInfo, "调用客户信用特批解冻申请流程接口，特批失败！,", msg);
                requestInfo.getRequestManager().setMessageid(MESSAGEID);
                requestInfo.getRequestManager().setMessagecontent(e_msg + msg);
                return FAILURECODE;
            } else{
                requestInfo.getRequestManager().setMessageid(MESSAGEID);
                requestInfo.getRequestManager().setMessagecontent("调用SAP接口失败，返回错误数据1！" + msg);
                return FAILURECODE;
            }
        } catch (Exception e){
            e.printStackTrace();
            requestInfo.getRequestManager().setMessageid(MESSAGEID);
            requestInfo.getRequestManager().setMessagecontent("调用SAP接口失败，返回错误数据2！" + msg);
            return FAILURECODE;
        }
    }

    /**
     * @param property
     * @return
     */
    public static Map<String, String> getPropertyMap(Property[] property) {
        Map<String, String> m = new HashMap<>();
        for(Property p : property){
            m.put( p.getName(), p.getValue());
        }
        return m;
    }

    /**输出打印信息*/
    private void printLog(RequestInfo requestInfo, String msg, String returnMsg){
        writeLog(msg +
                "创建人【"+requestInfo.getCreatorid()+"】" +
                "流程id【"+requestInfo.getWorkflowid()+"】" +
                "流程请求id【"+requestInfo.getRequestid()+"】" +
                "当前节点【"+requestInfo.getRequestManager().getNodeid()+"】" +
                "请求标题【"+requestInfo.getRequestManager().getRequestname()+"】"+
                "请求信息【"+ returnMsg +"】");
    }
}

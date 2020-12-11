package weaver.interfaces.workflow.action;

import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import com.weaver.general.Util;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static weaver.interfaces.workflow.action.CommonUtil.despositApproUrl;



public class DespositApproAction extends BaseBean implements Action {

    /**
     * @作者：高梦利
     * @流程名称：SD-订金特批流程审批回传
     */

    @Override
    public String execute(RequestInfo requestInfo) {

        String requestId = requestInfo.getRequestid();
        //获取主表信息、初始化主表
        Map<String, String> mid = getPropertyMap(requestInfo.getMainTableInfo().getProperty());

        String IV_ZOABH = Util.null2String(mid.get("liucbh")); //流程编号
        String IV_ZDJTP = "X"; //是否订金特批
        String IV_VBELN = Util.null2String(mid.get("xsddh")); //销售订单号-销售凭证

        JSONObject mainObject = new JSONObject();
        mainObject.put("IV_ZOABH", IV_ZOABH);
        mainObject.put("IV_ZDJTP", IV_ZDJTP);
        mainObject.put("IV_VBELN", IV_VBELN);

        String body = mainObject.toJSONString();
        JSONObject database;
        try {
            database = CommonUtil.Post(despositApproUrl, body);
            String e_code = database.getString("E_CODE");
            String e_msg = database.getString("E_MSG");
            if ("S".equals(e_code)) {
                //表示数据传输成功，正常提交
                System.out.println("成功");
                return SUCCESS;
            } else if ("E".equals(e_code)) {
                writeLog("订金特批接口回传信息," +
                        "创建人【"+requestInfo.getCreatorid()+"】" +
                        "流程id【"+requestInfo.getWorkflowid()+"】" +
                        "流程请求id【"+requestInfo.getRequestid()+"】" +
                        "当前节点【"+requestInfo.getRequestManager().getNodeid()+"】" +
                        "请求标题【"+requestInfo.getRequestManager().getRequestname()+"】"+
                        "返回信息【"+e_msg+"】");
                requestInfo.getRequestManager().setMessageid("9999");
                requestInfo.getRequestManager().setMessagecontent(e_msg);
                return "销售订单订金特批标识更新失败";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "销售订单订金特批标识更新失败";
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
}


package weaver.interfaces.workflow.action;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import com.weaver.general.Util;
import okhttp3.*;
import weaver.conn.RecordSetDataSource;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author gongchen
 * @流程名称 外部ECN流程
 */
public class OECNAction extends BaseBean implements Action {
    public static final String REQUESTPATH = "http://10.10.10.31:50000/RESTAdapter/OA/S0063PaymentDataTransfer";
    @Override
    public String execute(RequestInfo requestInfo) {
        JSONObject jsonObj =new JSONObject();
        String requestId = requestInfo.getRequestid();
        //获取主表信息、初始化主表
        Map<String, String> mid=getPropertyMap(requestInfo.getMainTableInfo().getProperty());
        String MATNR=Util.null2String(mid.get("dqcpxh"));  //当前产品型号
        JSONObject js = new JSONObject();
        js.put("MATNR",MATNR);
        jsonObj.put("IS_INPUT",js);
        String shuju = jsonObj.toString();
        JSONObject result = null;
        try {
             result= CommonUtil.Post(CommonUtil.OECNUrl,shuju);
            String code=result.getString("E_CODE");
            requestInfo.getRequestManager().setMessage(result.getString("E_MSG"));
            if(code.equals("S")){
                return SUCCESS;
            }else if(code.equals("E")){
                return FAILURE_AND_CONTINUE;
            }
        } catch (IOException e) {
            writeLog(e.getMessage());
            requestInfo.getRequestManager().setMessage(e.getMessage());
        }catch (JSONException e){
            writeLog(e.getMessage());
            requestInfo.getRequestManager().setMessage(e.getMessage());
        }
        String e_code = result.getString("E_CODE");
        String e_msg = result.getString("E_MSG");
        if ("S".equals(e_code)){
            //表示数据传输成功，正常提交
            System.out.println("成功");

            //获取新的型号
            RecordSetDataSource rsds = new RecordSetDataSource("OA");
            rsds.executeSql("update formtable_main_251 set dqcpxh ='"+123+"'where = '"+requestId+"'");
            return SUCCESS;
        }else{
            //数据传输失败，则将错误信息返回到页面
            writeLog("费用报销流程的接口回传信息," +
                    "创建人【"+requestInfo.getCreatorid()+"】" +
                    "流程id【"+requestInfo.getWorkflowid()+"】" +
                    "流程请求id【"+requestInfo.getRequestid()+"】" +
                    "当前节点【"+requestInfo.getRequestManager().getNodeid()+"】" +
                    "请求标题【"+requestInfo.getRequestManager().getRequestname()+"】"
            );

            requestInfo.getRequestManager().setMessageid("99999");
            requestInfo.getRequestManager().setMessagecontent(e_msg.toString());
            return "333";
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
}




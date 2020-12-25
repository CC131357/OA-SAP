package weaver.interfaces.workflow.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.api.workflow.sgc.hrm.HrmService;
import com.api.workflow.sgc.hrm.UserModel;
import com.finance.weixin.toolkit.DataRow;
import com.google.common.base.Strings;
import com.weaver.general.Util;
import okhttp3.*;
import weaver.general.BaseBean;
import weaver.soa.workflow.request.*;

import java.io.IOException;
import java.util.*;

public class PurchaseAction extends BaseBean implements Action  {
    @Override
    public String execute(RequestInfo requestInfo) {
        //获取主表信息、初始化主表
        Map<String, String> propertyMap=CommonUtil.getPropertyMap(requestInfo.getMainTableInfo().getProperty());
        String AFNAM= Util.null2String(propertyMap.get("sqr"));  //申请人
        HrmService hrmService=new HrmService();
        UserModel u= hrmService.getUserById(AFNAM);
        String flowNo=propertyMap.get("lcbh");
        DetailTable detailtable = requestInfo.getDetailTableInfo().getDetailTable()[0];// 获取明细表
        JSONObject jsonObject =new JSONObject();
        JSONArray detailArr=new JSONArray();
        for(int i=0;i<detailtable.getRowCount();i++){
            Row r=detailtable.getRow(i);
            Hashtable<String,String> ht=new Hashtable<>();
            ht.put("REQUESTID",flowNo);
            ht.put("AFNAM",u.getLoginId());
            ht.put("ZOAMXID",String.valueOf(i+1));
            ht.put("BSART","ZR04");//采购申请类型，默认值ZR04
            for(Cell c:r.getCell()){
                switch (c.getName()){
                    case "materialCode"://物料编码
                        ht.put("MATNR",c.getValue());
                        break;
                    case "kmlb"://科目类别
                        String kmlb="";
                        if("0".equals(c.getValue())){
                            kmlb="K";
                        }
                        ht.put("KNTTP",kmlb);
                        break;
                    case "costCenter"://成本中心
                        ht.put("KOSTL",c.getValue());
                        break;
                    case "wzmc"://物料名称
                        ht.put("MAKTX",c.getValue());
                        break;
                    case "gc"://工厂
                        ht.put("WERKS",c.getValue());
                        break;
                    case "sl":
                        ht.put("MENGE",c.getValue());
                        break;
                    case "dw":
                        ht.put("MEINS",c.getValue());
                        break;
                    case "wlz":
                        ht.put("MATKL",c.getValue());
                        break;
                    case "cgz":
                        ht.put("EKGRP",c.getValue());
                        break;
                    case "yqqx":
                        ht.put("LFDAT",c.getValue());
                        break;
                    default:
                        break;
                }
            }
            detailArr.add(ht);
        }
        jsonObject.put("I_TAB",detailArr);
        String body = jsonObject.toString();
        try {
            JSONObject result = CommonUtil.Post(CommonUtil.purchaseUrl,body);
            JSONArray data = result.getJSONArray("E_RETURN");
            JSONObject jsonResult = data.getJSONObject(0);
            String code= jsonResult.getString("E_CODE");
            requestInfo.getRequestManager().setMessage(jsonResult.getString("E_MSG"));
            if("S".equals(code)){
                return SUCCESS;
            }else if("E".equals(code)){
                return FAILURE_AND_CONTINUE;
            }
        } catch (IOException e) {
            printLog(requestInfo, "PurchaseAction",e.getMessage());
        } catch (JSONException e){
            printLog(requestInfo,"PurchaseAction",e.getMessage());
        }
        return FAILURE_AND_CONTINUE;
    }
    /**输出打印信息*/
    private void printLog(RequestInfo requestInfo, String msg, String returnMsg){
        writeLog(msg +
                "创建人【"+requestInfo.getCreatorid()+"】" +
                "流程id【"+requestInfo.getWorkflowid()+"】" +
                "流程请求id【"+requestInfo.getRequestid()+"】" +
                "当前节点【"+requestInfo.getRequestManager().getNodeid()+"】" +
                "请求标题【"+requestInfo.getRequestManager().getRequestname()+"】"+
                "返回信息或者请求信息【"+ returnMsg +"】");
    }
}

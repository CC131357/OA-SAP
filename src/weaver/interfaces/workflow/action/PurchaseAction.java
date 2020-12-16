package weaver.interfaces.workflow.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weaver.general.Util;
import okhttp3.*;
import weaver.general.BaseBean;
import weaver.soa.workflow.request.*;

import java.io.IOException;
import java.util.*;

public class PurchaseAction extends BaseBean implements Action  {
    @Override
    public String execute(RequestInfo requestInfo) {
        final String MESSAGEID = "99999";
        final String FAILURECODE = "333";

        //获取主表信息、初始化主表
        Map<String, String> mid=CommonUtil.getPropertyMap(requestInfo.getMainTableInfo().getProperty());
        String AFNAM= Util.null2String(mid.get("sqr"));  //申请人
        String KOSTL=Util.null2String(mid.get("cbzx1")); //成本中心

        DetailTable[] detailtable = requestInfo.getDetailTableInfo().getDetailTable();// 获取所有明细表
        JSONObject jsonObject =new JSONObject();
        if (detailtable.length > 0) {
            DetailTable dt = detailtable[0];// 指定明细表
            Row[] s = dt.getRow();// 当前明细表的所有数据,按行存储
            JSONArray puchasesArr = new JSONArray();
            for (int j = 0; j < s.length; j++) {
                Row r = s[j];// 指定行
                String id = r.getId();
                Cell c[] = r.getCell();// 每行数据再按列存储
                JSONObject detail = new  JSONObject();
                for (int k = 0; k < c.length; k++) {
                    Cell c1 = c[k];// 指定列
                    String name = c1.getName();// 明细字段名称
                    String value = c1.getValue();// 明细字段的值
                    System.out.println(name + " " + value);
                    if(name.equals("kmlb")){
                        if(KOSTL == null || KOSTL.equals("")
                                || KOSTL.equals(" ")){
                            detail.put("KNTTP","");//科目类别要求为，"空值"标准采购，"K成本中心采购，F内部订单采购"
                        } else if(value != null && (value.equals("0") || value.equalsIgnoreCase("K"))){
                            detail.put("KNTTP","K");
                        } else{
                            detail.put("KNTTP","");
                        }
                    } else if(name.equals("wlbm")){//物料编码
                        detail.put("MATNR",value);
                    } else if(name.equals("wzmc")){//物资名称
                        if(value != null){
                            detail.put("MAKTX",CommonUtil.subStringByLength(value, 35));
                        } else{
                            detail.put("MAKTX",value);
                        }
                    } else if(name.equals("sl")){//数量
                        detail.put("MENGE",value);
                    } else if(name.equals("dw")){//单位
                        detail.put("MEINS",value);
                    } else if(name.equals("wlz")){//物料组
                        detail.put("MATKL",value);
                        //根据id获取对于的字段
                    } else if(name.equals("cgz")){//采购组
                        detail.put("EKGRP",value);
                    } else if(name.equals("yqqx")){//日期期限
                        detail.put("LFDAT",value);
                    } else if(name.equals("gc")){//工厂
                        detail.put("WERKS",value);
                    }
                }
                detail.put("AFNAM",AFNAM);//申请人
                detail.put("KOSTL",KOSTL);//成本中心
                detail.put("REQUESTID",requestInfo.getRequestid());//请求id

                detail.put("ZOAMXID",id);//明细id
                detail.put("BSART","ZR04");//采购申请凭证类型
                detail.put("EKORG","");//采购组织
                detail.put("PREIS","1");//价格
                detail.put("WAERS","CNY");//货币码
                detail.put("PEINH","1");//价格单位
                detail.put("AUFNR","");//订单号
                detail.put("ABLAD","");//设备号
                detail.put("WEMPF","");//设备服务起始日期

                puchasesArr.add(detail);
            }
            jsonObject.put("I_TAB",puchasesArr);
        }
        OkHttpClient client = new OkHttpClient();
        String msg = jsonObject.toString();
        System.out.println(msg);
        try {
            JSONObject resultDatas = CommonUtil.Post(CommonUtil.purchaseUrl,msg);
            JSONArray d1 = resultDatas.getJSONArray("E_RETURN");
            JSONObject rData1 = d1.getJSONObject(0);
            if(rData1.get("E_CODE") != null && rData1.get("E_CODE").toString().equals("S")){
                System.out.println("E_MSG：" + rData1.get("E_MSG"));
                writeLog(rData1.get("E_MSG"));
                return SUCCESS;
            } else if(rData1.get("E_CODE") != null && rData1.get("E_CODE").toString().equals("E")){
                if(rData1.get("E_MSG") != null){
                    requestInfo.getRequestManager().setMessagecontent(rData1.get("E_MSG").toString() + msg);
                    printLog(requestInfo, "调用SAP插入物料信息失败,", rData1.get("E_CODE").toString());
                } else{
                    requestInfo.getRequestManager().setMessagecontent("调用SAP回调E_MSG缺失，插入物料信息失败！" + msg);
                    printLog(requestInfo, "调用SAP回调E_MSG缺失，插入物料信息失败,", rData1.get("E_CODE").toString());
                }
                System.out.println("E_MSG：" + rData1.get("E_MSG"));
                requestInfo.getRequestManager().setMessageid(MESSAGEID);
                return FAILURECODE ;//return返回固定返回`SUCCESS`
            } else{
                requestInfo.getRequestManager().setMessagecontent("调用SAP，插入物料信息失败！" + msg);
                printLog(requestInfo, "调用SAP，插入物料信息失败,", msg);
                requestInfo.getRequestManager().setMessageid(MESSAGEID);
                return FAILURECODE ;//return返回固定返回`SUCCESS`
            }
        } catch (IOException e) {
            requestInfo.getRequestManager().setMessagecontent("调用SAP程序出错，插入物料信息失败！" + msg);
            printLog(requestInfo, "调用SAP程序出错，插入物料信息失败！", msg);
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
                "返回信息或者请求信息【"+ returnMsg +"】");
    }
}

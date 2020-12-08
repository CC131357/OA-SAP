package weaver.interfaces.workflow.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weaver.general.Util;
import okhttp3.*;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.soa.workflow.request.*;

import java.io.IOException;
import java.util.*;

public class PurchaseAction  extends BaseBean implements Action  {
    @Override
    public String execute(RequestInfo requestinfo) {

        String requestPath = "http://10.10.10.31:50000/RESTAdapter/OA/S0042PRcreate";
        String mainTableName = "ecology.dbo.formtable_main_270_dt1";
        String detailTableName = "ecology.dbo.formtable_main_270";

        Map<String, String> commonInfos = new HashMap<>();
        Property[] properties = requestinfo.getMainTableInfo().getProperty();// 获取表单主字段信息
        for (int i = 0; i < properties.length; i++) {
            String name = properties[i].getName();// 主字段名称
            String value = Util.null2String(properties[i].getValue());// 主字段对应的值
            if(null!=name && name.equals("sqr")){
                commonInfos.put("AFNAM",value);
            } else if(null!=name && ("cbzx").equals(name)){
                commonInfos.put("KOSTL",value);
            } else if(null!=name && name.equals("gc")){
                commonInfos.put("WERKS",value);
            }
        }
        Map<String, Object> tableDatas = new HashMap<>();
        DetailTable[] detailtable = requestinfo.getDetailTableInfo().getDetailTable();// 获取所有明细表
        //获取明细Ids
        List<String> detailIds = new ArrayList<String>();
        RecordSet currs=new RecordSet();
        currs.executeQuery("select fd.id from "+ mainTableName +" fd where fd.mainid = " +
                "(select fm.id from "+ detailTableName +" fm where fm.requestid = '" + requestinfo.getRequestid() +"')");
        while(currs.next()){
            detailIds.add(currs.getString("id"));
        }
        if (detailtable.length > 0) {
            DetailTable dt = detailtable[0];// 指定明细表
            Row[] s = dt.getRow();// 当前明细表的所有数据,按行存储
            Map<String, Object>[] puchasesArr = new HashMap[s.length];
            for (int j = 0; j < s.length; j++) {
                Row r = s[j];// 指定行
                Cell c[] = r.getCell();// 每行数据再按列存储
                Map<String, Object> detail = new HashMap<>();
                for (int k = 0; k < c.length; k++) {
                    Cell c1 = c[k];// 指定列
                    String name = c1.getName();// 明细字段名称
                    String value = c1.getValue();// 明细字段的值
                    System.out.println(name + " " + value);
                    if(name.equals("kmlb")){
                        detail.put("KNTTP",value);
                    } else if(name.equals("wlbm")){
                        detail.put("MATNR",value);
                    } else if(name.equals("wzmc")){
                        detail.put("MAKTX",value);
                    } else if(name.equals("sl")){
                        detail.put("MENGE",value);
                    } else if(name.equals("dw")){
                        detail.put("MEINS",value);
                    } else if(name.equals("wlz")){
                        detail.put("MATKL",value);
                    } else if(name.equals("cgz")){
                        detail.put("EKGRP",value);
                    } else if(name.equals("yqqx")){
                        detail.put("LFDAT",value);
                    }
                }
                detail.put("AFNAM",commonInfos.get("AFNAM"));//申请人
                detail.put("KOSTL",commonInfos.get("KOSTL"));//成本中心
                detail.put("WERKS",commonInfos.get("WERKS"));//工厂
                detail.put("REQUESTID",requestinfo.getRequestid());//请求id
                if(detailIds.size() > j){
                    detail.put("ZOAMXID",detailIds.get(j));//明细id
                }
                detail.put("BSART","JR04");//采购申请凭证类型
                detail.put("EKORG","0");//采购组织
                detail.put("PREIS","0");//价格
                detail.put("WAERS","0");//货币码
                detail.put("PEINH","0");//价格单位
                detail.put("AUFNR","0");//订单号
                detail.put("ABLAD","0");//设备号
                detail.put("WEMPF","2020-11-23");//设备服务起始日期

                puchasesArr[j] = detail;
            }
            tableDatas.put("I_TAB",puchasesArr);
        }

        OkHttpClient client = new OkHttpClient();
        JSONObject jsonObject = new JSONObject(tableDatas);

        System.out.println(jsonObject.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        String author = "Basic " + Base64.getEncoder().encodeToString(("ZPOUSER"+":"+ "1qaz@WSX").getBytes());
        Request request = new Request.Builder()
                .addHeader("Authorization", author)
                .url(requestPath)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            //控制流程流转，增加以下两行，流程不会向下流转，表单上显示返回的自定义错误信息
            requestinfo.getRequestManager().setMessagecontent("调用采购SAP接口失败");
            requestinfo.getRequestManager().setMessageid("错误信息编号");
            writeLog("调用采购SAP接口失败");
            e.printStackTrace();
            return FAILURE_AND_CONTINUE ;
        }
        try {
            JSONObject resultDatas = new JSONObject().parseObject(response.body().string());
            JSONArray d1 = resultDatas.getJSONArray("E_RETURN");
            JSONObject rData1 = d1.getJSONObject(0);
            if(rData1.get("E_CODE") != null && rData1.get("E_CODE").toString().equals("S")){
                System.out.println("E_MSG：" + rData1.get("E_MSG"));
                writeLog(rData1.get("E_MSG"));
                return SUCCESS;
            } else if(rData1.get("E_CODE") != null && rData1.get("E_CODE").toString().equals("E")){
                if(rData1.get("E_MSG") != null){
                    requestinfo.getRequestManager().setMessagecontent(rData1.get("E_MSG").toString());
                } else{
                    requestinfo.getRequestManager().setMessagecontent("调用SAP，插入物料信息失败！");
                }
                System.out.println("E_MSG：" + rData1.get("E_MSG"));
                writeLog(rData1.get("E_MSG"));
                requestinfo.getRequestManager().setMessageid("错误信息编号" + requestinfo.getRequestid());
                return FAILURE_AND_CONTINUE ;//return返回固定返回`SUCCESS`
            } else{
                requestinfo.getRequestManager().setMessagecontent("调用SAP，插入物料信息失败！");
                requestinfo.getRequestManager().setMessageid("错误信息编号" + requestinfo.getRequestid());
                return FAILURE_AND_CONTINUE ;//return返回固定返回`SUCCESS`
            }
        } catch (IOException e) {
            requestinfo.getRequestManager().setMessagecontent("返回自定义的错误信息");
            requestinfo.getRequestManager().setMessageid("错误信息编号");
            writeLog("返回自定义的错误信息");
            e.printStackTrace();
            return SUCCESS ;
        }
    }

}

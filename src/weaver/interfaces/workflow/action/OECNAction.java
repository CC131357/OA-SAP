package weaver.interfaces.workflow.action;


import com.alibaba.fastjson.JSONArray;
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
        String BUKRS=Util.null2String(mid.get("gsdm"));  //公司代码
        String ZOADJ=Util.null2String(mid.get("liucbh")); //流程编号

        //获取明细表信息
        DetailTable[] detailtable = requestInfo.getDetailTableInfo().getDetailTable();
        DetailTable xm1 = detailtable[1];// 指定明细表2
        Row[] sxm1 = xm1.getRow();// 当前明细表的所有数据,按行存储
        Row r1 = sxm1[0];// 指定行
        Cell co[] = r1.getCell();//行按列存
        JSONObject detailtObject = new  JSONObject();
        JSONArray jsonArray = new JSONArray();
        detailtObject.put("BUKRS",BUKRS);//公司代码
        detailtObject.put("ZOADJ",ZOADJ);//OA单据类型
        jsonArray.add(detailtObject);
        System.out.println(jsonArray);
        jsonObj.put("IT_DATA",jsonArray);
        String shuju = jsonObj.toString();
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), shuju);
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic WlBPVVNFUjoxcWF6QFdTWA==")
                .url(REQUESTPATH)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String data = null;
        try {
            data = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject database = JSONObject.parseObject(data);
        String e_code = database.getString("E_CODE");
        String e_msg = database.getString("E_MSG");
        if ("S".equals(e_code)){
            //表示数据传输成功，正常提交
            System.out.println("成功");
            //获取新的型号
            RecordSetDataSource rsds = new RecordSetDataSource("OA");
            rsds.executeSql("update formtable_main_251 set dqcpxh ='"+123+"'where = '"+requestId+"'");
            return SUCCESS;
        }else{
            //数据传输失败，则将错误信息返回到页面
            JSONArray et_data = database.getJSONArray("ET_DATA");
            JSONObject et_datainfo = et_data.getJSONObject(0);
            writeLog("费用报销流程的接口回传信息," +
                    "创建人【"+requestInfo.getCreatorid()+"】" +
                    "流程id【"+requestInfo.getWorkflowid()+"】" +
                    "流程请求id【"+requestInfo.getRequestid()+"】" +
                    "当前节点【"+requestInfo.getRequestManager().getNodeid()+"】" +
                    "请求标题【"+requestInfo.getRequestManager().getRequestname()+"】"+
                    "返回信息【"+et_datainfo.toJSONString()+"】");

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




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

import static weaver.interfaces.workflow.action.ConstUrl.deductioUrl;
import static weaver.interfaces.workflow.action.ConstUrl.masterCustom;


/**
 * @author gongchen
 * @流程名称 退货扣款订单创建流程
 */
public class DeductionAction extends BaseBean implements Action {
    @Override
    public String execute(RequestInfo requestInfo) {
        JSONObject jsonObj =new JSONObject();
        //获取主表信息、初始化主表
        Map<String, String> mid=getPropertyMap(requestInfo.getMainTableInfo().getProperty());
        String BUKRS=Util.null2String(mid.get("gsdm"));  //公司代码
        String ZOABH =Util.null2String(mid.get("liucbh")); //流程编号
        String KUNNR = Util.null2String(mid.get("khdm")); //客户代码
        JSONObject detailOne =new JSONObject();
        detailOne.put("KUNNR","");
        String msg = detailOne.toString();
        String ZLSCH = Util.null2String(mid.get("fkfs"));//付款方式
        String BVTYP = Util.null2String(mid.get("zhxz")); //账户选择
        String WRBTR=Util.null2String(mid.get("zhaodfyzj")); //合计金额（人民币）
        JSONObject detailtObject = new  JSONObject();
        detailtObject.put("BUKRS",BUKRS);//公司代码
        detailtObject.put("ZOABH",ZOABH);//OA单据类型
        detailtObject.put("AUART","ZRD");//订单类型
        detailtObject.put("BVTYP",BVTYP);//账户选择
        detailtObject.put("WRBTR",WRBTR);//费用金额
        detailtObject.put("ZLSCH",ZLSCH);//付款方式
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(detailtObject);
        System.out.println(jsonArray);
        jsonObj.put("IT_DATA",jsonArray);
        String shuju = jsonObj.toString();
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), shuju);
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic WlBPVVNFUjoxcWF6QFdTWA==")
                .url(deductioUrl)
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
        /*String e_code = database.getString("E_CODE");
        JSONArray et_data = database.getJSONArray("ET_DATA");*/
        //JSONObject one = database.getJSONObject("MT_PaymentDataTransfer_Out_Resp");
        String e_code = database.getString("E_CODE");
        String e_msg = database.getString("E_MSG");
        if ("S".equals(e_code)){
            //表示数据传输成功，正常提交
            System.out.println("成功");
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




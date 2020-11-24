package weaver.interfaces.workflow.action;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import com.weaver.general.Util;
import okhttp3.*;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @流程名称 费用报销申请流程
 */
public class ReimbursementAction extends BaseBean implements Action {
    public static final String REQUESTPATH = "http://10.10.10.31:50000/RESTAdapter/OA/S0063PaymentDataTransfer";
    @Override
    public String execute(RequestInfo requestInfo) {
        JSONObject jsonObj =new JSONObject();
        String requestId = requestInfo.getRequestid();
        //获取主表信息、初始化主表
        Map<String, String> mid=getPropertyMap(requestInfo.getMainTableInfo().getProperty());
        String BUKRS=Util.null2String(mid.get("lrzx"));  //利润中心
        String ZOADJ=Util.null2String(mid.get("liucbh")); //流程编号
        String ZDJLX = "报销"; //票据类型
        String WRBTR=Util.null2String(mid.get("baoxjehjyb")); //报销合计金额（人民币）
        String LIFNR = Util.null2String(mid.get("lingkrkhhjzh")); //领款人开户行
        String BVTYP = Util.null2String(mid.get("yinhzh")); //领款人银行账号

        //获取明细表信息
        DetailTable[] detailtable = requestInfo.getDetailTableInfo().getDetailTable();
        DetailTable dt = detailtable[0];// 指定明细表
        Row[] s = dt.getRow();// 当前明细表的所有数据,按行存储
        JSONArray jsonArray = new JSONArray();
        for (int j = 0; j < s.length; j++) {
            Row r = s[j];// 指定行
            Cell c[] = r.getCell();// 每行数据再按列存储
            JSONObject detailtObject = new  JSONObject();
            for (int k = 0; k < c.length; k++) {
                Cell c1 = c[k];// 指定列
                String name = c1.getName();// 明细字段名称
                String value = c1.getValue();// 明细字段的值
                if (name.equals("bb")){
                    detailtObject.put("WAERS",value); //币别-货币码
                }
                if (name.equals("feiysm")){
                    detailtObject.put("BKTXT",value);  //费用说明-凭证抬头文本
                }
            }
            detailtObject.put("BUKRS",BUKRS);
            detailtObject.put("ZOADJ",ZOADJ);
            detailtObject.put("ZDJLX",ZDJLX);
            detailtObject.put("WRBTR",WRBTR);
            detailtObject.put("LIFNR",LIFNR);
            detailtObject.put("BVTYP",BVTYP);
            jsonArray.add(detailtObject);
            System.out.println(jsonArray);
        }
        jsonObj.put("IT_DATA",jsonArray);
        String shuju = jsonObj.toString();
        //System.out.println(jsonObj.toJSONString());
        //ApiUtil apiUtil = new ApiUtil();
        /*  JSONArray data = apiUtil.invokeApi(jsonObj.toJSONString());*/
        //JSONObject data = apiUtil.invokeApi(jsonObj.toJSONString());
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
        /*String e_code = database.getString("E_CODE");
        JSONArray et_data = database.getJSONArray("ET_DATA");*/
        JSONObject one = database.getJSONObject("MT_PaymentDataTransfer_Out_Resp");
        String e_code = one.getString("E_CODE");
        String e_msg = one.getString("E_MSG");
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




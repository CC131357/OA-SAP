package weaver.interfaces.workflow.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import com.weaver.general.Util;
import okhttp3.*;
import weaver.soa.workflow.request.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static weaver.interfaces.workflow.action.CommonUtil.reimbursementUrl;

public class LoanAction extends BaseBean implements Action {

/**
 * @作者：高梦利
 * @流程名称 员工借款流程
 */
    //public static final String REQUESTPATH = "http://10.10.10.31:50000/RESTAdapter/OA/S0063PaymentDataTransfer";
    @Override
    public String execute(RequestInfo requestInfo) {
        JSONObject jsonObj =new JSONObject();
        String requestId = requestInfo.getRequestid();
        //获取主表信息、初始化主表
        Map<String, String> mid=getPropertyMap(requestInfo.getMainTableInfo().getProperty());

        String ZOADJ=Util.null2String(mid.get("liucbh")); //流程编号
  /*      String ZDJLX = "Z004"; //票据类型_借支的编码为Z004*/
        String WRBTR=Util.null2String(mid.get("bencjkje")); //本次借款金额
        String LIFNR = Util.null2String(mid.get("zh")); //借款人开户行
        String BVTYP = Util.null2String(mid.get("zhxz")); //借款人银行账号
        String WAERS = Util.null2String(mid.get("hbm")); //货币
        String BUKRS = Util.null2String(mid.get("gsdm"));//公司代码
        String ZDJLX = Util.null2String(mid.get("djlx"));//单据类型
        String HKONT = Util.null2String(mid.get("hjkm"));//总账科目-会计科目


        JSONArray jsonArray = new JSONArray();
        JSONObject detailtObject = new  JSONObject();

        detailtObject.put("ZOADJ",ZOADJ);
        detailtObject.put("ZDJLX",ZDJLX);
        detailtObject.put("WRBTR",WRBTR);
        detailtObject.put("LIFNR",LIFNR);
        detailtObject.put("BVTYP",BVTYP);
        detailtObject.put("WAERS",WAERS);
        detailtObject.put("BUKRS",BUKRS);
        detailtObject.put("HKONT",HKONT);

        jsonArray.add(detailtObject);
//        System.out.println(jsonArray);

        jsonObj.put("IT_DATA",jsonArray);
        String shuju = jsonObj.toString();

        //调取接口
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), shuju);
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic WlBPVVNFUjoxcWF6QFdTWA==")
                .url(reimbursementUrl)
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
        if ("S".equals(e_code)){
            //表示数据传输成功，正常提交
            System.out.println("成功");
            return SUCCESS;
        }else{
            //数据传输失败，则将错误信息返回到页面
            JSONArray et_data = database.getJSONArray("ET_DATA");
            JSONObject et_datainfo = et_data.getJSONObject(0);
            writeLog("员工借款接口回传信息," +
                    "创建人【"+requestInfo.getCreatorid()+"】" +
                    "流程id【"+requestInfo.getWorkflowid()+"】" +
                    "流程请求id【"+requestInfo.getRequestid()+"】" +
                    "当前节点【"+requestInfo.getRequestManager().getNodeid()+"】" +
                    "请求标题【"+requestInfo.getRequestManager().getRequestname()+"】"+
                    "返回信息【"+et_datainfo.toJSONString()+"】");

            requestInfo.getRequestManager().setMessageid("99999");
            //requestInfo.getRequestManager().setMessagecontent(e_msg.toString());
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




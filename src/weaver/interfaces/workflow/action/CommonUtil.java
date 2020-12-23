package weaver.interfaces.workflow.action;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import okhttp3.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import weaver.soa.workflow.request.Cell;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;
import weaver.soa.workflow.request.Row;

public class CommonUtil extends BaseBean {
    private final static String baseUrl="http://10.10.10.32:50000/RESTAdapter/";
    //授权
    public final static String authorization="Basic WlBPVVNFUjoxcWF6QFdTWA==";
    //海关
    public final static String customStateUrl=baseUrl+"OA/S0006CustomsApproveUpdate";
    //报销（费用报销，业务招待报销，差旅报销）
    public final static String reimbursementUrl=baseUrl+"OA/S0063PaymentDataTransfer";
    //退货扣款
    public final static String deductioUrl=baseUrl+"OA/S0008PaymentSOCreate";
    //凭证创建
    public final static String voucherUrl=baseUrl+"OA/S0062AccDocCreate";
    //采购申请创建
    public final static String purchaseUrl=baseUrl+"OA/S0042PRcreate";
    //差旅费用报销
    public final static String travelExpenseUrl=baseUrl+"OA/S0063PaymentDataTransfer";
    //客户信用特批
    public final static String cMCreditApproveUrl=baseUrl+"OA/S0004CMCreditApprove";
    //外部ECN
    public final static String OECNUrl=baseUrl+"OA/S0125ECNcatCreate";
    //客户主数据
    public final static String masterCustomUrl=baseUrl+"OA/S0002CMget";
    //订金特批
    public final static String despositApproUrl=baseUrl+"OA/S0008PaymentSOCreate";
    //物料主数据
    public final static String MaterialUrl=baseUrl+"SW/MaterialModify";
    //盘点差异
    public final static String inventoryDiffUrl=baseUrl + "OA/S0051IvtryDiffApproveUpdate";
    //物料主数据创建
    public final static String masterMaterialUrl=baseUrl + "SW/MaterialModify";

    public static JSONObject Post(String url, String content) throws IOException,JSONException{
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic WlBPVVNFUjoxcWF6QFdTWA==")
                .url(url)
                .post(requestBody)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            return JSONObject.parseObject(response.body().string());
        } catch (IOException e) {
            throw e;
        }catch (JSONException e){
            throw e;
        }
    }

    /**
     * 获取表单主表的值
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

    /**往指定路径推送消息*/
    public JSONObject Post(String url, String content, String authorization) throws IOException,JSONException{
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        Request request = new Request.Builder()
                .addHeader("Authorization", authorization)
                .url(url)
                .post(requestBody)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            return JSONObject.parseObject(response.body().string());
        } catch (IOException e) {
            throw e;
        }catch (JSONException e){
            throw e;
        }
    }

    /**将对应的值设置经Json对象中去*/
    public JSONObject setJsonObject(Row row, Map<String,String> columnMap){
        JSONObject detailObject = new  JSONObject();
        Cell c[] = row.getCell();// 每行数据再按列存储
        for (int k = 0; k < c.length; k++) {
            String name = c[k].getName();// 明细字段名称
            String value = c[k].getValue();// 明细字段的值
            if(columnMap.get(name) != null){
                detailObject.put(columnMap.get(name),value);
            }
        }
        return detailObject;
    }

    /**
     * 获取表单主表的值
     * @param requestInfo
     * @return
     */
    public Map<String, String> getMainTableMap(RequestInfo requestInfo) {
        Property[] property = requestInfo.getMainTableInfo().getProperty();
        Map<String, String> m = new HashMap<>();
        for(Property p : property){
            m.put( p.getName(), p.getValue());
        }
        return m;
    }

    /**输出打印信息*/
    public void printLog(RequestInfo requestInfo, String reqMsg, String returnMsg){
        writeLog(reqMsg +
                "创建人【"+requestInfo.getCreatorid()+"】" +
                "流程id【"+requestInfo.getWorkflowid()+"】" +
                "流程请求id【"+requestInfo.getRequestid()+"】" +
                "当前节点【"+requestInfo.getRequestManager().getNodeid()+"】" +
                "请求标题【"+requestInfo.getRequestManager().getRequestname()+"】"+
                "返回信息【"+ returnMsg +"】");
    }

}

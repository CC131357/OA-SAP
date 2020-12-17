package weaver.interfaces.workflow.action;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import weaver.soa.workflow.request.Property;

public class CommonUtil {
    private final static String baseUrl="http://10.10.10.32:50000/RESTAdapter/";
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

    /**截取指定长度的字符串*/
    public static String subStringByLength(String source, int length) {
        if(source.length() <= length){
            return source;
        } else{
            return source.substring(0,length);
        }
    }
}

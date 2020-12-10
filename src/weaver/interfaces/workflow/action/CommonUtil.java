package weaver.interfaces.workflow.action;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import java.io.IOException;

public class CommonUtil {
    private final static String baseUrl="http://10.10.10.31:50000/RESTAdapter/";
    //海关
    public final static String customStateUrl=baseUrl+"OA/S0006CustomsApproveUpdate";
    //报销（费用报销，业务招待报销，差旅报销）
    public final static String reimbursementUrl=baseUrl+"OA/S0063PaymentDataTransfer";
    //退货扣款
    public final static String deductioUrl=baseUrl+"OA/S0006CustomsApproveUpdate";
    //采购申请创建
    public final static String purchaseUrl=baseUrl+"OA/S0042PRcreate";
    //差旅费用报销
    public final static String travelExpenseUrl=baseUrl+"OA/S0063PaymentDataTransfer";
    //外部ECN
    public final static String OECNUrl=baseUrl+"OA/S0125ECNcatCreate";
    //客户主数据
    public final static String masterCustomUrl=baseUrl+"OA/S0002CMget";
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
}

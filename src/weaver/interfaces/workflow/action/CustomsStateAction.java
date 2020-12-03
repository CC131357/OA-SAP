package weaver.interfaces.workflow.action;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;
import okhttp3.*;
import weaver.general.BaseBean;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;


public class CustomsStateAction extends BaseBean implements Action {
    final String url="http://10.10.10.31:50000/RESTAdapter/OA/S0006CustomsApproveUpdate";
    @Override
    public String execute(RequestInfo requestInfo) {
        String requestId = requestInfo.getRequestid();
        String formNo="",auditDate,auditState;
        Property[] properties= requestInfo.getMainTableInfo().getProperty();
        for (Property p:properties){
            if(p.getName().equals("formNo")){
                formNo=p.getValue();
                break;
            }
        }
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd" );
        Date d= new Date();
        auditDate = sdf.format(d);
        auditState= "C";
        Hashtable<String,String> ht=new Hashtable<>();
        ht.put("IV_ZSQDH",formNo);
        ht.put("IV_ZOABH",requestId);
        ht.put("IV_ZSPRQ",auditDate);
        ht.put("IV_ZSPZT",auditState);
        String body=JSONObject.toJSONString(ht);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body);
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic WlBPVVNFUjoxcWF6QFdTWA==")
                .url(url)
                .post(requestBody)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            JSONObject result= JSONObject.parseObject(response.body().string());
            String code=result.getString("E_CODE");
            requestInfo.getRequestManager().setMessage(result.getString("E_MSG"));
            if(code.equals("S")){
                return SUCCESS;
            }else if(code.equals("E")){
                return FAILURE_AND_CONTINUE;
            }
        } catch (IOException e) {
            e.printStackTrace();
            writeLog(e.getMessage());
            requestInfo.getRequestManager().setMessage(e.getMessage());
        }catch (JSONException e){
            e.printStackTrace();
            writeLog(e.getMessage());
            requestInfo.getRequestManager().setMessage(e.getMessage());
        }
        return FAILURE_AND_CONTINUE;
    }
}

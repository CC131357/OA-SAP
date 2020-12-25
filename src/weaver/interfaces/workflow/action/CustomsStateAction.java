package weaver.interfaces.workflow.action;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.caucho.xtpdoc.Code;
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
    @Override
    public String execute(RequestInfo requestInfo) {
        String auditDate,auditState;
        Property[] properties= requestInfo.getMainTableInfo().getProperty();
        Map<String, String> propertyMap=CommonUtil.getPropertyMap(properties);
        String flowNo=propertyMap.get("liucbh");
        String formNo=propertyMap.get("formNo");
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd" );
        Date d= new Date();
        auditDate = sdf.format(d);
        String src=requestInfo.getRequestManager().getSrc();
        if(src.equals("reject")){
            auditState= "D";
        }else{
            auditState= "C";
        }
        Hashtable<String,String> ht=new Hashtable<>();
        ht.put("IV_ZSQDH",formNo);
        ht.put("IV_ZOABH",flowNo);
        ht.put("IV_ZSPRQ",auditDate);
        ht.put("IV_ZSPZT",auditState);
        String body=JSONObject.toJSONString(ht);
        try {
            JSONObject result= CommonUtil.Post(CommonUtil.customStateUrl,body);
            String code=result.getString("E_CODE");
            requestInfo.getRequestManager().setMessage(result.getString("E_MSG"));
            if("S".equals(code)){
                return SUCCESS;
            }else if("E".equals(code)){
                return FAILURE_AND_CONTINUE;
            }
        } catch (IOException e) {
            writeLog(e.getMessage());
            requestInfo.getRequestManager().setMessage(e.getMessage());
        }catch (JSONException e){
            writeLog(e.getMessage());
            requestInfo.getRequestManager().setMessage(e.getMessage());
        }
        return FAILURE_AND_CONTINUE;
    }
}

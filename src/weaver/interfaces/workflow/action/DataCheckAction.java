package weaver.interfaces.workflow.action;

import com.weaver.general.BaseBean;
import com.weaver.general.Util;
import weaver.conn.RecordSetDataSource;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gongchen
 * @流程名称
 */
public class DataCheckAction extends BaseBean implements Action {
    @Override
    public String execute(RequestInfo requestInfo) {
        String requestId = requestInfo.getRequestid();
        //获取主表信息、初始化主表
        Map<String, String> mid=getPropertyMap(requestInfo.getMainTableInfo().getProperty());
        String MAKTX= Util.null2String(mid.get("wlms"));  //物料描述
        try {
            RecordSetDataSource rsds = new RecordSetDataSource("OA");
            rsds.executeSql("select wlms from  V_SAP_wlms where wlms='"+MAKTX+"'");
            //String wlms = null;
            String msg = "物料描述已经存在,请更换物料描述";
            if(rsds.next()){
                requestInfo.getRequestManager().setMessage(msg);
                return FAILURE_AND_CONTINUE;
            }
            else {
                return SUCCESS;
            }
        }catch (Exception e){
            requestInfo.getRequestManager().setMessage(e.toString());
        }
        return FAILURE_AND_CONTINUE;
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


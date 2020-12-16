package weaver.interfaces.workflow.action;



import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import com.weaver.general.Util;
import weaver.conn.RecordSetDataSource;
import weaver.mobile.webservices.workflow.WorkflowMainTableInfo;
import weaver.mobile.webservices.workflow.WorkflowRequestTableField;
import weaver.mobile.webservices.workflow.WorkflowRequestTableRecord;
import weaver.soa.workflow.request.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



/**
 * @author gongchen
 * @流程名称 外部ECN流程
 */
public class OECNAction extends BaseBean implements Action {
    @Override
    public String execute(RequestInfo requestInfo) {
        JSONObject jsonObj =new JSONObject();
        String requestId = requestInfo.getRequestid();
        //获取主表信息、初始化主表
        Map<String, String> mid=getPropertyMap(requestInfo.getMainTableInfo().getProperty());
        String MATNR=Util.null2String(mid.get("dqcpxh"));  //当前产品型号
        JSONObject js = new JSONObject();
        js.put("MATNR",MATNR);
        jsonObj.put("IS_INPUT",js);
        String shuju = jsonObj.toString();
        JSONObject result = null;
        try {
            result = CommonUtil.Post(CommonUtil.OECNUrl,shuju);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String e_code = result.getString("E_CODE");
        if ("S".equals(e_code)){
            //表示数据传输成功，正常提交
            System.out.println("成功");
            JSONObject data = result.getJSONObject("OS_OUTPUT");
            String ZMATNR_N = data.getString("ZMATNR_N");//新产品型号
            String VERID = data.getString("VERID");//当前旧版本
            RecordSetDataSource rsds = new RecordSetDataSource("OA");
            rsds.executeSql("update formtable_main_251 set xcpxh='" + ZMATNR_N + "',jbb='" + VERID + "' where requestId='" + requestId + "'");
/*            Property[] properties = requestInfo.getMainTableInfo().getProperty();// 获取表单主字段信息
            for (int i = 0; i < properties.length; i++) {
                String name = properties[i].getName();// 主字段名称
                String value = Util.null2String(properties[i].getValue());// 主字段对应的值
                if(name != null && name.equals("xcpxh")){
                    properties[i].setValue(ZMATNR_N);
                }
                if(name != null && name.equals("jbb")){
                    properties[i].setValue(VERID);
                }
            }
            String xcpxh=Util.null2String(mid.get("xcpxh"));  //新产品型号
            String jbb=Util.null2String(mid.get("jbb"));  //旧版本
            if (xcpxh != null && (xcpxh.length() != 0)&&jbb != null && (jbb.length() != 0)){
                return SUCCESS;
            }*/
/*            String XCPXH=Util.null2String(mid.get("xcpxh"));  //当前产品型号
            mid.put("dqcpxh",ZMATNR_N);
            mid.put("jbb",VERID);*/

/*            WorkflowMainTableInfo workflowMainTableInfo = new WorkflowMainTableInfo();//主表
            WorkflowRequestTableRecord[] workflowRequestTableRecord = new WorkflowRequestTableRecord[1];//主表字段只有一条记录
            WorkflowRequestTableField[] WorkflowRequestTableField = new WorkflowRequestTableField[2];//主的2个字段
            WorkflowRequestTableField[0] = new WorkflowRequestTableField();
            WorkflowRequestTableField[0].setFieldName("xcpxh");//新产品型号
            WorkflowRequestTableField[0].setFieldValue(ZMATNR_N);//值
            WorkflowRequestTableField[0].setFieldName("jbb");//旧版本
            WorkflowRequestTableField[0].setFieldValue(VERID);//值
            workflowRequestTableRecord[0] = new WorkflowRequestTableRecord();
            workflowRequestTableRecord[0].setWorkflowRequestTableFields(WorkflowRequestTableField);
            workflowMainTableInfo.setRequestRecords(workflowRequestTableRecord);*/
            return SUCCESS;
        }else{
            //数据传输失败，则将错误信息返回到页面
            JSONObject et_datainfo = result.getJSONObject("E_MSG");
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




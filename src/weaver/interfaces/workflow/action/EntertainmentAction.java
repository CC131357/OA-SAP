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


/**
 * @author gongchen
 * @流程名称 业务招待费用报销申请流程
 * @copy 明细中默认为0，即允许不增加明细
 */
public class EntertainmentAction extends BaseBean implements Action {
    //public static final String REQUESTPATH = "http://10.10.10.31:50000/RESTAdapter/OA/S0063PaymentDataTransfer";
    @Override
    public String execute(RequestInfo requestInfo) {
        JSONObject jsonObj =new JSONObject();
        //获取主表信息、初始化主表
        Map<String, String> mid=getPropertyMap(requestInfo.getMainTableInfo().getProperty());
        String BUKRS=Util.null2String(mid.get("gsdm"));  //公司代码
        String ZOADJ=Util.null2String(mid.get("liucbh")); //流程编号
        String LIFNR = Util.null2String(mid.get("gyshzqrzh")); //供应商或债权人账户
        String ZDJLX = Util.null2String(mid.get("djlx"));; //单据类型
        String ZLSCH = Util.null2String(mid.get("fkfs"));//付款方式
        String BVTYP = Util.null2String(mid.get("zhxz")); //账户选择
        //String WRBTR=Util.null2String(mid.get("zhaodfyzj")); //合计金额（人民币）
        String HKONT = Util.null2String(mid.get("hjkm")); //会计科目
        String KOSTL=Util.null2String(mid.get("cbzx")); //成本中心
        String CF = Util.null2String(mid.get("canfhj"));//餐费
        String JTF = Util.null2String(mid.get("jiaotfhj")); //
        String ZSF=Util.null2String(mid.get("zhusfhjrmb")); //
        String LPF = Util.null2String(mid.get("lipfhjrmb")); //
        String HDF=Util.null2String(mid.get("huodfhj")); //
        //获取明细表信息
        DetailTable[] detailtable = requestInfo.getDetailTableInfo().getDetailTable();// 获取所有明细表
        int len = detailtable.length;
        JSONArray detailArray = new JSONArray();
        for(int i = 0; i < detailtable.length; i ++){
            DetailTable dt = detailtable[i];// 指定明细表
            Row[] s = dt.getRow();// 当前明细表的所有数据,按行存储
            if (s.length>0) {
                Row r = s[0];// 指定第一行
                Cell c[] = r.getCell();// 第一行数据再按列存储
                JSONObject detailtObject = new JSONObject();
                for (int k = 0; k < c.length; k++) {
                    Cell c1 = c[k];// 指定列
                    String name = c1.getName();// 明细字段名称
                    String value = c1.getValue();// 明细字段的值
                    if (name.equals("bb")) {
                        detailtObject.put("WAERS", value); //币别-货币码
                    }
                    if (name.equals("fysm")){
                        detailtObject.put("BKTXT", value); //费用说明
                    }
                }
                detailtObject.put("BUKRS", BUKRS);//公司代码
                detailtObject.put("ZOADJ", ZOADJ);//OA单据
                detailtObject.put("LIFNR", LIFNR);//供应商或债权人账号
                detailtObject.put("ZDJLX", ZDJLX);//单据类型
                detailtObject.put("BVTYP", BVTYP);//账户选择
                detailtObject.put("ZLSCH", ZLSCH);//付款方式
                detailtObject.put("HKONT", HKONT);//会计科目
                detailtObject.put("KOSTL", KOSTL);//成本中心
                if (i==0){
                    detailtObject.put("WRBTR", CF);//餐费总计
                }
                if (i==1){
                    detailtObject.put("WRBTR", JTF);//交通总计
                }
                if (i==2){
                    detailtObject.put("WRBTR", ZSF);//住宿总计
                }
                if (i==3){
                    detailtObject.put("WRBTR", LPF);//礼品总计
                }
                if (i==4){
                    detailtObject.put("WRBTR", HDF);//活动总计
                }
                detailArray.add(detailtObject);
            }
        }
        jsonObj.put("IT_DATA",detailArray);
        String shuju = jsonObj.toString();
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), shuju);
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic WlBPVVNFUk9BOjFxYXpAV1NYM2VkYzw+")
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
            requestInfo.getRequestManager().setMessage(et_datainfo.getString("MEG"));
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




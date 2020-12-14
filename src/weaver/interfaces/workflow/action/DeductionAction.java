package weaver.interfaces.workflow.action;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import com.weaver.general.Util;
import okhttp3.*;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author gongchen
 * @流程名称 退货扣款订单创建流程
 */
public class DeductionAction extends BaseBean implements Action {
    //public static final String REQUESTPATH = "http://10.10.10.31:50000/RESTAdapter/OA/S0008PaymentSOCreate";
    @Override
    public String execute(RequestInfo requestInfo) {
        JSONObject jsonObj =new JSONObject();
        jsonObj.put( "I_TESTRUN","X");
        //获取主表信息、初始化主表
        Map<String, String> mid=getPropertyMap(requestInfo.getMainTableInfo().getProperty());
        String KUNAG=Util.null2String(mid.get("khdm"));  //客户代码
        String ZOABH=Util.null2String(mid.get("liucbh")); //流程编号
        JSONObject approveJson =new JSONObject();
        approveJson.put("KUNNR",KUNAG);
        String msg = approveJson.toString();
        String VKORG=null;
        String VTWEG=null;
        String SPART=null;
        try {
            JSONObject result= CommonUtil.Post(CommonUtil.masterCustomUrl,msg);
            JSONArray IT_VKG = result.getJSONArray("IT_VKG");
            JSONObject mastrData = IT_VKG.getJSONObject(0);
            VKORG = mastrData.getString("VKORG");//销售组织
            VTWEG = mastrData.getString("VTWEG");//分销渠道
            SPART = mastrData.getString("SPART");//产品组
        } catch (IOException e) {
            writeLog(e.getMessage());
            requestInfo.getRequestManager().setMessage(e.getMessage());
        }catch (JSONException e){
            writeLog(e.getMessage());
            requestInfo.getRequestManager().setMessage(e.getMessage());
        }

        String BSTKD = Util.null2String(mid.get("topskkdh")); //TOPS单号
        String BSTDK = Util.null2String(mid.get("tuihrq"));//退货日期
        String WAERK = Util.null2String(mid.get("bibie")); //货币
        String MATNR = Util.null2String(mid.get("chanpxh")); //物料（产品型号）
        String KWMENG = Util.null2String(mid.get("tuihsl"));//数量
        String VRKME = Util.null2String(mid.get("dw"));//单位
        String scgc = Util.null2String(mid.get("scgc"));//工厂
        String WRBTR = Util.null2String(mid.get("hejkk"));//合计扣款(凭证金额)
        String WERKS = null;//工厂
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd" );
        SimpleDateFormat sdf1 =new SimpleDateFormat("yyyyMM" );
        Date d= new Date();//创建格式对象获取当前日期
        String strDate = sdf.format(d);//格式化日期2020-12-10
        String strDate1 = sdf1.format(d);//格式化日期202012
        if (scgc.equals("0")){
            return FAILURE_AND_CONTINUE;
        }
        if (scgc.equals("1")){
            WERKS = "1000";
        }
        String kklx = Util.null2String(mid.get("kklx"));//扣款类型
        String ABRVW = null;
        String BKTXT = null;//参考凭证文本
        if (kklx.equals("0")){
            ABRVW = "A";//PCBA扣款
            BKTXT = strDate1+"PCBA扣款";
        }
        if (kklx.equals("1")){
            ABRVW = "B";//PCB扣款
            BKTXT =strDate1+ "PCB扣款";
        }
        if (kklx.equals("2")){
            ABRVW = "C";//商品修理费
            BKTXT = strDate1+"商品修理费";
        }
        if (kklx.equals("3")){
            ABRVW = strDate1+"D";//运费
            BKTXT = "运费";
        }
        String ZKKBS = Util.null2String(mid.get("koukbs"));//扣款倍数
        String ZKKYY = Util.null2String(mid.get("tuihyy"));//扣款原因
        JSONObject vbakObject = new  JSONObject();//创建第一个存储结构
        vbakObject.put("ZOABH",ZOABH);//OA流程编号
        vbakObject.put("AUART","ZRD");//订单类型
        vbakObject.put("VKORG",VKORG);//费用金额
        vbakObject.put("VTWEG",VTWEG);//分销渠道
        vbakObject.put("SPART",SPART);//产品组
        vbakObject.put("KUNAG",KUNAG);//客户编码
        vbakObject.put("BSTKD",BSTKD);//TOPS扣款单号
        vbakObject.put("BSTDK",BSTDK);//扣款日期
        vbakObject.put("WAERK",WAERK);//货币
        jsonObj.put("IT_VBAK",vbakObject);//添加第一个jsonobject

        JSONObject vbapObject = new  JSONObject();//创建第二个存储结构
        vbapObject.put("POSNR","10");//行项目号
        vbapObject.put("MATNR",MATNR);//物料
        vbapObject.put("KWMENG",KWMENG);//数量
        vbapObject.put("VRKME",VRKME);//单位
        vbapObject.put("PSTYV","ZRDN");//固定值
        vbapObject.put("WERKS",WERKS);//工厂
        vbapObject.put("ABRVW",ABRVW);//扣款类型
        vbapObject.put("ZKKBS",ZKKBS);//扣款倍数
        vbapObject.put("ZKKYY",ZKKYY);//扣款原因
        JSONArray vbapArray = new JSONArray();
        vbapArray.add(vbapObject);
        System.out.println(vbapArray);
        jsonObj.put("IT_VBAP",vbapArray);//添加第二个
        String shuju = jsonObj.toString();
        JSONObject database = null;
        try {
            database = CommonUtil.Post(CommonUtil.deductioUrl,shuju);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String e_code = database.getString("E_CODE");
        String e_msg = database.getString("E_MSG");
        /**
         * 获取扣款订单创建结果，开始创建凭证
         */
        if ("S".equals(e_code)){
            //表示数据传输成功，正常提交
            System.out.println("成功");
            //获取扣款订单号
            String E_VBELN = database.getString("E_VBELN");
            JSONObject  voucher = new JSONObject();//凭证数据结构
            JSONArray detailArray = new JSONArray();
            //凭证第一条数
            JSONObject  detail1 = new JSONObject();//记账码1
            JSONObject  detail2 = new JSONObject();//记账码2
            JSONObject  detail3 = new JSONObject();//记账码3
            detail1.put("BUKRS","1000");//公司代码
            detail1.put("BUDAT",strDate);//凭证中的过账日期
            detail1.put("BLDAT",strDate);//凭证中的凭证日期
            detail1.put("BLART","DG");//凭证类型
            detail1.put("BKTXT",BKTXT);//凭证抬头文本
            detail1.put("XBLNR",E_VBELN);//参考凭证编号
            detail1.put("WAERK",WAERK);//货币
            detail1.put("BSCHL","40");//过账码
            detail1.put("KUNNR","1000");//过账码
            //总账科目
            if ("CNY".equals(WAERK)){
                if (kklx.equals("0")){
                    double zje = Double.parseDouble(WRBTR);//字符串转数字
                    double jine1 = zje * 0.13;
                    double jine2 = zje - jine1;
                    detail1.put("HKON","2221010602");//总账科目1
                    detail1.put("WRBTR",jine1);//借款金额1


                    detail3.put("BUKRS","1000");//公司代码
                    detail3.put("BUDAT",strDate);//凭证中的过账日期
                    detail3.put("BLDAT",strDate);//凭证中的凭证日期
                    detail3.put("BLART","DG");//凭证类型
                    detail3.put("BKTXT",BKTXT);//凭证抬头文本
                    detail3.put("XBLNR",E_VBELN);//参考凭证编号
                    detail3.put("WAERK",WAERK);//货币
                    detail3.put("BSCHL","40");//过账码
                    detail3.put("HKON","6001020000");//总账科目2
                    detail3.put("WRBTR",jine2);//借款金额2
                    detail3.put("KUNNR","1000");//过账码
                }
            }
            detail1.put("HKONT","40");
            detail1.put("KUNNR","1000");//客户代码
            detail1.put("WRBTR",WRBTR);//凭证金额
            //凭证第二条数据(应收账款)
            detail2.put("BUKRS","1000");//公司代码
            detail2.put("BUDAT",strDate);//凭证中的过账日期
            detail2.put("BLDAT",strDate);//凭证中的凭证日期
            detail2.put("BLART","DG");//凭证类型
            detail2.put("BKTXT",BKTXT);//凭证抬头文本
            detail2.put("XBLNR",E_VBELN);//参考凭证编号
            detail2.put("WAERK",WAERK);//货币
            detail2.put("BSCHL","11");//过账码
            detail1.put("KUNNR",KUNAG);//外部客户代码
            detail2.put("WRBTR",WRBTR);//凭证金额
            if (kklx.equals("1")){
                detail1.put("HKON","6601012510");//总账科目1
                detail1.put("WRBTR",WRBTR);//借款金额
            }
            if (kklx.equals("2")){
                detail1.put("HKON","6601012520");//总账科目1
                detail1.put("WRBTR",WRBTR);//借款金额
            }
            if (kklx.equals("3")){
                detail1.put("HKON","6601012530");//总账科目1
                detail1.put("WRBTR",WRBTR);//借款金额
            }
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




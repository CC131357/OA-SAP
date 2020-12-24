package weaver.interfaces.workflow.action;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import com.weaver.general.Util;
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
    //public static final String REQUESTPATH = "http://10.10.10.32:50000/RESTAdapter/OA/S0008PaymentSOCreate";
    @Override
    public String execute(RequestInfo requestInfo) {
        JSONObject jsonObj =new JSONObject();
        jsonObj.put( "I_TESTRUN","");
        //获取主表信息、初始化主表
        Map<String, String> mid=getPropertyMap(requestInfo.getMainTableInfo().getProperty());
        String KUNAG=Util.null2String(mid.get("khdm"));  //客户代码
        String ZOABH=Util.null2String(mid.get("liucbh")); //流程编号
        JSONObject approveJson =new JSONObject();
        approveJson.put("KUNNR",KUNAG);
        JSONArray masterArr = new JSONArray();
        masterArr.add(approveJson);
        JSONObject masterJson = new JSONObject();
        masterJson.put("IT_QUERY",masterArr);
        String msg = masterJson.toString();
        String VKORG=null;
        String VTWEG=null;
        String SPART=null;
        String BUKRS=null;
        try {
            //根据客户编码读取对应的销售 组织
            JSONObject result= CommonUtil.Post(CommonUtil.masterCustomUrl,msg);
            JSONArray IT_KNB = result.getJSONArray("IT_KNB");//获取公司代码
            JSONObject dmData = IT_KNB.getJSONObject(0);
            BUKRS = dmData.getString("BUKRS");//公司代码
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
            ABRVW = "A";//PCB扣款
            BKTXT = strDate1+"PCB扣款";
        }
        if (kklx.equals("1")){
            ABRVW = "B";//PCBA扣款
            BKTXT =strDate1+ "PCBA扣款";
        }
        if (kklx.equals("2")){
            ABRVW = "C";//商品修理费
            BKTXT = strDate1+"商品修理费";
        }
        if (kklx.equals("3")){
            ABRVW = "D";//运费
            BKTXT = strDate1+"运费";
        }
        String ZKKBS = Util.null2String(mid.get("koukbs"));//扣款倍数
        String ZKKYY = Util.null2String(mid.get("tuihyy"));//扣款原因
        JSONObject vbakObject = new  JSONObject();//创建第一个存储结构
        vbakObject.put("ZOABH",ZOABH);//OA流程编号
        vbakObject.put("AUART","ZRD");//订单类型

        vbakObject.put("SPART",SPART);//产品组
        vbakObject.put("BSTKD",BSTKD);//TOPS扣款单号
        vbakObject.put("BSTDK",BSTDK);//扣款日期
        vbakObject.put("WAERK",WAERK);//货币

        JSONObject vbapObject = new  JSONObject();//创建第二个存储结构
        vbapObject.put("POSNR","10");//行项目号
        vbapObject.put("MATNR",MATNR);//物料
        vbapObject.put("KWMENG",KWMENG);//数量
        vbapObject.put("PSTYV","ZRDN");//固定值
        vbapObject.put("WERKS",WERKS);//工厂
        vbapObject.put("ABRVW",ABRVW);//扣款类型
        vbapObject.put("ZP01",WRBTR);//销售价
        vbapObject.put("ZKKBS",ZKKBS);//扣款倍数
        vbapObject.put("ZKKYY",ZKKYY);//扣款原因
        JSONArray vbapArray = new JSONArray();
        vbapArray.add(vbapObject);
        System.out.println(vbapArray);
        jsonObj.put("IT_VBAP",vbapArray);//添加第二个
        /**
         * 创建非关联扣款订单和扣款凭证
         */
        if (WERKS.equals("1000")&&VKORG.equals("1000")){//如果销售组织和工厂都是深圳，
            vbakObject.put("VTWEG",VTWEG);//分销渠道
            vbakObject.put("VKORG",VKORG);//销售组织
            vbakObject.put("KUNAG",KUNAG);//客户编码
            jsonObj.put("IS_VBAK",vbakObject);//添加第一个jsonobject
            String shuju = jsonObj.toString();
            JSONObject database = null;
            try {
                database = CommonUtil.Post(CommonUtil.deductioUrl,shuju);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String e_code = database.getString("E_CODE");
            requestInfo.getRequestManager().setMessage(database.getString("E_MSG"));
            /**
             * 获取扣款订单创建结果，开始创建凭证
             */
            if ("S".equals(e_code)) {
                //表示数据传输成功，正常提交
                System.out.println("成功");
                //获取扣款订单号
                String E_VBELN = database.getString("E_VBELN");
                JSONObject voucher = new JSONObject();//凭证数据结构
                JSONArray detailArray = new JSONArray();
                //凭证第一条数
                JSONObject detail1 = new JSONObject();//记账码1
                JSONObject detail2 = new JSONObject();//记账码2
                JSONObject detail3 = new JSONObject();//记账码3
                /**
                 * 创建深圳非关联凭证
                 */
                detail1.put("BUKRS", BUKRS);//公司代码
                detail1.put("BUDAT", strDate);//凭证中的过账日期
                detail1.put("BLDAT", strDate);//凭证中的凭证日期
                detail1.put("BLART", "DG");//凭证类型
                detail1.put("BKTXT", BKTXT);//凭证抬头文本
                detail1.put("XBLNR", E_VBELN);//参考凭证编号
                detail1.put("WAERS", WAERK);//货币
                detail1.put("BSCHL", "40");//过账码
                //detail1.put("WRBTR", WRBTR);//凭证金额
                detail1.put("KUNNR", "1000");//客户代码
                detail1.put("SGTXT",BKTXT);//文本
                //凭证第二条数据(应收账款)
                detail2.put("BUKRS", BUKRS);//公司代码
                detail2.put("BUDAT", strDate);//凭证中的过账日期
                detail2.put("BLDAT", strDate);//凭证中的凭证日期
                detail2.put("BLART", "DG");//凭证类型
                detail2.put("BKTXT", BKTXT);//凭证抬头文本
                detail2.put("XBLNR", E_VBELN);//参考凭证编号
                detail2.put("WAERS", WAERK);//货币
                detail2.put("BSCHL", "11");//过账码
                detail2.put("KUNNR", KUNAG);//外部客户代码
                detail2.put("WRBTR", WRBTR);//贷方金额
                detail2.put("SGTXT",BKTXT);//文本
                if (kklx.equals("1")) {//选择PCBA
                    detail1.put("HKONT", "6601012510");//总账科目1
                    detail1.put("KOSTL","1000106");//深圳成本中心
                    detail1.put("WRBTR", WRBTR);//借款金额
                    detail2.put("HKONT", "2241030100");//贷方总账科目1
                    detailArray.add(detail1);
                    detailArray.add(detail2);
                }
                if (kklx.equals("2")) {//选择商品修理
                    detail1.put("HKONT", "6601012520");//总账科目1
                    detail1.put("KOSTL","1000106");//深圳成本中心
                    detail1.put("WRBTR", WRBTR);//借款金额
                    detail2.put("HKONT", "2241030100");//贷方总账科目2
                    detailArray.add(detail1);
                    detailArray.add(detail2);
                }
                if (kklx.equals("3")) {//选择运费
                    detail1.put("HKONT", "6601012530");//总账科目1
                    detail1.put("KOSTL","1000106");//深圳成本中心
                    detail1.put("WRBTR", WRBTR);//借款金额
                    detail2.put("HKONT", "2241030100");//贷方总账科目3
                    detailArray.add(detail1);
                    detailArray.add(detail2);
                }
                if (kklx.equals("0")) {
                    if ("CNY".equals(WAERK)) {//如果货币是人民币，会生成两张借方凭证，一张贷方凭证
                        double zje = Double.parseDouble(WRBTR);//字符串转数字
                        double shui = zje/1.13*0.13;
                        double jine1 = (double)Math.round(shui*100)/100;
                        double jine2 = zje - jine1;
                        detail1.put("HKONT", "2221010602");//总账科目(税费)
                        detail1.put("WRBTR", jine1);//税额
                        detail1.put("MWSKZ","X2");
                        detail3.put("BUKRS", BUKRS);//公司代码
                        detail2.put("HKONT","1122010201");//总账科目(借方)
                        detail3.put("BUDAT", strDate);//凭证中的过账日期
                        detail3.put("BLDAT", strDate);//凭证中的凭证日期
                        detail3.put("BLART", "DG");//凭证类型
                        detail3.put("BKTXT", BKTXT);//凭证抬头文本
                        detail3.put("XBLNR", E_VBELN);//参考凭证编号
                        detail3.put("WAERS", WAERK);//货币
                        detail3.put("BSCHL", "40");//过账码
                        detail3.put("HKONT", "6001020000");//总账科目（应收）
                        detail3.put("WRBTR", jine2);//借款金额(应收)
                        detail3.put("SGTXT",BKTXT);//文本
                        detail3.put("MWSKZ","X2");//文本
                        detailArray.add(detail1);
                        detailArray.add(detail2);
                        detailArray.add(detail3);
                    } else {
                        detail1.put("WRBTR", WRBTR);//借款金额
                        detail1.put("HKONT", "6001020000");//总账科目1
                        detail2.put("HKONT", "1122010201");//总账科目1
                        detailArray.add(detail1);
                        detailArray.add(detail2);
                    }
                }
                voucher.put("IT_DATA", detailArray);
                String szData = voucher.toString();
                JSONObject reSZ = null;
                try {
                    reSZ = CommonUtil.Post(CommonUtil.voucherUrl,szData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String sz_e_code = reSZ.getString("E_CODE");
                requestInfo.getRequestManager().setMessage(reSZ.getString("E_MSG"));
                /**
                 * 获取扣款订单创建结果，开始创建凭证
                 */
                if ("S".equals(sz_e_code)) {
                    return SUCCESS;
                }
            }
        }
        /**
         *创建关联扣款订单和扣款凭证（即销售组织是深圳，工厂是深圳）
         */
        else if (WERKS.equals("1000")&&VKORG.equals("1010")){
            /**
             * 创建第一张客户扣香港的扣款单（即客户编码是客户，销售组织是1010）
             */
            vbakObject.put("VTWEG",VTWEG);//分销渠道
            vbakObject.put("VKORG",VKORG);//销售组织
            vbakObject.put("KUNAG",KUNAG);//客户编码
            jsonObj.put("IS_VBAK",vbakObject);//添加第1个
            String shuju = jsonObj.toString();
            JSONObject database = null;
            try {
                database = CommonUtil.Post(CommonUtil.deductioUrl,shuju);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String e_code = database.getString("E_CODE");
            requestInfo.getRequestManager().setMessage(database.getString("E_MSG"));
            /**
             * 获取扣款订单创建结果，开始创建凭证
             */
            if ("S".equals(e_code)) {
                //表示数据传输成功，正常提交
                System.out.println("成功");
                //获取扣款订单号
                String E_VBELN = database.getString("E_VBELN");
                JSONObject voucher = new JSONObject();//凭证数据结构
                JSONArray detailArray = new JSONArray();
                JSONObject detail1 = new JSONObject();//记账码1
                JSONObject detail2 = new JSONObject();//记账码2
                /**
                 * 创建深圳关联凭证（即客户扣香港扣款订单）
                 */
                detail1.put("BUKRS", BUKRS);//公司代码
                detail1.put("BUDAT", strDate);//凭证中的过账日期
                detail1.put("BLDAT", strDate);//凭证中的凭证日期
                detail1.put("BLART", "DG");//凭证类型
                detail1.put("BKTXT", BKTXT);//凭证抬头文本
                detail1.put("XBLNR", E_VBELN);//参考凭证编号
                detail1.put("WAERS", WAERK);//货币
                detail1.put("KUNNR", "");//客户代码
                detail1.put("WRBTR", WRBTR);//凭证金额
                detail1.put("LIFNR","1000");//供应商
                detail1.put("SGTXT",BKTXT);
                //凭证第二条数据(应收账款)
                detail2.put("BUKRS", BUKRS);//公司代码
                detail2.put("BUDAT", strDate);//凭证中的过账日期
                detail2.put("BLDAT", strDate);//凭证中的凭证日期
                detail2.put("BLART", "DG");//凭证类型
                detail2.put("BKTXT", BKTXT);//凭证抬头文本
                detail2.put("XBLNR", E_VBELN);//参考凭证编号
                detail2.put("WAERS", WAERK);//货币
                detail2.put("WRBTR", WRBTR);//凭证金额
                detail2.put("KUNNR", KUNAG);//客户代码
                detail2.put("SGTXT",BKTXT);
                if (kklx.equals("0")){
                    detail1.put("BSCHL", "21");//过账码
                    detail1.put("HKONT", "2202010000");
                    detail2.put("HKONT", "6401010100");
                    detail2.put("KOSTL","1010106");//6开头的添加成本中心
                    detail2.put("BSCHL", "50");//过账码
                }
                if (kklx.equals("1")||kklx.equals("2")||kklx.equals("3")) {//选择其他
                    detail1.put("HKONT", "1221010200");//借方总账科目
                    detail1.put("BSCHL", "24");//过账码
                    detail2.put("HKONT", "2241030200");//贷方总账科目
                    detail2.put("BSCHL", "11");//过账码
                }
                detailArray.add(detail1);
                detailArray.add(detail2);
                voucher.put("IT_DATA", detailArray);
                String pingzheng1 = voucher.toString();
                JSONObject reData = null;
                try {
                    reData = CommonUtil.Post(CommonUtil.voucherUrl,pingzheng1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String v_e_code = reData.getString("E_CODE");
                requestInfo.getRequestManager().setMessage(reData.getString("E_MSG"));
                if ("S".equals(v_e_code)){
                    //表示第一张凭证创建成功，可以开始生成第二张凭证
                    System.out.println("成功");
                    //return SUCCESS;
                    /**
                     * 创建第二张扣款凭证
                     */
                    vbakObject.put("VTWEG","30");//分销渠道
                    vbakObject.put("VKORG","1000");//销售组织
                    vbakObject.put("KUNAG","1010");//客户编码
                    jsonObj.put("IS_VBAK",vbakObject);//添加第二个
                    String shuju2 = jsonObj.toString();
                    JSONObject reData2 = null;
                    try {
                        reData2 = CommonUtil.Post(CommonUtil.deductioUrl,shuju2);//请求扣款订单接口
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String v_e_code2 = reData2.getString("E_CODE");
                    requestInfo.getRequestManager().setMessage(reData2.getString("E_MSG"));
                    if ("S".equals(v_e_code2)) {
                        //表示数据传输成功，正常提交
                        System.out.println("成功");
                        //获取扣款订单号
                        String E_VBELN2 = reData2.getString("E_VBELN");
                        JSONObject voucher2 = new JSONObject();//凭证数据结构
                        JSONArray detailArray2 = new JSONArray();
                        //凭证第一条数
                        JSONObject detail21 = new JSONObject();//记账码1
                        JSONObject detail22 = new JSONObject();//记账码2
                        /**
                         * 创建深圳关联凭证
                         */
                        detail21.put("BUKRS", "1000");//公司代码
                        detail21.put("BUDAT", strDate);//凭证中的过账日期
                        detail21.put("BLDAT", strDate);//凭证中的凭证日期
                        detail21.put("BLART", "DG");//凭证类型
                        detail21.put("BKTXT", BKTXT);//凭证抬头文本
                        detail21.put("XBLNR", E_VBELN2);//参考凭证编号
                        detail21.put("WAERS", WAERK);//货币
                        detail21.put("BSCHL", "40");//过账码
                        detail21.put("KUNNR", "");//客户代码
                        detail21.put("WRBTR", WRBTR);//凭证金额
                        //detail21.put("KOSTL","1000106");//客服组成本中心
                        detail21.put("SGTXT",BKTXT);
                        //凭证第二条数据(应收账款)
                        detail22.put("BUKRS", "1000");//公司代码
                        detail22.put("BUDAT", strDate);//凭证中的过账日期
                        detail22.put("BLDAT", strDate);//凭证中的凭证日期
                        detail22.put("BLART", "DG");//凭证类型
                        detail22.put("BKTXT", BKTXT);//凭证抬头文本
                        detail22.put("XBLNR", E_VBELN2);//参考凭证编号
                        detail22.put("WAERS", WAERK);//货币
                        detail22.put("BSCHL", "11");//过账码
                        detail22.put("KUNNR", "1010");//贷方客户代码
                        detail22.put("WRBTR", WRBTR);//凭证金额
                        detail22.put("SGTXT",BKTXT);
                        if (kklx.equals("0")) {//PCB
                            detail21.put("HKONT", "6001010000");//总账科目
                            detail22.put("HKONT", "1122010100");//贷方总账科目
                        }
                        if (kklx.equals("1")) {//选择PCBA
                            detail21.put("HKONT", "6601012510");//总账科目
                            detail21.put("KOSTL","1000106");//客服组成本中心
                            detail22.put("HKONT", "2241030600");//贷方总账科目
                        }
                        if (kklx.equals("2")) {//选择商品修理
                            detail21.put("HKONT", "6601012520");//总账科目1
                            detail21.put("KOSTL","1000106");//客服组成本中心
                            detail22.put("HKONT", "2241030600");//贷方总账科目2
                        }
                        if (kklx.equals("3")) {//选择运费
                            detail21.put("HKONT", "6601012530");//总账科目1
                            detail21.put("KOSTL","1000106");//客服组成本中心
                            detail22.put("HKONT", "2241030600");//贷方总账科目3
                        }
                        detailArray2.add(detail21);//添加借方数据
                        detailArray2.add(detail22);//添加贷方数据
                        voucher2.put("IT_DATA", detailArray2);
                        String shuju3 = voucher2.toString();
                        try {
                            JSONObject reData3 = CommonUtil.Post(CommonUtil.voucherUrl,shuju3);//请求扣款订单接口
                            String v_e_code3 = reData3.getString("E_CODE");
                            requestInfo.getRequestManager().setMessage(reData3.getString("E_MSG"));
                            if (v_e_code3.equals("S")){
                                return SUCCESS;
                            }else {
                                return FAILURE_AND_CONTINUE;
                            }
                        } catch (IOException e) {
                            printLog(requestInfo, "扣款",e.getMessage());
                        }

                    }
                }
            }
        }
        else{
            writeLog("费用报销流程的接口回传信息," +
                    "创建人【"+requestInfo.getCreatorid()+"】" +
                    "流程id【"+requestInfo.getWorkflowid()+"】" +
                    "流程请求id【"+requestInfo.getRequestid()+"】" +
                    "当前节点【"+requestInfo.getRequestManager().getNodeid()+"】" +
                    "请求标题【"+requestInfo.getRequestManager().getRequestname()+"】"+
                    "请求失败");

            requestInfo.getRequestManager().setMessageid("99999");
            requestInfo.getRequestManager().setMessagecontent("请求失败");
            return "333";
        }
        return "失败";
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
    /**输出打印信息*/
    private void printLog(RequestInfo requestInfo, String msg, String returnMsg){
        writeLog(msg +
                "创建人【"+requestInfo.getCreatorid()+"】" +
                "流程id【"+requestInfo.getWorkflowid()+"】" +
                "流程请求id【"+requestInfo.getRequestid()+"】" +
                "当前节点【"+requestInfo.getRequestManager().getNodeid()+"】" +
                "请求标题【"+requestInfo.getRequestManager().getRequestname()+"】"+
                "返回信息或者请求信息【"+ returnMsg +"】");
    }
}




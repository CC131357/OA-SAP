package weaver.interfaces.workflow.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import com.weaver.general.Util;
import weaver.soa.workflow.request.*;

import java.util.HashMap;
import java.util.Map;

public class TravelExpenseAction extends BaseBean implements Action  {

    final String MESSAGEID = "99999";
    final String FAILURECODE = "333";
    CommonUtil util = new CommonUtil();

    @Override
    public String execute(RequestInfo requestInfo) {
        return PostMsg(getPostJsonStr(requestInfo),requestInfo);
    }

    /**获得需要推送的字符串*/
    private String getPostJsonStr(RequestInfo requestInfo){
        //获取明细表信息
        DetailTable[] detailTable = requestInfo.getDetailTableInfo().getDetailTable();
        JSONObject jsonObj =new JSONObject();
        JSONArray detailArray = new JSONArray();
        for(int i = 0; i < detailTable.length - 1; i ++){
            DetailTable dt = detailTable[i];// 指定明细表
            Row[] s = dt.getRow();// 当前明细表的所有数据,按行存储
            for (int j = 0; j < s.length; j++) {
                Map columnMap = new HashMap<>();
                columnMap.put("bb","WAERS");//币别
                columnMap.put("cbzxin","KOSTL");//成本中心
                columnMap.put("hjkm","HKONT");//会计科目
                columnMap.put("jeyb","WRBTR");//金额原币
                columnMap.put("se","MWSBP");//税额
                columnMap.put("fysm","BKTXT");//费用说明
                columnMap.put("nbddxm","AUFNR");//内部订单项目

                Map<String, String> mainTableInfo = util.getMainTableMap(requestInfo);
                JSONObject detailtObject = util.setJsonObject(s[j], columnMap);

                detailtObject.put("BUKRS",Util.null2String(mainTableInfo.get("gongs")));//公司
                detailtObject.put("ZOADJ",Util.null2String(mainTableInfo.get("liucbh")));//OA单据编号
                detailtObject.put("ZDJLX",Util.null2String(mainTableInfo.get("djlx")));//票据类型

                detailtObject.put("LIFNR",Util.null2String(mainTableInfo.get("gyshzqrzh")));//供应商或者债权人的账号
                detailtObject.put("BVTYP",Util.null2String(mainTableInfo.get("zhxz")));//账户选择，收款人账号
                detailtObject.put("ZLSCH",Util.null2String(mainTableInfo.get("fukfs")));//付款方式，付款方式

                if(i == detailTable.length - 2 && j == s.length - 1){
                    detailtObject.put("ZJZKK",getLoanMoney(detailTable) + "");//借支金额，费用报销
                } else{
                    detailtObject.put("ZJZKK","0");//借支金额
                }
                detailArray.add(detailtObject);
            }
        }
        jsonObj.put("IT_DATA",detailArray);
        return jsonObj.toString();
    }

    /**向SAP推送消息*/
    private String PostMsg(String msg,RequestInfo requestInfo){
        try{
            JSONObject database = util.Post(CommonUtil.travelExpenseUrl,msg,CommonUtil.authorization);
            String e_code = database.getString("E_CODE");
            String e_msg = database.getString("E_MSG");
            if ("S".equals(e_code)){
                //表示数据传输成功，正常提交
                util.printLog(requestInfo, msg, "成功 !");
                return SUCCESS;
            }else if("E".equals(e_code)){
                //数据传输失败，则将错误信息返回到页面
                util.printLog(requestInfo, msg, database.getJSONArray("ET_DATA").getJSONObject(0).toJSONString());
                requestInfo.getRequestManager().setMessageid(MESSAGEID);
                requestInfo.getRequestManager().setMessagecontent(e_msg.toString() + msg);
                return FAILURECODE;
            } else{
                util.printLog(requestInfo, msg, "调用SAP接口失败，返回错误数据1！");
                requestInfo.getRequestManager().setMessageid(MESSAGEID);
                requestInfo.getRequestManager().setMessagecontent("调用SAP接口失败，返回错误数据1！" + msg);
                return FAILURECODE;
            }
        } catch (Exception e){
            util.printLog(requestInfo, msg, "调用SAP接口失败，返回错误数据2！" + e.getMessage());
            requestInfo.getRequestManager().setMessageid(MESSAGEID);
            requestInfo.getRequestManager().setMessagecontent("调用SAP接口失败，返回错误数据2！" + msg);
            return FAILURECODE;
        }

    }

    /**获得冲销金额*/
    private int getLoanMoney(DetailTable[] detailTable){
        int loan = 0;
        DetailTable loanDt = detailTable[detailTable.length - 1];
        Row[] sDt = loanDt.getRow();// 当前明细表的所有数据,按行存储
        for (int j = 0; j < sDt.length; j++) {
            Row r = sDt[j];// 指定行
            Cell c[] = r.getCell();// 每行数据再按列存储
            for (int k = 0; k < c.length; k++) {
                Cell c1 = c[k];// 指定列
                String name = c1.getName();// 明细字段名称
                String value = c1.getValue();// 明细字段的值
                if(name.equals("cxjeyb") && value != null && !value.equals("")){//冲销金额原币
                    loan += Float.parseFloat(value);
                }
            }
        }
        return loan;
    }


}

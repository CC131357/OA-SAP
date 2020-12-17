package weaver.interfaces.workflow.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import com.weaver.general.Util;
import weaver.soa.workflow.request.*;

import java.util.Map;

public class TravelExpenseAction extends BaseBean implements Action  {

    @Override
    public String execute(RequestInfo requestInfo) {
        final String MESSAGEID = "99999";
        final String FAILURECODE = "333";

        JSONObject jsonObj =new JSONObject();
        //获取主表信息、初始化主表
        Map<String, String> mid=CommonUtil.getPropertyMap(requestInfo.getMainTableInfo().getProperty());
        String BUKRS= Util.null2String(mid.get("gongs"));  //公司
        String ZOADJ=Util.null2String(mid.get("liucbh")); //流程编号
        String ZLSCH=Util.null2String(mid.get("fukfs")); //报销方式，付款方式

        String LIFNR = Util.null2String(mid.get("gyshzqrzh")); //收款人开户行，供应商及债权人的账号
        String BVTYP = Util.null2String(mid.get("zhxz")); //收款人账号，账户选择
        String ZDJLX = Util.null2String(mid.get("djlx")); //单据类型，票据类型

        //获取明细表信息
        DetailTable[] detailtable = requestInfo.getDetailTableInfo().getDetailTable();
        //明细4费用报销
        float loan = 0;
        DetailTable loanDt = detailtable[detailtable.length - 1];
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
        //其他明细
        JSONArray detailArray = new JSONArray();
        for(int i = 0; i < detailtable.length - 1; i ++){
            DetailTable dt = detailtable[i];// 指定明细表
            Row[] s = dt.getRow();// 当前明细表的所有数据,按行存储
            for (int j = 0; j < s.length; j++) {
                Row r = s[j];// 指定行
                Cell c[] = r.getCell();// 每行数据再按列存储
                JSONObject detailtObject = new  JSONObject();
                for (int k = 0; k < c.length; k++) {
                    Cell c1 = c[k];// 指定列
                    String name = c1.getName();// 明细字段名称
                    String value = c1.getValue();// 明细字段的值
                    if (name.equals("bb")){
                        detailtObject.put("WAERS",value); //币别-货币码
                    } else if (name.equals("cbzxin")){
                        detailtObject.put("KOSTL",value); //成本中心
                    } else if (name.equals("hjkm")){
                        detailtObject.put("HKONT",value); //会计科目，总帐科目
                    } else if (name.equals("jeyb")){    //原币
                        detailtObject.put("WRBTR",value);//费用金额，凭证货币金额
                    } else if (name.equals("se")){    //税额
                        detailtObject.put("MWSBP",value);//税额
                    } else if (name.equals("fysm")){
                        detailtObject.put("BKTXT",value);  //费用说明-凭证抬头文本
                    } else if (name.equals("nbddxm")){
                        detailtObject.put("AUFNR",value);  //内部订单
                    }
                }
                detailtObject.put("BUKRS",BUKRS);
                detailtObject.put("ZOADJ",ZOADJ);//OA单据编号
                detailtObject.put("ZDJLX",ZDJLX);//票据类型

                detailtObject.put("LIFNR",LIFNR);//供应商或者债权人的账号
                detailtObject.put("BVTYP",BVTYP);//账户选择

                detailtObject.put("ZLSCH",ZLSCH);//付款方式

                if(i == detailtable.length - 2){
                    detailtObject.put("ZJZKK",loan + "");//借支金额
                } else{
                    detailtObject.put("ZJZKK","0");//借支金额
                }
                detailArray.add(detailtObject);
            }
        }
        jsonObj.put("IT_DATA",detailArray);
        String msg = jsonObj.toString();
        System.out.println(msg);
        try{
            JSONObject database = CommonUtil.Post(CommonUtil.travelExpenseUrl,msg);
            String e_code = database.getString("E_CODE");
            String e_msg = database.getString("E_MSG");
            if ("S".equals(e_code)){
                //表示数据传输成功，正常提交
                System.out.println("成功 !" + msg);
                return SUCCESS;
            }else if("E".equals(e_code)){
                //数据传输失败，则将错误信息返回到页面
                printLog(requestInfo, msg, database.getJSONArray("ET_DATA").getJSONObject(0).toJSONString());
                requestInfo.getRequestManager().setMessageid(MESSAGEID);
                requestInfo.getRequestManager().setMessagecontent(e_msg.toString() + msg);
                return FAILURECODE;
            } else{
                requestInfo.getRequestManager().setMessageid(MESSAGEID);
                requestInfo.getRequestManager().setMessagecontent("调用SAP接口失败，返回错误数据1！" + msg);
                return FAILURECODE;
            }
        } catch (Exception e){
            e.printStackTrace();
            requestInfo.getRequestManager().setMessageid(MESSAGEID);
            requestInfo.getRequestManager().setMessagecontent("调用SAP接口失败，返回错误数据2！" + msg);
            return FAILURECODE;
        }
    }


    /**输出打印信息*/
    private void printLog(RequestInfo requestInfo, String msg, String returnMsg){
        writeLog(msg +
                "创建人【"+requestInfo.getCreatorid()+"】" +
                "流程id【"+requestInfo.getWorkflowid()+"】" +
                "流程请求id【"+requestInfo.getRequestid()+"】" +
                "当前节点【"+requestInfo.getRequestManager().getNodeid()+"】" +
                "请求标题【"+requestInfo.getRequestManager().getRequestname()+"】"+
                "返回信息【"+ returnMsg +"】");
    }
}

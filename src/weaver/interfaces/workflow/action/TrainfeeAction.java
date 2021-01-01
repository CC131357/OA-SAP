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

public class TrainfeeAction extends BaseBean implements Action {

    /**
     * @作者：高梦利
     * @流程名称 员工培训费用报销
     */
    @Override
    public String execute(RequestInfo requestInfo) {

        JSONObject jsonObject = new JSONObject();
        String requestid = requestInfo.getRequestid();

        //获取主表数据
        Map<String,String> mid = getPropertyMap(requestInfo.getMainTableInfo().getProperty());
        String BUKRS = Util.null2String(mid.get("gsdm"));  //公司代码
        String ZOADJ = Util.null2String(mid.get("liucbh")); //流程编号
        String LIFNR = Util.null2String(mid.get("ygyxbh")); //供应商或债权人账户-员工银行编号
        String ZDJLX = Util.null2String(mid.get("djlx")); //单据类型--报销
        String ZLSCH = Util.null2String(mid.get("fkfs"));//付款方式
        String BVTYP = Util.null2String(mid.get("zhxz")); //账户选择类型
        String HKONT = Util.null2String(mid.get("zzkm")); //总账科目—会计科目
        String WRBTR = Util.null2String(mid.get("hjjermb")); //报销合计金额（人民币）

        //获取明细表信息
        DetailTable[] detailtable = requestInfo.getDetailTableInfo().getDetailTable();// 获取所有明细表
        JSONArray detailArray = new JSONArray();
        JSONObject detailtObject = new JSONObject();
        for(int i = 0; i < detailtable.length; i ++){
            DetailTable dt = detailtable[i];// 指定明细表
            Row[] s = dt.getRow();// 当前明细表的所有数据,按行存储
            if (s.length>0) {
                Row r = s[0];// 指定第一行
                Cell c[] = r.getCell();// 第一行数据再按列存储

                for (int k = 0; k < c.length; k++) {
                    Cell c1 = c[k];// 指定列
                    String name = c1.getName();// 明细字段名称
                    String value = c1.getValue();// 明细字段的值
                    if (name.equals("bb") && value.length()>0 ) {
                        detailtObject.put("WAERS", value); //币别-货币码
                        break;
                    }
                }
                detailArray.add(detailtObject);
                detailtObject.put("BUKRS", BUKRS);//公司代码
                detailtObject.put("ZOADJ", ZOADJ);//OA单据
                detailtObject.put("LIFNR", LIFNR);//供应商或债权人账号
                detailtObject.put("ZDJLX", ZDJLX);//单据类型
                detailtObject.put("BVTYP", BVTYP);//账户选择
                detailtObject.put("ZLSCH", ZLSCH);//付款方式
                detailtObject.put("HKONT", HKONT);//总账科目—会计科目

                detailtObject.put("WRBTR", WRBTR);//报销金额合计
            }
        }
        jsonObject.put("IT_DATA",detailArray);
        String shuju = jsonObject.toString();

        //调取接口
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), shuju);
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic WlBPVVNFUjoxcWF6QFdTWA==")
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
        String e_code = database.getString("E_CODE");
        String e_msg = database.getString("E_MSG");

        if ("S".equals(e_code)){
            //表示数据传输成功，正常提交
            System.out.println("成功");
            return SUCCESS;
        }else{
            //数据传输失败，则将错误信息返回到页面
            JSONArray et_data = database.getJSONArray("ET_DATA");
            requestInfo.getRequestManager().setMessageid("99999");
            requestInfo.getRequestManager().setMessagecontent("执行节点附件操作失败！"+e_msg.toString());
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

package weaver.interfaces.workflow.action;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import com.weaver.general.Util;
import weaver.soa.workflow.request.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gongchen
 * @流程名称 供应商主数据维护
 */
public class SupplierCreateAction extends BaseBean implements Action {
    @Override
    public String execute(RequestInfo requestInfo) {
        JSONObject jsonObj =new JSONObject();
        String requestId = requestInfo.getRequestid();
        //获取主表信息、初始化主表
        Map<String, String> mid=getPropertyMap(requestInfo.getMainTableInfo().getProperty());
        String GYSBM = Util.null2String(mid.get("gysbm"));  //供应商编码
        String BU_GROUP = Util.null2String(mid.get("fz"));  //业务伙伴分组
        String NAME_ORG1 = Util.null2String(mid.get("gongsqc"));//组织名称
        String SORT1_TXT = Util.null2String(mid.get("jianc"));//简称
        String STREET = Util.null2String(mid.get("gongsdz"));//公司地址
        String ZFPDZ = Util.null2String(mid.get("fapdz"));  //发票地址
        String ZFRDB = Util.null2String(mid.get("fardb"));//法人代表
        String FOUND_DAT = Util.null2String(mid.get("gongsclsj"));  //组织成立日期
        String ZZCZB = Util.null2String(mid.get("zhuczb"));//注册资本
        String COUNTRY = Util.null2String(mid.get("gj"));//国家/地区代码
        String ysfs = Util.null2String(mid.get("ysfs"));  //运输方式
        String ZSHFS = null;
        if ("0".equals(ysfs)){
            ZSHFS = "汽运";
        }
        if ("1".equals(ysfs)){
            ZSHFS = "空运";
        }
        if ("2".equals(ysfs)){
            ZSHFS = "海运";
        }
        String ZCGNR = Util.null2String(mid.get("caignr"));//采购内容
        String ZSYGX = Util.null2String(mid.get("shiygx"));//使用工序
        String ZTLVN = Util.null2String(mid.get("tonglgys"));//同类供应商
        String bazklx = Util.null2String(mid.get("bazklx"));//备案状况类型
        String ZBALX = null;
        if ("0".equals(bazklx)){
            ZBALX = "新备案";
        }
        if ("1".equals(bazklx)){
            ZBALX = "价格更新备案";
        }
        if ("2".equals(bazklx)){
            ZBALX = "合同更新备案";
        }

        String ywhzlx = Util.null2String(mid.get("ywhzlx"));//业务合作类别
        String ZYWLB = null;
        if ("0".equals(ywhzlx)){
            ZYWLB = "外发加工";
        }
        if ("1".equals(ywhzlx)){
            ZYWLB = "原材料";
        }
        if ("2".equals(ywhzlx)){
            ZYWLB = "设备或备件";
        }
        if ("3".equals(ywhzlx)){
            ZYWLB = "维修或外包";
        }
        if ("4".equals(ywhzlx)){
            ZYWLB = "工程承包商";
        }
        String ZYWLX = Util.null2String(mid.get("ywlxr"));//业务联系人
        String ZYWDH = Util.null2String(mid.get("lxdh"));//业务人联系电话
        String ZYWSJ = Util.null2String(mid.get("yddh"));  //业务联系人手机号
        String ZYWYX = Util.null2String(mid.get("yxdz"));//业务联系人邮箱

        String ZGDLX = Util.null2String(mid.get("gdlxr"));//跟单联系人
        String ZGDDH = Util.null2String(mid.get("gdlxdh"));  //跟单联系人电话
        String ZGDSJ = Util.null2String(mid.get("gdyddh"));//跟单联系人手机号
        String ZGDYX = Util.null2String(mid.get("gdyxdz"));//跟单联系人邮箱
        String EKORG = Util.null2String(mid.get("cgzz"));//采购组织
        String faz = Util.null2String(mid.get("faz"));//计算方案组（供应商）
        String KALSK = null;
        if ("0".equals(faz)){
            KALSK = "";
        }
        if ("1".equals(faz)){
            KALSK = "01";
        }
        if ("2".equals(faz)){
            KALSK = "02";
        }
        String BUKRS = Util.null2String(mid.get("gsdm"));  //公司代码
        String WAERS = Util.null2String(mid.get("ddhb"));//货币码
        String ZWELS = Util.null2String(mid.get("fkfs"));//付款方式
        String ZTERM = Util.null2String(mid.get("fktj"));  //付款条件
        String ZDZSJ = Util.null2String(mid.get("dzsj"));//对账时间
        String TAXNUM = Util.null2String(mid.get("sh"));//税号
        String BANKS = Util.null2String(mid.get("yxgj"));//银行国家代码
        String BANKL = Util.null2String(mid.get("yxdm"));//银行代码
        String BANKA = Util.null2String(mid.get("yxzhm"));  //银行名称
        String BANKN = Util.null2String(mid.get("yxzh"));//银行帐户号码

        DetailTable[] detailtable = requestInfo.getDetailTableInfo().getDetailTable();
        DetailTable dt = detailtable[0];// 指定明细表1
        Row[] s = dt.getRow();// 当前明细表的所有数据,按行存储
        JSONArray lzs1Array = new JSONArray();
        for (int j = 0; j < s.length; j++) {
            Row r = s[j];// 指定行
            Cell c[] = r.getCell();// 每行数据再按列存储
            JSONObject lzs1Object =new JSONObject();
            for (int k = 0; k < c.length; k++) {
                Cell c1 = c[k];// 指定列
                String name = c1.getName();// 明细字段名称
                String value = c1.getValue();// 明细字段的值
                if (name.equals("yxjzrq")){//有效截止日期
                    lzs1Object.put("ZJZRQ",value);//会计科目
                }
                if (name.equals("zsbh")) {//证书编号
                    lzs1Object.put("ZZSLX", value);//会计科目
                }
            }
            lzs1Array.add(lzs1Object);
        }
        JSONObject lfa1Object =new JSONObject();
        lfa1Object.put("ACTVT","01");//操作方式为创建
        lfa1Object.put("BU_GROUP",BU_GROUP);//分组
        lfa1Object.put("NAME_ORG1",NAME_ORG1);//公司全称
        lfa1Object.put("SORT1_TXT",SORT1_TXT);//公司简称
        lfa1Object.put("COUNTRY",COUNTRY);//国家
        lfa1Object.put("STREET",STREET);//公司地址
        lfa1Object.put("FOUND_DAT",FOUND_DAT);//公司成立时间
        lfa1Object.put("ZFRDB",ZFRDB);//法人代表
        lfa1Object.put("ZZCZB",ZZCZB);//注册资本
        lfa1Object.put("ZFPDZ",ZFPDZ);//发票地址
        lfa1Object.put("ZCGNR",ZCGNR);//采购内容
        lfa1Object.put("ZSYGX",ZSYGX);//使用工序
        lfa1Object.put("ZTLVN",ZTLVN);//同类供应商
        lfa1Object.put("ZDZSJ",ZDZSJ);//对账时间
        lfa1Object.put("ZSHFS",ZSHFS);//运输方式
        lfa1Object.put("ZBALX",ZBALX);//备案状况类型
        lfa1Object.put("ZYWLB",ZYWLB);//业务合作类型
        lfa1Object.put("ZYWLX",ZYWLX);//业务联系人
        lfa1Object.put("ZYWDH",ZYWDH);//业务联系人电话
        lfa1Object.put("ZYWSJ",ZYWSJ);//业务联系人手机号
        lfa1Object.put("ZYWYX",ZYWYX);//业务联系人邮箱
        lfa1Object.put("ZGDLX",ZGDLX);//跟单联系人
        lfa1Object.put("ZGDDH",ZGDDH);//跟单联系人电话
        lfa1Object.put("ZGDSJ",ZGDSJ);//跟单联系人手机号
        lfa1Object.put("ZGDYX",ZGDYX);//跟单联系人邮箱
        lfa1Object.put("TAXTYPE","CN0");//税号类别
        lfa1Object.put("TAXNUM",TAXNUM);//税号
        lfa1Object.put("BANKS",BANKS);//银行国家代码
        lfa1Object.put("BANKL",BANKL);//银行代码
        lfa1Object.put("BANKA",BANKA);//银行账户名
        lfa1Object.put("BANKN",BANKN);//银行账号
        JSONArray lfa1Array = new JSONArray();
        lfa1Array.add(lfa1Object);
        jsonObj.put("IS_LFA1",lfa1Array);//供应商通用接口

        jsonObj.put("IT_LZS1",lzs1Array);//供应商资质证书

        JSONObject lfb1Object =new JSONObject();
        lfb1Object.put("BUKRS",BUKRS);//公司代码
        lfb1Object.put("AKONT","2202020000");//总帐中的统驭科目
        lfb1Object.put("ZWELS",ZWELS);//付款方式清单
        lfb1Object.put("ZTERM",ZTERM);//付款条件
        lfb1Object.put("REPRF", "X");//检验标志
        JSONArray lfb1Array = new JSONArray();
        lfb1Array.add(lfb1Object);
        jsonObj.put("IT_LFB1",lfb1Array);//供应商公司代码数据

        JSONObject lfm1Object = new JSONObject();
        lfm1Object.put("EKORG",EKORG);//采购组织
        lfm1Object.put("WAERS",WAERS);//货币码
        lfm1Object.put("ZTERM",ZTERM);//收付条件
        lfm1Object.put("WEBRE","X");//基于收获的发票校验
        lfm1Object.put("KALSK",KALSK);//方案组
        JSONArray lfm1Array = new JSONArray();
        lfm1Array.add(lfm1Object);
        jsonObj.put("IT_LFM1",lfm1Array);//供应商采购组织数据

        String shuju = jsonObj.toString();
        JSONObject result = null;
        try {
            result = CommonUtil.Post(CommonUtil.supplierMaterialUrl,shuju);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String e_code = result.getString("E_CODE");

        if ("S".equals(e_code)){
            //表示数据传输成功，正常提交
            System.out.println("成功");
            String E_PARTNER = result.getString("E_PARTNER");//供应商编码
            // 定义MySQL的数据库驱动程序
            final String DBDRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver" ;
            // 定义MySQL数据库的连接地址
            final String DBURL = "jdbc:sqlserver://192.168.0.30:1433;DatabaseName=ecology" ;
            // MySQL数据库的连接用户名
            final String DBUSER = "bin" ;
            // MySQL数据库的连接密码
            final String DBPASS = "wdhbb2016" ;
            // 所有的异常抛出
            try {
                Connection conn = null ;     // 数据库连接
                Statement stmt = null ;      // 数据库操作
                Class.forName(DBDRIVER) ;    // 加载驱动程序
                String sql = "UPDATE formtable_main_242 SET gysbm='"+E_PARTNER+"' where requestId='"+requestId+"'";
                conn = DriverManager.getConnection(DBURL,DBUSER,DBPASS) ;
                stmt = conn.createStatement() ;    // 实例化Statement对象
                stmt.executeUpdate(sql) ;        // 执行数据库更新操作
                stmt.close() ;                    // 关闭操作
                conn.close() ;            // 数据库关闭
            } catch (Exception e) {
                e.printStackTrace();
            }
            return SUCCESS;
        }
        if ("E".equals(e_code)){
            JSONObject RETURN = result.getJSONObject("RETURN");
            requestInfo.getRequestManager().setMessage(RETURN.getString("MESSAGE"));
            return FAILURE_AND_CONTINUE;
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

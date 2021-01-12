package weaver.interfaces.workflow.action;


import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import com.weaver.general.Util;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;

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
        String BU_GROUP = Util.null2String(mid.get("fz"));  //业务伙伴分组
        String NAME_ORG1 = Util.null2String(mid.get("gsqc"));//组织名称
        String ZFPDZ = Util.null2String(mid.get("fpdz"));  //发票地址
        String ZFRDB = Util.null2String(mid.get("frdb"));//法人代表
        String FOUND_DAT = Util.null2String(mid.get("gsclsj"));  //组织成立日期
        String ZZCZB = Util.null2String(mid.get("zczb"));//注册资本
        String COUNTRY = Util.null2String(mid.get("gj"));//国家/地区代码
        String ZSHFS = Util.null2String(mid.get("ysfs"));  //运输方式
        String ZCGNR = Util.null2String(mid.get("cgnr"));//采购内容
        String ZSYGX = Util.null2String(mid.get("sygx"));//使用工序
        String ZTLVN = Util.null2String(mid.get("tlgys"));//同类供应商
        String ZBALX = Util.null2String(mid.get("bazklx"));//备案状况类型
        String ZYWLB = Util.null2String(mid.get("ywhzlx"));//业务合作类别

        String ZYWLX = Util.null2String(mid.get("ywlxr"));//业务联系人
        String ZYWDH = Util.null2String(mid.get("ywrlxdh"));//业务人联系电话
        String ZYWSJ = Util.null2String(mid.get("ywryddh"));  //业务联系人手机号
        String ZYWYX = Util.null2String(mid.get("yxdz"));//业务联系人邮箱

        String ZGDLX = Util.null2String(mid.get("gdlxr"));//跟单联系人
        String ZGDDH = Util.null2String(mid.get("gdrlxdh"));  //跟单联系人电话
        String ZGDSJ = Util.null2String(mid.get("gdryddh"));//跟单联系人手机号
        String ZGDYX = Util.null2String(mid.get("gdryxdz"));//跟单联系人邮箱
        String EKORG = Util.null2String(mid.get("cgzz"));//采购组织
        String KALSK = Util.null2String(mid.get("faz"));//计算方案组（供应商）
        String BUKRS = Util.null2String(mid.get("gsdm"));  //公司代码
        String WAERS = Util.null2String(mid.get("ddhb"));//货币码


        //String ZGDLX = Util.null2String(mid.get("fkfs"));//付款方式
        String ZTERM = Util.null2String(mid.get("fktj"));  //付款条件
        String ZDZSJ = Util.null2String(mid.get("dzsj"));//对账时间
        String TAXNUM = Util.null2String(mid.get("sh"));//税号
        String BANKS = Util.null2String(mid.get("yhgj"));//银行国家代码
        String BANKL = Util.null2String(mid.get("yhdm"));//银行代码
        String BANKA = Util.null2String(mid.get("yhmc"));  //银行名称
        String BANKN = Util.null2String(mid.get("yhzh"));//银行帐户号码

        return "f" ;

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

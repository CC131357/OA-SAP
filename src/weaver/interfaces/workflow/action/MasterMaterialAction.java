package weaver.interfaces.workflow.action;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import com.weaver.general.Util;
import okhttp3.*;
import weaver.soa.workflow.request.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;


/**
 * @author gongchen
 * 物料主数据创建
 */

public class MasterMaterialAction extends BaseBean implements Action {
    @Override
    public String execute(RequestInfo requestInfo) {
        JSONObject jsonObj =new JSONObject();
        String requestId = requestInfo.getRequestid();
        //获取主表信息、初始化主表
        Map<String, String> mid=getPropertyMap(requestInfo.getMainTableInfo().getProperty());
       //String MATNR=Util.null2String(mid.get("wlbm")); //物料编码
        String MTART=Util.null2String(mid.get("wllxdl")); //物料类型大类
        String MATKL = Util.null2String(mid.get("wlzxl")); //物料组小类
        //String WAERS = Util.null2String(mid.get("wlzms")); //物料组描述
        String MAKTX = Util.null2String(mid.get("wlms"));//物料描述
        String MEINS = Util.null2String(mid.get("jbjldw")); //基本计量单位
        String ZWLCM = Util.null2String(mid.get("wlmscwb"));//物料长描述
        String ZYTSS = Util.null2String(mid.get("yt"));//  新增用途
        String SPART = Util.null2String(mid.get("cpz")); //产品组
        String EXTWG = Util.null2String(mid.get("wbwlz")); //外部物料组
        //String LIFNR = Util.null2String(mid.get("wllxms")); //物料类型描述
        String ZGGXH = Util.null2String(mid.get("ggxh")); //规格型号
        String ZBCXH = Util.null2String(mid.get("bcxh")); //板材型号
        String ZCAND = Util.null2String(mid.get("cd"));//长度
        String ZKUAN = Util.null2String(mid.get("kd"));//宽度
        String ZGAOD = Util.null2String(mid.get("gd"));//高度
        String ZCDDW = Util.null2String(mid.get("cddw"));//长度单位
        String ZKDDW = Util.null2String(mid.get("kddw"));//宽度单位
        String ZGDDW = Util.null2String(mid.get("gddw"));//高度单位
        String ZBZGG = Util.null2String(mid.get("bzgg"));//包装规格
        String ZSHHD = Util.null2String(mid.get("sfht"));//是否含铜(选择)
        String ZBANH = Util.null2String(mid.get("bh"));//板厚
        String ZBHGC = Util.null2String(mid.get("bhgc"));//板厚公差
        String ZTONH = Util.null2String(mid.get("sth"));//上铜厚
        String ZXTON = Util.null2String(mid.get("xthoz"));//下铜厚
        String ZTBLE = Util.null2String(mid.get("tblx"));//上铜箔类型
        String ZXTBL = Util.null2String(mid.get("xtblx"));//下铜箔类型
        String ZBLBZ = Util.null2String(mid.get("blbzh"));//玻璃布组合
        String ZYANS = Util.null2String(mid.get("ys"));//颜色
        String ZBLDJ = Util.null2String(mid.get("bldj"));//板料等级

        String ZWXCC = Util.null2String(mid.get("wxcc"));//纬向尺寸
        String ZTGSS = Util.null2String(mid.get("tg"));//TG
        String ZDKSS = Util.null2String(mid.get("dk"));//DK
        String ZDFSS = Util.null2String(mid.get("df"));//DF
        String ZCTIS = Util.null2String(mid.get("cti"));//CTI
        String ZSZHL = Util.null2String(mid.get("szhl"));//树脂含量
        String ZZJSS = Util.null2String(mid.get("zj"));//直径
        String ZRCSS = Util.null2String(mid.get("rc"));//刃长
        String ZBJSS = Util.null2String(mid.get("bj"));//柄径
        String ZZCSS = Util.null2String(mid.get("zc"));//总长
        String ZJDSS = Util.null2String(mid.get("jd"));//角度
        String ZWJSS = Util.null2String(mid.get("wj"));//外径
        String ZNJSS = Util.null2String(mid.get("nj"));//内径
        String ZCISS = Util.null2String(mid.get("cs"));//齿数
        String ZNDSS = Util.null2String(mid.get("nd"));//浓度
        String ZHDSS = Util.null2String(mid.get("hd"));//厚度
        String ZTIJS = Util.null2String(mid.get("tj"));//体积
        String sfzyl = Util.null2String(mid.get("sfzyl"));//是否专用料，是传X，否传空
        String ZZYWL=null;
        if ("0".equals(sfzyl)){
            ZZYWL = "";
        }else if ("1".equals(sfzyl)){
            ZZYWL = "X";
        }
        String ZDUDW = Util.null2String(mid.get("hddw"));//厚度单位
        String ZDF10 = Util.null2String(mid.get("dfz10ghzpl"));//DF值(10G赫兹频率)
        String ZDF15 = Util.null2String(mid.get("dfz15ghzpl"));//DF值(15G赫兹频率)
        String ZDF20 = Util.null2String(mid.get("dfz20ghzpl"));//DF值(20G赫兹频率)
        String ZDF02 = Util.null2String(mid.get("dfz2ghzpl"));//DF值(2G赫兹频率)
        String ZDF03 = Util.null2String(mid.get("dfz3ghzpl"));//DF值(3G赫兹频率)
        String ZDF04 = Util.null2String(mid.get("dfz4ghzpl"));//DF值(4G赫兹频率)
        String ZDF05 = Util.null2String(mid.get("dfz5ghzpl"));//DF值(5G赫兹频率)
        String ZDF06 = Util.null2String(mid.get("dfz6ghzpl"));//DF值(6G赫兹频率)
        String ZDF07 = Util.null2String(mid.get("dfz7ghzpl"));//DF值(7G赫兹频率)
        String ZDF08 = Util.null2String(mid.get("dfz8ghzpl"));//DF值(8G赫兹频率)
        String ZDF09 = Util.null2String(mid.get("dfz9ghzpl"));//DF值(9G赫兹频率)
        String ZDK10 = Util.null2String(mid.get("dkz10ghzpl"));//DK值(10G赫兹频率)
        String ZDK15 = Util.null2String(mid.get("dkz15ghzpl"));//DK值(15G赫兹频率)
        String ZDK20 = Util.null2String(mid.get("dkz20ghzpl"));//DK值(20G赫兹频率)
        String ZDK02 = Util.null2String(mid.get("dkz2ghzpl"));//DK值(2G赫兹频率)
        String ZDK03 = Util.null2String(mid.get("dkz3ghzpl"));//DK值(3G赫兹频率)
        String ZDK04 = Util.null2String(mid.get("dkz4ghzpl"));//DK值(4G赫兹频率)
        String ZDK05 = Util.null2String(mid.get("dkz5ghzpl"));//DK值(5G赫兹频率)
        String ZDK06 = Util.null2String(mid.get("dkz6ghzpl"));//DK值(6G赫兹频率)
        String ZDK07 = Util.null2String(mid.get("dkz7ghzpl"));//DK值(7G赫兹频率)
        String ZDK08 = Util.null2String(mid.get("dkz8ghzpl"));//DK值(8G赫兹频率)
        String ZDK09 = Util.null2String(mid.get("dkz9ghzpl"));//DK值(9G赫兹频率)
        String ZBJZT = Util.null2String(mid.get("bjztsy"));//标记状态(水印)
        String ZBLHD = Util.null2String(mid.get("bbhdmm"));//玻布厚度(MM)
        String ZGYSM = Util.null2String(mid.get("gysmc"));//供应商名称
        String ZLEIX = Util.null2String(mid.get("lx"));//类型
        String ZSFKB = Util.null2String(mid.get("sfkxb"));//是否开纤布（选择）
        String ZSHWN = Util.null2String(mid.get("sfwl"));//是否无卤
        String ZSZLD = Util.null2String(mid.get("szldd"));//树脂流动度
        String ZTBCC = Util.null2String(mid.get("tbccd"));//铜箔粗糙度
        String ZTBRA = Util.null2String(mid.get("tbccddmgmra"));//铜箔粗糙度 (顶面光面Ra)

        String ZTBRZ = Util.null2String(mid.get("tbzdccddmcmrz"));//铜箔最大粗糙度 (底面糙面Rz)
        String ZTBZA = Util.null2String(mid.get("tbzdccddmgmra"));//铜箔最大粗糙度 (底面光面Ra)
        String ZTBR1 = Util.null2String(mid.get("dingmcmrz"));//铜箔最大粗糙度 (顶面糙面Rz)
        String ZYMGL = Util.null2String(mid.get("ymglx"));//油墨光亮性
        String ZZTAI = Util.null2String(mid.get("zt"));//状态

        String WERKS = Util.null2String(mid.get("gc"));//工厂
        String DISPO  = Util.null2String(mid.get("mrpkzz"));//MRP控制者
        String LGPRO = Util.null2String(mid.get("scccdd"));//生产仓储地点
        String fc  = Util.null2String(mid.get("fc"));//反冲
        String RGEKZ=null;
        if ("0".equals(fc)){
            RGEKZ = "";
        }else if ("1".equals(fc)){
            RGEKZ = "1";
        }
        String EISBE = Util.null2String(mid.get("aqkc"));//安全库存
        String MABST = Util.null2String(mid.get("zdkcsl"));//最大库存数量
        String DISGR = Util.null2String(mid.get("mrpz"));//MRP组
        String mrplx = Util.null2String(mid.get("mrplx"));//MRP类型
        String DISMM = null;
        if (mrplx.equals("0")){
            DISMM = "PD";
        }else if (mrplx.equals("1")){
            DISMM = "ND";
        }
        String LGFSB = Util.null2String(mid.get("wbcgccdd"));//	外部采购仓储地点
        String DISLS = Util.null2String(mid.get("pil"));//批量

        String EKGRP = Util.null2String(mid.get("cgz"));//采购组
        String pcgl  = Util.null2String(mid.get("pcgl"));//批次管理(选择)
        String XCHAR = null;
        if ("0".equals(pcgl)){
            XCHAR = "";
        }else if ("1".equals(pcgl)){
            XCHAR = "X";
        }
        String yqd  = Util.null2String(mid.get("yqd"));//源清单（选择）
        String KORDB = null;
        if ("0".equals(yqd)){
            KORDB = "";
        }else if ("1".equals(yqd)){
            KORDB = "X";
        }
        String BSTMI = Util.null2String(mid.get("zxpldx"));//最小批量大小
        String BSTRF = Util.null2String(mid.get("srz"));//舍入值
        String PLIFZ = Util.null2String(mid.get("jhjhsjt"));//计划交货时间
        String SOBSL = Util.null2String(mid.get("tescgl"));//特殊采购类
        String ZMJXS = Util.null2String(mid.get("mjxs"));//面积系数
        String bxwl = Util.null2String(mid.get("bxwl"));//	包线物料，是传X，否传空
        String ZBXWL = null;
        if ("0".equals(bxwl)){
            ZBXWL = "";
        }else if ("1".equals(bxwl)){
            ZBXWL = "X";
        }
        String ZHGBM = Util.null2String(mid.get("hgbm"));//海关编码

        String bzq = Util.null2String(mid.get("bzqt"));//	保质期
        String MHDRZ = bzq;//最短剩余货架寿命
        String MHDHB = bzq;//总货架寿命
        String IPRKZ = "D";//货架存放期过期日期的期间标识
        String ART = Util.null2String(mid.get("jylxing"));//  检验类型
        String BKLAS = Util.null2String(mid.get("pgfl"));//	评估分类
        String PRCTR = Util.null2String(mid.get("lrzx"));//  利润中心
        String ZPLP1 = Util.null2String(mid.get("jhjg1"));//  计划价格1

        JSONArray jsonArray = new JSONArray();
        JSONObject detailtObject = new  JSONObject();

        detailtObject.put("ACTVT","01");//操作类型
        detailtObject.put("MTART",MTART);//物料类型大类
        detailtObject.put("MATKL",MATKL);//物料组小类
        detailtObject.put("MAKTX",MAKTX);//物料描述
        detailtObject.put("ZWLCM",ZWLCM);//物料长描述
        detailtObject.put("ZYTSS",ZYTSS);//新增用途说明
        detailtObject.put("MEINS",MEINS);//基本计量单位
        detailtObject.put("SPART",SPART);//产品组
        detailtObject.put("EXTWG",EXTWG);//外部物料组
        detailtObject.put("ZGGXH",ZGGXH);//规格型号
        detailtObject.put("ZBCXH",ZBCXH);//板材型号
        detailtObject.put("ZCAND",ZCAND);//长度
        detailtObject.put("ZKUAN",ZKUAN);//宽度
        detailtObject.put("ZGAOD",ZGAOD);//高度
        detailtObject.put("ZCDDW",ZCDDW);//长度单位
        detailtObject.put("ZKDDW",ZKDDW);//宽度单位
        detailtObject.put("ZGDDW",ZGDDW);//高度单位
        detailtObject.put("ZBZGG",ZBZGG);//包装规格
        detailtObject.put("ZSHHD",ZSHHD);//是否含铜
        detailtObject.put("ZBANH",ZBANH);//板厚
        detailtObject.put("ZBHGC",ZBHGC);//板厚公差
        detailtObject.put("ZTONH",ZTONH);//上铜厚
        detailtObject.put("ZXTON",ZXTON);//下铜厚
        detailtObject.put("ZTBLE",ZTBLE);//上铜箔类型
        detailtObject.put("ZXTBL",ZXTBL);//下铜箔类型
        detailtObject.put("ZBLBZ",ZBLBZ);//玻璃布组合
        detailtObject.put("ZYANS",ZYANS);//颜色
        detailtObject.put("ZBLDJ",ZBLDJ);//板料等级
        detailtObject.put("ZWXCC",ZWXCC);//纬向尺寸
        detailtObject.put("ZTGSS",ZTGSS);//TG
        detailtObject.put("ZDKSS",ZDKSS);//DK
        detailtObject.put("ZDFSS",ZDFSS);//DF
        detailtObject.put("ZCTIS",ZCTIS);//CTI
        detailtObject.put("ZSZHL",ZSZHL);//树脂含量
        detailtObject.put("ZZJSS",ZZJSS);//直径
        detailtObject.put("ZRCSS",ZRCSS);//刃长
        detailtObject.put("ZBJSS",ZBJSS);//柄径
        detailtObject.put("ZZCSS",ZZCSS);//总长
        detailtObject.put("ZJDSS",ZJDSS);//角度
        detailtObject.put("ZWJSS",ZWJSS);//外径
        detailtObject.put("ZNJSS",ZNJSS);//内径
        detailtObject.put("ZCISS",ZCISS);//齿数
        detailtObject.put("ZNDSS",ZNDSS);//浓度
        detailtObject.put("ZHDSS",ZHDSS);//厚度
        detailtObject.put("ZTIJS",ZTIJS);//体积
        detailtObject.put("ZZYWL",ZZYWL);//是否专用料
        detailtObject.put("ZDUDW",ZDUDW);//厚度单位

        detailtObject.put("ZDF10",ZDF10);//DF值(10G赫兹频率)
        detailtObject.put("ZDF15",ZDF15);//DF值(15G赫兹频率)
        detailtObject.put("ZDF20",ZDF20);//DF值(20G赫兹频率)
        detailtObject.put("ZDF02",ZDF02);//DF值(2G赫兹频率)
        detailtObject.put("ZDF03",ZDF03);//DF值(3G赫兹频率)
        detailtObject.put("ZDF04",ZDF04);//DF值(4G赫兹频率)
        detailtObject.put("ZDF05",ZDF05);//DF值(5G赫兹频率)
        detailtObject.put("ZDF06",ZDF06);//DF值(6G赫兹频率)
        detailtObject.put("ZDF07",ZDF07);//DF值(7G赫兹频率)
        detailtObject.put("ZDF08",ZDF08);//DF值(8G赫兹频率)
        detailtObject.put("ZDF09",ZDF09);//DF值(9G赫兹频率)

        detailtObject.put("ZDK10",ZDK10);//DK值(10G赫兹频率)
        detailtObject.put("ZDK15",ZDK15);//DK值(15G赫兹频率)
        detailtObject.put("ZDK20",ZDK20);//DK值(20G赫兹频率)
        detailtObject.put("ZDK02",ZDK02);//DK值(2G赫兹频率)
        detailtObject.put("ZDK03",ZDK03);//DK值(3G赫兹频率)
        detailtObject.put("ZDK04",ZDK04);//DK值(4G赫兹频率)
        detailtObject.put("ZDK05",ZDK05);//DK值(5G赫兹频率)
        detailtObject.put("ZDK06",ZDK06);//DK值(6G赫兹频率)
        detailtObject.put("ZDK07",ZDK07);//DK值(7G赫兹频率)
        detailtObject.put("ZDK08",ZDK08);//DK值(8G赫兹频率)
        detailtObject.put("ZDK09",ZDK09);//DK值(9G赫兹频率)

        detailtObject.put("ZBJZT",ZBJZT);//标记状态
        detailtObject.put("ZBLHD",ZBLHD);//玻璃布厚度
        detailtObject.put("ZGYSM",ZGYSM);//供应商名称
        detailtObject.put("ZLEIX",ZLEIX);//类型
        detailtObject.put("ZSFKB",ZSFKB);//是否开纤布
        detailtObject.put("ZSHWN",ZSHWN);//是否无卤
        detailtObject.put("ZSZLD",ZSZLD);//树脂流动度
        detailtObject.put("ZTBCC",ZTBCC);//铜箔粗糙度
        detailtObject.put("ZTBRA",ZTBRA);//铜箔粗糙度 (顶面光面Ra)
        detailtObject.put("ZTBRZ",ZTBRZ);//铜箔最大粗糙度 (底面糙面Rz)
        detailtObject.put("ZTBZA",ZTBZA);//铜箔最大粗糙度 (底面光面Ra)
        detailtObject.put("ZTBR1",ZTBR1);//铜箔最大粗糙度 (顶面糙面Rz)
        detailtObject.put("ZYMGL",ZYMGL);//油墨光亮度
        detailtObject.put("ZZTAI",ZZTAI);//状态

        detailtObject.put("WERKS",WERKS);//工厂
        detailtObject.put("DISPO",DISPO);//MRP控制者
        detailtObject.put("LGPRO",LGPRO);//生产仓储地点
        detailtObject.put("LGORT",LGPRO);
        detailtObject.put("RGEKZ",RGEKZ);//反冲
        detailtObject.put("EISBE",EISBE);//安全库存
        detailtObject.put("MABST",MABST);//最大库存数量
        detailtObject.put("DISGR",DISGR);//MRP组
        detailtObject.put("DISMM",DISMM);//MRP类型
        detailtObject.put("LGFSB",LGFSB);//外部采购仓储地点
        detailtObject.put("DISLS",DISLS);//批量

        detailtObject.put("EKGRP",EKGRP);//采购组
        detailtObject.put("XCHAR",XCHAR);//批次管理
        detailtObject.put("KORDB",KORDB);//源清单
        detailtObject.put("BSTMI",BSTMI);//最小批量大小
        detailtObject.put("BSTRF",BSTRF);//舍入值
        detailtObject.put("PLIFZ",PLIFZ);//计划交货时间
        detailtObject.put("SOBSL",SOBSL);//特殊采购类
        detailtObject.put("ZMJXS",ZMJXS);//面积系数
        detailtObject.put("ZBXWL",ZBXWL);//包线物料
        detailtObject.put("ZHGBM",ZHGBM);//海关编码

        detailtObject.put("MHDRZ",MHDRZ);//最短剩余货架寿命
        detailtObject.put("MHDHB",MHDHB);//总货架寿命
        detailtObject.put("IPRKZ",IPRKZ);//货架存放期过期日期的期间标识
        detailtObject.put("ART",ART);//检验类型

        detailtObject.put("BKLAS",BKLAS);//评估分类
        detailtObject.put("PRCTR",PRCTR);//利润中心
        detailtObject.put("ZPLP1",ZPLP1);//计划价格1

        detailtObject.put("HKMAT","X");
        jsonArray.add(detailtObject);
        JSONObject typeObject = new JSONObject();
        typeObject.put("I_SOUSYS","OA");
        JSONArray typeArray = new JSONArray();
        typeArray.add(typeObject);
        System.out.println(jsonArray);
        jsonObj.put("IM_BASEINFO",typeArray);
        jsonObj.put("IT_MAR",jsonArray);
        String shuju = jsonObj.toString();
        JSONObject result;
        try {
            result= CommonUtil.Post(CommonUtil.masterMaterialUrl,shuju);
            //requestInfo.getRequestManager().setMessage(result.getString("E_MSG"));
            JSONArray reArray = result.getJSONArray("ET_RETURN");
            System.out.println(reArray);
            JSONObject reData = reArray.getJSONObject(0);
            String ecode = reData.getString("CODE");
            requestInfo.getRequestManager().setMessage(reData.getString("MESSAGE"));
            if ("S".equals(ecode)){
                String MATNR = reData.getString("MATNR");//物料编码
                // 定义MySQL的数据库驱动程序
                final String DBDRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver" ;
                // 定义MySQL数据库的连接地址
                final String DBURL = "jdbc:sqlserver://192.168.0.48:1433;DatabaseName=ecology" ;
                // MySQL数据库的连接用户名
                final String DBUSER = "sa" ;
                // MySQL数据库的连接密码
                final String DBPASS = "kbdyn2015" ;
                // 所有的异常抛出
                try {
                    Connection conn = null ;        // 数据库连接
                    Statement stmt = null ;            // 数据库操作
                    Class.forName(DBDRIVER) ;    // 加载驱动程序
                    String sql = "UPDATE formtable_main_279 SET wlbm='"+MATNR+"' where requestId='"+requestId+"'";
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
            else if("E".equals(ecode)){
                return FAILURE_AND_CONTINUE;
            }

        } catch (IOException e) {
            writeLog(e.getMessage());
            requestInfo.getRequestManager().setMessage(e.getMessage());
        }catch (JSONException e){
            writeLog(e.getMessage());
            requestInfo.getRequestManager().setMessage(e.getMessage());
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





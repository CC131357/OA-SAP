package weaver.interfaces.workflow.action;



import com.alibaba.fastjson.JSONObject;
import com.weaver.general.BaseBean;
import com.weaver.general.Util;
import weaver.conn.RecordSetDataSource;

import weaver.soa.workflow.request.*;
import java.io.IOException;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import java.sql.Connection;



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
        String MATNR=Util.null2String(mid.get("dangqcpxh"));  //当前产品型号
        String XCPXH = Util.null2String(mid.get("xcpxh"));
        if ((null==XCPXH)||("".equals(XCPXH))){
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
            String e_code = result.getString("E_COE");
            requestInfo.getRequestManager().setMessage(result.getString("E_MSG"));
            if ("S".equals(e_code)){
                //表示数据传输成功，正常提交
                System.out.println("成功");
                JSONObject data = result.getJSONObject("OS_OUTPUT");
                String ZMATNR_N = data.getString("ZMATNR_N");//新产品型号
                String VERID = data.getString("VERID");//当前旧版本
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
                    String sql = "UPDATE formtable_main_282 SET xcpxh='"+ZMATNR_N+"', jbb='" + VERID+"' where requestId='"+requestId+"'";
                    conn = DriverManager.getConnection(DBURL,DBUSER,DBPASS) ;
                    stmt = conn.createStatement() ;    // 实例化Statement对象
                    stmt.executeUpdate(sql) ;        // 执行数据库更新操作
                    stmt.close() ;                    // 关闭操作
                    conn.close() ;            // 数据库关闭
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String xcpxh=Util.null2String(mid.get("xcpxh"));  //新产品型号
                String jbb=Util.null2String(mid.get("jbb"));  //旧版本
                return SUCCESS;
            }
        }else {
            return SUCCESS;
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




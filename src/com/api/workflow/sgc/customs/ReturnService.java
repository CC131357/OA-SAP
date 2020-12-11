package com.api.workflow.sgc.customs;

import com.api.workflow.sgc.hrm.HrmService;
import com.api.workflow.sgc.hrm.UserModel;
import com.api.workflow.sgc.utils.ApiResult;
import com.api.workflow.sgc.utils.Utils;
import com.google.common.base.Strings;
import weaver.conn.RecordSetDataSource;
import weaver.general.Util;
import weaver.workflow.webservices.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


@Path("/workflow/finance")
public class ReturnService {
    /*退货扣款申请流程ID*/
    final String CUSTOMSWFID="1418";
    @POST
    @Path("/createReturn")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult createApply(@Context HttpServletRequest request, @Context HttpServletResponse response, final ReturnModel model) {
        HrmService hrmService=new HrmService();
        UserModel userModel= hrmService.getUser(model.getUserNo());
        ApiResult apiResult=new ApiResult();
        if(null==userModel){
            apiResult.setStateCode("0");
            apiResult.setMsg("查无此用户");
            return apiResult;
        }
        try {
            WorkflowServiceImpl client=new WorkflowServiceImpl();
            WorkflowRequestInfo requestInfo=new WorkflowRequestInfo();//创建工作流信息
            requestInfo.setCanView(true);//显示
            requestInfo.setCanEdit(true);//可编辑
            SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd" );
            Date d= new Date();
            String strDate = sdf.format(d);//格式化日期
            requestInfo.setRequestName(String.format("退货扣款申请流程-%s-%s",model.getUserName(),strDate));//流程请求的标题
            requestInfo.setRequestLevel("0");//请求重要级别 0：正常 1：重要 2：紧急
            requestInfo.setCreatorId(userModel.getId().toString());//创建者ID 创建流程时为必输项
            WorkflowBaseInfo baseInfo=new WorkflowBaseInfo();//创建工作流信息
            baseInfo.setWorkflowId(CUSTOMSWFID);//流程ID
            baseInfo.setWorkflowName("退货扣款申请流程");//流程名称
            baseInfo.setWorkflowTypeName("SAP_OA流程");//流程类型名称
            requestInfo.setWorkflowBaseInfo(baseInfo);
            //main
            WorkflowMainTableInfo tableInfo=new WorkflowMainTableInfo();
            tableInfo.setRequestRecords(generateMainRecord(model,userModel));//主表添加信息
            requestInfo.setWorkflowMainTableInfo(tableInfo);//主表数据添加进工作流程
            String requestId= client.doCreateWorkflowRequest(requestInfo,userModel.getId());
            apiResult.setStateCode("1");
            apiResult.setMsg("requestId:"+requestId);
        }catch(Exception ex){
            ex.printStackTrace();
            apiResult.setStateCode("0");
            apiResult.setMsg(ex.getMessage());
        }
        return apiResult;
    }
    WorkflowRequestTableRecord[] generateMainRecord(ReturnModel model,UserModel userModel){//主表添加信息方法
        WorkflowRequestTableRecord[] records=new WorkflowRequestTableRecord[1];//主表只有一条数据，
        records[0]=new WorkflowRequestTableRecord();
        WorkflowRequestTableField[] tableFields=new WorkflowRequestTableField[45];//创建主表字段存储数组
        tableFields[0]=Utils.generateFeild("dw",model.getUnit());//单位
        tableFields[1]=Utils.generateFeild("topskkdh",model.getTOPSNo());//TOPS扣款单号
        tableFields[2]=Utils.generateFeild("khdm",model.getCustomerNo());//客户代码
        tableFields[3]=Utils.generateFeild("tuihdh",model.getReturnNo());//退款单号
        tableFields[4]=Utils.generateFeild("tuihrq",model.getReturnDate());//退货日期
        tableFields[5]=Utils.generateFeild("kehmc",model.getCustomerName());//客户名称
        tableFields[6]=Utils.generateFeild("tuihclr",model.getReturnEngineer());//退货处理人
        tableFields[7]=Utils.generateFeild("scgc",model.getBranchCompany());//生产工厂
        tableFields[8]=Utils.generateFeild("chanpxh",model.getProductModel());//产品型号
        tableFields[9]=Utils.generateFeild("banb",model.getEdition());//版本
        tableFields[10]=Utils.generateFeild("tuihsl",model.getAmountOfReturn());//退货数量
        tableFields[11]=Utils.generateFeild("tuihmj",model.getAreaOfReturn());//退货面积
        tableFields[12]=Utils.generateFeild("bibie",model.getCurrency());//币别
        tableFields[13]=Utils.generateFeild("kklx",model.getReturnType());//扣款类型
        tableFields[14]=Utils.generateFeild("tuihyy",model.getReturnReasonExplain());//原因说明
        tableFields[15]=Utils.generateFeild("koukyy",model.getReasonDescription());//原因描述
        tableFields[16]=Utils.generateFeild("clfa",model.getWayOfSolution());//处理方案
        tableFields[17]=Utils.generateFeild("pinzkk",model.getQuality());//品质扣款
        tableFields[18]=Utils.generateFeild("yunfkk",model.getFreightFree());//运费扣款
        tableFields[19]=Utils.generateFeild("hejkk",model.getSumAmount());//合计扣款
        tableFields[20]=Utils.generateFeild("pcbakk",model.getPCBA());//PCBA扣款
        tableFields[21]=Utils.generateFeild("pcbkk",model.getPCB());//PCB扣款
        tableFields[22]=Utils.generateFeild("fangwxkk",model.getRepair());//返工扣款
        tableFields[23]=Utils.generateFeild("yunf",model.getFreight());//运费
        tableFields[24]=Utils.generateFeild("qitkk",model.getOther());//其他扣款
        tableFields[25]=Utils.generateFeild("fukhDN",model.getCustomerDN());//附DN
        //check框方式
/*        String fkhdnje = model.getCustomerDN();
        if (!Strings.isNullOrEmpty(fkhdnje)){
            tableFields[28]=Utils.generateFeild("fkhdn","1");//扣款项目6
            tableFields[29]=Utils.generateFeild("fkhdnje",fkhdnje);
        }*/
        tableFields[26]=Utils.generateFeild("koukbs",model.getReturnTimes());//扣款倍数
        tableFields[27]=Utils.generateFeild("goutjgsm",model.getConnectResult());//沟通结果说明
        tableFields[28]=Utils.generateFeild("cljg",model.getResults());//处理结果
        //表头部分字段
        String fileUrl = model.getAppendix();//读取多个连接拼接的字符串
        String[] urls = fileUrl.split("\\|");//分割多个以|连接的字符串
        if (urls.length>0){
            for (int i=0;i<urls.length;i++){
                File file = new File(urls[i]);//遍历读取每个字符串
                String fileName = file.getName();
                String suffix = fileName.substring(fileName.lastIndexOf("."));//以最后一个.来读取每个http文件的后缀名
                String appendixName ="http:退货扣款"+ Integer.toString(i)+suffix;//拼接出想要的文件名
                tableFields[35+i]=Utils.generateFeild("xianggfj",urls[i]);//将将每个http文件的路径存储
                tableFields[35+i].setFieldType(appendixName);//设置附件的类型
            }
        }
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd" );
        Date d= new Date();
        String strDate = sdf.format(d);
        RecordSetDataSource rsds = new RecordSetDataSource("OA");
        String id = userModel.getId().toString();
        rsds.executeSql("select field3 from cus_fielddata where id='"+id+"'");
        String ldgx = null;
        if(rsds.next()){
            ldgx = rsds.getString(1);
            System.out.println(ldgx);
        }
        tableFields[29]=Utils.generateFeild("shenqr",userModel.getId().toString());
        tableFields[30]=Utils.generateFeild("zhij",userModel.getJobLevel());
        tableFields[31]=Utils.generateFeild("gangw",userModel.getJobTitle());
        tableFields[32]=Utils.generateFeild("shenqrq",strDate);
        tableFields[33]=Utils.generateFeild("bum",userModel.getDepartmentId());
        tableFields[34]=Utils.generateFeild("gongs",userModel.getSubCompanyId1());
        tableFields[35]=Utils.generateFeild("ldgx",ldgx);
        tableFields[36]=Utils.generateFeild("biaotfb","11");
        records[0].setWorkflowRequestTableFields(tableFields);
        return records;
    }
}

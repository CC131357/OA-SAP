package com.api.workflow.sgc.payment;

import com.api.workflow.sgc.payment.ApplyModel;
import com.api.workflow.sgc.hrm.HrmService;
import com.api.workflow.sgc.hrm.UserModel;
import com.api.workflow.sgc.utils.ApiResult;
import com.api.workflow.sgc.utils.Utils;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;
import weaver.soa.workflow.request.RequestService;
import weaver.workflow.webservices.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Path("/workflow/payment")
public class PaymentService {
    /*付款申请2流程ID*/
    final String PAYMENTWFID="1432";
    RequestService requestService = new RequestService();
    @POST
    @Path("/createApply")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult createApply(@Context HttpServletRequest request, @Context HttpServletResponse response, final ApplyModel model) {
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
            requestInfo.setIsnextflow("0");//不提交到下一个审批节点
            requestInfo.setCanView(true);//显示
            requestInfo.setCanEdit(true);//可编辑
            SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd" );
            Date d= new Date();
            String strDate = sdf.format(d);//格式化日期
            requestInfo.setRequestName(String.format("付款通知-%s-%s",userModel.getUserName(),strDate));//流程请求的标题
            requestInfo.setRequestLevel("0");//请求重要级别 0：正常 1：重要 2：紧急
            requestInfo.setCreatorId(userModel.getId().toString());//创建者ID 创建流程时为必输项
            WorkflowBaseInfo baseInfo=new WorkflowBaseInfo();//创建工作流信息
            baseInfo.setWorkflowId(PAYMENTWFID);//流程ID
            baseInfo.setWorkflowName("付款通知");//流程名称
            baseInfo.setWorkflowTypeName("财务管理");//流程类型名称
            requestInfo.setWorkflowBaseInfo(baseInfo);
            //main
            WorkflowMainTableInfo tableInfo=new WorkflowMainTableInfo();
            tableInfo.setRequestRecords(generateMainRecord(model,userModel));//主表添加信息
            requestInfo.setWorkflowMainTableInfo(tableInfo);//主表数据添加进工作流程
            //detail
            WorkflowDetailTableInfo[] details=new WorkflowDetailTableInfo[1];//创建两个明细表
            WorkflowDetailTableInfo d0 =new WorkflowDetailTableInfo();
            d0.setWorkflowRequestTableRecords(generateDetail1(model));
            details[0]=d0;
            requestInfo.setWorkflowDetailTableInfos(details);
            String requestId= client.doCreateWorkflowRequest(requestInfo,userModel.getId());
            if(requestId.equals("-7")){
                apiResult.setStateCode("-7");
                apiResult.setMsg("流程创建失败");
            }else{
                RequestInfo info= requestService.getRequest(Integer.parseInt(requestId));
                if(null!=info){
                    Property[] properties=info.getMainTableInfo().getProperty();
                    String workflowNo="";
                    for (Property p:properties){
                        if(p.getName().equals("liucbh")){
                            workflowNo=p.getValue();
                            break;
                        }
                    }
                    apiResult.setStateCode("1");
                    apiResult.setMsg("workflowNo:"+workflowNo);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            apiResult.setStateCode("0");
            apiResult.setMsg(ex.getMessage());
        }
        return apiResult;
    }
    WorkflowRequestTableRecord[] generateMainRecord(ApplyModel model,UserModel userModel){//主表添加信息方法
        WorkflowRequestTableRecord[] records=new WorkflowRequestTableRecord[1];//主表只有一条数据，
        records[0]=new WorkflowRequestTableRecord();
        WorkflowRequestTableField[] tableFields=new WorkflowRequestTableField[21];//创建主表字段存储数组
        //业务信息
        tableFields[0]=Utils.generateFeild("gsdm",model.getCompanyNo());
        tableFields[1]= Utils.generateFeild("zfdz",model.getMethod());
        tableFields[2]= Utils.generateFeild("fkdh",model.getPaymentNo());
        tableFields[3]=Utils.generateFeild("djlx",model.getDocType());
        tableFields[4]=Utils.generateFeild("ysqjez",model.getUsedAmount());
        tableFields[5]=Utils.generateFeild("ksqjez",model.getBalance());
        tableFields[6]=Utils.generateFeild("sqjez",model.getAmount());
        tableFields[7]=Utils.generateFeild("sqjedx",model.getAmount());
        tableFields[8]=Utils.generateFeild("bb",model.getCurrency());
        //供应商信息
        tableFields[9]=Utils.generateFeild("gyssap",model.getSupplierNo());
        tableFields[10]=Utils.generateFeild("skzhsap",model.getAccountNo());
        tableFields[11]=Utils.generateFeild("khxsap",model.getBankName());
        tableFields[12]=Utils.generateFeild("skdwsap",model.getSupplierName());
        tableFields[13]=Utils.generateFeild("fktk",model.getPaymentProvision());
        tableFields[14]=Utils.generateFeild("fkfs",model.getPaymentType());
        //表头部分字段
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd" );
        Date d= new Date();
        String strDate = sdf.format(d);
        tableFields[15]=Utils.generateFeild("shenqr",userModel.getId().toString());
        tableFields[16]=Utils.generateFeild("zhij",userModel.getJobLevel());
        tableFields[17]=Utils.generateFeild("gangw",userModel.getJobTitle());
        tableFields[18]=Utils.generateFeild("shenqrq",strDate);
        tableFields[19]=Utils.generateFeild("bum",userModel.getDepartmentId());
        tableFields[20]=Utils.generateFeild("gongs",userModel.getSubCompanyId1());
        records[0].setWorkflowRequestTableFields(tableFields);
        return records;
    }
    WorkflowRequestTableRecord[] generateDetail1(ApplyModel model){//明细表1添加数据方法
        List<ApplyModel.Detail> lst=model.getDetailList();//获取明细表1的行数
        WorkflowRequestTableRecord[] records=new WorkflowRequestTableRecord[lst.size()];//根据行数创建对应行数据存储容器
        for(int i=0;i<lst.size();i++){//遍历明细表1的行数，将对应的行数据插入明细表中
            WorkflowRequestTableRecord record =new WorkflowRequestTableRecord();//创建对应行数据存储的容器
            WorkflowRequestTableField[] tableFields=new WorkflowRequestTableField[11];//明细表1每行字段个数
            tableFields[0]= Utils.generateFeild("cgddh",lst.get(i).getPo());//明细字段添加进表中
            tableFields[1]= Utils.generateFeild("xxmh",lst.get(i).getItemNo());
            tableFields[2]= Utils.generateFeild("dzdh",lst.get(i).getStatementNo());
            tableFields[3]= Utils.generateFeild("fph",lst.get(i).getInvoiceNo());
            tableFields[4]= Utils.generateFeild("hth",lst.get(i).getContractNo());
            tableFields[5]= Utils.generateFeild("mc",lst.get(i).getName());
            tableFields[6]= Utils.generateFeild("dj",lst.get(i).getPrice());
            tableFields[7]=Utils.generateFeild("sl",lst.get(i).getNum());
            tableFields[8]=Utils.generateFeild("ysqje",lst.get(i).getUsedAmount());
            tableFields[9]=Utils.generateFeild("ksqje",lst.get(i).getBalance());
            tableFields[10]=Utils.generateFeild("sqje",lst.get(i).getAmount());
            record.setWorkflowRequestTableFields(tableFields);//将此时遍历的行数数据插入行数容器
            records[i]=record;
        }
        return records;
    }
}

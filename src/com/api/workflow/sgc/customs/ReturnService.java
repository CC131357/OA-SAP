package com.api.workflow.sgc.customs;

import com.api.workflow.sgc.hrm.HrmService;
import com.api.workflow.sgc.hrm.UserModel;
import com.api.workflow.sgc.utils.ApiResult;
import com.api.workflow.sgc.utils.Utils;
import weaver.workflow.webservices.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.Date;


@Path("/workflow/sgc/customs")
public class ReturnService {
    /*退货扣款申请流程ID*/
    final String CUSTOMSWFID="1372";
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
        WorkflowRequestTableField[] tableFields=new WorkflowRequestTableField[28];//创建主表字段存储数组
        tableFields[0]=Utils.generateFeild("sqlb",model.getAppType());
        tableFields[1]=Utils.generateFeild("thclgcsgh",model.getReturnEngineerNo());
        tableFields[2]=Utils.generateFeild("khdm",model.getCustomerNo());
        tableFields[3]=Utils.generateFeild("thdh",model.getReturnNo());
        tableFields[4]=Utils.generateFeild("thrq",model.getReturnDate());
        tableFields[5]=Utils.generateFeild("khmc",model.getCustomerName());
        tableFields[6]=Utils.generateFeild("thclgcs",model.getReturnEngineer());
        tableFields[7]=Utils.generateFeild("zrfgs",model.getBranchCompany());
        tableFields[8]=Utils.generateFeild("cpxh",model.getProductModel());
        tableFields[9]=Utils.generateFeild("bbn",model.getEdition());
        tableFields[10]=Utils.generateFeild("thsl",model.getAmountOfReturn());
        tableFields[11]=Utils.generateFeild("thmj",model.getAreaOfReturn());
        tableFields[12]=Utils.generateFeild("bb",model.getCurrency());
        tableFields[13]=Utils.generateFeild("thlx",model.getReturnType());
        tableFields[14]=Utils.generateFeild("thyysm",model.getReturnReasonExplain());
        tableFields[15]=Utils.generateFeild("thyyms",model.getReasonDescription());
        tableFields[16]=Utils.generateFeild("clfa",model.getWayOfSolution());
        tableFields[17]=Utils.generateFeild("kkzje",model.getSumAmount());
        tableFields[18]=Utils.generateFeild("kkbs",model.getReturnItem());
        tableFields[19]=Utils.generateFeild("kkxm",model.getReturnTimes());
        tableFields[20]=Utils.generateFeild("cljg",model.getResults());
        tableFields[21]=Utils.generateFeild("bz",model.getRemark());
        //tableFields[18]=Utils.generateFeild("thsbjz",model.getReturnItem());
        //tableFields[19]=Utils.generateFeild("thjz",model.getReturnTimes());
        //表头部分字段
/*        String durl = model.getAppendix();
        tableFields[28].setFieldType("http:xgfj");//http:开头代表该字段为附件字段多附件 用 | 分隔
        tableFields[28].setFieldValue(durl);*/

        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd" );
        Date d= new Date();
        String strDate = sdf.format(d);
        tableFields[22]=Utils.generateFeild("shenqr",userModel.getId().toString());
        tableFields[23]=Utils.generateFeild("zhij",userModel.getJobLevel());
        tableFields[24]=Utils.generateFeild("gangw",userModel.getJobTitle());
        tableFields[25]=Utils.generateFeild("shenqrq",strDate);
        tableFields[26]=Utils.generateFeild("bum",userModel.getDepartmentId());
        tableFields[27]=Utils.generateFeild("gongs",userModel.getSubCompanyId1());
        records[0].setWorkflowRequestTableFields(tableFields);
        return records;
    }
}

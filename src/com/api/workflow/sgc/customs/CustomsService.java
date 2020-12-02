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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("/workflow/customs")
public class CustomsService {
    /*海关申报与运费申请流程ID*/
    final String CUSTOMSWFID="1389";
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
            requestInfo.setCanView(true);//显示
            requestInfo.setCanEdit(true);//可编辑
            SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd" );
            Date d= new Date();
            String strDate = sdf.format(d);//格式化日期
            requestInfo.setRequestName(String.format("海关申报与运费申请流程-%s-%s",model.getUserName(),strDate));//流程请求的标题
            requestInfo.setRequestLevel("0");//请求重要级别 0：正常 1：重要 2：紧急
            requestInfo.setCreatorId(userModel.getId().toString());//创建者ID 创建流程时为必输项
            WorkflowBaseInfo baseInfo=new WorkflowBaseInfo();//创建工作流信息
            baseInfo.setWorkflowId(CUSTOMSWFID);//流程ID
            baseInfo.setWorkflowName("海关申报与运费申请流程");//流程名称
            baseInfo.setWorkflowTypeName("SAP_OA流程");//流程类型名称
            requestInfo.setWorkflowBaseInfo(baseInfo);
            //main
            WorkflowMainTableInfo tableInfo=new WorkflowMainTableInfo();
            tableInfo.setRequestRecords(generateMainRecord(model,userModel));//主表添加信息
            requestInfo.setWorkflowMainTableInfo(tableInfo);//主表数据添加进工作流程
            //detail
            WorkflowDetailTableInfo[] details=new WorkflowDetailTableInfo[2];//创建两个明细表
            WorkflowDetailTableInfo d0 =new WorkflowDetailTableInfo();
            d0.setWorkflowRequestTableRecords(generateDetail1(model));
            details[0]=d0;
            //detail1
            WorkflowDetailTableInfo d1=new WorkflowDetailTableInfo();
            d1.setWorkflowRequestTableRecords(generateDetail2(model));
            details[1]=d1;
            requestInfo.setWorkflowDetailTableInfos(details);

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
    WorkflowRequestTableRecord[] generateMainRecord(ApplyModel model,UserModel userModel){//主表添加信息方法
        WorkflowRequestTableRecord[] records=new WorkflowRequestTableRecord[1];//主表只有一条数据，
        records[0]=new WorkflowRequestTableRecord();
        WorkflowRequestTableField[] tableFields=new WorkflowRequestTableField[22];//创建主表字段存储数组
        tableFields[0]=Utils.generateFeild("formNo",model.getFormNo());
        tableFields[1]=Utils.generateFeild("sqr",model.getApplicant());
        tableFields[2]=Utils.generateFeild("sqsj",model.getApplyDate());
        tableFields[3]=Utils.generateFeild("applyType",model.getApplyType());
        tableFields[4]=Utils.generateFeild("shipmentType",model.getDeliveryMode());
        tableFields[5]=Utils.generateFeild("khdm",model.getCustomerNo());
        tableFields[6]=Utils.generateFeild("jckd",model.getSrcAddress());
        tableFields[7]=Utils.generateFeild("mdd",model.getDestAddress());
        tableFields[8]=Utils.generateFeild("khmc",model.getCustomerName());
        tableFields[9]=Utils.generateFeild("zzd",model.getTransitAddress());
        tableFields[10]=Utils.generateFeild("ysfycd",model.getFeeSponsor());
        tableFields[11]=Utils.generateFeild("transportType",model.getShipmentType());
        tableFields[12]=Utils.generateFeild("fphm",model.getInvoiceNo());
        tableFields[13]=Utils.generateFeild("bz",model.getRemark());
        tableFields[14]=Utils.generateFeild("thsbjz",model.getValueOfReturnDeclaration());
        tableFields[15]=Utils.generateFeild("thjz",model.getValueOfReturn());
        //表头部分字段
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd" );
        Date d= new Date();
        String strDate = sdf.format(d);
        tableFields[16]=Utils.generateFeild("shenqr",userModel.getId().toString());
        tableFields[17]=Utils.generateFeild("zhij",userModel.getJobLevel());
        tableFields[18]=Utils.generateFeild("gangw",userModel.getJobTitle());
        tableFields[19]=Utils.generateFeild("shenqrq",strDate);
        tableFields[20]=Utils.generateFeild("bum",userModel.getDepartmentId());
        tableFields[21]=Utils.generateFeild("gongs",userModel.getSubCompanyId1());
        records[0].setWorkflowRequestTableFields(tableFields);
        return records;
    }
    WorkflowRequestTableRecord[] generateDetail1(ApplyModel model){//明细表1添加数据方法
        List<ApplyModel.Declaration> lst=model.getDeclarationList();//获取明细表1的行数
        WorkflowRequestTableRecord[] records=new WorkflowRequestTableRecord[lst.size()];//根据行数创建对应行数据存储容器
        for(Integer i=0;i<lst.size();i++){//遍历明细表1的行数，将对应的行数据插入明细表中
            WorkflowRequestTableRecord record =new WorkflowRequestTableRecord();//创建对应行数据存储的容器
            WorkflowRequestTableField[] tableFields=new WorkflowRequestTableField[11];//明细表1每行字段个数
            tableFields[0]= Utils.generateFeild("cs",lst.get(i).getLayer());//明细字段添加进表中
            tableFields[1]= Utils.generateFeild("sl",lst.get(i).getNum());
            tableFields[2]= Utils.generateFeild("jz",lst.get(i).getNetWeight());
            tableFields[3]= Utils.generateFeild("mz",lst.get(i).getGrossWeight());
            tableFields[4]= Utils.generateFeild("je",lst.get(i).getAmountMoney());
            tableFields[5]= Utils.generateFeild("js",lst.get(i).getPieceNum());
            tableFields[6]= Utils.generateFeild("bz",lst.get(i).getRemark());
            tableFields[7]=Utils.generateFeild("qp",lst.get(i).getSection());
            tableFields[8]=Utils.generateFeild("sxb",lst.get(i).getTinPlate());
            tableFields[9]=Utils.generateFeild("zkt",lst.get(i).getImpedanceBar());
            tableFields[10]=Utils.generateFeild("mk",lst.get(i).getModule());
            record.setWorkflowRequestTableFields(tableFields);//将此时遍历的行数数据插入行数容器
            records[i]=record;
        }
        return records;
    }
    WorkflowRequestTableRecord[] generateDetail2(ApplyModel model){//明细表2添加数据方法
        List<ApplyModel.Logistics> lst=model.getLogisticsList();
        WorkflowRequestTableRecord[] records=new WorkflowRequestTableRecord[lst.size()];
        for(Integer i=0;i<lst.size();i++){
            WorkflowRequestTableRecord record =new WorkflowRequestTableRecord();
            WorkflowRequestTableField[] tableFields=new WorkflowRequestTableField[3];
            tableFields[0]= Utils.generateFeild("yflx",lst.get(i).getTransit());
            tableFields[1]= Utils.generateFeild("ysgs",lst.get(i).getCompany());
            tableFields[2]= Utils.generateFeild("fyyg",lst.get(i).getCost());
            record.setWorkflowRequestTableFields(tableFields);
            records[i]=record;
        }
        return records;
    }
    private void AssignModel(){
        ApplyModel model1=new ApplyModel();
        ApplyModel.Declaration ad=model1.new Declaration();
        model1.setUserNo("20006800");
        model1.setUserName("牌");
        model1.setFormNo("20201121001");
        model1.setApplicant("李梦");
        model1.setApplyDate("2020-11-01");
        model1.setApplyType("申请类型1");
        model1.setDeliveryMode("空运");
        model1.setCustomerNo("cs0001");
        model1.setSrcAddress("美国");
        model1.setDestAddress("深圳宝安");
        model1.setCustomerName("摩根大通");
        model1.setTransitAddress("柬埔寨");
        model1.setFeeSponsor("摩根大通");
        model1.setShipmentType("集装箱");
        model1.setInvoiceNo("FP0001");
        model1.setRemark("暂无备注");
        model1.setSection("切片");
        model1.setTinPlate("试锡板");
        model1.setImpedanceBar("阻抗");
        model1.setModule("模块");
        model1.setValueOfReturnDeclaration("退货申报价值");
        model1.setValueOfReturn("退货价值");
        ApplyModel.Declaration declaration=model1.new Declaration();
        declaration.setLayer("100");
        declaration.setNum("1");
        declaration.setNetWeight("10.5");
        declaration.setGrossWeight("20");
        declaration.setAmountMoney("96785");
        declaration.setPieceNum("2");
        declaration.setRemark("暂无申报明细备注");
        List<ApplyModel.Declaration> declarationList=new ArrayList<>();
        declarationList.add(declaration);
        model1.setDeclarationList(declarationList);
    }
}

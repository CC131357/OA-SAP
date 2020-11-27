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
            WorkflowRequestInfo requestInfo=new WorkflowRequestInfo();
            requestInfo.setCanView(true);
            requestInfo.setCanEdit(true);
            SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd" );
            Date d= new Date();
            String strDate = sdf.format(d);
            requestInfo.setRequestName(String.format("海关申报与运费申请流程-%s-%s",model.getUserName(),strDate));
            requestInfo.setRequestLevel("0");
            requestInfo.setCreatorId(userModel.getId().toString());
            WorkflowBaseInfo baseInfo=new WorkflowBaseInfo();
            baseInfo.setWorkflowId(CUSTOMSWFID);
            baseInfo.setWorkflowName("海关申报与运费申请流程");
            baseInfo.setWorkflowTypeName("SAP_OA流程");
            requestInfo.setWorkflowBaseInfo(baseInfo);
            //main
            WorkflowMainTableInfo tableInfo=new WorkflowMainTableInfo();
            tableInfo.setRequestRecords(generateMainRecord(model,userModel));
            requestInfo.setWorkflowMainTableInfo(tableInfo);
            //detail
            WorkflowDetailTableInfo[] details=new WorkflowDetailTableInfo[2];
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
    WorkflowRequestTableRecord[] generateMainRecord(ApplyModel model,UserModel userModel){
        WorkflowRequestTableRecord[] records=new WorkflowRequestTableRecord[1];
        records[0]=new WorkflowRequestTableRecord();
        WorkflowRequestTableField[] tableFields=new WorkflowRequestTableField[26];
        tableFields[0]=Utils.generateFeild("sqdh",model.getFormNo());
        tableFields[1]=Utils.generateFeild("sqr",model.getApplicant());
        tableFields[2]=Utils.generateFeild("sqsj",model.getApplyDate());
        tableFields[3]=Utils.generateFeild("sqlx",model.getApplyType());
        tableFields[4]=Utils.generateFeild("chfs",model.getDeliveryMode());
        tableFields[5]=Utils.generateFeild("khdm",model.getCustomerNo());
        tableFields[6]=Utils.generateFeild("jckd",model.getSrcAddress());
        tableFields[7]=Utils.generateFeild("mdd",model.getDestAddress());
        tableFields[8]=Utils.generateFeild("khmc",model.getCustomerName());
        tableFields[9]=Utils.generateFeild("zzd",model.getTransitAddress());
        tableFields[10]=Utils.generateFeild("ysfycd",model.getFeeSponsor());
        tableFields[11]=Utils.generateFeild("zylx",model.getShipmentType());
        tableFields[12]=Utils.generateFeild("fphm",model.getInvoiceNo());
        tableFields[13]=Utils.generateFeild("bz",model.getRemark());
        tableFields[14]=Utils.generateFeild("qp",model.getSection());
        tableFields[15]=Utils.generateFeild("sxb",model.getTinPlate());
        tableFields[16]=Utils.generateFeild("zkt",model.getImpedanceBar());
        tableFields[17]=Utils.generateFeild("mk",model.getModule());
        tableFields[18]=Utils.generateFeild("thsbjz",model.getValueOfReturnDeclaration());
        tableFields[19]=Utils.generateFeild("thjz",model.getValueOfReturn());
        //表头部分字段
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd" );
        Date d= new Date();
        String strDate = sdf.format(d);
        tableFields[20]=Utils.generateFeild("shenqr",userModel.getId().toString());
        tableFields[21]=Utils.generateFeild("zhij",userModel.getJobLevel());
        tableFields[22]=Utils.generateFeild("gangw",userModel.getJobTitle());
        tableFields[23]=Utils.generateFeild("shenqrq",strDate);
        tableFields[24]=Utils.generateFeild("bum",userModel.getDepartmentId());
        tableFields[25]=Utils.generateFeild("gongs",userModel.getSubCompanyId1());
        records[0].setWorkflowRequestTableFields(tableFields);
        return records;
    }
    WorkflowRequestTableRecord[] generateDetail1(ApplyModel model){
        List<ApplyModel.Declaration> lst=model.getDeclarationList();
        WorkflowRequestTableRecord[] records=new WorkflowRequestTableRecord[lst.size()];
        for(Integer i=0;i<lst.size();i++){
            WorkflowRequestTableRecord record =new WorkflowRequestTableRecord();
            WorkflowRequestTableField[] tableFields=new WorkflowRequestTableField[7];
            tableFields[0]= Utils.generateFeild("cs",lst.get(i).getLayer());
            tableFields[1]= Utils.generateFeild("sl",lst.get(i).getNum());
            tableFields[2]= Utils.generateFeild("jz",lst.get(i).getNetWeight());
            tableFields[3]= Utils.generateFeild("mz",lst.get(i).getGrossWeight());
            tableFields[4]= Utils.generateFeild("je",lst.get(i).getAmountMoney());
            tableFields[5]= Utils.generateFeild("js",lst.get(i).getPieceNum());
            tableFields[6]= Utils.generateFeild("bz",lst.get(i).getRemark());
            record.setWorkflowRequestTableFields(tableFields);
            records[i]=record;
        }
        return records;
    }
    WorkflowRequestTableRecord[] generateDetail2(ApplyModel model){
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

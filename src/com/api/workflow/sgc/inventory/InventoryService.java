package com.api.workflow.sgc.inventory;

import com.api.workflow.sgc.hrm.HrmService;
import com.api.workflow.sgc.hrm.UserModel;
import com.api.workflow.sgc.utils.ApiResult;
import com.api.workflow.sgc.utils.Utils;
import weaver.soa.workflow.request.Property;
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

@Path("/workflow/inventoryDiff")
public class InventoryService {

    /*盘点差异审批流程ID*/
    //final String INVENTORYWFID="18";//本地
    final String INVENTORYWFID="1422";
    RequestService requestService = new RequestService();
    @POST
    @Path("/createApply")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ApiResult createApply(@Context HttpServletRequest request, @Context HttpServletResponse response, final InventoryModel model) {

        UserModel userModel= new HrmService().getUser(model.getUSERNO());
        //test
        /*if(userModel == null && model.getUSERNO().equals("10073700")){
            userModel = new UserModel();
            userModel.setId(11);
            userModel.setDepartmentId("7");
            userModel.setJobLevel("9");
            userModel.setLoginId("10073700");
            userModel.setSecLevel("9");
            userModel.setJobTitle("2476");
            userModel.setSubCompanyId1("3");
        }*/
        ApiResult apiResult=new ApiResult();
        if(null==userModel){
            apiResult.setStateCode("0");
            apiResult.setMsg("查无此用户");
            return apiResult;
        }

        try{
            WorkflowRequestInfo requestInfo=new WorkflowRequestInfo();//创建工作流信息
            requestInfo.setCanView(true);//显示
            requestInfo.setCanEdit(true);//可编辑
            SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd" );
            Date d= new Date();
            String strDate = sdf.format(d);//格式化日期
            requestInfo.setRequestName(String.format("盘点差异申请流程-%s-%s",model.getUSERNAME(),strDate));//流程请求的标题
            requestInfo.setRequestLevel("0");//请求重要级别 0：正常 1：重要 2：紧急
            requestInfo.setCreatorId(userModel.getId().toString());//创建者ID 创建流程时为必输项
            WorkflowBaseInfo baseInfo=new WorkflowBaseInfo();//创建工作流信息
            baseInfo.setWorkflowId(INVENTORYWFID);//流程ID
            baseInfo.setWorkflowName("盘点差异申请流程");//流程名称
            baseInfo.setWorkflowTypeName("SAP_OA流程");//流程类型名称
            requestInfo.setWorkflowBaseInfo(baseInfo);
            //main
            WorkflowMainTableInfo tableInfo=new WorkflowMainTableInfo();
            tableInfo.setRequestRecords(generateMainRecord(model,userModel));//主表添加信息
            requestInfo.setWorkflowMainTableInfo(tableInfo);//主表数据添加进工作流程
            //detail
            WorkflowDetailTableInfo[] details=new WorkflowDetailTableInfo[1];//创建两个明细表
            WorkflowDetailTableInfo d0 =new WorkflowDetailTableInfo();
            d0.setWorkflowRequestTableRecords(generateDetail(model));
            details[0]=d0;

            requestInfo.setWorkflowDetailTableInfos(details);

            String requestId= new WorkflowServiceImpl().doCreateWorkflowRequest(requestInfo,userModel.getId());
            Property[] properties=requestService.getRequest(Integer.parseInt(requestId)).getMainTableInfo().getProperty();
            String workflowNo="";
            for (Property p:properties){
                if(p.getName().equals("liucbh")){
                    workflowNo=p.getValue();
                    break;
                }
            }
            apiResult.setStateCode("1");
            apiResult.setMsg("workflowNo:"+workflowNo);
        }catch(Exception ex){
            ex.printStackTrace();
            apiResult.setStateCode("0");
            apiResult.setMsg(ex.getMessage());
        }
        return apiResult;
    }

    private WorkflowRequestTableRecord[] generateMainRecord(InventoryModel model, UserModel userModel){//主表添加信息方法
        WorkflowRequestTableRecord[] records=new WorkflowRequestTableRecord[1];//主表只有一条数据，
        records[0]=new WorkflowRequestTableRecord();
        WorkflowRequestTableField[] tableFields=new WorkflowRequestTableField[7];//创建主表字段存储数组

        //表头部分字段
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd" );
        Date d= new Date();
        String strDate = sdf.format(d);
        tableFields[0]=Utils.generateFeild("shenqr",userModel.getId().toString());
        tableFields[1]=Utils.generateFeild("zhij",userModel.getJobLevel());
        tableFields[2]=Utils.generateFeild("gangw",userModel.getJobTitle());
        tableFields[3]=Utils.generateFeild("shenqrq",strDate);
        tableFields[4]=Utils.generateFeild("bum",userModel.getDepartmentId());
        tableFields[5]=Utils.generateFeild("gongs",userModel.getSubCompanyId1());
        tableFields[6]=Utils.generateFeild("pdcybtt",model.getIBLNR());
        records[0].setWorkflowRequestTableFields(tableFields);
        return records;
    }

    private WorkflowRequestTableRecord[] generateDetail(InventoryModel model){//明细表添加数据方法
        List<InventoryModel.InventoryDetail> lst=model.getDETAILS();//获取明细表1的行数
        WorkflowRequestTableRecord[] records=new WorkflowRequestTableRecord[lst.size()];//根据行数创建对应行数据存储容器
        for(int i=0;i<lst.size();i++){//遍历明细表1的行数，将对应的行数据插入明细表中
            WorkflowRequestTableRecord record =new WorkflowRequestTableRecord();//创建对应行数据存储的容器
            WorkflowRequestTableField[] tableFields=new WorkflowRequestTableField[10];//明细表1每行字段个数
            tableFields[0]= Utils.generateFeild("pdxmh",lst.get(i).getZPDIT());//盘点项目号
            tableFields[1]= Utils.generateFeild("bm",lst.get(i).getMATNR());//编码
            tableFields[2]= Utils.generateFeild("wlms",lst.get(i).getMAKTX());//物料描述
            tableFields[3]= Utils.generateFeild("gc",lst.get(i).getWERKS());//工厂
            tableFields[4]= Utils.generateFeild("ck",lst.get(i).getLGORT());//仓库
            tableFields[5]= Utils.generateFeild("ph",lst.get(i).getCHARG());//批号
            tableFields[6]= Utils.generateFeild("dw",lst.get(i).getMEINS());//单位
            tableFields[7]= Utils.generateFeild("zmshu",lst.get(i).getBUCHM());//账面数
            tableFields[8]=Utils.generateFeild("spshu",lst.get(i).getMENGE());//实盘数
            tableFields[9]=Utils.generateFeild("cyshu",lst.get(i).getZCYSL());//差异数
            record.setWorkflowRequestTableFields(tableFields);//将此时遍历的行数数据插入行数容器
            records[i]=record;
        }
        return records;
    }
}

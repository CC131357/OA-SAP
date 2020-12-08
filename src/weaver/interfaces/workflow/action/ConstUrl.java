package weaver.interfaces.workflow.action;

public class ConstUrl {
    private final static String baseUrl="http://10.10.10.31:50000/RESTAdapter/";
    //海关
    public final static String customStateUrl=baseUrl+"OA/S0006CustomsApproveUpdate";
    //报销（费用报销，业务招待报销，差旅报销）
    public final static String reimbursementUrl=baseUrl+"OA/S0063PaymentDataTransfer";
    //退货扣款
    public final static String deductioUrl=baseUrl+"OA/S0006CustomsApproveUpdate";
    //采购申请创建
    public final static String purchaseUrl=baseUrl+"S0042PRcreate";
    //差旅费用报销
    public final static String travelExpenseUrl=baseUrl+"S0063PaymentDataTransfer";

}

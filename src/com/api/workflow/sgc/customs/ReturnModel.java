package com.api.workflow.sgc.customs;

public class ReturnModel {
    private String userNo;
    private String userName;
    private String TOPSNo;//TOPS扣款单号
    private String customerNo;//客户代码
    private String returnNo;//退货单号
    private String returnDate;//退货日期
    private String customerName;//客户名称
    private String returnEngineer;//退货工程师
    private String branchCompany;//生产工厂
    private String productModel;//产品型号
    private String edition;//版本
    private String amountOfReturn;//退货数量
    private String areaOfReturn;//退货面积
    private String unit;//单位
    private String currency;//币别
    private String returnType;//退货类型
    private String returnReasonExplain;//退货原因说明
    private String ReasonDescription;//退货原因描述
    private String wayOfSolution;//处理方案
    private String quality; //品质扣款
    private String freightFree;//运费扣款
    private String sumAmount;//合计扣款
    private String PCBA;
    private String PCB;
    private String repair;
    private String freight;
    private String other;
    private String customerDN;
    private String returnTimes;//扣款倍数
    private String connectResult;//沟通结果
    private String results;//处理结果
    //private String process;//相关流程
    private String appendix;//相关附件
    //private String document;//相关文档

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTOPSNo() {
        return TOPSNo;
    }

    public void setTOPSNo(String TOPSNo) {
        this.TOPSNo = TOPSNo;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getReturnNo() {
        return returnNo;
    }

    public void setReturnNo(String returnNo) {
        this.returnNo = returnNo;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getReturnEngineer() {
        return returnEngineer;
    }

    public void setReturnEngineer(String returnEngineer) {
        this.returnEngineer = returnEngineer;
    }

    public String getBranchCompany() {
        return branchCompany;
    }

    public void setBranchCompany(String branchCompany) {
        this.branchCompany = branchCompany;
    }

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getAmountOfReturn() {
        return amountOfReturn;
    }

    public void setAmountOfReturn(String amountOfReturn) {
        this.amountOfReturn = amountOfReturn;
    }

    public String getAreaOfReturn() {
        return areaOfReturn;
    }

    public void setAreaOfReturn(String areaOfReturn) {
        this.areaOfReturn = areaOfReturn;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getReturnReasonExplain() {
        return returnReasonExplain;
    }

    public void setReturnReasonExplain(String returnReasonExplain) {
        this.returnReasonExplain = returnReasonExplain;
    }

    public String getReasonDescription() {
        return ReasonDescription;
    }

    public void setReasonDescription(String reasonDescription) {
        ReasonDescription = reasonDescription;
    }

    public String getWayOfSolution() {
        return wayOfSolution;
    }

    public void setWayOfSolution(String wayOfSolution) {
        this.wayOfSolution = wayOfSolution;
    }

    public String getSumAmount() {
        return sumAmount;
    }

    public void setSumAmount(String sumAmount) {
        this.sumAmount = sumAmount;
    }

    public String getReturnTimes() {
        return returnTimes;
    }

    public void setReturnTimes(String returnTimes) {
        this.returnTimes = returnTimes;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getFreightFree() {
        return freightFree;
    }

    public void setFreightFree(String freightFree) {
        this.freightFree = freightFree;
    }

    public String getConnectResult() {
        return connectResult;
    }

    public void setConnectResult(String connectResult) {
        this.connectResult = connectResult;
    }

    /**
     * 扣款项目
     */
    public String getPCBA() {
        return PCBA;
    }

    public void setPCBA(String PCBA) {
        this.PCBA = PCBA;
    }

    public String getPCB() {
        return PCB;
    }

    public void setPCB(String PCB) {
        this.PCB = PCB;
    }

    public String getRepair() {
        return repair;
    }

    public void setRepair(String repair) {
        this.repair = repair;
    }

    public String getFreight() {
        return freight;
    }

    public void setFreight(String freight) {
        this.freight = freight;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getCustomerDN() {
        return customerDN;
    }

    public void setCustomerDN(String customerDN) {
        this.customerDN = customerDN;
    }

    /*    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }*/


    public String getAppendix() {
        return appendix;
    }

    public void setAppendix(String appendix) {
        this.appendix = appendix;
    }


/*    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }*/

}

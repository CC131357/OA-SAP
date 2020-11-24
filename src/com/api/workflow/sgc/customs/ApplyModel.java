package com.api.workflow.sgc.customs;

import com.sun.media.jai.opimage.PatternRIF;
import jdk.nashorn.internal.objects.annotations.Getter;

import java.security.Principal;
import java.util.List;

public class ApplyModel {
    private String userNo;
    private String userName;
    /*
     * 申请单号
     * */
    private String formNo;
    /*
     * 申请人
     * */
    private String applicant;
    private String applyDate;
    private String applyType;
    private String deliveryMode;
    private String customerNo;
    private String srcAddress;
    private String destAddress;
    private String customerName;
    private String transitAddress;
    private String feeSponsor;
    private String shipmentType;
    private String invoiceNo;
    private String remark;
    private String section;
    private String tinPlate;
    private String impedanceBar;
    private String module;
    private String valueOfReturnDeclaration;
    private String valueOfReturn;
    private List<Declaration> declarationList;

    public List<Logistics> getLogisticsList() {
        return logisticsList;
    }

    public void setLogisticsList(List<Logistics> logisticsList) {
        this.logisticsList = logisticsList;
    }

    private List<Logistics> logisticsList;

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

    public String getFormNo() {
        return formNo;
    }

    public void setFormNo(String formNo) {
        this.formNo = formNo;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(String applyDate) {
        this.applyDate = applyDate;
    }

    public String getApplyType() {
        return applyType;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getSrcAddress() {
        return srcAddress;
    }

    public void setSrcAddress(String srcAddress) {
        this.srcAddress = srcAddress;
    }

    public String getDestAddress() {
        return destAddress;
    }

    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTransitAddress() {
        return transitAddress;
    }

    public void setTransitAddress(String transitAddress) {
        this.transitAddress = transitAddress;
    }

    public String getFeeSponsor() {
        return feeSponsor;
    }

    public void setFeeSponsor(String feeSponsor) {
        this.feeSponsor = feeSponsor;
    }

    public String getShipmentType() {
        return shipmentType;
    }

    public void setShipmentType(String shipmentType) {
        this.shipmentType = shipmentType;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getTinPlate() {
        return tinPlate;
    }

    public void setTinPlate(String tinPlate) {
        this.tinPlate = tinPlate;
    }

    public String getImpedanceBar() {
        return impedanceBar;
    }

    public void setImpedanceBar(String impedanceBar) {
        this.impedanceBar = impedanceBar;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getValueOfReturnDeclaration() {
        return valueOfReturnDeclaration;
    }

    public void setValueOfReturnDeclaration(String valueOfReturnDeclaration) {
        this.valueOfReturnDeclaration = valueOfReturnDeclaration;
    }

    public String getValueOfReturn() {
        return valueOfReturn;
    }

    public void setValueOfReturn(String valueOfReturn) {
        this.valueOfReturn = valueOfReturn;
    }

    public List<Declaration> getDeclarationList() {
        return declarationList;
    }

    public void setDeclarationList(List<Declaration> declarationList) {
        this.declarationList = declarationList;
    }

    class Declaration{
        String layer;
        String num;
        String netWeight;
        String grossWeight;
        String amountMoney;
        String pieceNum;
        String remark;

        public String getLayer() {
            return layer;
        }

        public void setLayer(String layer) {
            this.layer = layer;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getNetWeight() {
            return netWeight;
        }

        public void setNetWeight(String netWeight) {
            this.netWeight = netWeight;
        }

        public String getGrossWeight() {
            return grossWeight;
        }

        public void setGrossWeight(String grossWeight) {
            this.grossWeight = grossWeight;
        }

        public String getAmountMoney() {
            return amountMoney;
        }

        public void setAmountMoney(String amountMoney) {
            this.amountMoney = amountMoney;
        }

        public String getPieceNum() {
            return pieceNum;
        }

        public void setPieceNum(String pieceNum) {
            this.pieceNum = pieceNum;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
    class Logistics{
        private String transit;
        private String company;
        private String cost;

        public String getTransit() {
            return transit;
        }

        public void setTransit(String transit) {
            this.transit = transit;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getCost() {
            return cost;
        }

        public void setCost(String cost) {
            this.cost = cost;
        }
    }
}

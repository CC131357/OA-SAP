package com.api.workflow.sgc.payment;

import java.util.List;

public class ApplyModel {
    private String userNo;
    /*
    SAP信息
     */
    private String paymentNo;//付款单号

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(String usedAmount) {
        this.usedAmount = usedAmount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getSupplierNo() {
        return supplierNo;
    }

    public void setSupplierNo(String supplierNo) {
        this.supplierNo = supplierNo;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getPaymentProvision() {
        return paymentProvision;
    }

    public void setPaymentProvision(String paymentProvision) {
        this.paymentProvision = paymentProvision;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public List<Detail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<Detail> detailList) {
        this.detailList = detailList;
    }

    private String docType;//单据类型
    private String usedAmount;//已申请金额
    private String balance;//可申请金额
    private String amount;//申请金额
    private String currency;
    /*
    A.预付
    B.付款
     */
    private String method;
    /*
    供应商信息
     */
    private String supplierNo;
    private String accountNo;
    private String bankName;
    private String supplierName;
    private String paymentProvision;
    private String paymentType;
    /*
    明细
     */
    private List<Detail> detailList;
    class Detail{
        public String getPo() {
            return po;
        }

        public void setPo(String po) {
            this.po = po;
        }

        public String getItemNo() {
            return itemNo;
        }

        public void setItemNo(String itemNo) {
            this.itemNo = itemNo;
        }

        public String getStatementNo() {
            return statementNo;
        }

        public void setStatementNo(String statementNo) {
            this.statementNo = statementNo;
        }

        public String getInvoiceNo() {
            return invoiceNo;
        }

        public void setInvoiceNo(String invoiceNo) {
            this.invoiceNo = invoiceNo;
        }

        public String getContractNo() {
            return contractNo;
        }

        public void setContractNo(String contractNo) {
            this.contractNo = contractNo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getUsedAmount() {
            return usedAmount;
        }

        public void setUsedAmount(String usedAmount) {
            this.usedAmount = usedAmount;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        private String po;
        private String itemNo;
        private String statementNo;
        private String invoiceNo;
        private String contractNo;
        private String name;
        private String price;
        private String num;
        private String usedAmount;//已申请金额
        private String balance;//可申请金额
        private String amount;//申请金额
    }
}

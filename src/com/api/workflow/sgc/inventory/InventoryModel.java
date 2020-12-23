package com.api.workflow.sgc.inventory;

import java.util.List;

public class InventoryModel {

    private String id;

    /**申请人id*/
    private String USERNO;

    /**用户名称*/
    private String USERNAME;
    /**盘点单号*/
    private String IBLNR;
    /**获取盘点明细*/
    private List<InventoryDetail> DETAILS;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUSERNO() {
        return USERNO;
    }

    public void setUSERNO(String USERNO) {
        this.USERNO = USERNO;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getIBLNR() {
        return IBLNR;
    }

    public void setIBLNR(String IBLNR) {
        this.IBLNR = IBLNR;
    }

    public List<InventoryDetail> getDETAILS() {
        return DETAILS;
    }

    public void setDETAILS(List<InventoryDetail> DETAILS) {
        this.DETAILS = DETAILS;
    }

    class InventoryDetail{
        /**盘点项目号*/
        private String ZPDIT;
        /**编码*/
        private String MATNR;
        /**物料描述*/
        private String MAKTX;
        /**工厂*/
        private String WERKS;
        /**仓库*/
        private String LGORT;
        /**批号*/
        private String CHARG;
        /**单位*/
        private String MEINS;
        /**账面数*/
        private String BUCHM;
        /**实盘数*/
        private String MENGE;
        /**差异数*/
        private String ZCYSL;


        public String getZPDIT() {
            return ZPDIT;
        }

        public void setZPDIT(String ZPDIT) {
            this.ZPDIT = ZPDIT;
        }

        public String getMATNR() {
            return MATNR;
        }

        public void setMATNR(String MATNR) {
            this.MATNR = MATNR;
        }

        public String getMAKTX() {
            return MAKTX;
        }

        public void setMAKTX(String MAKTX) {
            this.MAKTX = MAKTX;
        }

        public String getWERKS() {
            return WERKS;
        }

        public void setWERKS(String WERKS) {
            this.WERKS = WERKS;
        }

        public String getLGORT() {
            return LGORT;
        }

        public void setLGORT(String LGORT) {
            this.LGORT = LGORT;
        }

        public String getCHARG() {
            return CHARG;
        }

        public void setCHARG(String CHARG) {
            this.CHARG = CHARG;
        }

        public String getMEINS() {
            return MEINS;
        }

        public void setMEINS(String MEINS) {
            this.MEINS = MEINS;
        }

        public String getBUCHM() {
            return BUCHM;
        }

        public void setBUCHM(String BUCHM) {
            this.BUCHM = BUCHM;
        }

        public String getMENGE() {
            return MENGE;
        }

        public void setMENGE(String MENGE) {
            this.MENGE = MENGE;
        }

        public String getZCYSL() { return ZCYSL; }

        public void setZCYSL(String ZCYSL) {
            //Sap传过来的数为类似10.01-数时，需要帮他们处理一下，处理成-10.01
            if(ZCYSL != null && ZCYSL.contains("-") &&
                    ZCYSL.charAt(ZCYSL.length() - 1) == '-'){
                this.ZCYSL = "-" + ZCYSL.substring(0,ZCYSL.length() - 1);//差异数
            } else{
                this.ZCYSL = ZCYSL;
            }
        }

        @Override
        public String toString() {
            return "InventoryDetail{" +
                    "ZPDIT='" + ZPDIT + '\'' +
                    ", MATNR='" + MATNR + '\'' +
                    ", MAKTX='" + MAKTX + '\'' +
                    ", WERKS='" + WERKS + '\'' +
                    ", LGORT='" + LGORT + '\'' +
                    ", CHARG='" + CHARG + '\'' +
                    ", MEINS='" + MEINS + '\'' +
                    ", BUCHM='" + BUCHM + '\'' +
                    ", MENGE='" + MENGE + '\'' +
                    ", ZCYSL='" + ZCYSL + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "InventoryModel{" +
                "id='" + id + '\'' +
                ", USERNO='" + USERNO + '\'' +
                ", USERNAME='" + USERNAME + '\'' +
                ", IBLNR='" + IBLNR + '\'' +
                ", DETAILS=" + DETAILS +
                '}';
    }
}

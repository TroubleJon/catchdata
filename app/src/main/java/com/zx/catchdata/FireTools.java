package com.zx.catchdata;

/**
 * 作者：H7111906 on 2019/8/2 11:58
 */
public class FireTools {
    private String id;
    private String code;
    private String producer;
    private String fileNO;
    private String name;
    private String model;
    private String formType;
    private String dealerInfo;
    private String saleType;
    private String arriveDate;
    private String saleArea;
    private String location;
    private String pillarindex;

    public FireTools(String id, String code, String producer, String fileNO, String name, String model, String formType, String dealerInfo, String saleType,
                     String arriveDate, String saleArea, String location, String pillarindex) {
        this.id = id;
        this.code = code;
        this.producer = producer;
        this.fileNO = fileNO;
        this.name = name;
        this.model = model;
        this.formType = formType;
        this.dealerInfo = dealerInfo;
        this.saleType = saleType;
        this.arriveDate = arriveDate;
        this.saleArea = saleArea;
        this.location = location;
        this.pillarindex = pillarindex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getFileNO() {
        return fileNO;
    }

    public void setFileNO(String fileNO) {
        this.fileNO = fileNO;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getDealerInfo() {
        return dealerInfo;
    }

    public void setDealerInfo(String dealerInfo) {
        this.dealerInfo = dealerInfo;
    }

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public String getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(String arriveDate) {
        this.arriveDate = arriveDate;
    }

    public String getSaleArea() {
        return saleArea;
    }

    public void setSaleArea(String saleArea) {
        this.saleArea = saleArea;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPillarindex() {
        return pillarindex;
    }

    public void setPillarindex(String pillarindex) {
        this.pillarindex = pillarindex;
    }
}

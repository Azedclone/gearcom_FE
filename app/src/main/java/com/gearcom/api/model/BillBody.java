package com.gearcom.api.model;

import java.util.List;

public class BillBody {

    private int billId;
    private List<BillDetailBody> billDetailBodies;

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public List<BillDetailBody> getBillDetailBodies() {
        return billDetailBodies;
    }

    public void setBillDetailBodies(List<BillDetailBody> billDetailBodies) {
        this.billDetailBodies = billDetailBodies;
    }

}

package com.gearcom.api.model;

import com.gearcom.model.Bill;

import java.util.List;

public class BillBody {

    private Bill bill;
    private List<BillDetailBody> billDetailBodies;

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public List<BillDetailBody> getBillDetailBodies() {
        return billDetailBodies;
    }

    public void setBillDetailBodies(List<BillDetailBody> billDetailBodies) {
        this.billDetailBodies = billDetailBodies;
    }

}

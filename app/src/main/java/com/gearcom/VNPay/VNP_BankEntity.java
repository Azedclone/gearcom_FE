package com.gearcom.VNPay;

import com.google.gson.annotations.SerializedName;

class VNP_BankEntity {
    @SerializedName("bank_code")
    String bank_code;
    @SerializedName("ios_scheme")
    String ios_scheme;
    @SerializedName("andr_scheme")
    String andr_scheme;

    VNP_BankEntity() {
    }
}

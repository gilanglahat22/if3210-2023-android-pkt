package com.pkt.majika.utils.retrofit

import com.google.gson.annotations.SerializedName

data class DataPayment (
    @SerializedName("status") val status: String
)
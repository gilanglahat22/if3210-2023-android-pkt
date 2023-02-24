package com.pkt.majika.utils.retrofit

import com.google.gson.annotations.SerializedName

data class DatasBranch (
    @SerializedName("data") val data: List<DataBranch>
)
package com.pkt.majika.utils.retrofit

import com.google.gson.annotations.SerializedName

data class DatasMenu (
    @SerializedName("data") val data: List<DataMenu>
)
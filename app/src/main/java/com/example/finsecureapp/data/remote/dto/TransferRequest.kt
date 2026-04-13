package com.example.finsecureapp.data.remote.dto

data class TransferRequest(
    val receiverAccountNumber: String,
    val amount: Double
)
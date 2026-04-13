package com.example.finsecureapp.data.repository

import com.example.finsecureapp.data.remote.dto.TransactionHistoryItem
import com.example.finsecureapp.data.remote.dto.TransferRequest
import com.example.finsecureapp.data.remote.dto.TransferResponse
import com.example.finsecureapp.data.remote.retrofit.RetrofitInstance
import com.example.finsecureapp.utils.Resource

class TransactionRepository {

    suspend fun transfer(token: String, request: TransferRequest): Resource<TransferResponse> {
        return try {
            val response = RetrofitInstance.transactionApi.transferMoney("Bearer $token", request)

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Transfer failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getHistory(token: String): Resource<List<TransactionHistoryItem>> {
        return try {
            val response = RetrofitInstance.transactionApi.getTransactionHistory("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.errorBody()?.string() ?: "Failed to load history")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
}
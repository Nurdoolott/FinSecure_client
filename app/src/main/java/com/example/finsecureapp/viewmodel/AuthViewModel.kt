package com.example.finsecureapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finsecureapp.data.local.datastore.TokenManager
import com.example.finsecureapp.data.remote.dto.LoginResponse
import com.example.finsecureapp.data.remote.dto.StartRegisterResponse
import com.example.finsecureapp.data.remote.dto.VerifyRegisterResponse
import com.example.finsecureapp.data.repository.AuthRepository
import com.example.finsecureapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<LoginResponse>?>(null)
    val loginState: StateFlow<Resource<LoginResponse>?> = _loginState

    private val _startRegisterState = MutableStateFlow<Resource<StartRegisterResponse>?>(null)
    val startRegisterState: StateFlow<Resource<StartRegisterResponse>?> = _startRegisterState

    private val _verifyRegisterState = MutableStateFlow<Resource<VerifyRegisterResponse>?>(null)
    val verifyRegisterState: StateFlow<Resource<VerifyRegisterResponse>?> = _verifyRegisterState

    fun login(phoneNumber: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading
            val result = repository.login(phoneNumber, password)

            if (result is Resource.Success) {
                tokenManager.saveToken(result.data.token)
            }

            _loginState.value = result
        }
    }

    fun startRegister(fullName: String, phoneNumber: String, password: String, email: String?) {
        viewModelScope.launch {
            _startRegisterState.value = Resource.Loading
            _startRegisterState.value = repository.startRegister(fullName, phoneNumber, password, email)
        }
    }

    fun verifyRegister(pendingRegistrationId: String, otpCode: String) {
        viewModelScope.launch {
            _verifyRegisterState.value = Resource.Loading
            _verifyRegisterState.value = repository.verifyRegister(pendingRegistrationId, otpCode)
        }
    }
}

class AuthViewModelFactory(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
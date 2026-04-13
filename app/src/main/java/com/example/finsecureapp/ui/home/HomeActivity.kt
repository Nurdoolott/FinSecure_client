package com.example.finsecureapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.finsecureapp.data.local.datastore.TokenManager
import com.example.finsecureapp.data.repository.AccountRepository
import com.example.finsecureapp.databinding.ActivityHomeBinding
import com.example.finsecureapp.ui.auth.login.LoginActivity
import com.example.finsecureapp.ui.transfer.TransferActivity
import com.example.finsecureapp.utils.Resource
import com.example.finsecureapp.viewmodel.AccountViewModel
import com.example.finsecureapp.viewmodel.AccountViewModelFactory
import kotlinx.coroutines.launch
import com.example.finsecureapp.ui.history.HistoryActivity
import com.example.finsecureapp.ui.news.NewsActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: AccountViewModel
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(applicationContext)

        viewModel = ViewModelProvider(
            this,
            AccountViewModelFactory(
                AccountRepository(),
                tokenManager
            )
        )[AccountViewModel::class.java]

        binding.btnLogout.setOnClickListener {
            logout()
        }

        binding.btnTransfer.setOnClickListener {
            startActivity(Intent(this, TransferActivity::class.java))
        }
        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        binding.btnNews.setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
        }

        observeBalanceState()
        viewModel.loadBalance()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadBalance()
    }

    private fun observeBalanceState() {
        lifecycleScope.launch {
            viewModel.balanceState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvAccountNumber.text = "Account Number: ${state.data.accountNumber}"
                        binding.tvBalance.text = "Balance: ${state.data.balance}"
                        binding.tvCurrency.text = "Currency: ${state.data.currency}"
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@HomeActivity, state.message, Toast.LENGTH_LONG).show()
                    }

                    null -> Unit
                }
            }
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            tokenManager.clearToken()
            startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
            finish()
        }
    }
}
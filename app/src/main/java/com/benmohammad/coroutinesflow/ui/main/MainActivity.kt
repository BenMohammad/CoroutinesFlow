package com.benmohammad.coroutinesflow.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.benmohammad.coroutinesflow.*
import com.benmohammad.coroutinesflow.databinding.ActivityMainBinding
import com.benmohammad.coroutinesflow.ui.add.AddActivity
import com.benmohammad.coroutinesflow.ui.main.MainContract.*
import com.benmohammad.coroutinesflow.ui.main.MainContract.ViewState.Companion.initial
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.LazyThreadSafetyMode.NONE

class MainActivity : AppCompatActivity(), View {


    private val mainVM by viewModel<MainVM>()
    private val userAdapter = UserAdapter()
    private val mainBinding by lazy(NONE) { ActivityMainBinding.inflate(layoutInflater)}

    private val removeChannel = BroadcastChannel<UserItem>(Channel.BUFFERED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)

        setupView()
        bindVM(mainVM)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_action -> {
                startActivity(Intent(this, AddActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    private fun setupView() {
        mainBinding.usersRecycler.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = userAdapter
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))

            ItemTouchHelper(
                SwipeLeftToDeleteCallback(context) cb@{ position ->
                    val userItem = mainVM.viewState.value.userItems[position]
                    removeChannel.offer(userItem)
                }
            ).attachToRecyclerView(this)
        }
    }

    private fun bindVM(mainVM: MainVM) {
        lifecycleScope.launchWhenStarted {
            mainVM.viewState
                .onEach { render(it) }
                .catch {  }
                .collect()
        }
        lifecycleScope.launchWhenStarted {
            mainVM.singleEvent
                .onEach { handleSingleEvent(it) }
                .catch {  }
                .collect()
        }

        intents()
            .onEach { mainVM.processIntent(it) }
            .launchIn(lifecycleScope)
    }



    override fun intents()= merge(
        flowOf(ViewIntent.Initial),
        mainBinding.swipeRefreshLayout.refreshes().map { MainContract.ViewIntent.Refresh },
        mainBinding.retryButton.clicks().map { MainContract.ViewIntent.Retry },
        removeChannel.asFlow().map{MainContract.ViewIntent.RemoveUser(it)}
    )


    private fun handleSingleEvent(event: SingleEvent) {
        Log.d("MainActivity", "handleSingleEvent : $event")
        return when (event) {
            SingleEvent.Refresh.Success -> toast("Refresh success")
            is SingleEvent.Refresh.Failure -> toast("Refresh Failure")
            is SingleEvent.GetUserError -> toast("Get user failure")
            is SingleEvent.RemoveUser.Success -> toast("Removed:  ${event.user.fullName}")
            is SingleEvent.RemoveUser.Failure -> toast("Error when removing ${event.user.fullName}")
        }
    }

    private fun render(viewState: ViewState) {
        Log.d("MainActivity", "render: $viewState")
        userAdapter.submitList(viewState.userItems)
        mainBinding.run {
            errorGroup.isVisible = viewState.error != null
            errorMessageTextView.text = viewState.error?.message

            progressBar.isVisible = viewState.isLoading

            if(viewState.isRefreshing) {
                swipeRefreshLayout.post { swipeRefreshLayout.isRefreshing = true }
            } else {
                swipeRefreshLayout.isRefreshing = false
            }

            swipeRefreshLayout.isEnabled = !viewState.isLoading && viewState.error === null
        }
    }
}
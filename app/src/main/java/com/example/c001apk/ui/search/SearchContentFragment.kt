package com.example.c001apk.ui.search

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.c001apk.adapter.FooterState
import com.example.c001apk.adapter.LoadingState
import com.example.c001apk.ui.base.BaseAppFragment
import com.example.c001apk.ui.home.IOnTabClickContainer
import com.example.c001apk.ui.home.IOnTabClickListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchContentFragment : BaseAppFragment<SearchContentViewModel>(),
    IOnSearchMenuClickListener, IOnTabClickListener {

    @Inject
    lateinit var viewModelAssistedFactory: SearchContentViewModel.Factory
    override val viewModel by viewModels<SearchContentViewModel> {
        SearchContentViewModel.provideFactory(
            viewModelAssistedFactory,
            arguments?.getString("keyWord").orEmpty(),
            arguments?.getString("type").orEmpty(),
            arguments?.getString("pageType").orEmpty(),
            arguments?.getString("pageParam").orEmpty(),
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(keyWord: String, type: String, pageType: String?, pageParam: String?) =
            SearchContentFragment().apply {
                arguments = Bundle().apply {
                    putString("keyWord", keyWord)
                    putString("type", type)
                    putString("pageType", pageType)
                    putString("pageParam", pageParam)
                }
            }
    }

    override fun onSearch(type: String, value: String, id: String?) {
        when (type) {
            "sort" -> viewModel.sort = value
            "feedType" -> viewModel.feedType = value
        }
        viewModel.dataList.value = emptyList()
        viewModel.footerState.value = FooterState.LoadingDone
        binding.swipeRefresh.isEnabled = false
        binding.errorMessage.errMsg.isVisible = false
        binding.errorLayout.parent.isVisible = false
        viewModel.loadingState.value = LoadingState.Loading
    }

    override fun onReturnTop(isRefresh: Boolean?) {
        binding.swipeRefresh.isRefreshing = true
        binding.recyclerView.scrollToPosition(0)
        refreshData()
    }

    override fun onResume() {
        super.onResume()
        (parentFragment as? IOnSearchMenuClickContainer)?.controller = this
        (parentFragment as? IOnTabClickContainer)?.tabController = this
    }

    override fun onPause() {
        super.onPause()
        (parentFragment as? IOnSearchMenuClickContainer)?.controller = null
        (parentFragment as? IOnTabClickContainer)?.tabController = null
    }

}
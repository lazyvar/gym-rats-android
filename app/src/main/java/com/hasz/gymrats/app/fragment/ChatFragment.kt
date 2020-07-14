package com.hasz.gymrats.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.databinding.FragmentChatBinding
import com.hasz.gymrats.app.loader.GlideLoader

import com.hasz.gymrats.app.model.Challenge
import com.hasz.gymrats.app.model.ChatMessage
import com.hasz.gymrats.app.service.AuthService
import com.hasz.gymrats.app.service.GymRatsApi
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment: Fragment() {
  private lateinit var challenge: Challenge

  private var savedView: View? = null
  private var canLoadMorePosts = true
  private var currentPage = 0
  private var loading = false

  private val loader = GlideLoader()
  private val adapter: MessagesListAdapter<ChatMessage> = MessagesListAdapter(AuthService.currentAccount!!.id.toString(), loader)

  companion object {
    fun newInstance(challenge: Challenge): ChallengeFragment {
      return ChallengeFragment().also {
        it.arguments = Bundle().also { b -> b.putParcelable("challenge", challenge) }
      }
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    if (savedView != null) {
      return savedView
    }
  
    adapter.setLoadMoreListener { _, _ ->
      loadNextPage()
    }

    challenge = requireArguments().getParcelable("challenge")!!
    savedView = DataBindingUtil.inflate<FragmentChatBinding>(
      inflater, R.layout.fragment_chat, container, false
    ).apply {
      messagesList.setAdapter(adapter)
      refresh()
    }.root

    return savedView
  }

  private fun refresh() {
    canLoadMorePosts = true
    loadChats(page = 0, clear = true)
  }

  private fun loadNextPage() {
    loadChats(page = currentPage + 1)
  }

  private fun loadChats(page: Int, clear: Boolean = false) {
    if (!canLoadMorePosts || loading) { return }

    loading = true

    GymRatsApi.chatMessages(challenge = challenge, page = page) { result ->
      result.fold(
        onSuccess = { chats ->
          loading = false

          if (chats.isEmpty()) {
            canLoadMorePosts = false

            if (page > 0) { return@fold }
          }

          if (clear) { adapter.clear() }

          currentPage = page
          adapter.addToEnd(chats, false)
        },
        onFailure = { error ->
          Snackbar.make(messagesList, error.message ?: "Something unpredictable happened.", Snackbar.LENGTH_LONG).show()
        }
      )
    }
  }
}
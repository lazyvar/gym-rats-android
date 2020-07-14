package com.hasz.gymrats.app.fragment

import android.annotation.SuppressLint
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.work.Logger
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.config.EnvironmentConfig
import com.hasz.gymrats.app.databinding.FragmentChatBinding
import com.hasz.gymrats.app.loader.GlideLoader
import com.hasz.gymrats.app.model.Account

import com.hasz.gymrats.app.model.Challenge
import com.hasz.gymrats.app.model.ChatMessage
import com.hasz.gymrats.app.service.AuthService
import com.hasz.gymrats.app.service.GymRatsApi
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.android.synthetic.main.fragment_chat.*
import org.phoenixframework.Channel
import org.phoenixframework.Socket
import org.threeten.bp.Instant

class ChatFragment: Fragment() {
  private lateinit var challenge: Challenge

  private var savedView: View? = null
  private var canLoadMorePosts = true
  private var currentPage = 0
  private var loading = false

  private val loader = GlideLoader()
  private val adapter: MessagesListAdapter<ChatMessage> = MessagesListAdapter(AuthService.currentAccount!!.id.toString(), loader)

  private lateinit var socket: Socket
  private lateinit var channel: Channel

  companion object {
    fun newInstance(challenge: Challenge): ChallengeFragment {
      return ChallengeFragment().also {
        it.arguments = Bundle().also { b -> b.putParcelable("challenge", challenge) }
      }
    }
  }

  @SuppressLint("RestrictedApi")
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val params = hashMapOf("token" to AuthService.currentAccount?.token)

    socket = Socket(EnvironmentConfig.ws, params)
    socket.onOpen {
      channel = socket.channel("room:challenge:${challenge.id}")
      channel.on("new_msg") { event ->
        Handler(Looper.getMainLooper()).post {
          val payload = event.payload
          val json = GymRatsApi.gsonGuy.toJson(payload)
          val chat = GymRatsApi.gsonGuy.fromJson<ChatMessage>(json, ChatMessage::class.java)

          adapter.addToStart(chat, true)

          if (chat.account.id == AuthService.currentAccount!!.id) {
            input.inputEditText.text = null
          }
        }
      }

      channel.onError { message ->
        Logger.LogcatLogger.get().error("mack", message.payload.toString())
      }

      channel.join()
    }

    socket.onError { throwable, response ->
      Logger.LogcatLogger.get().error("mack", throwable.message)
      Logger.LogcatLogger.get().error("mack", response?.message())
    }

    socket.connect()

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

      input.setInputListener { message ->
        channel.push("new_msg", payload = mapOf("message" to message.toString()))
        false
      }

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
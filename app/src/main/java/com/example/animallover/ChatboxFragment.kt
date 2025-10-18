package com.example.animallover

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animallover.model.Message
import com.example.animallover.ui.adapters.MessageAdapter

class ChatboxFragment : Fragment() {

    private lateinit var rvMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var tvChatName: TextView
    private lateinit var messageAdapter: MessageAdapter
    private val messagesList = mutableListOf<Message>()

    private var chatUserName: String? = null
    private var chatUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            chatUserName = it.getString("userName")
            chatUserId = it.getString("userId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chatbox, container, false)

        rvMessages = view.findViewById(R.id.rvMessages)
        etMessage = view.findViewById(R.id.etMessage)
        btnSend = view.findViewById(R.id.btnSend)
        tvChatName = view.findViewById(R.id.tvChatName)

        setupUI()
        loadDummyMessages()

        return view
    }

    private fun setupUI() {
        // Set chat name in header
        tvChatName.text = chatUserName ?: "Chat"

        // Setup RecyclerView
        messageAdapter = MessageAdapter(messagesList, getCurrentUserId())
        rvMessages.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
        }

        // Send button click listener
        btnSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage() {
        val messageText = etMessage.text.toString().trim()

        if (messageText.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a message", Toast.LENGTH_SHORT).show()
            return
        }

        // Create new message
        val newMessage = Message(
            messageId = System.currentTimeMillis().toString(),
            senderId = getCurrentUserId(),
            receiverId = chatUserId ?: "",
            message = messageText,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )

        // Add to list and update adapter
        messagesList.add(newMessage)
        messageAdapter.notifyItemInserted(messagesList.size - 1)
        rvMessages.smoothScrollToPosition(messagesList.size - 1)

        // Clear input
        etMessage.text.clear()

        // TODO: Send to Firebase
        // sendMessageToFirebase(newMessage)
    }

    private fun loadDummyMessages() {
        // Load some dummy messages for demonstration
        val currentUserId = getCurrentUserId()
        val otherUserId = chatUserId ?: "other_user"

        messagesList.addAll(listOf(
            Message(
                messageId = "1",
                senderId = otherUserId,
                receiverId = currentUserId,
                message = "Hey! How's your cat doing?",
                timestamp = System.currentTimeMillis() - 3600000,
                isRead = true
            ),
            Message(
                messageId = "2",
                senderId = currentUserId,
                receiverId = otherUserId,
                message = "She's doing great! Just adopted a new kitten too 😊",
                timestamp = System.currentTimeMillis() - 3500000,
                isRead = true
            ),
            Message(
                messageId = "3",
                senderId = otherUserId,
                receiverId = currentUserId,
                message = "That's amazing! What breed is it?",
                timestamp = System.currentTimeMillis() - 3400000,
                isRead = true
            ),
            Message(
                messageId = "4",
                senderId = currentUserId,
                receiverId = otherUserId,
                message = "A Persian cat, very fluffy and adorable!",
                timestamp = System.currentTimeMillis() - 3300000,
                isRead = true
            ),
            Message(
                messageId = "5",
                senderId = otherUserId,
                receiverId = currentUserId,
                message = "I'd love to see photos! Can you share some?",
                timestamp = System.currentTimeMillis() - 1000,
                isRead = false
            )
        ))

        messageAdapter.notifyDataSetChanged()
        rvMessages.scrollToPosition(messagesList.size - 1)
    }

    private fun getCurrentUserId(): String {
        // TODO: Get from Firebase Auth
        return "current_user_id"
    }

    // TODO: Firebase implementation
    /*
    private fun sendMessageToFirebase(message: Message) {
        val database = FirebaseDatabase.getInstance().reference
        val messageRef = database.child("Messages").push()

        messageRef.setValue(message).addOnSuccessListener {
            // Update chat list
            updateChatList(message)
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to send message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadMessagesFromFirebase() {
        val database = FirebaseDatabase.getInstance().reference
        val messagesRef = database.child("Messages")
            .orderByChild("timestamp")

        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messagesList.clear()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(Message::class.java)
                    message?.let {
                        if ((it.senderId == getCurrentUserId() && it.receiverId == chatUserId) ||
                            (it.senderId == chatUserId && it.receiverId == getCurrentUserId())) {
                            messagesList.add(it)
                        }
                    }
                }
                messageAdapter.notifyDataSetChanged()
                rvMessages.scrollToPosition(messagesList.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error loading messages", Toast.LENGTH_SHORT).show()
            }
        })
    }
    */
}
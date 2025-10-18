package com.example.animallover

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.widget.addTextChangedListener
import com.example.animallover.model.ChatUser
import com.example.animallover.ui.adapters.ChatAdapter

class ChatFragment : Fragment() {

    private lateinit var rvChatList: RecyclerView
    private lateinit var etSearchChat: EditText
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var chatAdapter: ChatAdapter
    private var allChats = mutableListOf<ChatUser>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        rvChatList = view.findViewById(R.id.rvChatList)
        etSearchChat = view.findViewById(R.id.etSearchChat)
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout)

        setupRecyclerView()
        loadDummyData()
        setupSearchFunctionality()

        return view
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(allChats) { chatUser ->
            // Navigate to chat box fragment with user data
            val bundle = Bundle().apply {
                putString("userName", chatUser.name)
                putString("userId", chatUser.userId)
            }
            findNavController().navigate(R.id.action_chatFragment_to_chatboxFragment, bundle)
        }

        rvChatList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }
    }

    private fun loadDummyData() {
        // Create dummy chat users
        allChats = mutableListOf(
            ChatUser(
                userId = "user001",
                name = "Sarah Johnson",
                profileImageUrl = "", // Use default cat icon
                lastMessage = "My cat loves the new toys! 🐱",
                lastMessageTime = "2m ago",
                unreadCount = 3,
                isOnline = true
            ),
            ChatUser(
                userId = "user002",
                name = "Mike Chen",
                profileImageUrl = "",
                lastMessage = "Thanks for the cat care tips!",
                lastMessageTime = "15m ago",
                unreadCount = 1,
                isOnline = true
            ),
            ChatUser(
                userId = "user003",
                name = "Emma Williams",
                profileImageUrl = "",
                lastMessage = "See you at the cat event tomorrow 😊",
                lastMessageTime = "1h ago",
                unreadCount = 0,
                isOnline = false
            ),
            ChatUser(
                userId = "user004",
                name = "David Brown",
                profileImageUrl = "",
                lastMessage = "Do you know any good vet nearby?",
                lastMessageTime = "3h ago",
                unreadCount = 0,
                isOnline = false
            ),
            ChatUser(
                userId = "user005",
                name = "Lisa Anderson",
                profileImageUrl = "",
                lastMessage = "Your cat is so adorable! 😍",
                lastMessageTime = "5h ago",
                unreadCount = 0,
                isOnline = true
            ),
            ChatUser(
                userId = "user006",
                name = "Tom Martinez",
                profileImageUrl = "",
                lastMessage = "Let's organize a cat meetup!",
                lastMessageTime = "Yesterday",
                unreadCount = 5,
                isOnline = false
            ),
            ChatUser(
                userId = "user007",
                name = "Rachel Green",
                profileImageUrl = "",
                lastMessage = "I adopted a new kitten! 🐈",
                lastMessageTime = "Yesterday",
                unreadCount = 0,
                isOnline = true
            ),
            ChatUser(
                userId = "user008",
                name = "James Wilson",
                profileImageUrl = "",
                lastMessage = "Can you recommend cat food brands?",
                lastMessageTime = "2 days ago",
                unreadCount = 0,
                isOnline = false
            )
        )

        chatAdapter.updateList(allChats)
        updateEmptyState()
    }

    private fun setupSearchFunctionality() {
        etSearchChat.addTextChangedListener { text ->
            val searchQuery = text.toString().trim().lowercase()

            if (searchQuery.isEmpty()) {
                chatAdapter.updateList(allChats)
            } else {
                val filteredList = allChats.filter { chatUser ->
                    chatUser.name.lowercase().contains(searchQuery) ||
                            chatUser.lastMessage.lowercase().contains(searchQuery)
                }
                chatAdapter.updateList(filteredList)
            }
            updateEmptyState()
        }
    }

    private fun updateEmptyState() {
        if (allChats.isEmpty()) {
            rvChatList.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
        } else {
            rvChatList.visibility = View.VISIBLE
            emptyStateLayout.visibility = View.GONE
        }
    }

    // TODO: For future Firebase implementation
    /*
    private fun loadChatsFromFirebase() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val chatsRef = FirebaseDatabase.getInstance().reference
            .child("Chats")
            .child(currentUserId)

        chatsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allChats.clear()
                for (chatSnapshot in snapshot.children) {
                    val chat = chatSnapshot.getValue(ChatUser::class.java)
                    chat?.let { allChats.add(it) }
                }
                chatAdapter.updateList(allChats)
                updateEmptyState()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error loading chats", Toast.LENGTH_SHORT).show()
            }
        })
    }
    */
}
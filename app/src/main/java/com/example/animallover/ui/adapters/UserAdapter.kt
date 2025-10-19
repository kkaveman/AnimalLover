package com.example.animallover.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.animallover.R
import com.example.animallover.data.model.User

class UserAdapter(
    private val users: MutableList<User>,
    private val onEditClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    init {
        android.util.Log.d("UserAdapter", "Adapter initialized with ${users.size} users")
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewProfile: ImageView
        private val textViewUsername: TextView
        private val textViewUid: TextView
        private val textViewBio: TextView
        private val buttonEdit: Button
        private val buttonDelete: Button

        init {
            try {
                android.util.Log.d("UserAdapter", "Initializing ViewHolder views")
                imageViewProfile = itemView.findViewById(R.id.imageViewProfile)
                textViewUsername = itemView.findViewById(R.id.textViewUsername)
                textViewUid = itemView.findViewById(R.id.textViewUid)
                textViewBio = itemView.findViewById(R.id.textViewBio)
                buttonEdit = itemView.findViewById(R.id.buttonEdit)
                buttonDelete = itemView.findViewById(R.id.buttonDelete)
                android.util.Log.d("UserAdapter", "ViewHolder views initialized successfully")
            } catch (e: Exception) {
                android.util.Log.e("UserAdapter", "Error initializing ViewHolder", e)
                throw e
            }
        }

        fun bind(user: User) {
            android.util.Log.d("UserAdapter", "Binding user: ${user.getUsername()}")

            textViewUsername.text = user.getUsername()
            textViewUid.text = "UID: ${user.getUID()}"
            textViewBio.text = if (user.getBio().isEmpty()) "No bio" else user.getBio()

            android.util.Log.d("UserAdapter", "Bound - Username: ${textViewUsername.text}")

            buttonEdit.setOnClickListener {
                android.util.Log.d("UserAdapter", "Edit clicked for ${user.getUsername()}")
                onEditClick(user)
            }

            buttonDelete.setOnClickListener {
                android.util.Log.d("UserAdapter", "Delete clicked for ${user.getUsername()}")
                onDeleteClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        android.util.Log.d("UserAdapter", "onCreateViewHolder called")
        android.util.Log.d("UserAdapter", "Parent: $parent")

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_crud_user, parent, false)

        android.util.Log.d("UserAdapter", "View inflated: $view")
        android.util.Log.d("UserAdapter", "View dimensions: ${view.layoutParams?.width}x${view.layoutParams?.height}")

        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        android.util.Log.d("UserAdapter", "onBindViewHolder called for position: $position")
        android.util.Log.d("UserAdapter", "Total items: ${users.size}")

        if (position < users.size) {
            android.util.Log.d("UserAdapter", "User at position $position: ${users[position].getUsername()}")
            holder.bind(users[position])
        } else {
            android.util.Log.e("UserAdapter", "ERROR: Position $position out of bounds (size: ${users.size})")
        }
    }

    override fun getItemCount(): Int {
        android.util.Log.d("UserAdapter", "getItemCount called: ${users.size}")
        return users.size
    }

    fun updateUsers(newUsers: List<User>) {
        android.util.Log.d("UserAdapter", "updateUsers called with ${newUsers.size} users")
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }

    fun removeUser(user: User) {
        android.util.Log.d("UserAdapter", "removeUser called for ${user.getUsername()}")
        val position = users.indexOfFirst { it.getUID() == user.getUID() }
        if (position != -1) {
            users.removeAt(position)
            notifyItemRemoved(position)
            android.util.Log.d("UserAdapter", "User removed at position $position")
        } else {
            android.util.Log.e("UserAdapter", "User not found in list")
        }
    }
}
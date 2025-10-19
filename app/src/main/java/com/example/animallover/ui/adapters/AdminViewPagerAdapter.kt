package com.example.animallover.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.animallover.EventCrudFragment
import com.example.animallover.UserCrudFragment
class AdminViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> EventCrudFragment()
            1 -> UserCrudFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}
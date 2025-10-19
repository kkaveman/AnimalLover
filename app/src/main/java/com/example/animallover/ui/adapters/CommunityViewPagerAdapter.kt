package com.example.animallover.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.animallover.AdoptionCenterFragment
import com.example.animallover.CommunityPostFragment
import com.example.animallover.InformationCenterFragment

class CommunityViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CommunityPostFragment()
            1 -> AdoptionCenterFragment()
            2 -> InformationCenterFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}
package com.example.paylasimmvvm.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.paylasimmvvm.R
import com.google.android.material.bottomnavigation.BottomNavigationView


    fun BottomNavigationView.setBadge(tabResId: Int, badgeValue: Int) {
        getOrCreateBadge(this, tabResId)?.let { badge ->
            badge.visibility = if (badgeValue > 0) {
                badge.text = "$badgeValue"
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun getOrCreateBadge(bottomBar: View, tabResId: Int): TextView? {
        val parentView = bottomBar.findViewById<ViewGroup>(tabResId)
        return parentView?.let {
            var badge = parentView.findViewById<TextView>(R.id.menuItemBadge)
            if (badge == null) {
                LayoutInflater.from(parentView.context).inflate(R.layout.item_badge, parentView, true)
                badge = parentView.findViewById(R.id.menuItemBadge)
            }
            badge
        }
    }

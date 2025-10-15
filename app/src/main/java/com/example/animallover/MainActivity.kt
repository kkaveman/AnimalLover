package com.example.animallover

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                    as NavHostFragment
        navController = navHostFragment.navController

        // Load profile image into nav header and set click listener
        loadDrawerProfileImage()

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.eventFragment,
                R.id.communityFragment,
            ),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        navView?.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.settingFragment || menuItem.itemId == R.id.helpFragment) {
                navController.navigate(menuItem.itemId)
            } else {
                NavigationUI.onNavDestinationSelected(menuItem, navController)
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Custom bottom navigation handling to fix back stack issues
        bottomNavigationView.setOnItemSelectedListener { item ->
            val currentDestination = navController.currentDestination?.id

            // Don't navigate if already at the destination
            if (currentDestination == item.itemId) {
                return@setOnItemSelectedListener true
            }

            when (item.itemId) {
                R.id.homeFragment, R.id.eventFragment, R.id.communityFragment, R.id.profileFragment -> {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.homeFragment, false)
                        .setLaunchSingleTop(true)
                        .build()

                    try {
                        navController.navigate(item.itemId, null, navOptions)
                        true
                    } catch (e: Exception) {
                        false
                    }
                }
                else -> false
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Update bottom navigation selection without triggering navigation
            bottomNavigationView.menu.findItem(destination.id)?.isChecked = true

            // Show/hide bottom navigation
            if (destination.id == R.id.settingFragment || destination.id == R.id.helpFragment ||
                destination.id == R.id.registerFragment || destination.id == R.id.loginFragment) {
                bottomNavigationView.visibility = View.GONE
            } else {
                bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }

    private fun loadDrawerProfileImage() {
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        val profileImageView = headerView.findViewById<ImageView>(R.id.profile_image_view)

        // Set click listener
        profileImageView.setOnClickListener {
            // Navigate to profile and close the drawer
            if (navController.currentDestination?.id != R.id.profileFragment) {
                navController.navigate(R.id.profileFragment)
            }
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        // Load image from Firebase
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val usersRef = FirebaseDatabase.getInstance("https://cat-app-4922a-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("Users").child(currentUser.uid)
            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val imageUrl = snapshot.child("image").getValue(String::class.java)
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this@MainActivity)
                            .load(imageUrl)
                            .placeholder(R.drawable.cat_icon)
                            .error(R.drawable.cat_icon)
                            .into(profileImageView)
                    } else {
                        profileImageView.setImageResource(R.drawable.cat_icon)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    profileImageView.setImageResource(R.drawable.cat_icon)
                }
            })
        } else {
            profileImageView.setImageResource(R.drawable.cat_icon)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }
}

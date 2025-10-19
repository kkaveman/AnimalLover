package com.example.animallover.data.model

import androidx.annotation.DrawableRes

data class Event(@DrawableRes val imageResId: Int,
                 val title: String,
                 val desc : String,
                 val date : String,)
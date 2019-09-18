package com.untha.model.models

import androidx.room.Entity

@Entity
data class Category (var title: String,
                     var description: String,
                     var subTitle: String ) : BaseModel()

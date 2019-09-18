package com.untha.model.models

import androidx.room.Entity

@Entity
class Category : BaseModel() {

    var title: String = ""
    var description: String = ""

}

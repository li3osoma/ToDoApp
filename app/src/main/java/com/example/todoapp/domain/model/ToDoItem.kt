package com.example.todoapp.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoapp.utils.CLIENT_ID
import com.example.todoapp.utils.DATABASE_NAME
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.UUID

/*

Model, entity

 */
@Entity(tableName = DATABASE_NAME)
data class ToDoItem(
    @PrimaryKey
    @ColumnInfo(name="id")
    @SerializedName("id")
    var id:UUID,

    @ColumnInfo(name = "text")
    @SerializedName("text")
    var text:String,

    @ColumnInfo(name = "importance")
    @SerializedName("importance")
    var importance: Importance,

    @ColumnInfo(name = "deadline")
    @SerializedName("deadline")
    var deadline:Long? = null,

    @ColumnInfo(name = "done")
    @SerializedName("done")
    var done:Boolean,

    @ColumnInfo(name = "color")
    @SerializedName("color")
    var color:String? = null,

    @ColumnInfo(name = "created_at")
    @SerializedName("created_at")
    var created_at:Long,

    @ColumnInfo(name = "changed_at")
    @SerializedName("changed_at")
    var changed_at:Long,

    @SerializedName("last_updated_by")
    var last_updated_by:String = CLIENT_ID
): Serializable{
    enum class Importance {

        low, basic, important

    }
}
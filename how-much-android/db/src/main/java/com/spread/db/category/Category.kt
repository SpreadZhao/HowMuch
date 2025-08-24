package com.spread.db.category

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class IconId internal constructor(val id: Int)

val ICON_ID_0 = IconId(0)
val ICON_ID_1 = IconId(1)
val ICON_ID_2 = IconId(2)
val ICON_ID_3 = IconId(3)
val ICON_ID_4 = IconId(4)
val ICON_ID_5 = IconId(5)
val ICON_ID_6 = IconId(6)
val ICON_ID_7 = IconId(7)
val ICON_ID_8 = IconId(8)
val ICON_ID_9 = IconId(9)
val ICON_ID_10 = IconId(10)

@Serializable
data class CategoryItem(
    var text: String,
    var id: IconId,
    // 记录用户使用该分类项的次数
    var count: Int = 0,
    // 记录用户设置该分类项置顶的时间戳，0 表示未置顶
    var lastUseTime: Long = 0L
)
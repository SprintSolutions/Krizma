package com.sprint.krizma.Adapter.DataModel

/**
 * Created by Umar Muzaffar Guraya on 8/19/2018.
 */
data class Book (var b_id :Int = 0,
                 var u_id :Int = 0,
                 var b_name: String = "",
                 var b_author: String = "",
                 var b_language: String = "",
                 var b_type: String = "",
                 var b_image: String = "",
                 var favourite_flag: Int = 0
                 )
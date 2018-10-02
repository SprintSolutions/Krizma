package com.sprint.krizma.Adapter.DataModel



data class User(var u_id: Int? = 0,
//                var fb_id: Int? = 0,
//                var li_id: Int? = 0,
//                var gp_id:Int? = 0,
                var u_name:String? = "",
                var u_email:String? = "",
                var u_password:String? = "",
                var u_status:String = "",
                var u_user_status:String? = "",
                var u_gender:String? = "",
                var u_age:String? = "",
                var u_lat:String? = "",
                var u_long:String? = "",
                var u_location:String? = "",
                var u_country: String? = "",
                var u_city: String? = "",
                var u_area: String? = "",
                var u_description: String? = "",
                var u_avatar: String? = "",
                var u_languages: String? = "",
                var u_genres: String? = "",
                var u_authors: String? = "",
                var u_type: String? = "",
                var u_token: String? = "",
                var u_role: String? = "",
                var u_user_type: String? = "",
                var u_verify_code: String? = "",
                var u_verify_flag: String? = "",
                var u_favourite_flag:Int? = 0,
                var u_distance:String? = "",
                var u_books:ArrayList<Book> = ArrayList()
)


//"u_id": 13,
//"fb_id": null,
//"li_id": null,
//"gp_id": null,
//"u_name": "tayyab",
//"u_email": "tayyabbs@hotmail.com",
//"u_password": "4297f44b13955235245b2497399d7a93",
//"u_status": "1",
//"u_user_status": null,
//"u_gender": null,
//"u_age": null,
//"u_lat": null,
//"u_long": null,
//"u_location": null,
//"u_country": null,
//"u_city": null,
//"u_area": null,
//"u_description": null,
//"u_avatar": null,
//"u_languages": null,
//"u_genres": null,
//"u_authors": null,
//"u_type": null,
//"u_token": null,
//"u_role": "u",
//"u_user_type": null,
//"u_verify_code": "6683",
//"u_verify_flag": 1,
//"remember_token": null,
//"created_at": "2018-08-02 10:38:50",
//"updated_at": "2018-08-02 10:38:50"
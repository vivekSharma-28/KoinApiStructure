package com.koinapistructure.utils

import com.koinapistructure.response.ImageData
import java.util.ArrayList

object Constant {
    const val baseURL = "https://theweekin.co.uk/api/cms/"
    const val REQUEST_GET_PHOTO = 2
    const val REQUEST_TAKE_PHOTO = 1

    fun imageData(): ArrayList<ImageData> {
        val parking_image = ArrayList<ImageData>()

        parking_image.add(ImageData("cat", "https://i.imgur.com/CzXTtJV.jpg"))
        parking_image.add(ImageData("dog", "https://i.imgur.com/OB0y6MR.jpg"))
        parking_image.add(
            ImageData(
                "cheetah", "https://farm2.staticflickr.com/1533/26541536141_41abe98db3_z_d.jpg"
            )
        )
        parking_image.add(
            ImageData(
                "bird", "https://farm4.staticflickr.com/3075/3168662394_7d7103de7d_z_d.jpg"
            )
        )
        parking_image.add(
            ImageData(
                "whale", "https://farm9.staticflickr.com/8505/8441256181_4e98d8bff5_z_d.jpg"
            )
        )
        return parking_image
    }
}
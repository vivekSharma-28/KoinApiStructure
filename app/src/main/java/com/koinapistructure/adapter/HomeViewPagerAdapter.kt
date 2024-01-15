package com.koinapistructure.adapter

import com.bumptech.glide.Glide
import com.koinapistructure.R
import com.koinapistructure.response.ImageData
import com.makeramen.roundedimageview.RoundedImageView
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class HomeViewPagerAdapter(var banner: List<ImageData?>?, var listnerer: OnSubViewClickListener2) :
    BaseBannerAdapter<ImageData?>() {
    var mOnSubViewClickListener2: OnSubViewClickListener2? = null


    override fun bindData(
        holder: BaseViewHolder<ImageData?>, data: ImageData?, position: Int, pageSize: Int
    ) {
        val imageStart: RoundedImageView = holder.findViewById(R.id.iv_home_banner_img)

        //put this url in else case if it crashed here
        //https://as1.ftcdn.net/v2/jpg/03/35/51/06/1000_F_335510693_HY7mLg3ARdLccKoXk3m66NLDpJRJh51p.jpg

        data?.let { dataInside ->
//            holder.setOnClickListener(R.id.iv_banner_img) { view: View? ->
//                if (null != mOnSubViewClickListener) mOnSubViewClickListener!!.onViewClick(
//                    view, holder.adapterPosition , dataInside )
//            }
        }


        val bannerdata = banner?.get(position)?.value

        Glide.with(imageStart.context).load(bannerdata)
            .placeholder(R.drawable.ic_launcher_background).into(imageStart)



        imageStart.setOnClickListener {
            listnerer.click(position)
        }


    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_view_pager_banner
    }

    interface OnSubViewClickListener2 {
        // fun onViewClick(view: View?, position: Int, data: Banner)

        fun click(position: Int)
    }


}

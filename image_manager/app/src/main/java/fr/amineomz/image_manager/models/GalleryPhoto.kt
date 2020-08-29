package fr.amineomz.image_manager.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GalleryPhoto(val name: String, val user_email: String) : Parcelable {

    /*companion object {
        fun getGalleryPhoto() : Array<GalleryPhoto> {
            return  arrayOf<GalleryPhoto>(
                GalleryPhoto("Photo1","https://www.google.com/images/srpr/logo11w.png"),
                GalleryPhoto("Photo1","https://media.gettyimages.com/photos/eiffel-tower-in-paris-france-picture-id924894324?s=612x612"),
                GalleryPhoto("Photo1","https://www.lamodeenimages.com/sites/default/files-lmi/styles/1365x768/public/images/article/homepage/full/miss-dior-exposition-love-nroses-shanghai-2019-la-mode-en-images-cover2.jpg?itok=iDaxTcAu"),
                GalleryPhoto("Photo1","https://www.sciencesetavenir.fr/assets/img/2019/12/16/images_list-r4x3w1000-5df7bb299141e-10-04.jpg"),
                GalleryPhoto("Photo1","https://images.unsplash.com/photo-1503023345310-bd7c1de61c7d?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80"),
                GalleryPhoto("Photo1","https://www.journaldugeek.com/content/uploads/2019/05/clashbannerimage.jpg"),
                GalleryPhoto("Photo1","https://www.telerama.fr/sites/tr_master/files/styles/simplecrop1000/public/illustrations_league_legends_artists_66_0.jpg?itok=VOBt4nhd&sc=c4324097900b176eb5ecf5368cda9618"),
                GalleryPhoto("Photo1","https://static1.millenium.org/articles/0/34/82/10/@/1177665-annieversary-final-1-article_m-1.jpg"),
                GalleryPhoto("Photo1","https://images.contentstack.io/v3/assets/blt731acb42bb3d1659/bltbac3610b13b2a91b/5e22818ce3340f1154e34927/SS2020_YT_Thumbnail_Skins_TEXTLESS_v02.jpg"),
                GalleryPhoto("Photo1","https://www.geekplay.fr/wp-content/uploads/2018/04/kaisa-jugee-trop-sexualisee-par-la-communaute-de-league-of-legend-49189.jpg")
            )
        }
    }*/
}
package fr.amineomz.image_manager.graphics


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import fr.amineomz.image_manager.R
import fr.amineomz.image_manager.models.GalleryPhoto


class ImageGalleryAdapter(
    val galleryPhotos: Array<GalleryPhoto>,
    val itemOnClickListener: View.OnClickListener
) : RecyclerView.Adapter<ImageGalleryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView = itemView.findViewById<CardView>(R.id.cv_images)
        val imageView = cardView.findViewById<ImageView>(R.id.iv_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val photoView = inflater.inflate(R.layout.item_image, parent, false)
        return ViewHolder(photoView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val galleryPhoto = galleryPhotos[position]
        holder.cardView.setOnClickListener(itemOnClickListener)
        holder.cardView.tag = position
        downloadImage(holder.imageView, galleryPhoto)

        //DownloadImageTask(holder.imageView).execute(galleryPhoto.url)
    }

    override fun getItemCount(): Int {
        return galleryPhotos.size
    }

}
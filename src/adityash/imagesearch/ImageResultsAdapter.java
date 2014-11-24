package adityash.imagesearch;

import java.util.ArrayList;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageResultsAdapter extends ArrayAdapter<Image> {

	public ImageResultsAdapter(Context context, ArrayList<Image> images) {
		super(context, R.layout.image_result, images);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.image_result, parent, false);
	    }
		
		final Image img = (Image) getItem(position);
		ImageView ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);
		TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
		
		Picasso.with(getContext()).load(img.tbUrl).resize(150, 150).centerCrop().into(ivThumbnail);
		tvTitle.setText(img.title);
		
		ivThumbnail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getContext(), ImagePreviewActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra("url", img.url);
				getContext().startActivity(i);
			}
		});
		
		return convertView;
	}

}

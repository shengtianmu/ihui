package com.zpstudio.ui.game.puzzle;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zpstudio.R;

public class PuzzleGameGalleryAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<PuzzleGameCustomGallery> data = new ArrayList<PuzzleGameCustomGallery>();
	ImageLoader imageLoader;
	private boolean isActionMultiplePick;
	ColorMatrixColorFilter mGrayCf ;
	public PuzzleGameGalleryAdapter(Context c, ImageLoader imageLoader) {
		infalter = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = c;
		this.imageLoader = imageLoader;
		ColorMatrix matrix = new ColorMatrix();
		matrix.setSaturation(0);
		mGrayCf = new ColorMatrixColorFilter(matrix);
		// clearCache();
		
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public PuzzleGameCustomGallery getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setMultiplePick(boolean isMultiplePick) {
		this.isActionMultiplePick = isMultiplePick;
	}

	public void selectAll(boolean selection) {
		for (int i = 0; i < data.size(); i++) {
			data.get(i).isSeleted = selection;

		}
		notifyDataSetChanged();
	}

	public boolean isAllSelected() {
		boolean isAllSelected = true;

		for (int i = 0; i < data.size(); i++) {
			if (!data.get(i).isSeleted) {
				isAllSelected = false;
				break;
			}
		}

		return isAllSelected;
	}

	public boolean isAnySelected() {
		boolean isAnySelected = false;

		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isSeleted) {
				isAnySelected = true;
				break;
			}
		}

		return isAnySelected;
	}

	public ArrayList<PuzzleGameCustomGallery> getSelected() {
		ArrayList<PuzzleGameCustomGallery> dataT = new ArrayList<PuzzleGameCustomGallery>();

		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isSeleted) {
				dataT.add(data.get(i));
			}
		}

		return dataT;
	}

	public void addAll(ArrayList<PuzzleGameCustomGallery> files) {

		try {
			this.data.clear();
			this.data.addAll(files);

		} catch (Exception e) {
			e.printStackTrace();
		}

		notifyDataSetChanged();
	}
	public void changeSelection(View v, int position) {

		if (data.get(position).isSeleted) {
			data.get(position).isSeleted = false;
		} else {
			data.get(position).isSeleted = true;
		}

		((ViewHolder) v.getTag()).imgQueueMultiSelected.setSelected(data
				.get(position).isSeleted);
		
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {

			convertView = infalter.inflate(R.layout.puzzlegame_picker_item, null);
			holder = new ViewHolder();
			holder.imgQueue = (ImageView) convertView
					.findViewById(R.id.imgQueue);

			holder.imgQueueMultiSelected = (ImageView) convertView
					.findViewById(R.id.imgQueueMultiSelected);

			if (isActionMultiplePick) 
			{
				holder.imgQueueMultiSelected.setVisibility(View.VISIBLE);
			}
			else 
			{
				holder.imgQueueMultiSelected.setVisibility(View.GONE);
			}
			
			
			convertView.setTag(holder);

		} 
		else 
		{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.imgQueue.setTag(position);
		
		try {
			String imgPath ;
			imgPath = data.get(position).url;
			
			imageLoader.displayImage(imgPath,
					holder.imgQueue, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							holder.imgQueue.setImageResource(R.drawable.util_imagepicker_no_media);
							int position = (Integer)holder.imgQueue.getTag();
							data.get(position).isDownloaded = false;
							super.onLoadingStarted(imageUri, view);
						}
						
						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							// Empty implementation
							int position = (Integer)holder.imgQueue.getTag();
							data.get(position).isDownloaded = true;
							if(data.get(position).isTimelimited)
							{
								
								holder.imgQueue.setColorFilter(mGrayCf);
								holder.imgQueue.setEnabled(false);
							}
							else
							{
								holder.imgQueue.setColorFilter(null);
								holder.imgQueue.setEnabled(true);
							}
							super.onLoadingComplete(imageUri, view ,loadedImage);
						}
					});

			if (isActionMultiplePick) {

				holder.imgQueueMultiSelected
						.setSelected(data.get(position).isSeleted);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	public class ViewHolder {
		ImageView imgQueue;
		ImageView imgQueueMultiSelected;
	}


	public void clear() {
		data.clear();
		notifyDataSetChanged();
	}
	
	public interface OnSelectedListener
	{
		public void onSelected();
	}
}

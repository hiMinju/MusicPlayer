//package com.example.hw3;
//
//import android.content.Context;
//import android.net.Uri;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//
//public class MyAdapter extends BaseAdapter {
//    Context mContext = null;
//    LayoutInflater mLayoutInflater = null;
//    ArrayList<Music> music;
//    private final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
//
//    public MyAdapter(Context context, ArrayList<Music> data) {
//        mContext = context;
//        music = data;
//        mLayoutInflater = LayoutInflater.from(mContext);
//    }
//
//    @Override
//    public int getCount() {
//        return music.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return music.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    public void addItem(long trackId, long albumId, String title, String artist, String album, long mDuration, String dataPath) {
//        // item에 추가할 인자들 넣기
//        Music music = new Music(trackId, albumId, title, artist, album, dataPath, mDuration);
//
//    }
//
//    class ViewHolder {
//        ImageView mImage;
//        TextView mTitle;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View view = convertView;
//        ViewHolder viewHolder = null;
//        // 어댑터 뷰가 재사용할 뷰를 넘겨주지 않은 경우에만 새로운 뷰를 생성한다.
//        if(view == null) {
//            view = mLayoutInflater.inflate(R.layout.listview_item, null);
//
//            viewHolder = new ViewHolder();
//            // 현재 아이템에 내용을 변경할 뷰를 찾는다.
//            viewHolder.mImage = (ImageView)view.findViewById(R.id.imageView);
//            viewHolder.mTitle = (TextView)view.findViewById(R.id.title);
//        }
//        else {
//            viewHolder = (ViewHolder)view.getTag();
//        }
//        // 이름, 학번, 학과 데이터를 참조하여 레이아웃을 갱신한다.
//        viewHolder.mImage.setImageURI(music.get(position).getImageUri());
//        viewHolder.mTitle.setText(music.get(position).getTitle());
//
//        return view;
//    }
//}

package com.example.hw3;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView mListView = null;
    MyAdapter mAdapter = null;
    Music music = null;
    ArrayList<Music> musicList = null;

    Context mContext = null;

    String[] permission_list = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permission(); // permission request method

        mContext = this.getBaseContext();

        // 어댑터에 사용할 데이터 설정
        musicList = new ArrayList<>();
        mAdapter = new MyAdapter(this);

        // 리스트뷰에 어댑터 설정
        mListView = (ListView)findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);

        // the clickListener of listView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, activity_player.class);
//                intent.putExtra("music", (Serializable) music);
                intent.putExtra("music", musicList);
                Log.i("musicList", musicList.toString());
                startActivity(intent);
            }
        });

        loadAudio();
    }

    public void permission() {
        // 접근권한이 없을때(저장공간)
        if(PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {

            // 최초 권한 요청인지, 혹은 사용자에 의한 재요청인지 확인
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // 사용자가 임의로 권한을 취소한 경우
                // 권한 재요청
                Log.i("TAG", "권한 재요청");
                requestPermissions(permission_list, 0);
            }else {
                // 최초로 권한을 요청하는 경우(첫실행)
                Log.i("TAG", "권한 최초요청");
                requestPermissions(permission_list, 0);
            }
        } else { // 접근권한이 있을때
            Log.i("TAG", "접근 허용");
        }
    }

    class ViewHolder {
        ImageView mImage;
        TextView mTitle;
    }

    private class MyAdapter extends BaseAdapter {
        Context mContext = null;
        LayoutInflater mLayoutInflater;

        public MyAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return musicList.size();
        }

        @Override
        public Object getItem(int position) {
            return musicList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addItem(long trackId, long albumId, String title, String artist, String album, long mDuration, String dataPath) {
            // item에 추가할 인자들 넣기
            music = new Music(trackId, albumId, title, artist, album, dataPath, mDuration);
            musicList.add(music);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder = null;
            // 어댑터 뷰가 재사용할 뷰를 넘겨주지 않은 경우에만 새로운 뷰를 생성한다.
            if(view == null) {
                mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = mLayoutInflater.inflate(R.layout.listview_item, parent, false);

                viewHolder = new ViewHolder();
                // 현재 아이템에 내용을 변경할 뷰를 찾는다.
                viewHolder.mImage = (ImageView)view.findViewById(R.id.imageView);
                viewHolder.mTitle = (TextView)view.findViewById(R.id.title);

                view.setTag(viewHolder); // itemLayout에 viewHolder를 등록함
            }
            else {
                viewHolder = (ViewHolder)view.getTag();
            }
            // 이름, 학번, 학과 데이터를 참조하여 레이아웃을 갱신한다.
//            viewHolder.mImage.setImageURI(musicList.get(position).getImageUri());
            viewHolder.mTitle.setText(musicList.get(position).getTitle());

            // musicList에서 position에 위차한 데이터 참조 획득
            final Music musicItem = musicList.get(position);

            // 아이템 내 각 위젯에 데이터 반영
            Bitmap albumArt = MainActivity.getArtworkQuick(mContext, (int)musicItem.getAlbumId(), 100, 100);
            if(albumArt != null) {
                viewHolder.mImage.setImageBitmap(albumArt);
            }

            return view;
        }
    }

    /* Album ID로 부터 Bitmap 이미지를 생성해 리턴해 주는 메소드 */
    private static final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    // Get album art for specified album. This method will not try to
    // fall back to getting artwork directly from the file, nor will it attempt to repair the database.
    private static Bitmap getArtworkQuick(Context context, int album_id, int w, int h) {
        w -= 2;
        h -= 2;
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        if (uri != null) {
            ParcelFileDescriptor fd = null;
            try {
                fd = res.openFileDescriptor(uri, "r");
                int sampleSize = 1;

                sBitmapOptionsCache.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(
                        fd.getFileDescriptor(), null, sBitmapOptionsCache);
                int nextWidth = sBitmapOptionsCache.outWidth >> 1;
                int nextHeight = sBitmapOptionsCache.outHeight >> 1;
                while (nextWidth>w && nextHeight>h) {
                    sampleSize <<= 1;
                    nextWidth >>= 1;
                    nextHeight >>= 1;
                }

                sBitmapOptionsCache.inSampleSize = sampleSize;
                sBitmapOptionsCache.inJustDecodeBounds = false;
                Bitmap b = BitmapFactory.decodeFileDescriptor(
                        fd.getFileDescriptor(), null, sBitmapOptionsCache);

                if (b != null) {
                    // finally rescale to exactly the size we need
                    if (sBitmapOptionsCache.outWidth != w || sBitmapOptionsCache.outHeight != h) {
                        Bitmap tmp = Bitmap.createScaledBitmap(b, w, h, true);
                        b.recycle();
                        b = tmp;
                    }
                }

                return b;
            } catch (FileNotFoundException e) {
            } finally {
                try {
                    if (fd != null)
                        fd.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    private void loadAudio() {
        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);
        cursor.moveToFirst();

        Log.i("num of files", Integer.toString(cursor.getCount()));
        System.out.println("음악파일 개수 = " + cursor.getCount());
        if (cursor != null && cursor.getCount() > 0) {
            do {
                long trackId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                long mDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String dataPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String info = "mId = " + trackId + " albumId = " +albumId+ " title : "+title+" album : "+album+" artist: "+artist+" 총시간 : "+mDuration;
                Log.i("info", info);
                // Save to audioList
                mAdapter.addItem(trackId,albumId,title,artist,album,mDuration,dataPath);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private String[] readFile() {
        Uri externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.DATA
        };

        Cursor cursor = getContentResolver().query(externalUri, projection, null, null, null);

        if(cursor == null || !cursor.moveToFirst()) {
            Log.e("TAG", "cursor null or empty");
            return null;
        }

        do {
            String contentUrl = externalUri.toString() + "/" + cursor.getString(0);
            try {
                InputStream stream = getContentResolver().openInputStream(Uri.parse(contentUrl));
                int data = 0;
                StringBuilder builder = new StringBuilder();

                while((data=stream.read()) != -1) {
                    builder.append((char)data);
                }
                stream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (cursor.moveToNext());
        return projection;
    }
}

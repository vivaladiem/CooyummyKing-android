package com.coo.y2.cooyummyking.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.coo.y2.cooyummyking.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by Y2 on 2015-05-21.
 */
public class ToolDetailEditorPageFragment extends Fragment {
    int mPosition;
    private String mImageUrl;
    private String mInstruction;
    private DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY)
            .considerExifParams(true)
            .build();

    private ImageView mImageView;

    public static ToolDetailEditorPageFragment newInstance(int position) {
        // 왜 이렇게 하는건지 까먹었다.. 그냥 일반 생성자에서 해도 될텐데.
        // Set Bundle
        ToolDetailEditorPageFragment fragment = new ToolDetailEditorPageFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }
    //TODO MainImageNum 등 포함 데이터 제대로 관리(지금처럼 ToolFragment에 static으로 하는거 나쁘지않은듯. 굳이 새로운 클래스 만들어봐야 자원소모만 많음. 아직은 필요없음
    // 크롭은 그냥 보류하자.
    // 스크롤 문제
    // 저장기능 구현
    // 하단바 메뉴들 구현
    // 임시저장기능 구현

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments().getInt("position");

        mImageUrl = ToolFragment.sImageUrls.get(mPosition);
        mInstruction = ToolFragment.sInstructions.get(mPosition);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tool_detail_editor_viewpager, container, false);
        mImageView = (ImageView) v.findViewById(R.id.tool_detail_editor_image);
        EditText tv = (EditText) v.findViewById(R.id.tool_detail_editor_text);
        TextView tvTagNum = (TextView)  v.findViewById(R.id.tool_detail_editor_tag_num);

        ImageLoader.getInstance().displayImage("file://" + mImageUrl, mImageView, mOptions);
        tv.setText(mInstruction);
        tvTagNum.setText(String.valueOf(mPosition + 1));

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mImageView.getDrawable().setCallback(null);
        mImageView.setImageDrawable(null);
    }
}

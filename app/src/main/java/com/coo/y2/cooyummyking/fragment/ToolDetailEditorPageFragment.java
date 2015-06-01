package com.coo.y2.cooyummyking.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import com.coo.y2.cooyummyking.entity.RecipeDesign;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Y2 on 2015-05-21.
 * 레시피 상세 편집
 */
public class ToolDetailEditorPageFragment extends Fragment {
    int mPosition;
    private String mImageUrl;
    private String mInstruction;
    private RecipeDesign mRecipe = RecipeDesign.getDesign();

    private ImageView mImageView;
    private EditText mEdInstruction;

    private DisplayImageOptions mOptions;

    public static ToolDetailEditorPageFragment newInstance(int position, DisplayImageOptions options) {
        ToolDetailEditorPageFragment fragment = new ToolDetailEditorPageFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);

        fragment.mOptions = options;
        return fragment;
    }

    // 하단바 메뉴들 구현

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments().getInt("position");

        mImageUrl = mRecipe.getImagePath(mPosition);
        mInstruction = mRecipe.instructions.get(mPosition);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tool_detail_editor_viewpager, container, false);
        mImageView = (ImageView) v.findViewById(R.id.tool_detail_editor_image);
        mEdInstruction = (EditText) v.findViewById(R.id.tool_detail_editor_text);
        TextView tvTagNum = (TextView)  v.findViewById(R.id.tool_detail_editor_tag_num);

        ImageLoader.getInstance().displayImage("file://" + mImageUrl, mImageView, mOptions);
        mEdInstruction.setText(mInstruction);
        tvTagNum.setText(String.valueOf(mPosition + 1));

        if (mRecipe.mainImageIndex == mPosition) v.findViewById(R.id.tool_detail_editor_tag_main).setVisibility(View.VISIBLE);

        // ToolDetailEditorFragment에서 page의 View에 접근하기 위해 Tag를 설정합니다.(findViewByTag사용)
        mImageView.setTag("iv" + mPosition);
        mEdInstruction.setTag("ed" + mPosition);

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();

        // Save instruction text and image
        String inst;
        if ((inst = mEdInstruction.getText().toString()).equals(mRecipe.instructions.get(mPosition))) return;
        mRecipe.instructions.set(mPosition, inst);
        mRecipe.isChanged = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        returnBitmapMemory(mImageView);
        mImageView = null;

    }

    private void returnBitmapMemory(ImageView v) {
        Drawable drawable;
        if ((drawable = v.getDrawable()) != null) {
            Bitmap bm = ((BitmapDrawable) drawable).getBitmap();
            v.setImageBitmap(null);
            drawable.setCallback(null); // callback이 남아있어서 비트맵이 반환되지 않는다는 말도 있는데 항상 그런진 모르겠다.
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
                // 허니콤부터는 비트맵 참조만 없어져도 메모리가 반환됐는데 그 이전에는 recycle 해줘야함.
                try {
                    bm.recycle();
                } catch (Exception e) { }
            }
        }
    }
}

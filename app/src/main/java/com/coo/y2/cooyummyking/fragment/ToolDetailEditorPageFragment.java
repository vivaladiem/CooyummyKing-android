package com.coo.y2.cooyummyking.fragment;

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
import com.coo.y2.cooyummyking.entity.Recipe;
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
    private Recipe mRecipe = Recipe.getScheme();

    private ImageView mImageView;
    private EditText mEdInstruction;

    private DisplayImageOptions mOptions;
//    private View.OnKeyListener mKeyListener;

    public static ToolDetailEditorPageFragment newInstance(int position, DisplayImageOptions options) {
        ToolDetailEditorPageFragment fragment = new ToolDetailEditorPageFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);

        fragment.mOptions = options;
//        fragment.mKeyListener = keyListener;
        return fragment;
    }

    // 저장기능 구현
    // 하단바 메뉴들 구현
    // 임시저장기능 구현

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments().getInt("position");

        mImageUrl = Recipe.imagePaths.get(mPosition);
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

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();

        // Save instruction text and image
        String inst;
        if ((inst = mEdInstruction.getText().toString()).equals(mRecipe.instructions.get(mPosition))) return;
        mRecipe.instructions.set(mPosition, inst);
        Recipe.isChanged = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mImageView.getDrawable() != null) mImageView.getDrawable().setCallback(null);
        mImageView.setImageDrawable(null);
        mImageView = null;

    }
}

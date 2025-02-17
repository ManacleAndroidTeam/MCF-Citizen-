package com.citizen.fmc.fragment;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.citizen.fmc.R;
import com.citizen.fmc.fresco.zoomable.ZoomableDraweeView;
import com.citizen.fmc.utils.Constants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;

/**
 * ======> Created by dheeraj-gangwar on 2018-04-21 <======
 */

public class ZoomableFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_zoomable, container, false);
        if (getArguments() != null) {
            String imagePath = getArguments().getString(Constants.KEY_IMAGE_PATH);
            if (imagePath != null) {
                ZoomableDraweeView view = mView.findViewById(R.id.zoomable);
                DraweeController ctrl = Fresco.newDraweeControllerBuilder().setUri(Uri.parse(imagePath))
                        .setTapToRetryEnabled(true).build();
                GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                        .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                        .setProgressBarImage(new ProgressBarDrawable())
                        .build();
                view.setController(ctrl);
                view.setHierarchy(hierarchy);
            }
        }

        return mView;
    }
}

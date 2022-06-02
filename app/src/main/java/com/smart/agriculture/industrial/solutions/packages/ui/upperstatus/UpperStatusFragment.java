package com.smart.agriculture.industrial.solutions.packages.ui.upperstatus;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;

import com.smart.agriculture.industrial.solutions.packages.R;
import com.smart.agriculture.industrial.solutions.packages.databinding.UpperStatusFragmentBinding;
import com.smart.agriculture.industrial.solutions.packages.models.GeoFence;
import com.smart.agriculture.industrial.solutions.packages.utils.interfaces.GeoFenceInterface;

public class UpperStatusFragment extends Fragment {
    private UpperStatus upperStatus;
    private static final String TAG = "UpperStatusFragment";
    private GeoFenceInterface geoFenceInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        upperStatus = new UpperStatus();
        UpperStatusFragmentBinding upperStatusFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.upper_status_fragment, null, false);
        View view = upperStatusFragmentBinding.getRoot();
        upperStatusFragmentBinding.setUpper(upperStatus);
        return view;
    }


    public void updateFromField(GeoFence geoFenceModel) {
        if (geoFenceModel == null)
            upperStatus.setTextViewFrom("");
        else
            upperStatus.setTextViewFrom(geoFenceModel.getDescription());
    }

    public void updateToField(GeoFence geoFenceModel) {
        if (geoFenceModel == null)
            upperStatus.setTextViewTo("");
        else
            upperStatus.setTextViewTo(geoFenceModel.getDescription());

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        geoFenceInterface = (GeoFenceInterface) context;
    }

    public class UpperStatus extends BaseObservable {
        String textViewFrom;
        String textViewTo;

        @Bindable
        public String getTextViewFrom() {
            return textViewFrom;
        }

        public void setTextViewFrom(String textViewFrom) {
            this.textViewFrom = textViewFrom;
            notifyPropertyChanged(BR.textViewFrom);
        }

        @Bindable
        public String getTextViewTo() {
            return textViewTo;
        }

        public void setTextViewTo(String textViewTo) {
            this.textViewTo = textViewTo;
            notifyPropertyChanged(BR.textViewTo);
        }
    }


}

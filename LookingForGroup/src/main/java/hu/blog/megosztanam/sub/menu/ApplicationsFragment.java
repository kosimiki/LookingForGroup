package hu.blog.megosztanam.sub.menu;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.lookingforgroup.R;

/**
 * Created by Miklós on 2017. 04. 19..
 */
public class ApplicationsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.applications_sub_menu, container, false);

        return rootView;
    }
}

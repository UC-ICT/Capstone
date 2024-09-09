package kr.ac.uc.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class ButtonFragment extends Fragment {

    // 액티비티와 상호작용할 인터페이스 정의
    public interface OnButtonClickListener {
        void onButtonClicked(int buttonId);
    }

    private OnButtonClickListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // 프래그먼트를 액티비티에 연결하고, 액티비티가 인터페이스를 구현하는지 확인
        if (context instanceof OnButtonClickListener) {
            mListener = (OnButtonClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnButtonClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 프래그먼트 레이아웃을 인플레이트합니다.
        View view = inflater.inflate(R.layout.button_fragment, container, false);

        // 프래그먼트에서 버튼을 찾고 클릭 이벤트를 설정합니다.
        Button btnHome = view.findViewById(R.id.btnHome);
        Button btnDiary = view.findViewById(R.id.btnDiary);
        Button btnPlant = view.findViewById(R.id.btnPlant);
        Button btnGame = view.findViewById(R.id.btnGame);

        // 각 버튼의 클릭 이벤트에서 mListener를 사용하여 버튼 ID를 액티비티로 전달합니다.
        btnHome.setOnClickListener(v -> mListener.onButtonClicked(v.getId()));
        btnDiary.setOnClickListener(v -> mListener.onButtonClicked(v.getId()));
        btnPlant.setOnClickListener(v -> mListener.onButtonClicked(v.getId()));
        btnGame.setOnClickListener(v -> mListener.onButtonClicked(v.getId()));

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // 프래그먼트가 분리될 때 리스너를 null로 설정하여 메모리 누수를 방지합니다.
        mListener = null;
    }
}

package com.uu.udemo.transition;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.uu.udemo.R;

public class TransitionActivity extends AppCompatActivity {

    Button btnStart;
    ViewGroup mSceneRoot;
    Scene mAScene;
    Scene mAnotherScene;
    TextView textView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        init();
    }

    private void init() {
        textView1 = (TextView) findViewById(R.id.text_view1);
        mSceneRoot = (ViewGroup) findViewById(R.id.scene_root);
        mAScene = Scene.getSceneForLayout(mSceneRoot, R.layout.a_scene, this);
        mAnotherScene =
                Scene.getSceneForLayout(mSceneRoot, R.layout.another_scene, this);

        btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transition transition = new AutoTransition();
//                transition.addTarget(textView1);
                Transition complex = TransitionInflater.from(TransitionActivity.this).inflateTransition(R.transition.sdsd);
                TransitionManager.go(mAnotherScene, complex);
            }
        });
    }
}

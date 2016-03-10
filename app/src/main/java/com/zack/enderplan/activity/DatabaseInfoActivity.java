package com.zack.enderplan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.manager.TypeManager;
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.bean.Type;
import com.zack.enderplan.util.Util;

import java.text.SimpleDateFormat;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class DatabaseInfoActivity extends BaseActivity {

    private static final String CLASS_NAME = "DatabaseInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CircleImageView typeMark = (CircleImageView) findViewById(R.id.type_mark);
        TextView content = (TextView) findViewById(R.id.content);
        TextView typeName = (TextView) findViewById(R.id.type_name);
        TextView creationTime = (TextView) findViewById(R.id.creation_time);
        TextView deadline = (TextView) findViewById(R.id.deadline);

        setSupportActionBar(toolbar);
        setupActionBar();

        Intent intent = getIntent();
        Plan plan = intent.getParcelableExtra("plan_detail");

        //TypeManager typeManager = new TypeManager(this);

        boolean isCompleted = plan.getCompletionTime() != 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd EEE HH:mm", Locale.getDefault());

        /*typeMark.setImageResource(typeManager.findColorIdByTypeMark(isCompleted ?
                Type.TYPE_MARK_GREY : typeManager.convertTypeCode(plan.getTypeCode(),
                TypeManager.TYPE_MARK_CODE)));
        content.setText(isCompleted ? Util.addStrikethroughSpan(plan.getContent()) : plan.getContent());
        typeName.setText(typeManager.convertTypeCode(plan.getTypeCode(), TypeManager.TYPE_NAME_CODE));*/
        creationTime.setText(simpleDateFormat.format(plan.getCreationTime()));
        deadline.setText(plan.getDeadline() == 0 ? getResources().getString(R.string.unsettled) :
                simpleDateFormat.format(plan.getDeadline()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

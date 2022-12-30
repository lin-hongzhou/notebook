package com.example.notebook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends BaseActivity {

    EditText et;

    private Toolbar myToolbar;

    private String old_content = "";
    private String old_time = "";
    private int old_Tag = 1;
    private long id = 0;
    private int openMode = 0;
    private int tag = 1;
    private boolean tagChange = false;
    public Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_layout);
        et = findViewById(R.id.et);

        //修改顶部样式
        myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //设置toolbar取代actionbar
        //返回键
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoSetMessage();
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        Intent getIntent = getIntent();
        openMode = getIntent.getIntExtra("mode",0);

        //打开已经存在的note
        if (openMode == 3) {
            id = getIntent.getLongExtra("id", 0);
            old_content = getIntent.getStringExtra("content");
            old_time = getIntent.getStringExtra("time");
            old_Tag = getIntent.getIntExtra("tag", 1);
            et.setText(old_content);
            et.setSelection(old_content.length());
        }
    }

    @Override
    protected void needRefresh() {
        setNightMode();
        startActivity(new Intent(this, EditActivity.class));
        overridePendingTransition(R.anim.night_switch, R.anim.night_switch_over);
        finish();
    }

    /**
     *  按钮监控
     *  返回则把数据保存
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME){
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_BACK) {
            autoSetMessage();
            setResult(RESULT_OK,intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    /**
     * openMode == 4，对数据进行增改
     * 把数据返回给 Intent通道
     */
    public void autoSetMessage() {
        if (openMode == 4) {    //openMode == 4 .表示新建新的笔记
            if (et.getText().toString().length() == 0) {
                //如果没有添加内容，返回 mode = -1，表示不需要操作
                intent.putExtra("mode",-1);
            }
            else {
                //将增加的内容传递进去，返回 mode = 0，表示新增
                intent.putExtra("mode",0);
                intent.putExtra("content",et.getText().toString());
                intent.putExtra("time",dateToStr());
                intent.putExtra("tag",tag);
            }
        }
        else { //点击原有note修改 openMode ！= 4
            //如果修改的内容相等，则返回 mode = -1.表示没有修改
            if (et.getText().toString().equals(old_content)&&!tagChange) {
                intent.putExtra("mode",-1);
            }
            else {
                //将修改的内容传递进去，返回 mode = 1，表示修改内容
                intent.putExtra("mode", 1); //edit the content
                intent.putExtra("content", et.getText().toString());
                intent.putExtra("time", dateToStr());
                intent.putExtra("id", id);
                intent.putExtra("tag", tag);
            }
        }
    }

    //引入edit_menu.xml文件
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //菜单栏delete删除键
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.delete:
                new AlertDialog.Builder(EditActivity.this)
                        .setMessage("是否删除？")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (openMode == 4) {
                                    intent.putExtra("mode",-1);
                                    setResult(RESULT_OK,intent);
                                }
                                else {
                                    intent.putExtra("mode",2); //删除note
                                    intent.putExtra("id",id);
                                    setResult(RESULT_OK,intent);
                                }
                                finish();
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //把数据库的时间戳转化成我们想要的格式
    public String dateToStr(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }
}

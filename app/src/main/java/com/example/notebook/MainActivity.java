package com.example.notebook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.example.notebook.setting.UserSettingsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private NoteDatabase dbHelper;

    final String TAG = "tag";
    FloatingActionButton btn;
    private ListView lv;
    private NoteAdapter adapter;
    private Context context = this;
    private List<Note> noteList = new ArrayList<Note>();
    private Toolbar myToolbar;

    //弹出菜单
    private PopupWindow popupWindow;
    private PopupWindow popupCover;
    private ViewGroup customView;
    private ViewGroup coverView;
    private LayoutInflater layoutInflater;
    private RelativeLayout main;
    private WindowManager wm;
    private DisplayMetrics metrics;

    //设置
    ImageView setting_image;
    TextView setting_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (FloatingActionButton) findViewById(R.id.fab);
        lv = findViewById(R.id.lv);
        myToolbar = findViewById(R.id.myToolbar);

        adapter = new NoteAdapter(getApplicationContext(), noteList);
        refreshListView();
        lv.setAdapter(adapter);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //设置toolbar取代actionbar

        //菜单弹窗
        initPopUpView();
        if (super.isNightMode())
            myToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        else
            myToolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpView();
            }
        });

        //笔记列表
        lv.setOnItemClickListener(this);

        //添加笔记按钮
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,EditActivity.class);
                intent.putExtra("mode",4);
                startActivityForResult(intent,0);
            }
        });

    }

    @Override
    protected void needRefresh() {
        setNightMode();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("opMode", 10);
        startActivity(intent);
        overridePendingTransition(R.anim.night_switch, R.anim.night_switch_over);
        if (popupWindow.isShowing()) popupWindow.dismiss();
        finish();
    }

    public void initPopUpView() {
        layoutInflater = (LayoutInflater)MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        customView = (ViewGroup) layoutInflater.inflate(R.layout.setting_layout,null);
        coverView = (ViewGroup) layoutInflater.inflate(R.layout.setting_cover,null);
        main = findViewById(R.id.main_layout);
        wm = getWindowManager();
        metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
    }

    public void showPopUpView() {
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        popupCover = new PopupWindow(coverView,width,height,false);
        popupWindow = new PopupWindow(customView,(int)(width * 0.7),height, true);
        //修改弹窗的颜色
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        //在主界面加载成功之后 显示弹出
        findViewById(R.id.main_layout).post(new Runnable() {    //等待main_layout加载完，再show popupwindow
            @Override
            public void run() {
                popupCover.showAtLocation(main, Gravity.NO_GRAVITY,0,0);
                popupWindow.showAtLocation(main, Gravity.NO_GRAVITY,0,0);

                //设置按钮
                setting_text = customView.findViewById(R.id.setting_settings_text);
                setting_image = customView.findViewById(R.id.setting_settings_image);
                setting_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, UserSettingsActivity.class));
                        overridePendingTransition(R.anim.in_lefttoright, R.anim.no);

                    }
                });
                setting_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, UserSettingsActivity.class));
                        overridePendingTransition(R.anim.in_lefttoright, R.anim.no);

                    }
                });

                coverView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popupWindow.dismiss();
                        return true;
                    }
                });

                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        popupCover.dismiss();
                    }
                });
            }
        });
    }

    //接受startActivityForResult的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        int returnMode;
        long note_Id;
        returnMode = data.getExtras().getInt("mode",-1);
        note_Id = data.getExtras().getLong("id",0);

        if (returnMode == 1) {  //修改笔记
            String content = data.getExtras().getString("content");
            String time = data.getExtras().getString("time");
            int tag = data.getExtras().getInt("tag", 1);
            Note newNote = new Note(content, time, tag);
            newNote.setId(note_Id);
            CRUD op = new CRUD(context);
            op.open();
            op.updateNote(newNote);
            op.close();
        } else if (returnMode == 0) { //增加笔记
            String content = data.getExtras().getString("content");
            String time = data.getExtras().getString("time");
            int tag = data.getExtras().getInt("tag", 1);
            Note newNote = new Note(content, time, tag);
            CRUD op = new CRUD(context);
            op.open();
            op.addNote(newNote);
            op.close();
        }else if (returnMode == 2) { //删除单个笔记
            Note curNote = new Note();
            curNote.setId(note_Id);
            CRUD op = new CRUD(context);
            op.open();
            op.removeNote(curNote);
            op.close();
        }
        else if (returnMode == -1) { }
        refreshListView();
        super.onActivityResult(requestCode, resultCode, data);
    }

    //菜单栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);

        //菜单栏搜索
        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();

        mSearchView.setQueryHint("Search");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    //删除所有笔记
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("是否删除全部笔记？")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper =new NoteDatabase(context);
                                SQLiteDatabase db =dbHelper.getWritableDatabase();
                                db.delete("notes",null,null);
                                db.execSQL("update sqlite_sequence set seq=0 where name='notes'");
                                refreshListView();
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

    //刷新页面
    public void refreshListView() {
        CRUD op = new CRUD(context);
        op.open();
        if (noteList.size() > 0)
            noteList.clear();
        //添加获取数据库的内容
        noteList.addAll(op.getAllNote());
        op.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch(parent.getId()) {
            case R.id.lv:
                Note curNote = (Note) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("content", curNote.getContent());
                intent.putExtra("id", curNote.getId());
                intent.putExtra("time", curNote.getTime());
                intent.putExtra("mode", 3);     // MODE of 'click to edit'
                intent.putExtra("tag", curNote.getTag());
                startActivityForResult(intent, 1);      //collect data from edit
                break;
        }
    }
}

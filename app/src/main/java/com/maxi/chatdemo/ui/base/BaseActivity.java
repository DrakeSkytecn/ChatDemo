package com.maxi.chatdemo.ui.base;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.maxi.chatdemo.R;
import com.maxi.chatdemo.common.ChatConst;
import com.maxi.chatdemo.db.ChatDbManager;
import com.maxi.chatdemo.db.ChatMessageBean;
import com.maxi.chatdemo.utils.ScreenUtil;
import com.maxi.chatdemo.widget.ChatBottomView;
import com.maxi.chatdemo.widget.HeadIconSelectorView;
import com.maxi.chatdemo.widget.pulltorefresh.PullToRefreshLayout;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public abstract class BaseActivity extends Activity {

    //玩家的選項放在這裏
    public String[][] player_choice_short = {
            {""},
            {"just come out from the toilet"},
            {"I thought you will be waiting"},
            {"ok"},
            {"left", "right"},
            {"left", "right"},
            {"left", "right"}
    };

    //玩家的Plot放在這裏
    public String[][] player_choice = {
            {""},
            {"剛從廁所出來"},
            {"我以為你會等"},
            {"好的"},
            {"left", "right"},
            {"left", "right"},
            {"left", "right"}
    };

    //媽媽的Plot放在這裏
    public String[][][] mom_answer = {
            {{"你在哪裡？"}},
            {{"你為什麼不告訴我？"}},
            {{"...無論如何，你現在在售貨亭見面嗎？"}},
            {{"hello111111"}, {"hahahahaha", "hahahahaha"}},
            {{"hello111111"}, {"hahahahaha", "hahahahaha"}},
            {{"hello111111"}, {"hahahahaha", "hahahahaha"}},
            {{"hello111111"}, {"hahahahaha", "hahahahaha"}},
    };

    public String[][][] getMom_answer() {
        return mom_answer;
    }

    int choice;

    public int getChoice() {
        return choice;
    }

    int plot_index = 0;

    public void setPlot_index(int plot_index) {
        if (plot_index < player_choice_short.length -1) {
            this.plot_index = plot_index;
        }
    }

    public int getPlot_index() {
        return plot_index;
    }

    public PullToRefreshLayout pullList;
    public boolean isDown = false;
    private boolean CAN_WRITE_EXTERNAL_STORAGE = true;
    private boolean CAN_RECORD_AUDIO = true;
    public int position;
    public int bottomStatusHeight = 0;
    private Button send;
    private ChatBottomView tbbv;
    private Button choice1;
    private Button choice2;
    public View mess_et_click;
    public EditText mEditTextContent;
    private File mCurrentPhotoFile;
//    public View activityRootView;
    private Toast mToast;
    public String userName = "test";
    private String camPicPath;
    public List<ChatMessageBean> tblist = new ArrayList<>();
    private List<String> reslist;
    public ChatDbManager mChatDbManager;
    public int page = 0;
    public int number = 10;
    public List<ChatMessageBean> pagelist = new ArrayList<>();
    public ArrayList<String> imageList = new ArrayList<>();
    public HashMap<Integer, Integer> imagePosition = new HashMap<>();
    private static final int SDK_PERMISSION_REQUEST = 127;
    private static final int IMAGE_SIZE = 100 * 1024;
    public static final int SEND_OK = 0x1110;
    public static final int REFRESH = 0x0011;
    public static final int RECERIVE_OK = 0x1111;
    public static final int PULL_TO_REFRESH_DOWN = 0x0111;

    protected BaseActivity() {

    }


    protected abstract void sendMessage();



    protected abstract void loadRecords();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        findView();
        initpop();
        init();
//        getPersimmions();
    }

    @Override
    protected void onDestroy() {
        cancelToast();
        super.onDestroy();
    }

    protected void findView() {
        pullList = (PullToRefreshLayout) findViewById(R.id.content_lv);
//        activityRootView = findViewById(R.id.layout_tongbao_rl);
        mEditTextContent = (EditText) findViewById(R.id.mess_et);
        mess_et_click = findViewById(R.id.mess_et_click);
        send = findViewById(R.id.send);
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            mEditTextContent.setInputType(InputType.TYPE_NULL);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setSoftInputShownOnFocus;
                setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setSoftInputShownOnFocus.setAccessible(true);
                setSoftInputShownOnFocus.invoke(mEditTextContent, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        tbbv = (ChatBottomView) findViewById(R.id.other_lv);
        choice1 = tbbv.getImageGroup();
        choice2 = tbbv.getCameraGroup();
        setPlot();
        initActionBar();
    }

    protected void setPlot() {
        if (plot_index < player_choice_short.length) {
            if (player_choice_short[plot_index].length == 1) {
                choice2.setVisibility(View.GONE);
                choice1.setText(player_choice_short[plot_index][0]);
            } else {
                choice2.setVisibility(View.VISIBLE);
                choice1.setText(player_choice_short[plot_index][0]);
                choice2.setText(player_choice_short[plot_index][1]);
            }
        } else {
            plot_index = 0;
        }
    }

    protected void init() {
        mEditTextContent.setOnKeyListener(onKeyListener);
        mChatDbManager = new ChatDbManager();
        PullToRefreshLayout.pulltorefreshNotifier pullNotifier = new PullToRefreshLayout.pulltorefreshNotifier() {
            @Override
            public void onPull() {
                                downLoad();
            }
        };
        mess_et_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tbbv.getVisibility() == View.GONE
                        ) {
                    mEditTextContent.setVisibility(View.VISIBLE);
                    tbbv.setVisibility(View.VISIBLE);
                } else {
                    tbbv.setVisibility(View.GONE);
                }
            }
        });
        pullList.setpulltorefreshNotifier(pullNotifier);
        tbbv.setOnHeadIconClickListener(new HeadIconSelectorView.OnHeadIconClickListener() {

            @SuppressLint("InlinedApi")
            @Override
            public void onClick(int from) {
                switch (from) {
                    case ChatBottomView.FROM_GALLERY:
                        mEditTextContent.setText(player_choice[plot_index][0]);
                        choice = 0;
                        break;
                    case ChatBottomView.FROM_CAMERA:
                        mEditTextContent.setText(player_choice[plot_index][1]);
                        choice = 1;
                        break;

                }
            }

        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mEditTextContent.getText().toString().isEmpty()) {
                    mess_et_click.callOnClick();
                    sendMessage();
//                    if(plot_index<player_choice_short.length-1) {
//                        plot_index++;
//                        setPlot();
//                    }
                }
            }
        });
        reslist = getExpressionRes(40);

        List<View> views = new ArrayList<View>();
        View gv1 = getGridChildView(1);
        View gv2 = getGridChildView(2);
        views.add(gv1);
        views.add(gv2);

        mEditTextContent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tbbv.setVisibility(View.GONE);
            }

        });

        bottomStatusHeight = ScreenUtil.getNavigationBarHeight(this);

        page = (int) mChatDbManager.getPages(number);
        loadRecords();
    }

    private void initActionBar() {
        if (getActionBar() == null) {
            return;
        }
        getActionBar().setCustomView(R.layout.layout_action_bar);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ivLeft:
                        doLeft();
                        break;
                    case R.id.ivRight:
                        doRight();
                        break;
                    case R.id.llRight:
                        doRight();
                        break;
                }
            }
        };
        getActionBar().getCustomView().findViewById(R.id.ivLeft).setOnClickListener(listener);
        getActionBar().getCustomView().findViewById(R.id.ivRight).setOnClickListener(listener);
        getActionBar().getCustomView().findViewById(R.id.llRight).setOnClickListener(listener);
        ((TextView) getActionBar().getCustomView().findViewById(R.id.tvTitle)).setText(getTitle().toString());
    }

//    @TargetApi(23)
//    protected void getPersimmions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            ArrayList<String> permissions = new ArrayList<String>();
//
//            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            }
//
//            if (addPermission(permissions, Manifest.permission.RECORD_AUDIO)) {
//            }
//            if (permissions.size() > 0) {
//                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
//            }
//        }
//    }

//    @TargetApi(23)
//    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
//        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
//            if (shouldShowRequestPermissionRationale(permission)) {
//                return true;
//            } else {
//                permissionsList.add(permission);
//                return false;
//            }
//
//        } else {
//            return true;
//        }
//    }

//    @TargetApi(23)
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case SDK_PERMISSION_REQUEST:
//                Map<String, Integer> perms = new HashMap<String, Integer>();
//
//                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
//                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
//
//                for (int i = 0; i < permissions.length; i++)
//                    perms.put(permissions[i], grantResults[i]);
//
//                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//                    CAN_WRITE_EXTERNAL_STORAGE = false;
//                }
//                if (perms.get(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                    CAN_RECORD_AUDIO = false;
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }

    public void setTitle(CharSequence title) {
        ((TextView) getActionBar().getCustomView().findViewById(R.id.tvTitle)).setText(title);
    }

    protected void doLeft() {
        finish();
    }

    protected void doRight() {

    }

    @SuppressLint({"NewApi", "InflateParams"})
    private void initpop() {
    }

    private void downLoad() {
        if (!isDown) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    loadRecords();
                }
            }).start();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            tbbv.setVisibility(View.GONE);
//            switch (requestCode) {
//                case ChatBottomView.FROM_CAMERA:
//                    FileInputStream is = null;
//                    try {
//                        is = new FileInputStream(camPicPath);
//                        File camFile = new File(camPicPath);
//                        if (camFile.exists()) {
//                            int size = ImageCheckoutUtil
//                                    .getImageSize(ImageCheckoutUtil
//                                            .getLoacalBitmap(camPicPath));
//                            if (size > IMAGE_SIZE) {
//                            } else {
//
//                            }
//                        }
//                    } catch (FileNotFoundException e) {
//                                                e.printStackTrace();
//                    } finally {
//
//                        try {
//                            is.close();
//                        } catch (IOException e) {
//                                                        e.printStackTrace();
//                        }
//                    }
//                    break;
//            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void reset() {
        tbbv.setVisibility(View.GONE);

    }

    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    private View getGridChildView(int i) {
        View view = View.inflate(this, R.layout.layout_expression_gridview, null);
        List<String> list = new ArrayList<String>();
        if (i == 1) {
            List<String> list1 = reslist.subList(0, 20);
            list.addAll(list1);
        } else if (i == 2) {
            list.addAll(reslist.subList(20, reslist.size()));
        }
        list.add("delete_expression");
        return view;
    }

    public List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 1; x <= getSum; x++) {
            String filename = "f" + x;
            reslist.add(filename);
        }
        return reslist;

    }


    public ChatMessageBean getTbub(String username, int type,
                                   String Content, String imageIconUrl, String imageUrl,
                                   String imageLocal, String userVoicePath, String userVoiceUrl,
                                   Float userVoiceTime, @ChatConst.SendState int sendState) {
        ChatMessageBean tbub = new ChatMessageBean();
        tbub.setUserName(username);
        String time = returnTime();
        tbub.setTime(time);
        tbub.setType(type);
        tbub.setUserContent(Content);
        tbub.setImageIconUrl(imageIconUrl);
        tbub.setImageUrl(imageUrl);
        tbub.setUserVoicePath(userVoicePath);
        tbub.setUserVoiceUrl(userVoiceUrl);
        tbub.setUserVoiceTime(userVoiceTime);
        tbub.setSendState(sendState);
        tbub.setImageLocal(imageLocal);
//        mChatDbManager.insert(tbub);

        return tbub;
    }

    private View.OnKeyListener onKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                sendMessage();
                return true;
            }
            return false;
        }
    };

    @SuppressLint("SimpleDateFormat")
    public static String returnTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }
}

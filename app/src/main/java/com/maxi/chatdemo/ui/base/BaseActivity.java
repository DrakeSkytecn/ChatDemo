package com.maxi.chatdemo.ui.base;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    int group_index = 0;
    int player_plot_index = 0;
    int mom_plot_index = 0;
    public final static int MOM = 0;
    public final static int PLAYER = 1;
    public final static int CHOICE = 2;
    public final static int AUTO_PIC = 0;
    public final static int MANUAL_PIC = 1;
    int pic_mode = 0;
    int role = 1;

    public void setRole(int role) {
        this.role = role;
    }

    public void setAllIndexs(int new_group_index) {
        group_index = new_group_index;
        player_plot_index = 0;
        mom_plot_index = 0;
    }

    public void addMomIndex() {
        if (mom_plot_index < plots[group_index][0].length - 1) {
            mom_plot_index++;
        }
    }

    public void addPlayerIndex() {
        if (player_plot_index < plots[group_index][1].length - 1) {
            player_plot_index++;
        }
    }

    /**
     * mom and son plots
     */
    public String[][][] plots = {{{
            "你在哪裡？",
            "你為什麼不告訴我？",
            "...無論如何，你現在在售貨亭見面嗎？"
    }, {
            "剛從廁所出來",
            "我以為你會等",
            "好的"
    }}, {{
            "不...我以前從未見過",
            "好的，等你，注意",
            "你好嗎？",
            "",
            "發生了什麼事？",
            "哦，真的嗎？是燒食物的老方法，現在沒有多少人會使用它"
    }, {
            "樹為什麼變得那樣？你看到了嗎？",
            "那真是太奇怪了...什麼都沒有...我來了",
            "一切都還好。",
            "？？！！！？！",
            "什麼都沒有，我只是看到一個染色的區域",
            "哦，我看到這裡有一些木頭在草地上，好像有人在這裡燒烤過"
    }}, {{
            "什麼？",
            "",
            "什麼？",
            "發生了什麼事？",
            "快點。"
    }, {
            "哦，天哪，我剛剛看到了什麼……",
            "一頭死鹿……等等……",
            "…",
            "是幻覺還是只是……",
            "沒事。"
    }}, {{
            "不要想太多，也許只是動物。",
            "總之，快點。",
            "兒子？"
    }, {
            "我覺得有人在跟著我……有人躲在那兒……",
            "但是我不認為這是……",
            "等等。現在聲音越來越近…"
    }}, {{
            "看起來怎麼樣？",
            "兒子不要去那裡，我認為這很危險。"
    }, {
            "等等，媽媽，我看到一所房子",
            "這是一間木屋，但是很笨重。等等，我給你發照片",
            "但是也許有什麼..."
    }}, {{
            "你在那裡看到了什麼？",
            "給我發照片嗎？"
    }, {
            "...",
            "...一個家庭...我不知道為什麼，我覺得我很早以前就認識他們。"
    }}, {{
            "這是什麼",
            "...馬上離開。",
            "躲起來！"
    }, {
            "還有別的…",
            "有人來了"
    }}, {{
            "請盡快來",
            "",
            "一定要小心。"
    }, {
            "讓我確認現在是安全的",
            "當然",
            "我剛下車，我會小心的。"
    }}, {{
            "那好，快過來。",
            "兒子？",
            "回答我",
            "兒子？！",
            "你還好嗎？"
    }, {
            "我認為那個人已經走了。",
            "",
            ""
    }}};

    /**
     * the logic of choice branch
     */
    void roadMap() {
        if (group_index == 0 && choice == 1) {
            setAllIndexs(1);
        } else if (group_index == 1) {
            if (choice == 0) {
                setAllIndexs(2);
            } else {
                setAllIndexs(3);
            }
        } else if (group_index == 2) {
            if (choice == 0) {
                setAllIndexs(1);
            } else {
                setAllIndexs(1);
            }
        } else if (group_index == 3) {
            if (choice == 0) {
                setAllIndexs(1);
            } else {
                setAllIndexs(1);
            }
        }
        setPlotView();
    }

    public String getMomPlot() {
        return plots[group_index][MOM][mom_plot_index];
    }

    public String getPlayerPlot() {
        return plots[group_index][PLAYER][player_plot_index];
    }

    protected void setPlotView() {
        if (role == PLAYER) {
            choice2.setVisibility(View.GONE);
            choice1.setText(getPlayerPlot());
        } else if (role == CHOICE) {
            String[] c = getOnechoice();
            choice1.setText(c[0]);
            choice2.setText(c[1]);
            choice2.setVisibility(View.VISIBLE);
        }
//        choice2.setText();
//        if (plot_index < player_choice_short.length) {
//            if (player_choice_short[plot_index].length == 1) {
//                choice2.setVisibility(View.GONE);
//                choice1.setText(player_choice_short[plot_index][0]);
//            } else {
//                choice2.setVisibility(View.VISIBLE);
//                choice1.setText(player_choice_short[plot_index][0]);
//                choice2.setText(player_choice_short[plot_index][1]);
//            }
//        } else {
//            plot_index = 0;
//        }
    }

    int[][] choice_indexs = {
            {0, PLAYER, 2},
            {1, MOM, 5}
    };

    public String[][] choices = {
            {"左", "右"},
            {"更近看", "走開"},
            {"被幻覺吞噬", "避免被幻想誤吞"},
            {"讓我檢查一下。", "無論如何，我來了。"},
            {"我將其拿下", "我將其留在這裡"},
            {"藏在壁櫥裡", "躲在門後"}
    };

    public String[] getOnechoice() {
        for (int i = 0; i < choice_indexs.length; i++) {
            if (role == PLAYER) {
                if (choice_indexs[i][0] == 0 && choice_indexs[i][2] == player_plot_index) {
                    return choices[i];
                }
            } else {
                if (choice_indexs[i][0] == 0 && choice_indexs[i][2] == mom_plot_index) {
                    return choices[i];
                }
            }
        }
        return null;
    }

    public int[] pics = {
            R.drawable.pic1,
            R.drawable.pic2
    };

    int[][] pic_indexs = {
            {0, PLAYER, 2, AUTO_PIC},
            {0, CHOICE, 1, AUTO_PIC},
            {1, PLAYER, 0, AUTO_PIC},
            {1, MOM, 1, AUTO_PIC},
            {1, PLAYER, 4, AUTO_PIC},
            {1, PLAYER, 5, AUTO_PIC},
            {1, PLAYER, 6, AUTO_PIC},
            {2, CHOICE, 0, AUTO_PIC},
            {2, PLAYER, 1, AUTO_PIC},
            {3, PLAYER, 2, AUTO_PIC},
            {3, MOM, 2, AUTO_PIC},
            {4, CHOICE, 0,AUTO_PIC}};

    public int getPics() {
        for (int i = 0; i < pic_indexs.length; i++) {
//            if (pic_indexs[i][0] == group_index && pic_indexs[i][1] == role && (pic_indexs[i][2] == player_plot_index | pic_indexs[i][2] == mom_plot_index)) {
//                return pics[i];
//            }
            if (role == PLAYER) {
                if (pic_indexs[i][0] == 0 && pic_indexs[i][2] == player_plot_index) {
                    pic_mode = pic_indexs[i][3];
                    return pics[i];
                }
            } else {
                if (pic_indexs[i][0] == 0 && pic_indexs[i][2] == mom_plot_index) {
                    pic_mode = pic_indexs[i][3];
                    return pics[i];
                }
            }
        }
        return -1;
    }

    final Handler autoPicHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    photoIv.setVisibility(View.VISIBLE);
                    clearPic();
                    break;
                case 1:
                    photoIv.setVisibility(View.GONE);
                    showPic();
                    break;
                default:
                    break;
            }
        }
    };

//    //玩家的選項放在這裏
//    public String[][] player_choice_short = {
//            {""},
//            {"剛從廁所出來"},
//            {"我以為你會等"},
//            {"好的"},
//            {"left", "right"},
//            {"left", "right"},
//            {"left", "right"}
//    };

//    //玩家的Plot放在這裏
//    public String[][] player_choice = {
//            {""},
//            {"剛從廁所出來"},
//            {"我以為你會等"},
//            {"好的"},
//            {"left", "right"},
//            {"left", "right"},
//            {"left", "right"}
//    };

//    //媽媽的Plot放在這裏
//    public String[][][] mom_answer = {
//            {{"你在哪裡？"}},
//            {{"你為什麼不告訴我？"}},
//            {{"...無論如何，你現在在售貨亭見面嗎？"}},
//            {{"hello111111"}, {"hahahahaha", "hahahahaha"}},
//            {{"hello111111"}, {"hahahahaha", "hahahahaha"}},
//            {{"hello111111"}, {"hahahahaha", "hahahahaha"}},
//            {{"hello111111"}, {"hahahahaha", "hahahahaha"}},
//    };


//    public String[][][] getMom_answer() {
//        return mom_answer;
//    }

    int choice;

//    public int getChoice() {
//        return choice;
//    }

//    int plot_index = 0;

//    public void setPlot_index(int plot_index) {
//        if (plot_index < player_choice_short.length - 1) {
//            this.plot_index = plot_index;
//            mess_et_click.setClickable(true);
//            mEditTextContent.setClickable(true);
//            tbbv.setClickable(true);
//            if (plot_index == 4) {
//                mess_et_click.setClickable(false);
//                mEditTextContent.setClickable(false);
//                tbbv.setClickable(false);
//                photo.setClickable(true);
//                photo.setBackgroundColor(getResources().getColor(R.color.red2));
//            }
//        }
//    }

//    public int getPlot_index() {
//        return plot_index;
//    }

    public PullToRefreshLayout pullList;
    public boolean isDown = false;
    private boolean CAN_WRITE_EXTERNAL_STORAGE = true;
    private boolean CAN_RECORD_AUDIO = true;
    public int position;
    public int bottomStatusHeight = 0;
    private Button send;
    private Button photo;

    public Button getPhoto() {
        return photo;
    }

    private ImageView photoIv;
    private ChatBottomView tbbv;

    public ChatBottomView getTbbv() {
        return tbbv;
    }

    private Button choice1;
    private Button choice2;
    public View mess_et_click;

    public View getMess_et_click() {
        return mess_et_click;
    }

    public EditText mEditTextContent;

    public EditText getmEditTextContent() {
        return mEditTextContent;
    }

    private File mCurrentPhotoFile;
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

    protected abstract void sendMessageWithoutReceive();

    protected abstract void loadRecords();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        findView();
        initpop();
        init();
    }

    @Override
    protected void onDestroy() {
        cancelToast();
        super.onDestroy();
    }

    protected void findView() {
        pullList = (PullToRefreshLayout) findViewById(R.id.content_lv);
        mEditTextContent = (EditText) findViewById(R.id.mess_et);
        mess_et_click = findViewById(R.id.mess_et_click);
        send = findViewById(R.id.send);
        photo = findViewById(R.id.photo);
        photoIv = findViewById(R.id.photoIv);
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
        setPlotView();
        initActionBar();
    }

//    protected void setPlot() {
//        if (plot_index < player_choice_short.length) {
//            if (player_choice_short[plot_index].length == 1) {
//                choice2.setVisibility(View.GONE);
//                choice1.setText(player_choice_short[plot_index][0]);
//            } else {
//                choice2.setVisibility(View.VISIBLE);
//                choice1.setText(player_choice_short[plot_index][0]);
//                choice2.setText(player_choice_short[plot_index][1]);
//            }
//        } else {
//            plot_index = 0;
//        }
//    }

    int pic_id = -1;

    protected void showPic() {
        pic_id = getPics();
        if (pic_id != -1) {
            if (pic_mode == 0) {
                autoPicHandler.sendEmptyMessageDelayed(0, 2000);
            } else {
                photo.setBackgroundColor(getResources().getColor(R.color.red1));
                photo.setClickable(true);
            }
        }
    }

    void clearPic() {
        autoPicHandler.sendEmptyMessageDelayed(1, 3000);
    }

    protected void init() {
//        mEditTextContent.setOnKeyListener(onKeyListener);
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
                if (tbbv.getVisibility() == View.GONE) {
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
                if (role == PLAYER) {
                    if (from == ChatBottomView.FROM_GALLERY) {
                        mEditTextContent.setText(getPlayerPlot());
                    }
                } else if (role == CHOICE) {
                    if (from == ChatBottomView.FROM_GALLERY) {
                        choice = 0;
                    } else if (from == ChatBottomView.FROM_GALLERY) {
                        choice = 1;
                    }
                    roadMap();
                }
            }
        });
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!mEditTextContent.getText().toString().isEmpty()) {
                    tbbv.setVisibility(View.GONE);
//                    mEditTextContent.setClickable(false);
//                    tbbv.setClickable(false);
//                    mess_et_click.callOnClick();
                    mess_et_click.setClickable(false);
                    photo.setClickable(false);
                    String[] onechoice = getOnechoice();
                    int pics = getPics();
                    if (onechoice != null) {
                        setRole(CHOICE);
                        sendMessageWithoutReceive();
                    } else {
                        setRole(PLAYER);
                        sendMessage();
                    }
//                    if(plot_index<player_choice_short.length-1) {
//                        plot_index++;
//                        setPlot();
//                    }
                }
            }
        });

        final Handler takePhotoHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                photoIv.setVisibility(View.GONE);
                photo.setClickable(false);
                photo.setBackgroundColor(getResources().getColor(R.color.light_gray_11));
                mess_et_click.setClickable(true);
            }
        };
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoIv.setVisibility(View.VISIBLE);
                photoIv.setImageDrawable(getResources().getDrawable(pic_id));
                takePhotoHandler.sendEmptyMessageDelayed(0, 4000);
            }
        });
        photo.setClickable(false);
        reslist = getExpressionRes(40);

        List<View> views = new ArrayList<>();
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


    @SuppressLint("SimpleDateFormat")
    public static String returnTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }
}

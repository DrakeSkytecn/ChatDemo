package com.maxi.chatdemo.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import com.maxi.chatdemo.adapter.ChatListViewAdapter;
import com.maxi.chatdemo.common.ChatConst;
import com.maxi.chatdemo.db.ChatMessageBean;
import com.maxi.chatdemo.ui.base.BaseActivity;
import com.maxi.chatdemo.widget.pulltorefresh.PullToRefreshListView;
import com.maxi.chatdemo.widget.pulltorefresh.base.PullToRefreshView;

import java.lang.ref.WeakReference;


public class ListViewChatActivity extends BaseActivity {

    public PullToRefreshListView myList;
    public ChatListViewAdapter tbAdapter;
    private SendMessageHandler sendMessageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        tblist.clear();
        tbAdapter.notifyDataSetChanged();
        myList.setAdapter(null);
        sendMessageHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    protected void findView() {
        super.findView();
        View view = new PullToRefreshView(this).getSlideView(PullToRefreshView.LISTVIEW);
        pullList.setSlideView(view);
        myList = (PullToRefreshListView) pullList.returnMylist();
    }

    @Override
    protected void init() {
        setTitle("媽媽");
        sendMessageHandler = new SendMessageHandler(this);
        tbAdapter = new ChatListViewAdapter(this);
        tbAdapter.setUserList(tblist);
        myList.setAdapter(tbAdapter);
        tbAdapter.isPicRefresh = true;
        tbAdapter.notifyDataSetChanged();
        tbAdapter.setSendErrorListener(new ChatListViewAdapter.SendErrorListener() {

            @Override
            public void onClick(int position) {
                                ChatMessageBean tbub = tblist.get(position);
                if (tbub.getType() == ChatListViewAdapter.TO_USER_VOICE) {
                    tblist.remove(position);
                } else if (tbub.getType() == ChatListViewAdapter.TO_USER_IMG) {
                    tblist.remove(position);
                }
            }
        });
        tbAdapter.setVoiceIsReadListener(new ChatListViewAdapter.VoiceIsRead() {

            @Override
            public void voiceOnClick(int position) {
                                for (int i = 0; i < tbAdapter.unReadPosition.size(); i++) {
                    if (tbAdapter.unReadPosition.get(i).equals(position + "")) {
                        tbAdapter.unReadPosition.remove(i);
                        break;
                    }
                }
            }

        });
        myList.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        tbAdapter.handler.removeCallbacksAndMessages(null);
                        tbAdapter.setIsGif(true);
                        tbAdapter.isPicRefresh = false;
                        tbAdapter.notifyDataSetChanged();
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        tbAdapter.handler.removeCallbacksAndMessages(null);
                        tbAdapter.setIsGif(false);
                        tbAdapter.isPicRefresh = true;
                        reset();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
//        String[] answers = getMom_answer()[getPlot_index()][getChoice()];
        receriveMsgText(getMomPlot());
        super.init();
    }

    void setPlotFunc() {
//        addPlayerIndex();
//        setPlot_index(getPlot_index()+1);
//        setPlotView();
    }

    static class SendMessageHandler extends Handler {

        WeakReference<ListViewChatActivity> mActivity;

        SendMessageHandler(ListViewChatActivity activity) {
            mActivity = new WeakReference<ListViewChatActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
                        ListViewChatActivity theActivity = mActivity.get();
            if (theActivity != null) {
                switch (msg.what) {
                    case REFRESH:
                        theActivity.tbAdapter.isPicRefresh = true;
                        theActivity.tbAdapter.notifyDataSetChanged();
                        theActivity.myList.setSelection(theActivity.tblist
                                .size() - 1);
                        break;
                    case SEND_OK:
                        theActivity.mEditTextContent.setText("");
                        theActivity.tbAdapter.isPicRefresh = true;
                        theActivity.tbAdapter.notifyDataSetChanged();
                        theActivity.myList.setSelection(theActivity.tblist
                                .size() - 1);
                        theActivity.addPlayerIndex();
                        theActivity.setPlotView();
                        break;
                    case RECERIVE_OK:
                        theActivity.tbAdapter.isPicRefresh = true;
                        theActivity.tbAdapter.notifyDataSetChanged();
                        theActivity.myList.setSelection(theActivity.tblist
                                .size() - 1);
                        theActivity.addMomIndex();
//                        theActivity.addPlayerIndex();
//                        theActivity.setPlotView();
                        break;
                    case PULL_TO_REFRESH_DOWN:
                        theActivity.pullList.refreshComplete();
                        theActivity.tbAdapter.notifyDataSetChanged();
                        theActivity.myList.setSelection(theActivity.position - 1);
                        theActivity.isDown = false;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void loadRecords() {
        isDown = true;
        if (pagelist != null) {
            pagelist.clear();
        }
        pagelist = mChatDbManager.loadPages(page, number);
        position = pagelist.size();
        if (pagelist.size() != 0) {
            pagelist.addAll(tblist);
            tblist.clear();
            tblist.addAll(pagelist);
            if (imageList != null) {
                imageList.clear();
            }
            if (imagePosition != null) {
                imagePosition.clear();
            }
            int key = 0;
            int position = 0;
            for (ChatMessageBean cmb : tblist) {
                if (cmb.getType() == ChatListViewAdapter.FROM_USER_IMG || cmb.getType() == ChatListViewAdapter.TO_USER_IMG) {
                    imageList.add(cmb.getImageLocal());
                    imagePosition.put(key, position);
                    position++;
                }
                key++;
            }
            tbAdapter.setImageList(imageList);
            tbAdapter.setImagePosition(imagePosition);
            sendMessageHandler.sendEmptyMessage(PULL_TO_REFRESH_DOWN);
            if (page == 0) {
                pullList.refreshComplete();
                pullList.setPullGone();
            } else {
                page--;
            }
        } else {
            if (page == 0) {
                pullList.refreshComplete();
                pullList.setPullGone();
            }
        }
    }

    @Override
    protected void sendMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String content = mEditTextContent.getText().toString();
                tblist.add(getTbub(userName, ChatListViewAdapter.TO_USER_MSG, content, null, null,
                        null, null, null, 0f, ChatConst.COMPLETED));
                sendMessageHandler.sendEmptyMessage(SEND_OK);
                ListViewChatActivity.this.content = content;
                receriveHandler.sendEmptyMessageDelayed(0, 1000);
            }
        }).start();
    }

    String content = "";

    protected void receriveMsgText(final String content) {
        Log.i("receriveMsgText", content);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message = content;
                ChatMessageBean tbub = new ChatMessageBean();
                tbub.setUserName(userName);
                String time = returnTime();
                tbub.setUserContent(message);
                tbub.setTime(time);
                tbub.setType(ChatListViewAdapter.FROM_USER_MSG);
                tblist.add(tbub);
                sendMessageHandler.sendEmptyMessage(RECERIVE_OK);
//                mChatDbManager.insert(tbub);
            }
        }).start();
    }

    private Handler receriveHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    String answer = getMomPlot();
                    receriveMsgText(answer);
//                    String[] answers = getMom_answer()[getPlot_index()][getChoice()];
//                    for (String answer:answers) {
//                        receriveMsgText(answer);
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    break;
                default:
                    break;
            }
        }
    };



}

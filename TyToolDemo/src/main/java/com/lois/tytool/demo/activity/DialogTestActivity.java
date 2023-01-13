package com.lois.tytool.demo.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.lois.tytool.TyDialog;
import com.lois.tytool.demo.R;
import com.lois.tytool.dialog.builder.EditTextDialogBuilder;
import com.lois.tytool.dialog.builder.ListDialogBuilder;
import com.lois.tytool.dialog.builder.ProgressDialogBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * @Description 对话框测试Demo
 * @Author Luo.T.Y
 * @Date 2022/3/28
 * @Time 17:19
 */
public class DialogTestActivity extends Activity {
    private static final String TAG = DialogTestActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_test);
        ListView listView = (ListView) this.findViewById(R.id.listview);

        final ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{"载入对话框0", "载入对话框1", "载入对话框2", "消息对话框", "确认对话框", "普通选择对话框", "单选对话框", "多选对话框", "文本输入对话框",
                "普通进度对话框", "圆环进度对话框", "圆形进度对话框", "弧形进度对话框", "自定义对话框", "bottom sheet对话框"});

        listView.setAdapter(adapter);
        final Object[] list = {"选择项1", "选择项2", "选择项3", "选择项4", "选择项5", "选择项6"};
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        TyDialog.loadingDialog0(DialogTestActivity.this).setCancelable(false).setCanceledOnTouchOutside(false).setTitle("ffffffffffff").show();
                        break;
                    case 1:
                        TyDialog.loadingDialog1(DialogTestActivity.this).setPositiveButtonText("确定").setOnPositiveButtonClickListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
                        break;
                    case 2:
                        TyDialog.loadingDialog2(DialogTestActivity.this).show();
                        break;
                    case 3:
                        TyDialog.messageDialog(DialogTestActivity.this).setMessage("消息对话框").show();
                        break;
                    case 4:
                        TyDialog.alertDialog(DialogTestActivity.this).setMessage("确认对话框")
                                .setOnPositiveButtonClickListener(new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(DialogTestActivity.this, "点击了确认按钮", Toast.LENGTH_LONG).show();
                                    }
                                }).create().show();
                        break;
                    case 5:
                        TyDialog.listDialog(DialogTestActivity.this).setChoiceItems(list)
                                .setChoiceType(ListDialogBuilder.TYPE_CHOICE_NORMAL)
                                .setOnChoiceListener(new ListDialogBuilder.OnChoiceListener() {
                                    // 对话框关闭后回调的一个方法，返回选择的条目
                                    @Override
                                    public void onChoiceItem(int index, Object item) {
                                        Toast.makeText(DialogTestActivity.this, "最终选择了：" + item, Toast.LENGTH_LONG).show();
                                    }
                                }).setOnChoiceClickListener(new DialogInterface.OnClickListener() {
                            // 点击条目后回调的一个方法
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(DialogTestActivity.this, "点击了第" + (which + 1) + "个条目",
                                        Toast.LENGTH_LONG).show();
                            }
                        }).show();
                        break;
                    case 6:
                        TyDialog.listDialog(DialogTestActivity.this).setChoiceItems(list)
                                .setChoiceType(ListDialogBuilder.TYPE_CHOICE_SINGLE)
                                .setOnChoiceListener(new ListDialogBuilder.OnChoiceListener() {
                                    // 对话框关闭后回调的一个方法，返回选择的条目
                                    @Override
                                    public void onChoiceItem(int index, Object item) {
                                        Toast.makeText(DialogTestActivity.this, "最终选择了：" + item, Toast.LENGTH_LONG).show();
                                    }
                                }).setOnChoiceClickListener(new DialogInterface.OnClickListener() {
                            // 选择某一个条目的时候回调的一个方法，返回选择的是哪一个条目
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(DialogTestActivity.this, "点击了第" + (which + 1) + "个条目",
                                        Toast.LENGTH_LONG).show();
                            }
                        }).setOnPositiveButtonClickListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(DialogTestActivity.this, "点击了确定按钮", Toast.LENGTH_LONG).show();
                            }
                        }).show();
                        break;
                    case 7:
                        // 已经选好的条目
                        int[] checkedItems = {1, 3, 4};
                        TyDialog.listDialog(DialogTestActivity.this)
                                .setChoiceType(ListDialogBuilder.TYPE_CHOICE_MULTI).setChoiceItems(list)
                                .setCheckedItems(checkedItems)
                                .setOnPositiveButtonClickListener(new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(DialogTestActivity.this, "点击了确定按钮", Toast.LENGTH_LONG).show();
                                    }
                                }).setOnMultiChoiceClickListener(new DialogInterface.OnMultiChoiceClickListener() {
                            // 选择或者取消选择某一个条目的时候回调的一个方法，返回某一个条目的选择情况
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                Toast.makeText(DialogTestActivity.this,
                                        (isChecked ? "选择" : "取消选择") + "了第" + (which + 1) + "个条目", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }).setOnMultiChoiceListener(new ListDialogBuilder.OnMultiChoiceListener() {
                            // 对话框关闭后回调的一个方法，返回选择的条目
                            @Override
                            public void onMultiChoiceItems(List<Integer> indexs, Object[] items) {
                                Toast.makeText(DialogTestActivity.this, "最终选择了：" + Arrays.toString(items),
                                        Toast.LENGTH_LONG).show();
                            }
                        }).show();
                        break;
                    case 8:
                        TyDialog.editTextDialog(DialogTestActivity.this)
                                .setOnEditTextDialogListener(new EditTextDialogBuilder.OnEditTextDialogListener() {
                                    @Override
                                    public void onEditTextCreated(EditText editText) {
                                        editText.setHint("请输入文本内容");
                                    }

                                    @Override
                                    public boolean onEditTextSelected(EditText editText, String text) {
                                        Toast.makeText(DialogTestActivity.this, editText.getText().toString(),
                                                Toast.LENGTH_LONG).show();
                                        return false;
                                    }
                                }).show();
                        break;
                    case 9:
                        final ProgressDialogBuilder.ProgressHandler progressHandler0 =
                                new ProgressDialogBuilder.ProgressHandler();
                        TyDialog.progressDialog(DialogTestActivity.this).progressHandler(progressHandler0)
                                .show();
                        final Handler handler0 = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                int progress = msg.arg1;
                                progressHandler0.setProgress(progress);
                            }
                        };
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 1; i <= 100; i++) {
                                    Message message = new Message();
                                    message.arg1 = i;
                                    handler0.sendMessage(message);
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();
                        break;
                    case 10:
                        final ProgressDialogBuilder.ProgressHandler progressHandler1 =
                                new ProgressDialogBuilder.ProgressHandler();
                        TyDialog.progressDonutDialog(DialogTestActivity.this).progressHandler(progressHandler1)
                                .show();
                        final Handler handler1 = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                int progress = msg.arg1;
                                progressHandler1.setProgress(progress);
                            }
                        };
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 1; i <= 100; i++) {
                                    Message message = new Message();
                                    message.arg1 = i;
                                    handler1.sendMessage(message);
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();
                        break;
                    case 11:
                        final ProgressDialogBuilder.ProgressHandler progressHandler2 =
                                new ProgressDialogBuilder.ProgressHandler();
                        TyDialog.progressCircleDialog(DialogTestActivity.this).progressHandler(progressHandler2)
                                .show();
                        final Handler handler2 = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                int progress = msg.arg1;
                                progressHandler2.setProgress(progress);
                            }
                        };
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 1; i <= 100; i++) {
                                    Message message = new Message();
                                    message.arg1 = i;
                                    handler2.sendMessage(message);
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();
                        break;
                    case 12:
                        final ProgressDialogBuilder.ProgressHandler progressHandler3 =
                                new ProgressDialogBuilder.ProgressHandler();
                        TyDialog.progressArcDialog(DialogTestActivity.this).progressHandler(progressHandler3)
                                .show();
                        final Handler handler3 = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                int progress = msg.arg1;
                                progressHandler3.setProgress(progress);
                            }
                        };
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 100; i > 1; i--) {
                                    Message message = new Message();
                                    message.arg1 = i;
                                    handler3.sendMessage(message);
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();
                        break;
                    case 13:
                        View customView =
                                View.inflate(DialogTestActivity.this, android.R.layout.simple_list_item_1, null);
                        TextView textView = (TextView) customView.findViewById(android.R.id.text1);
                        textView.setText("自定义视图");
                        TyDialog.otherDialog(DialogTestActivity.this)
                                .setContentView(customView,
                                        new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 800))
                                .show();
                        break;
                    case 14:
                        View sheetView =
                                View.inflate(DialogTestActivity.this, android.R.layout.simple_list_item_1, null);
                        TextView sheetViewTextView = (TextView) sheetView.findViewById(android.R.id.text1);
                        sheetViewTextView.setText("自定义视图");
                        TyDialog.bottomSheetDialog(DialogTestActivity.this)
                                .setContentView(sheetView,
                                        new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 800))
                                .show().getInnerDialog();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

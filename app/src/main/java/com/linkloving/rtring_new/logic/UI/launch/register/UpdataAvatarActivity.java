package com.linkloving.rtring_new.logic.UI.launch.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.http.data.DataFromServer;
import com.linkloving.rtring_new.logic.UI.personal.PersonalInfoActivity;
import com.linkloving.rtring_new.logic.UI.personal.util.FileHelper;
import com.linkloving.rtring_new.logic.UI.personal.util.NetUtil;
import com.linkloving.rtring_new.logic.UI.personal.util.SelectPicPopupWindow;
import com.linkloving.rtring_new.logic.UI.ranking.rankinglistitem.AvatarHelper;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.logic.utils.CircleImageView;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.utils.CommonUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class UpdataAvatarActivity extends ToolBarActivity {
    public final static String TAG = PersonalInfoActivity.class.getSimpleName();
    @InjectView(R.id.user_head)
    CircleImageView user_head;

    @InjectView(R.id.nickName)
    EditText nickName;

    @InjectView(R.id.next)
    Button next;

    //自定义的弹出框类
    private PopupWindow menuWindow = null;
    // 修改头像的临时文件存放路径（头像修改成功后，会自动删除之）
    private String __tempImageFileLocation = null;
    /** 回调常量之：拍照 */
    private static final int TAKE_BIG_PICTURE = 991;
    /** 回调常量之：拍照后裁剪 */
    private static final int CROP_BIG_PICTURE = 993;
//	/** 回调常量之：从相册中选取 */
//	private static final int CHOOSE_BIG_PICTURE = 995;
    /** 回调常量之：从相册中选取2 */
    private static final int CHOOSE_BIG_PICTURE2 = 996;
    /** 图像保存大小（微信的也是这个大小） */
    private static final int AVATAR_SIZE = 640;

    private static ProgressDialog pd;// 等待进度圈
    File fileOfTempAvatar;

    private UserEntity userEntity;
    private String resultStr = "";	// 服务端返回结果集


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updata_avatar);
        ButterKnife.inject(this);
        userEntity = MyApplication.getInstance(this).getLocalUserInfoProvider();
        nickName.setHint(getResources().getString(R.string.register_nickname));
    }
    @OnClick(R.id.next)
    void next(){
        if(!CommonUtils.isStringEmpty(nickName.getText().toString())){
            userEntity.getUserBase().setNickname(nickName.getText().toString());
            MyApplication.getInstance(UpdataAvatarActivity.this).setLocalUserInfoProvider(userEntity);
            IntentFactory.startSexActivityIntent(UpdataAvatarActivity.this,1); //1是注册
        }else{
            ToolKits.showCommonTosat(UpdataAvatarActivity.this, false, ToolKits.getStringbyId(UpdataAvatarActivity.this, R.string.login_error11), Toast.LENGTH_SHORT);
        }

    }
    @OnClick(R.id.user_head)
    void changeHead(){
        menuWindow = new SelectPicPopupWindow(UpdataAvatarActivity.this, itemsOnClick);
        menuWindow.showAtLocation(findViewById(R.id.layout_avatar), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final Uri imagePhotoUri = getTempImageFileUri();
        switch (requestCode)
        {
            case TAKE_BIG_PICTURE:// 拍照完成则新拍的文件将会存放于指定的位置（即uri、tempImaheLocation所表示的地方）
            {
                if (resultCode == RESULT_OK) {
                    //从相机拍摄保存的Uri中取出图片，调用系统剪裁工具
                    if (imagePhotoUri != null) {
                        startPhotoZoom(imagePhotoUri,AVATAR_SIZE, AVATAR_SIZE, CROP_BIG_PICTURE);
                    } else {
//                        MyToast.showShort(this, "没有得到拍照图片");
                    }
                } else if (resultCode == RESULT_CANCELED) {
//                        MyToast.showShort(this, "取消拍照");
                } else {
//                   MyToast.showShort(this, "拍照失败");
                }
                break;
            }
            //裁切完成后的处理（上传头像）
            case CROP_BIG_PICTURE://from crop_big_picture
            {
                if (resultCode == RESULT_OK) {
                    MyLog.i("裁剪完成");
                    processAvatarUpload(imagePhotoUri);
                }else {
//                        MyToast.showShort(this, "裁剪取消");
                }
                break;
            }

            case CHOOSE_BIG_PICTURE2:// 图片选取完成时，其实该图片还有原处，如要裁剪则应把它复制出来一份（以免裁剪时覆盖原图)
            {

                if (resultCode == RESULT_OK) {
                    MyLog.i("选择图片,裁剪完成");
                    processAvatarUpload(imagePhotoUri);
                }else {
//                        MyToast.showShort(this, "选择图片取消");
                }
                break;
            }
        }
    }

    /**
     * 处理用户头像的上传.
     *
     * @param avatarTermpImgUri
     */
    private void processAvatarUpload(final Uri avatarTermpImgUri) {

        Log.e(TAG, "处理用户头像的上传.");

        //********************************************************** 【1】压缩头像文件
        // 先将拍好的临时文件decode成bitmap
        Bitmap bmOfTempAvatar = null;
        try {
            bmOfTempAvatar = decodeUriAsBitmap(avatarTermpImgUri);
        }
        // 显示处理下OOM使得APP更健壮（OOM只能显示抓取，否则会按系统Error的方式报出从而使APP崩溃哦）
        catch (OutOfMemoryError e) {
            //WidgetUtils.showToast(parentActivity, parentActivity.getString(R.string.user_info_avatar_upload_faild3), ToastType.WARN);
            Log.e(TAG, "【ChangeAvatar】将头像文件数据decodeUriAsBitmap到内存时内存溢出了，上传没有继续！", e);
        }
        if (bmOfTempAvatar == null) {
            // WidgetUtils.showToast(parentActivity, parentActivity.getString(R.string.user_info_avatar_upload_faild1), ToastType.WARN);
            return;
        }
        //** 再将该bitmap压缩后覆盖原临时文件
        fileOfTempAvatar = new File(getTempImageFileLocation());
        if (bmOfTempAvatar != null) {
            try {
                // # 据测试，微信将640*640的图片裁剪、压缩后的大小约为34K左右，经测试估计是质量75哦
                // # 调整此值可压缩图像大小，经测试，再小于75后，压缩大小就不明显了
                // # 经Jack Jiang在Galaxy sIII上测试：原拍照裁剪完成的60K左右的头像按75压缩后大小约为34K左右，
                //   从高清图片中选取的裁剪完成时的200K左右按75压缩后大小依然约为34K左右，所以75的压缩比率在头
                //   像质量和文件大小上应是一个较好的平衡点
                com.linkloving.rtring_new.logic.UI.personal.util.BitmapHelper.saveBitmapToFile(bmOfTempAvatar, 75, fileOfTempAvatar);
                pd = ProgressDialog.show(UpdataAvatarActivity.this, null, "正在上传图片，请稍候...");

                new Thread(uploadImageRunnable).start();

                Log.d(TAG, "【ChangeAvatar】尝试压缩本地用户头像临时文件已成功完成.");
            } catch (Exception e) {
                Log.e(TAG, "【ChangeAvatar】要更新的本地用户头像在尝试压缩临时文件时出错了，" + e.getMessage() + "，压缩将不能继续，但不影响继续上传处理！", e);


            }
        }

    }

    String imgUrl= CommParams.APP_ROOT_URL_NEW+"upload/avatar";

    Runnable uploadImageRunnable = new Runnable() {



        @Override
        public void run() {
            if(TextUtils.isEmpty(imgUrl)){
                return;
            }
            Log.e(TAG, "imgUrl="+imgUrl);

            Map<String, String> textParams = new HashMap<String, String>();
            Map<String, File> fileparams = new HashMap<String, File>();
            try {
                // 创建一个URL对象
                URL url = new URL(imgUrl);
                textParams = new HashMap<String, String>();
                fileparams = new HashMap<String, File>();
                // 要上传的图片文件
                textParams.put("user_id",String.valueOf(userEntity.getUser_id()));
                fileparams.put("file",fileOfTempAvatar);
                // 利用HttpURLConnection对象从网络中获取网页数据
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 设置连接超时（记得设置连接超时,如果网络不好,Android系统在超过默认时间会收回资源中断操作）
                conn.setConnectTimeout(5000);
                // 设置允许输出（发送POST请求必须设置允许输出）
                conn.setDoOutput(true);
                // 设置使用POST的方式发送
                conn.setRequestMethod("POST");
                // 设置不使用缓存（容易出现问题）
                conn.setUseCaches(false);
                conn.setRequestProperty("Charset", "UTF-8");//设置编码
                // 设置contentType
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + NetUtil.BOUNDARY);
                OutputStream os = conn.getOutputStream();
                DataOutputStream ds = new DataOutputStream(os);
                NetUtil.writeStringParams(textParams, ds);
                NetUtil.writeFileParams(fileparams, ds);
                NetUtil.paramsEnd(ds);
                // 对文件流操作完,要记得及时关闭
                os.close();
                // 服务器返回的响应吗
                int code = conn.getResponseCode(); // 从Internet获取网页,发送请求,将网页以流的形式读回来
                // 对响应码进行判断
                if (code == 200) {// 返回的响应码200,是成功
                    // 得到网络返回的输入流
                    InputStream is = conn.getInputStream();
                    resultStr = NetUtil.readString(is);
                } else {
                    Toast.makeText(UpdataAvatarActivity.this, "请求URL失败！", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendEmptyMessage(0);// 执行耗时的方法之后发送消给handler
        }
    };

    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    pd.dismiss();
                    try {
                        // 返回数据示例，根据需求和后台数据灵活处理
                        Bitmap bitmap = decodeUriAsBitmap(getTempImageFileUri());
                        MyLog.i(TAG, "returnValue" + resultStr);
                        DataFromServer dataFromServer = JSON.parseObject(resultStr,DataFromServer.class);
                        JSONObject filename=JSON.parseObject(dataFromServer.getReturnValue().toString());
                        user_head.setImageBitmap(bitmap);
                        //先尝试删除所有自已的以前老的头像缓存（否则占用用户SD卡的空间）
                        AvatarHelper.deleteUserAvatar(UpdataAvatarActivity.this, userEntity.getUser_id()+"", null);
                        // 先将临时文件（也就是本次拍好或选好的新头像）复制一份（更名
                        // 为正式的本地用户头像缓存文件名）作为本地用户图像的正式文件
                        try {
                            FileHelper.copyFile(fileOfTempAvatar, new File(fileOfTempAvatar.getParent()+"/"+filename.getString("fileName")));
                            // 将用户信息的修改实时同步到设备中
                            UserEntity user = MyApplication.getInstance(UpdataAvatarActivity.this).getLocalUserInfoProvider();
                            user.getUserBase().setUser_avatar_file_name(filename.getString("fileName"));
                            MyLog.i(TAG,"存本地的地址头像"+MyApplication.Const.DIR_KCHAT_AVATART_RELATIVE_DIR+"/"+filename.getString("fileName"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // 再删除用完的临时文件（也就是本次拍好或选好的新头像），因为
                        // 正式的本地缓存文件已生成，本临时文件就失去意义了，当然删除之
                        FileHelper.deleteFile(getTempImageFileLocation());
//                        ToolKits.showCommonTosat(UpdataAvatarActivity.this, true, ToolKits.getStringbyId(UpdataAvatarActivity.this, R.string.changeinfook), Toast.LENGTH_SHORT);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                default:
                    break;
            }
            return false;
        }
    });

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                // 拍照
                case R.id.takePhotoBtn:
                    //进入拍照
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempImageFileUri());
                    startActivityForResult(intent, TAKE_BIG_PICTURE);
                    break;
                // 相册选择图片
                case R.id.pickPhotoBtn:
                    startPhoto();//相册
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 拍照后裁剪图片方法实现
     *
     */
    public void startPhotoZoom(Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 获得临时文件存放地址的Uri(此地址存在与否并不代表该文件一定存在哦).
     *
     * @return 正常获得uri则返回，否则返回null
     */
    private Uri getTempImageFileUri()
    {
        String tempImageFileLocation = getTempImageFileLocation();
        if(tempImageFileLocation != null)
        {
            return Uri.parse("file://" + tempImageFileLocation);
        }
        return null;
    }
    /**
     * 获得临时文件存放地址(此地址存在与否并不代表该文件一定存在哦).
     *
     * @return 正常获得则返回，否则返回null
     */
    private String getTempImageFileLocation()
    {
        try
        {
            if(__tempImageFileLocation == null)
            {
                String avatarTempDirStr = AvatarHelper.getUserAvatarSavedDir(UpdataAvatarActivity.this);
                File avatarTempDir = new File(avatarTempDirStr);
                if(avatarTempDir != null)
                {
                    // 目录不存在则新建之
                    if(!avatarTempDir.exists())
                        avatarTempDir.mkdirs();
                    // 临时文件名
                    __tempImageFileLocation = avatarTempDir.getAbsolutePath()+"/"+"local_avatar_temp.jpg";
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "【ChangeAvatar】读取本地用户的头像临时存储路径时出错了，" + e.getMessage(), e);
        }

        Log.d(TAG, "【ChangeAvatar】正在获取本地用户的头像临时存储路径：" + __tempImageFileLocation);

        return __tempImageFileLocation;
    }


    /*进入相册*/
    private void startPhoto (){
        Intent intent = new Intent("android.intent.action.PICK");
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", AVATAR_SIZE);
        intent.putExtra("outputY", AVATAR_SIZE);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempImageFileUri());
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, CHOOSE_BIG_PICTURE2);
    }

    private Bitmap decodeUriAsBitmap(Uri uri){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }


    @Override
    protected void getIntentforActivity() {
      int tag = getIntent().getIntExtra("tag",0);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListeners() {

    }
}

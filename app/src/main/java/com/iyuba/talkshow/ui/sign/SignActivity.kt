package com.iyuba.talkshow.ui.sign

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.onekeyshare.OnekeyShare
import cn.sharesdk.wechat.moments.WechatMoments
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.iyuba.talkshow.Constant
import com.iyuba.talkshow.R
import com.iyuba.talkshow.TalkShowApplication
import com.iyuba.talkshow.constant.App
import com.iyuba.talkshow.constant.ConfigData
import com.iyuba.talkshow.http.Http
import com.iyuba.talkshow.http.HttpCallback
import com.iyuba.talkshow.lil.help_mvp.util.BigDecimalUtil
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil
import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util
import com.iyuba.talkshow.lil.user.UserInfoManager
import com.iyuba.talkshow.ui.base.BaseActivity
import com.iyuba.talkshow.ui.sign.bean.SignBean
import com.iyuba.talkshow.ui.sign.bean.StudyTimeBeanNew
import com.iyuba.talkshow.ui.widget.LoadingDialog
import com.iyuba.talkshow.util.BaseStorageUtil
import com.iyuba.talkshow.util.DensityUtil
import com.iyuba.talkshow.util.QRCodeEncoder
import com.umeng.analytics.MobclickAgent
import okhttp3.Call
import org.apache.commons.codec.binary.Base64
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat", "SetTextI18n")
class SignActivity : BaseActivity() {
    private var imageView: ImageView? = null
    private var qrImage: ImageView? = null
    private var tv1: TextView? = null
    private var tv2: TextView? = null
    private var tv3: TextView? = null
    private var sign: TextView? = null
    private var userIcon: ImageView? = null
    private var tvShareMsg: TextView? = null
    private val signStudyTime = 2 * 60
    private val loadFiledHint = "打卡加载失败"
    private var totalDays = 0
    private var shareId = ""
    private var qr_bitmap: Bitmap? = null
    private var signPath = ""
    private val drawable = intArrayOf(
            R.drawable.sign_1,
            R.drawable.sign_2,
            R.drawable.sign_3,
            R.drawable.sign_4,
            R.drawable.sign_5,
            R.drawable.sign_6,
            R.drawable.sign_6)
    private var drawableIndex = 0
    var shareTxt: String? = null
    var getTimeUrl: String? = null

    var ll: LinearLayout? = null
    var mWaittingDialog: LoadingDialog? = null
    var addCredit = "" //Integer.parseInt(bean.getAddcredit());
    var days = "" //Integer.parseInt(bean.getDays());
    var totalCredit = "" //bean.getTotalcredit();
    var money = ""
    private var tvUserName: TextView? = null
    private var tvAppName: TextView? = null

    @SuppressLint("HandlerLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        mContext = this
        mWaittingDialog = LoadingDialog(mContext)
        mWaittingDialog!!.show()
        setContentView(R.layout.activity_sign)
        drawableIndex = Random().nextInt(6)
        initView()
        initData()
        initBackGround()
    }

    private fun initData() {
        val uid = UserInfoManager.getInstance().userId
        val url = String.format(Locale.CHINA,
                "http://daxue." + Constant.Web.WEB_SUFFIX + "ecollege/getMyTime.jsp?uid=%s&day=%s&flg=1", uid, getDays())
        getTimeUrl = url
        Http.get(url, object : HttpCallback() {
            override fun onSucceed(call: Call, response: String) {
                Log.e("SignActivity", "initData response " + response)
                if (null != mWaittingDialog) {
                    if (mWaittingDialog!!.isShowing) {
                        mWaittingDialog!!.dismiss()
                    }
                }
                try {
                    val bean = Gson().fromJson(response, StudyTimeBeanNew::class.java)
                    if ("1" == bean.result) {
                        val time = bean.totalTime.toInt()
                        totalDays = bean.totalDays.toInt()
                        val url = "http://staticvip." + Constant.Web.WEB_SUFFIX + "images/mobile/" + totalDays % 30 + ".jpg"
                        shareId = bean.shareId
                        TalkShowApplication.getSubHandler().post {
                            val qrIconUrl = ("http://app." + Constant.Web.WEB_SUFFIX + "share.jsp?uid=" + UserInfoManager.getInstance().userId
                                    + "&appId=" + App.APP_ID + "&shareId=" + shareId)
                            Log.e("SignActivity", "onSucceed qrIconUrl " + qrIconUrl)
                            qr_bitmap = QRCodeEncoder.syncEncodeQRCode(qrIconUrl, DensityUtil.dp2px(mContext, 90f), Color.BLACK, Color.WHITE, null)
                            runOnUiThread { qrImage!!.setImageBitmap(qr_bitmap) }
                        }
                        Glide.with(mContext).load(drawable[drawableIndex]).placeholder(R.drawable.place).dontAnimate().into(imageView!!)
                        tv1!!.text = """
                            学习天数:
                            ${bean.totalDays}
                            """.trimIndent()
                        tv2!!.text = """
                            今日单词:
                            ${bean.totalWord}
                            """.trimIndent()
                        val rankRate = 100 - bean.ranking.toInt() * 100 / bean.totalUser.toInt()
                        tv3!!.text = "超越了：\n$rankRate%同学"
                        shareTxt = (bean.sentence + "我在爱语吧坚持学习了" + bean.totalDays + "天,积累了" + bean.totalWords
                                + "单词如下")
                        if (time < signStudyTime) {
                            sign!!.setOnClickListener { toast(String.format(Locale.CHINA, "打卡失败，当前已学习%d秒，\n满%d分钟可打卡", time, signStudyTime / 60)) }
                        } else {
                            sign!!.setOnClickListener {
                                qrImage!!.visibility = View.VISIBLE
                                sign!!.visibility = View.GONE
                                tvShareMsg!!.visibility = View.VISIBLE
                                findViewById<View>(R.id.tv_close).visibility = View.INVISIBLE
                                tvShareMsg!!.text = "长按图片识别二维码"
                                tvShareMsg!!.gravity = Gravity.CENTER_HORIZONTAL
                                findViewById<View>(R.id.tv_desc).visibility = View.VISIBLE
                                tvShareMsg!!.background = resources.getDrawable(R.drawable.sign_bg_yellow)
                                writeBitmapToFile()
                                findViewById<View>(R.id.tv_desc).visibility = View.INVISIBLE
                                if (ConfigData.openShare) {
                                    showShareOnMoment(mContext, UserInfoManager.getInstance().userId.toString(), App.APP_ID.toString() + "")
                                } else {
                                    ToastUtil.showToast(mContext, "对不起，分享朋友圈暂时不支持")
                                }
                            }
                        }
                    } else {
                        toast(loadFiledHint + bean.result)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    toast("$loadFiledHint！！")
                }
            }

            override fun onError(call: Call, e: Exception) {}
        })
    }

    private fun toast(msg: String) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show()
    }

    private fun getDays(): Long {
        //东八区;
        val cal = Calendar.getInstance(Locale.CHINA)
        cal[1970, 0, 1, 0, 0] = 0
        val now = Calendar.getInstance(Locale.CHINA)
        val intervalMilli = now.timeInMillis - cal.timeInMillis
        return intervalMilli / (24 * 60 * 60 * 1000)
    }

    private fun initView() {
        imageView = findViewById<View>(R.id.iv) as ImageView
        tv1 = findViewById<View>(R.id.tv1) as TextView
        tv2 = findViewById<View>(R.id.tv2) as TextView
        tv3 = findViewById<View>(R.id.tv3) as TextView
        sign = findViewById<View>(R.id.tv_sign) as TextView
        ll = findViewById<View>(R.id.ll) as LinearLayout
        qrImage = findViewById<View>(R.id.tv_qrcode) as ImageView
        userIcon = findViewById<View>(R.id.iv_userimg) as ImageView
        tvUserName = findViewById<View>(R.id.tv_username) as TextView
        tvAppName = findViewById<View>(R.id.tv_appname) as TextView
        tvShareMsg = findViewById<View>(R.id.tv_sharemsg) as TextView
        findViewById<View>(R.id.tv_close).setOnClickListener { showCloseAlert() }
        Glide.with(mContext).load(drawable[drawableIndex]).placeholder(R.drawable.place).dontAnimate().into(imageView!!)
    }

    private fun showCloseAlert() {
        val dialog = AlertDialog.Builder(mContext)
        dialog.setTitle("温馨提示")
        dialog.setMessage("点击下面的打卡按钮,成功分享至微信朋友圈,可以领取红包哦!您确定要退出吗?")
        dialog.setPositiveButton("留下打卡") { dialog, which -> dialog.dismiss() }
        dialog.setNegativeButton("去意已决") { dialog, which ->
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }

    private fun initBackGround() {
        val userIconUrl = ("http://api." + Constant.Web.WEB2_SUFFIX + "v2/api.iyuba?protocol=10005&uid="
                + UserInfoManager.getInstance().userId + "&size=middle")
//        Glide.with(mContext).load(userIconUrl).placeholder(R.drawable.noavatar_small).dontAnimate().transform(CircleTransform(mContext)).into(userIcon)
        Glide3Util.loadCircleImg(mContext,userIconUrl,R.drawable.noavatar_small,userIcon)
        if (TextUtils.isEmpty(UserInfoManager.getInstance().userName)) {
            tvUserName!!.text = UserInfoManager.getInstance().userId.toString()
        } else {
            tvUserName!!.text = UserInfoManager.getInstance().userName
        }
        tvAppName!!.text = App.APP_NAME_CH + "--电影配音练口语的好帮手!"
//        Glide.with(mContext).load("").placeholder(R.drawable.qrcode).into(qrImage)
    }

    fun writeBitmapToFile() {
        val view = window.decorView
        (findViewById<View>(R.id.tv_desc) as TextView).text = "在 " + App.APP_NAME_CH + " 上完成了打卡"
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bitmap = view.drawingCache ?: return
        bitmap.setHasAlpha(false)
        bitmap.prepareToDraw()
        val newpngfile = BaseStorageUtil.getExternalFile(this, "sign",  System.currentTimeMillis().toString() + ".png")
        signPath = newpngfile.absolutePath
        Log.e("SignActivity", "writeBitmapToFile signPath " + signPath)
        if (newpngfile.exists()) {
            newpngfile.delete()
        }
        try {
            val out = FileOutputStream(newpngfile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.flush()
            out.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * @author
     * @time
     * @describe 启动获取积分(红包的接口)
     */
    private fun startInterfaceADDScore(userID: String, appid: String) {
        val currentTime = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val dateString = formatter.format(currentTime)
        //        final String time = Base64Coder.encode(dateString);
        val time = String(Base64.encodeBase64(dateString.toByteArray()))
        val url = ("http://api." + Constant.Web.WEB_SUFFIX + "credits/updateScore.jsp?srid=81&mobile=1&flag=" + time + "&uid=" + userID
                + "&appid=" + appid)
        Http.get(url, object : HttpCallback() {
            override fun onSucceed(call: Call, response: String) {
                Log.e("SignActivity", "startInterfaceADDScore response " + response)
                val bean = Gson().fromJson(response, SignBean::class.java)
                if (bean.result == "200") {
                    money = bean.money
                    addCredit = bean.addcredit
                    days = bean.days
                    totalCredit = bean.totalcredit

                    //打卡成功,您已连续打卡xx天,获得xx元红包,关注[爱语课吧]微信公众号即可提现!
                    runOnUiThread {
                        val moneyThisTime = money.toFloat()
                        MobclickAgent.onEvent(mContext, "dailybonus")
                        if (moneyThisTime > 0) {
                            val moneyTotal = totalCredit.toFloat()
                            Toast.makeText(mContext, "当前钱包金额: " + BigDecimalUtil.trans2Double(moneyTotal * 0.01) + "元," + "满10元[爱语吧]微信公众号提现(关注绑定爱语吧账号),每天坚持打卡分享,获得更多红包吧!", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(mContext, "打卡成功，连续打卡" + days + "天,获得" + addCredit + "积分，总积分: " + totalCredit, Toast.LENGTH_LONG).show()
                            finish()
                        }

                        //加载用户信息
                        UserInfoManager.getInstance().getRemoteUserInfo(UserInfoManager.getInstance().userId,null)
                    }
                } else {
                    runOnUiThread { Toast.makeText(mContext, "您今日已打卡", Toast.LENGTH_SHORT).show() }
                }
            }

            override fun onError(call: Call, e: Exception) {
                Log.e("SignActivity", "startInterfaceADDScore onError " + e.message)
            }
        })
    }

    fun showShareOnMoment(context: Context?, userID: String, AppId: String) {
        val oks = OnekeyShare()
        oks.disableSSOWhenAuthorize()
        oks.setPlatform(WechatMoments.NAME)
//        oks.setImagePath(Environment.getDataDirectory().absolutePath + "/aaa.png")
        oks.setImagePath(signPath)
        oks.setSilent(true)
        oks.callback = object : PlatformActionListener {
            override fun onComplete(platform: Platform, i: Int, hashMap: HashMap<String, Any>) {
                Log.e("SignActivity", "showShareOnMoment onComplete " + i)
                startInterfaceADDScore(userID, AppId)
            }

            override fun onError(platform: Platform, i: Int, throwable: Throwable) {
                Log.e("SignActivity", "showShareOnMoment onError " + throwable.message)
            }

            override fun onCancel(platform: Platform, i: Int) {
                Log.e("SignActivity", "showShareOnMoment onCancel " + i)
            }
        }
        oks.show(context)
    }

}
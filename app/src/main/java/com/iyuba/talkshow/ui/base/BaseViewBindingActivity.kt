//package com.iyuba.talkshow.ui.base
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import androidx.appcompat.app.AppCompatActivity
//import androidx.viewbinding.ViewBinding
//import java.lang.reflect.ParameterizedType
//
//abstract class BaseViewBindingActivity<VB : ViewBinding> : BaseActivity() {
//
//    lateinit var binding: VB
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        //利用反射，调用指定ViewBinding中的inflate方法填充视图
//        val type = javaClass.genericSuperclass
//        if (type is ParameterizedType) {
//            val clazz = type.actualTypeArguments[0] as Class<VB>
//            val method = clazz.getMethod("inflate", LayoutInflater::class.java)
//            binding = method.invoke(null, layoutInflater) as VB
//            setContentView(binding.root)
//        }
//
////        init()
//    }
//
////    abstract fun init()
//}

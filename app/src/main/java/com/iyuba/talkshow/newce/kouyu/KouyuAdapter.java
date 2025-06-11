package com.iyuba.talkshow.newce.kouyu;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.model.AdNativeResponse;
import com.iyuba.talkshow.data.model.CategoryFooter;
import com.iyuba.talkshow.data.model.Header;
import com.iyuba.talkshow.data.model.HomeHeaderImage;
import com.iyuba.talkshow.data.model.LoopItem;
import com.iyuba.talkshow.data.model.RecyclerItem;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.FragmentKouyuDetailBinding;
import com.iyuba.talkshow.databinding.ItemHomeAdBinding;
import com.iyuba.talkshow.databinding.ItemHomeCategoryFooterBinding;
import com.iyuba.talkshow.databinding.ItemHomeHeaderImageBinding;
import com.iyuba.talkshow.databinding.LoopBinding;
import com.iyuba.talkshow.databinding.VoaHeaderBinding;
import com.iyuba.talkshow.ui.courses.coursechoose.CourseKouActivity;
import com.iyuba.talkshow.ui.courses.coursechoose.OpenFlag;
import com.iyuba.talkshow.ui.user.me.dubbing.MyDubbingActivity;
import com.iyuba.talkshow.util.GlideImageLoader;
import com.youdao.sdk.nativeads.NativeResponse;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

/**
 * Created by carl shen on 2020/7/30
 * New Primary English, new study experience.
 */
class KouyuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private enum ITEM_TYPE {
//        LUNBO, HEADER, PEIYIN, CATEGORY_FOOTER, HEADER_FUNCTION_IMAGE,
        AD
    }

    private static final int BANNER_POSITION = 0;

    private final List<RecyclerItem> mItemList = new ArrayList<>();
    private List<Voa> mVoaList = new ArrayList<>();
    private final RecyclerItem bannerHolder = new RecyclerItem() {
    };
    private final HomeHeaderImage homeHeaderImage = new HomeHeaderImage();
    private List<LoopItem> mLoopItemList;
    private final List<String> mImageUrls;
    private final List<String> mNames;

    private VoaCallback mVoaCallback;
    private LoopCallback mLoopCallback;
    private DataChangeCallback mDataChangeCallback;
    private WordBookCallBack mWordBookCallback;
    private boolean isVip;
    @Inject
    DataManager dataManager;

    @Inject
    public KouyuAdapter() {
//        this.mItemList = new ArrayList<>();
        this.mImageUrls = new ArrayList<>();
        this.mNames = new ArrayList<>();

//        mItemList.add(bannerHolder);
//        mItemList.add(homeHeaderImage);
    }

    private String curTitle = App.getBookDefaultShowData().getBookName();
    public void setBookTitle(String id) {
        curTitle = id;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == ITEM_TYPE.LUNBO.ordinal()) {
//            LoopBinding bindingLoop = LoopBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//            return new LoopViewHolder(bindingLoop);
//        } else if (viewType == ITEM_TYPE.HEADER.ordinal()) {
//            VoaHeaderBinding bindingVoaHeader = VoaHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//            return new HeaderViewHolder(bindingVoaHeader);
//        } else if (viewType == ITEM_TYPE.CATEGORY_FOOTER.ordinal()) {
//            ItemHomeCategoryFooterBinding bindFooter = ItemHomeCategoryFooterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//            return new CategoryFooterViewHolder(bindFooter);
//        } else if (viewType == ITEM_TYPE.HEADER_FUNCTION_IMAGE.ordinal()) {
//            ItemHomeHeaderImageBinding bindHead = ItemHomeHeaderImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//            return new HeaderImageViewHolder(bindHead);
//        } else if (viewType == ITEM_TYPE.AD.ordinal()) {
//            ItemCourseDetailBinding bindAd = ItemCourseDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//            return new ViewHolder(bindAd);
//        } else {
        FragmentKouyuDetailBinding bindVoa = FragmentKouyuDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(bindVoa);
//        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            if (mItemList.get(position) instanceof Voa) {
                Voa voa = (Voa) mItemList.get(position);
                ((ViewHolder) holder).setItem(voa);
                ((ViewHolder) holder).setClick(position);
            } else {
                AdNativeResponse response = (AdNativeResponse) mItemList.get(position);
                ((ViewHolder) holder).setItem(response.getNativeResponse());
            }

//        } else if (holder instanceof HeaderViewHolder) {
//            Header header = (Header) mItemList.get(position);
//            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
//            headerHolder.name.setText(header.getName());
//            headerHolder.setClick();
//            setCategoryDrawable(headerHolder, header.getPic());
//            headerHolder.name.setText(header.getDesc());
//        } else if (holder instanceof HeaderImageViewHolder) {
//            ((HeaderImageViewHolder) holder).setClick();
//        } else if (holder instanceof CategoryFooterViewHolder) {
//        } else if (holder instanceof AdHolder) {
//            Log.d("com.iyuba.talkshow", "onBindViewHolder: ");
//
//        } else if (holder instanceof LoopViewHolder) {
//            LoopViewHolder loopViewHolder = (LoopViewHolder) holder;
//            loopViewHolder.setBanner();
        }
    }

    private String getCategoryString(int category) {
        return "人教版";
    }

    private void setCategoryDrawable(HeaderViewHolder holder, int id) {
        Drawable drawable = ContextCompat.getDrawable(holder.itemView.getContext(), id);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        holder.name.setCompoundDrawables(drawable, null, null, null);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
//        int type = super.getItemViewType(position);
//        if (mItemList.get(position) instanceof Voa) {
//            type = ITEM_TYPE.PEIYIN.ordinal();
//        } else if (mItemList.get(position) instanceof Header) {
//            type = ITEM_TYPE.HEADER.ordinal();
//        } else if (mItemList.get(position) instanceof CategoryFooter) {
//            type = ITEM_TYPE.CATEGORY_FOOTER.ordinal();
//        } else if (mItemList.get(position) instanceof HomeHeaderImage) {
//            type = ITEM_TYPE.HEADER_FUNCTION_IMAGE.ordinal();
//        } else if (mItemList.get(position) instanceof AdNativeResponse) {
//            type = ITEM_TYPE.AD.ordinal();
//        } else {
//            type = ITEM_TYPE.LUNBO.ordinal();
//        }
        return ITEM_TYPE.AD.ordinal();
    }

    public void setVoasByCategory(final List<Voa> voas, final CategoryFooter category) {
        int pos = category.getPos() - 4;
        mItemList.remove(pos);
        mItemList.remove(pos);
        mItemList.remove(pos);
        mItemList.remove(pos);
        mItemList.add(pos, voas.get(0));
        mItemList.add(pos, voas.get(1));
        mItemList.add(pos, voas.get(2));
        mItemList.add(pos, voas.get(3));
        notifyItemRangeChanged(pos, 4);
    }

    public void setAd(AdNativeResponse nativeResponse) {
        int pos = Header.startIndex;
        Iterator<RecyclerItem> iterator = mItemList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof AdNativeResponse) {
                iterator.remove();
            }
        }
        mItemList.add(pos, nativeResponse);
//        Header.setStartIndex(pos + 1);
        notifyDataSetChanged();
    }

    public void addVoas(List<Voa> voas) {
        mVoaList.addAll(voas);
        mItemList.addAll(voas);
        notifyDataSetChanged();
    }

    public void setVoas(List<Voa> mVoaList) {
        this.mVoaList = mVoaList;
        try {
            setRecyclerItemList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRecyclerItemList() {
        mItemList.clear();
//        mItemList.add(bannerHolder);
//        mItemList.add(homeHeaderImage);
        mItemList.addAll(mVoaList);
    }

    public void setBanner(List<LoopItem> loopItemList) {
        this.mLoopItemList = loopItemList;
        if (loopItemList != null && loopItemList.size() > 0) {
            mImageUrls.clear();
            mNames.clear();
            for (LoopItem item : mLoopItemList) {
                mImageUrls.add(item.pic());
                mNames.add(item.name());
            }
            notifyItemChanged(BANNER_POSITION);
        }
    }

    public List<RecyclerItem> getItemList() {
        return mItemList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Voa voa;
        private final TextView title;
        private final TextView views;
        private final TextView desc;
        private final ImageView image;
        private final ImageView lock;


        public ViewHolder(@NonNull FragmentKouyuDetailBinding binding) {
            super(binding.getRoot());
            title = binding.title;
            views = binding.views;
            desc = binding.desc;
            image = binding.image;
            lock = binding.lock;
        }

        public void setItem(Voa dataBean) {
            this.voa = dataBean;
            if (!isVip && !dataManager.isTrial(voa)) {
                lock.setVisibility(View.VISIBLE);
            } else {
                lock.setVisibility(View.GONE);
            }
            if (dataBean.titleCn().contains(curTitle)) {
                title.setText(dataBean.titleCn().replace(curTitle, "").trim());
            } else {
                title.setText(dataBean.titleCn());
            }
            views.setText("浏览量" + new Random().nextInt(500));
            desc.setText(dataBean.title());
            Glide.with(image.getContext()).load(dataBean.pic()).placeholder(R.drawable.beishi_grade7).into(image);
        }

        public void setItem(NativeResponse dataBean) {
            itemView.setOnClickListener(dataBean::handleClick);
            lock.setVisibility(View.GONE);
            title.setText(dataBean.getTitle());
            views.setText("浏览量" + new Random().nextInt(100000));
            desc.setText("推广");
            desc.setTextColor(desc.getContext().getResources().getColor(R.color.dark_gray));
            Glide.with(image.getContext()).load(dataBean.getMainImageUrl()).into(image);
        }

        public void setClick(int position) {
            image.setOnClickListener(v -> onViewClicked(position));
            itemView.setOnClickListener(v -> onViewClicked(position));
        }

        public void onViewClicked(int position) {
            if (mItemList.get(position) instanceof Voa) {
                mVoaCallback.onVoaClick((Voa) mItemList.get(position));
            }
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        public TextView name;

        public HeaderViewHolder(VoaHeaderBinding binding) {
            super(binding.getRoot());
            name = binding.name;
        }

        public void setClick() {
        }
    }

    class LoopViewHolder extends RecyclerView.ViewHolder {

        Banner banner;

        public LoopViewHolder(LoopBinding binding) {
            super(binding.getRoot());
            banner = binding.banner;
        }

        public void setBanner() {
            //设置banner样式
            banner.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE)
                    //设置图片加载器
                    .setImageLoader(new GlideImageLoader())
                    //设置图片集合
                    .setImages(mImageUrls)
                    //设置banner动画效果
                    .setBannerAnimation(Transformer.DepthPage)
                    //设置标题集合（当banner样式有显示title时）
                    .setBannerTitles(mNames)
                    //设置自动轮播，默认为true
                    .isAutoPlay(true)
                    //设置轮播时间
                    .setDelayTime(5000)
                    //设置指示器位置（当banner模式中有指示器时）
                    .setIndicatorGravity(BannerConfig.RIGHT)
                    .setOnBannerListener(position -> {
                        LoopItem loopItem = mLoopItemList.get(position);
                        Log.e("banner", "点击了！");
                        mLoopCallback.onLoopClick(loopItem.id());
                    });
            //banner设置方法全部调用完毕时最后调用
            banner.start();
            banner.setVisibility(View.GONE);
        }

    }

    class AdHolder extends RecyclerView.ViewHolder {


        private final ImageView ivAd;
        private final TextView title;
        private final TextView read;

        public AdHolder(ItemHomeAdBinding binding) {
            super(binding.getRoot());
            ivAd = binding.ivAd;
            title = binding.title;
            read = binding.read;
        }

        public void setItem(int position) {
            final NativeResponse nativeResponse = ((AdNativeResponse) mItemList.get(position)).getNativeResponse();
            nativeResponse.recordImpression(itemView);
            itemView.setOnClickListener(nativeResponse::handleClick);
            Glide.with(itemView.getContext()).load(nativeResponse.getMainImageUrl())
                    .placeholder(R.drawable.voa_default)
                    .into(ivAd);
            title.setText(nativeResponse.getTitle());
            read.setText(String.format(Locale.CHINA, "%d5270", new Random().nextInt(9)));
        }
    }

    interface VoaCallback {
        void onVoaClick(Voa voa);
    }

    interface LoopCallback {
        void onLoopClick(int voaId);
    }

    interface WordBookCallBack {
        void onBookClick();
    }

    interface DataChangeCallback {
        void onClick(View v, CategoryFooter category, int limit, String ids);
    }

    public void setVoaCallback(VoaCallback voaCallback) {
        this.mVoaCallback = voaCallback;
    }

    public void setmWordBookCallback(WordBookCallBack wordBookCallback) {
        this.mWordBookCallback = wordBookCallback;
    }

    public void setLoopCallback(LoopCallback loopCallback) {
        this.mLoopCallback = loopCallback;
    }

    public void setDataChangeCallback(DataChangeCallback mDataChangeCallback) {
        this.mDataChangeCallback = mDataChangeCallback;
    }

    private View.OnClickListener dailyBonusOnClickListener;

    public void setDailyBonusCallback(View.OnClickListener onClickListener) {
        this.dailyBonusOnClickListener = onClickListener;
    }
}
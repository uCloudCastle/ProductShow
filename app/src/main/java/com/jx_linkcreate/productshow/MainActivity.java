package com.jx_linkcreate.productshow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jx_linkcreate.productshow.adapter.LabelAdapter;
import com.jx_linkcreate.productshow.layout.FilterDrawerLayout;
import com.jx_linkcreate.productshow.manager.ConfigManager;
import com.jx_linkcreate.productshow.transmitter.NetworkCallback;
import com.jx_linkcreate.productshow.transmitter.NetworkManager;
import com.jx_linkcreate.productshow.transmitter.netbean.HResult;
import com.jx_linkcreate.productshow.transmitter.netbean.HttpResponse;
import com.jx_linkcreate.productshow.transmitter.netbean.Product;
import com.jx_linkcreate.productshow.uibean.FilterEvent;
import com.randal.aviana.BitmapUtils;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoActivity;
import org.devio.takephoto.compress.CompressConfig;
import org.devio.takephoto.model.TImage;
import org.devio.takephoto.model.TResult;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.jx_linkcreate.productshow.ImagePreviewActivity.INTENT_KEY_PREVIEW_PATHS;
import static com.jx_linkcreate.productshow.manager.ConfigManager.ADD_ITEM;
import static com.jx_linkcreate.productshow.manager.ConfigManager.APP_KEY;

public class MainActivity extends TakePhotoActivity {

    private MaterialDialog mUploadDialog;
    private DrawerLayout mDrawerLayout;
    private FilterDrawerLayout mFilterDrawerLayout;
    private MenuItem mMenuItem;

    private XRecyclerView mRecyclerView;
    private ProductAdapter mAdapter;

    private ArrayList<String> mFilter = new ArrayList<>();
    private ArrayList<Product> mProductOrigin = new ArrayList<>();
    private ArrayList<Product> mProductFilter = new ArrayList<>();
    private boolean mIsDrawerOpened = false;

    private TakePhoto mTakePhoto;
    private ArrayList<String> mImgPaths = new ArrayList<>();
    private GridImageAdapter mDialogImgAdapter;
    private LabelAdapter mLabelAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConfigManager.getInstance(this).init();
        mTakePhoto = getTakePhoto();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_recyclerview_drawer_layout);
        mRecyclerView = (XRecyclerView) findViewById(R.id.activity_main_recyclerview);
        mFilterDrawerLayout = (FilterDrawerLayout) findViewById(R.id.activity_main_recyclerview_drawer);
        initRecyclerView();
        initDrawer();

        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fragment_checkdetail, menu);
        mMenuItem = menu.findItem(R.id.menu_item_filter);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);

        ArrayList<String> paths = convertTImages2Paths(result.getImages());
        if (paths.size() > 0) {
            mImgPaths.addAll(paths);
            refreshImgList();
        }
    }

    private ArrayList<String> convertTImages2Paths(ArrayList<TImage> images) {
        ArrayList<String> arrays = new ArrayList<>();
        if (images != null && images.size() > 0) {
            for (TImage img : images) {
                if (img.getCompressPath() != null) {
                    arrays.add(img.getCompressPath());
                }
            }
        }
        return arrays;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_filter: {
                if (mIsDrawerOpened) {
                    closeMenu();
                } else {
                    openMenu();
                }
                break;
            }
            case R.id.menu_item_upload: {
                mUploadDialog = new MaterialDialog.Builder(this)
                        .title("上传新商品")
                        .customView(R.layout.layout_dialog_upload, true)
                        .positiveText("确认")
                        .negativeText("取消")
                        .canceledOnTouchOutside(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                View cv = dialog.getCustomView();
                                EditText et_name = cv.findViewById(R.id.layout_dialog_name_value);
                                EditText et_price = cv.findViewById(R.id.layout_dialog_price_value);

                                ArrayList<String> sendFilePaths = new ArrayList<>(mImgPaths);
                                sendFilePaths.remove(ADD_ITEM);
                                uploadProduct(et_name.getText().toString(),
                                        et_price.getText().toString(),
                                        ConfigManager.getInstance(MainActivity.this).join(mLabelAdapter.getSelectedLabel(), ";"),
                                        sendFilePaths);
                            }
                        }).build();

                View cv = mUploadDialog.getCustomView();
                RecyclerView imgRecyc = cv.findViewById(R.id.layout_dialog_image_recyclerview);
                imgRecyc.setLayoutManager(new GridLayoutManager(this, 3));
                mDialogImgAdapter = new GridImageAdapter();
                imgRecyc.setAdapter(mDialogImgAdapter);

                RecyclerView tagRecyc = cv.findViewById(R.id.layout_dialog_tags_recyclerview);
                FlexboxLayoutManager lm = new FlexboxLayoutManager(this);
                lm.setFlexDirection(FlexDirection.ROW);
                lm.setJustifyContent(JustifyContent.FLEX_START);
                tagRecyc.setLayoutManager(lm);
                ArrayList<String> filters = ConfigManager.getInstance(this).getAllSubTitle();
                mLabelAdapter = new LabelAdapter(this);
                mLabelAdapter.addTextDataSet(filters);
                tagRecyc.setAdapter(mLabelAdapter);

                refreshImgList();
                mUploadDialog.show();
                break;
            }
            case R.id.menu_item_about: {
                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title("商品展示系统Android客户端")
                        .content(R.string.intro, true)
                        .build();
                dialog.getContentView().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                dialog.show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadProduct(String name, String price, String tags, ArrayList<String> filePaths) {
        Product product = new Product();
        product.name = name;
        product.price = price;
        product.tags = tags;
        product.localPaths = filePaths;

        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content("正在上传...")
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .canceledOnTouchOutside(false)
                .build();
        dialog.show();

        NetworkManager.getInstance(this).uploadProduct(APP_KEY, product, new NetworkCallback<HResult>() {
            @Override
            public void onNext(HResult hResult) {
                if (hResult.code != 0) {
                    dialog.getProgressBar().setVisibility(View.GONE);
                    dialog.setContent("上传失败，错误码 " + hResult.code + "：" + hResult.msg);
                    dialog.setActionButton(DialogAction.POSITIVE, "确定");
                } else {
                    dialog.getProgressBar().setVisibility(View.GONE);
                    dialog.setContent("上传成功");
                    dialog.setActionButton(DialogAction.POSITIVE, "确定");
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                dialog.getProgressBar().setVisibility(View.GONE);
                dialog.setContent("上传失败，请检查网络");
                dialog.setActionButton(DialogAction.POSITIVE, "确定");
            }

            @Override
            public void onComplete() { }
        });
    }

    private void loadData() {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content("加载中...")
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .canceledOnTouchOutside(false)
                .build();
        dialog.show();

        NetworkManager.getInstance(this).getAllProduct(APP_KEY, new NetworkCallback<HttpResponse<List<Product>>>() {
            @Override
            public void onNext(HttpResponse<List<Product>> response) {
                if (response.code != 0) {
                    dialog.getProgressBar().setVisibility(View.GONE);
                    dialog.setContent("加载失败，错误码 " + response.code + "：" + response.msg);
                    dialog.setActionButton(DialogAction.POSITIVE, "确定");
                } else if (response.result == null) {
                    dialog.getProgressBar().setVisibility(View.GONE);
                    dialog.setContent("加载数据异常");
                    dialog.setActionButton(DialogAction.POSITIVE, "确定");
                } else {
                    mProductOrigin.clear();
                    mProductOrigin.addAll(response.result);
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                dialog.getProgressBar().setVisibility(View.GONE);
                dialog.setContent("加载失败，请检查网络");
                dialog.setActionButton(DialogAction.POSITIVE, "确定");
            }

            @Override
            public void onComplete() {
                dialog.dismiss();
                refreshUI();
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new ProductAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.getDefaultRefreshHeaderView()
                .setRefreshTimeVisible(true);
        mRecyclerView.setArrowImageView(R.drawable.arrow_dot_down);
        mRecyclerView.setPullRefreshEnabled(false);

        mRecyclerView.getDefaultFootView().setLoadingHint("加载中...");
        mRecyclerView.getDefaultFootView().setNoMoreHint("\n— 数据加载完毕 —");
        mRecyclerView.setLimitNumberToCallLoadMore(0);
        mRecyclerView.setNoMore(true);

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                loadData();
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mRecyclerView.setNoMore(true);
                    }
                }, 1000);
            }
        });
    }

    private void initDrawer() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mIsDrawerOpened = true;
                mMenuItem.setTitle("确定");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mIsDrawerOpened = false;
                mMenuItem.setTitle("筛选");
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFilterChanged(FilterEvent event) {
        if (event.filterType == 0) {                             // add filter
            if (mFilter.contains(event.value)) {
                // do nothing
            } else {
                mFilter.add(event.value);
            }
        } else if (event.filterType == 1) {                       // remove filter
            mFilter.remove(event.value);
        }
        makeFilterList();
    }

    // 获取原始数据
    public ArrayList<Product> getProductOrigins() {
        return mProductOrigin;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsDrawerOpened) {
                closeMenu();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void refreshUI() {
        //mFilterDrawerLayout.refreshViews();
        makeFilterList();
    }

    private void makeFilterList() {
        mProductFilter = new ArrayList<>(mProductOrigin);

        Iterator<Product> iter = mProductFilter.iterator();
        while (iter.hasNext()) {
            Product product = iter.next();
            ArrayList<String> tags = new ArrayList<>(Arrays.asList(product.tags.split(";")));

            if (!tags.containsAll(mFilter)) {
                iter.remove();
            }
        }
        mAdapter.notifyDataSetChanged();
    }


    public void closeMenu() {
        mDrawerLayout.closeDrawer(GravityCompat.END);
    }

    public void openMenu() {
        mDrawerLayout.openDrawer(GravityCompat.END);
    }


    /*
     *  ************************************************************************* ProductAdapter
     */
    private class ProductAdapter extends RecyclerView.Adapter<ProductHolder> {
        @Override
        public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_product, parent, false);
            return new ProductHolder(view);
        }

        @Override
        public void onBindViewHolder(ProductHolder holder, int position) {
            if (mProductFilter != null) {
                holder.bindDataSet(mProductFilter.get(position));
            }
        }

        @Override
        public int getItemCount() {
            if (mProductFilter == null) {
                return 0;
            } else {
                return mProductFilter.size();
            }
        }
    }

    class ProductHolder extends RecyclerView.ViewHolder {
        private View mLayout;

        private ViewGroup mReviewImgLayout;
        private ImageView mImageView;
        private View mImgNum;
        private TextView mProductName;
        private TextView mProductPrice;

        ProductHolder(View itemView) {
            super(itemView);
            mLayout = itemView;

            mReviewImgLayout = itemView.findViewById(R.id.item_product_image_layout);
            mImageView = itemView.findViewById(R.id.item_product_image);
            mImgNum = itemView.findViewById(R.id.item_product_image_number);
            mProductName = itemView.findViewById(R.id.item_product_name);
            mProductPrice = itemView.findViewById(R.id.item_product_price);
        }

        void bindDataSet(final Product product) {
            mProductName.setText(product.name);
            mProductPrice.setText(product.price);

            final ArrayList<String> urls = product.urls;
            if (urls.size() > 0) {
                //mImgNum

                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.img_empty);
                Glide.with(MainActivity.this)
                        .load(urls.get(0))
                        .apply(options)
                        .into(mImageView);
            } else {
                mImageView.setImageDrawable(getDrawable(R.drawable.img_empty));
            }


            mReviewImgLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (urls.size() == 0) {
                        return;
                    }

                    Intent intent = new Intent(MainActivity.this, ImagePreviewActivity.class);
                    intent.putStringArrayListExtra(INTENT_KEY_PREVIEW_PATHS, urls);
                    startActivity(intent);
                }
            });
        }
    }


    /*
     *  ************************************************************************* GridImageAdapter
     */
    private class GridImageAdapter extends RecyclerView.Adapter<GridImageHolder> {
        @Override
        public GridImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_chooseimg_item, parent, false);
            return new GridImageHolder(view);
        }

        @Override
        public void onBindViewHolder(GridImageHolder holder, int position) {
            if (mImgPaths != null) {
                holder.bindDataSet(position);
            }
        }

        @Override
        public int getItemCount() {
            if (mImgPaths == null) {
                return 0;
            } else {
                return mImgPaths.size();
            }
        }
    }


    /*
        RecycleView Holder
     */
    private class GridImageHolder extends RecyclerView.ViewHolder {
        private View mLayout;
        private ImageView mImageView;
        private View mDelete;
        private View mAdd;

        GridImageHolder(View itemView) {
            super(itemView);
            mLayout = itemView;
            mImageView = itemView.findViewById(R.id.layout_gridlayout_imageview);
            mDelete = itemView.findViewById(R.id.layout_gridlayout_delete);
            mAdd = itemView.findViewById(R.id.layout_gridlayout_add);
        }

        void bindDataSet(final int position) {
            String path = mImgPaths.get(position);
            if (path.equals(ADD_ITEM)) {
                showAddImageItem();
            } else {
                showRealItem(path, position);
            }
        }

        private void showAddImageItem() {
            mImageView.setVisibility(View.INVISIBLE);
            mAdd.setVisibility(View.VISIBLE);

            mAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showChooseStyleDialog();
                }
            });
        }

        private void showRealItem(String path, final int position) {
            mImageView.setVisibility(View.VISIBLE);
            mDelete.setVisibility(View.INVISIBLE);
            mAdd.setVisibility(View.INVISIBLE);

            Bitmap bitmap = BitmapUtils.getBitmap(path);
            mImageView.setImageBitmap(bitmap);

            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDelete.setVisibility(View.INVISIBLE);
                    mImgPaths.remove(position);
                    refreshImgList();
                }
            });

            mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDelete.getVisibility() == View.INVISIBLE) {
                        mDelete.setVisibility(View.VISIBLE);
                    } else {
                        mDelete.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    // *********************************************************************** Adapter Function

    private void showChooseStyleDialog() {
        final MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
                .customView(R.layout.layout_dialog_choose_style, false)
                .show();

        View cv = dialog.getCustomView();
        TextView tc = cv.findViewById(R.id.chooseimg_btn_fromCapture);
        tc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFromCapture();
                dialog.dismiss();
            }
        });

        TextView ta = cv.findViewById(R.id.chooseimg_btn_fromAlbum);
        ta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseMultiple();
                dialog.dismiss();
            }
        });
    }

    private void chooseMultiple() {
        enableCompressBeforePick();
        mTakePhoto.onPickMultiple(9);
    }

    private void chooseFromCapture() {
        enableCompressBeforePick();
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri imageUri = Uri.fromFile(file);
        mTakePhoto.onPickFromCapture(imageUri);
    }

    private void enableCompressBeforePick() {
        CompressConfig config = CompressConfig.ofDefaultConfig();
        config.setMaxPixel(800);                                             // 800 px
        config.setMaxSize(50 * 1024);                                         // 50 KB
        mTakePhoto.onEnableCompress(config, true);
    }

    private void refreshImgList() {
        clearAddItem();
        while (mImgPaths.size() > 9) {
            mImgPaths.remove(0);
        }

        if (mImgPaths.size() < 9) {
            mImgPaths.add(ADD_ITEM);
        }
        mDialogImgAdapter.notifyDataSetChanged();
    }

    private void clearAddItem() {
        if (mImgPaths.size() == 0) {
            return;
        }

        Iterator<String> iter = mImgPaths.iterator();
        while (iter.hasNext()) {
            String path = iter.next();
            if (path.equals(ADD_ITEM)) {
                iter.remove();
            }
        }
    }
}

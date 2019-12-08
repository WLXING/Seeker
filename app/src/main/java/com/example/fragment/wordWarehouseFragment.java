package com.example.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.MyWordListAdapter;
import com.example.bean.MyWordList;
import com.example.seeker.MyWordListRecyclerViewListener;
import com.example.seeker.R;
import com.example.ui.WordEditActivity;
import com.example.utils.L;

import org.litepal.LitePal;

import java.util.List;

/**
 * Created by ${WLX} on 2019/7/23.
 */

public class wordWarehouseFragment extends Fragment {
    private static final String TAG = "wordWarehouseFragment";
    private SearchView searchView;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingBtn;
    private String searchContent;
    private boolean isSearching2 = false;
    private List<MyWordList> myWordLists = null;
    private static final String MY_NOTEBOOK = "1009";//我的词库对应索引
    private MyWordListAdapter myWordListAdapter;
    private static final int WAREHOUSE_FRAGMENT_EDIT = 302;
    private static final int WORD_EDIT_OK = 301;
    private static final String ORDER_RULER = "id desc";
    private TextView warehouseRefresh;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.word_warehouse_fragment, container, false);
        searchView = (SearchView) view.findViewById(R.id.warehouse_fragment_searchview);
        recyclerView = (RecyclerView) view.findViewById(R.id.warehouse_fragment_recyclerView);
        floatingBtn = (FloatingActionButton) view.findViewById(R.id.warehouse_fragment_floatingBar);
        warehouseRefresh = (TextView) view.findViewById(R.id.warehouse_fragment_refresh);
        myWordLists = LitePal.where("notebookguid = ?", MY_NOTEBOOK).order(ORDER_RULER).find(MyWordList.class);
        myWordListAdapter = new MyWordListAdapter(new MyWordListRecyclerViewListener() {
            @Override
            public void onItemClick(View view, MyWordList myWordList) {
                //短按编辑
                //跳转到另一个activity进行修改
                Intent intent = new Intent(getActivity(), WordEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("myWordList", myWordList);
                bundle.putString("type","编辑");
                bundle.putString("bean","MyWordList");
                intent.putExtras(bundle);
                startActivityForResult(intent, WAREHOUSE_FRAGMENT_EDIT);
            }

            @Override
            public void onItemLongClick(View view, final MyWordList myWordList) {
                //长按删除
                //弹出一个dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                AlertDialog alertDialog = builder.setTitle("系统提示：")
                        .setMessage("确定删除这个单词吗？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                LitePal.delete(MyWordList.class, myWordList.getId());
                                Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                                refreshWarehouseFragment();
                                L.e(TAG, "------------->" + LitePal.where("notebookguid=?", MY_NOTEBOOK));
                            }
                        }).create();
                alertDialog.show();
            }
        },myWordLists);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myWordListAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchContent = s;
                isSearching2= true;
                refreshWarehouseFragment();
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {//关闭搜索之后搜索全部降序排列
                isSearching2 = false;
                refreshWarehouseFragment();
                return false;
            }
        });
        floatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到另一个activity进行修改
                Intent intent = new Intent(getActivity(), WordEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("myWordList", null);
                bundle.putString("type","新建");
                bundle.putString("bean","MyWordList");
                intent.putExtras(bundle);
                startActivityForResult(intent, WAREHOUSE_FRAGMENT_EDIT);
            }
        });
        warehouseRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshWarehouseFragment();
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WAREHOUSE_FRAGMENT_EDIT && resultCode == WORD_EDIT_OK) {
            refreshWarehouseFragment();
        }
    }

    public  void refreshWarehouseFragment() {
        //如果用户正在搜索，则按搜索内容刷新，否则搜索全部降序刷新
        if (isSearching2) {
            myWordLists = LitePal.limit(15).where("headword like ? or quickdefinition like ?", "%" + searchContent + "%","%" + searchContent + "%").find(MyWordList.class);
               myWordListAdapter .refreshShow(myWordLists);
            }else {
            List<MyWordList> myWordLists = LitePal.limit(10).where("notebookguid = ?", MY_NOTEBOOK).order(ORDER_RULER).find(MyWordList.class);
                myWordListAdapter.refreshShow(myWordLists);
        }
    }

}

package com.example.sstep.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sstep.AppInData;
import com.example.sstep.R;
import com.example.sstep.alarm.Alarm;

import com.example.sstep.performance.MonthState;
import com.example.sstep.staffinvite.StaffInvite;
import com.example.sstep.store.SelectStore;
import com.example.sstep.store.modifyStore;
import com.example.sstep.store.store_api.StoreApiService;
import com.example.sstep.store.store_api.StoreResponseDto;
import com.example.sstep.todo.checklist.CheckList;
import com.example.sstep.todo.checklist.Checklist_detail;
import com.example.sstep.todo.checklist.checklist_api.CheckListResponseDto;
import com.example.sstep.todo.checklist.checklist_api.ChecklistApiService;
import com.example.sstep.user.mypage.MyPage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home_Ceo extends AppCompatActivity implements View.OnClickListener {


    private RecyclerView checkRecyclerView;
    private ArrayList<HomeCeoCheck_recyclerViewWordItemData> check_list = new ArrayList<>();
    private LinearLayout check_nodataLayout, check_dataLayout;

    ImageButton menuIBtn, alarmIBtn, staffInviteCloseIBtn;
    Button selectStoreBtn, mypageBtn, checklistBtn, staffInviteBtn;;
    TextView monthstateTv, modifyStoreTv, checkNumTv, storeName;
    FrameLayout staffInviteFLayout;
    LinearLayout staffApprovalL, commutePerL;
    private RecyclerView checkListRecyclerView;

    private Home_Ceo_checkList_RecyclerViewAdpater checkListRecyclerViewAdapter;

    private List<Home_Ceo_checkList_recyclerViewItem> checkListList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_ceo);
        menuIBtn=findViewById(R.id.homeceo_menuIBtn); menuIBtn.setOnClickListener(this);
        alarmIBtn=findViewById(R.id.homeceo_alarmIBtn); alarmIBtn.setOnClickListener(this);
        selectStoreBtn=findViewById(R.id.homeceo_selectStoreBtn); selectStoreBtn.setOnClickListener(this);
        mypageBtn=findViewById(R.id.homeceo_mypageBtn); mypageBtn.setOnClickListener(this);
        modifyStoreTv=findViewById(R.id.homeceo_modifyStoreTv); modifyStoreTv.setOnClickListener(this);
        monthstateTv=findViewById(R.id.homeceo_monthstateTv); monthstateTv.setOnClickListener(this);
        checklistBtn=findViewById(R.id.homeceo_checklistBtn); checklistBtn.setOnClickListener(this);
        staffInviteBtn=findViewById(R.id.homeceo_staffInviteBtn); staffInviteBtn.setOnClickListener(this);
        staffInviteCloseIBtn=findViewById(R.id.homeceo_staffInviteCloseIBtn); staffInviteCloseIBtn.setOnClickListener(this);
        staffInviteFLayout=findViewById(R.id.homeceo_staffInviteFLayout);
        checkNumTv=findViewById(R.id.homeceo_checkNumTv);
        staffApprovalL=findViewById(R.id.homeceo_staffApprovalL);
        commutePerL=findViewById(R.id.homeceo_commutePerL);
        storeName = findViewById(R.id.homeceo_storeNameTv);


        // ID값, storeId가지고 오기
        AppInData loginData = (AppInData) getApplication(); // AppInData 클래스의 인스턴스 가져오기
        String userId = loginData.getUserId(); // 사용자 ID 가져오기
        long storeCode = loginData.getStoreCode();
        long storeId = loginData.getStoreId();


        /*
        // 리사이클 뷰
        check_list = HomeCeoCheck_recyclerViewWordItemData.createContactsList(16);// 리스트 갯수
        checkRecyclerView = (RecyclerView) findViewById(R.id.homeceo_checklist_recycleView);
        check_nodataLayout = (LinearLayout) findViewById(R.id.homeceo_checklist_nodataLayout);
        check_dataLayout = (LinearLayout)  findViewById(R.id.homeceo_checklist_dataLayout);
        checkRecyclerView.setHasFixedSize(true);

         */

        regFirstInit();
        checkListRecyclerViewAdapter = new Home_Ceo_checkList_RecyclerViewAdpater(checkListList);
        checkListRecyclerView.setAdapter(checkListRecyclerViewAdapter);
        checkListRecyclerView.setLayoutManager(new LinearLayoutManager(Home_Ceo.this, RecyclerView.VERTICAL, false)); //리사이클러뷰 양식지정

        try {

            //사업장 이름 가져오기
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://ec2-3-35-10-138.ap-northeast-2.compute.amazonaws.com:3306/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            StoreApiService apiService = retrofit.create(StoreApiService.class);
            //Long storeCode = Long.valueOf(store_Code);
            //적은 id를 기반으로 db에 검색
            Call<StoreResponseDto> call = apiService.getStore(storeCode);
            call.enqueue(new Callback<StoreResponseDto>() {
                @Override
                public void onResponse(Call<StoreResponseDto> call, Response<StoreResponseDto> response) {
                    if (response.isSuccessful()) {
                        StoreResponseDto storeResponseData  = response.body();
                        storeName.setText(storeResponseData.getName()); //이름 가져와서 넣기
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "실패"+response, Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(Call<StoreResponseDto> call, Throwable t) {
                    // 실패 처리
                    String errorMessage = "요청 실패: " + t.getMessage();
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }

        //체크리스트 리사이클러뷰 값 불러오기
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://ec2-3-35-10-138.ap-northeast-2.compute.amazonaws.com:3306/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    ChecklistApiService apiService = retrofit.create(ChecklistApiService.class);


                    Call<Set<CheckListResponseDto>> call = apiService.getStoreCheckLists(storeId);

                    retrofit2.Response<Set<CheckListResponseDto>> response = call.execute();

                    if (response.isSuccessful()) {
                        final Set<CheckListResponseDto> codeStaffs = response.body();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                regOnResume(codeStaffs);
                            }
                        });
                    } else {

                    }
                } catch (Exception e) {
                    final String errorMsg = e.toString();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleError(errorMsg);
                        }
                    });
                }
            }
        }).start();
        LinearLayout checkListLL = findViewById(R.id.homeceo_checklist_nodataLayout);
        if(checkListRecyclerViewAdapter.getItemCount() == 0){
            checkListLL.setVisibility(View.VISIBLE);
            checkListRecyclerView.setVisibility(View.GONE);
        }else{
            checkNumTv.setText(checkListRecyclerViewAdapter.getItemCount());
            checkListLL.setVisibility(View.GONE);
            checkListRecyclerView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.homeceo_staffInviteCloseIBtn: // 멤버 초대 닫기 버튼
                staffInviteFLayout.setVisibility(View.GONE);
                staffApprovalL.setVisibility(View.GONE);
                commutePerL.setVisibility(View.GONE);
                break;
            case R.id.homeceo_menuIBtn: // 메뉴
                Animation slideInLeft = AnimationUtils.loadAnimation(Home_Ceo.this, R.anim.slide_in_left);
                menuIBtn.startAnimation(slideInLeft);

                intent = new Intent(Home_Ceo.this, Home_menu.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                finish();
                break;
            case R.id.homeceo_alarmIBtn: // 알림
                intent = new Intent(getApplicationContext(), Alarm.class);
                startActivity(intent);
                finish();
                break;
            case R.id.homeceo_selectStoreBtn: // 사업장 선택
                intent = new Intent(getApplicationContext(), SelectStore.class);
                startActivity(intent);
                finish();
                break;
            case R.id.homeceo_mypageBtn: // 마이페이지
                intent = new Intent(getApplicationContext(), MyPage.class);
                startActivity(intent);
                finish();
                break;
            case R.id.homeceo_checklistBtn: // 해야할 일
                intent = new Intent(getApplicationContext(), CheckList.class);
                startActivity(intent);
                finish();
                break;
            case R.id.homeceo_modifyStoreTv: // 사업장 정보
                intent = new Intent(getApplicationContext(), modifyStore.class);
                startActivity(intent);
                finish();
                break;
            case R.id.homeceo_monthstateTv: // 사업장 월별 현황
                intent = new Intent(getApplicationContext(), MonthState.class);
                startActivity(intent);
                finish();
                break;
            case R.id.homeceo_staffInviteBtn: // 멤버 초대
                intent = new Intent(getApplicationContext(), StaffInvite.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }
    public void regFirstInit(){
        checkListRecyclerView = (RecyclerView) findViewById(R.id.homeceo_checklist_recycleView); //리사이클뷰 아이디 받기
        checkListList = new ArrayList<>();
    }

    protected void regOnResume(Set<CheckListResponseDto> list) {
        super.onResume();

        // 이곳에서 리사이클러뷰 데이터를 업데이트하고 어댑터를 갱신합니다.
        RegUpdateRecyclerView(list); // 원하는 업데이트 로직을 여기에 작성

        checkListRecyclerViewAdapter.notifyDataSetChanged(); // 어댑터 갱신
    }


    public void RegAddItem(String name, String date,long id){
        Home_Ceo_checkList_recyclerViewItem item = new Home_Ceo_checkList_recyclerViewItem();

        item.setHome_Check_title(name);
        item.setHome_Check_date(date);
        item.setHome_Check_checkListId(id);

        checkListList.add(item);
    }
    private void  RegUpdateRecyclerView(Set<CheckListResponseDto> list) {
        checkListList.clear(); // 기존 데이터를 모두 지우고 새로운 데이터로 갱신
        for (CheckListResponseDto clRes : list) {
            RegAddItem(clRes.getTitle(), clRes.getDate(),clRes.getId());
        }

        Home_Ceo_checkList_RecyclerViewAdpater.setOnItemClickListener(new Home_Ceo_checkList_RecyclerViewAdpater.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // 해당 아이템 레이아웃 클릭 시 처리할 코드 이쪽 수정 필!
                Home_Ceo_checkList_recyclerViewItem item = checkListList.get(position);
                Intent intent = new Intent(getApplicationContext(), Checklist_detail.class); //사장, 스테프 구분 필요
                //체크리스트 id 받아서 넘기기
                intent.putExtra("checkListId", item.getHome_Check_checkListId());
                //intent.putExtra("staffId", item.getPaperH_reg_staffId);

                startActivity(intent);
                finish();
            }
        });


        checkListRecyclerViewAdapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알림

    }

    private void handleError(String errorMsg) {
        Toast.makeText(this, errorMsg + "!!", Toast.LENGTH_SHORT).show();
    }

}
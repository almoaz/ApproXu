package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.approxsoft.approxu.Model.Page;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PageActivity extends AppCompatActivity {

    Toolbar mToolBar;
    TextView CreateNewPageBtn, pageNameText, PageInvitationBtn, PageNewBtn, PageStarBtn;
    RecyclerView MyPageList;
    String currentUserId;
    FirebaseAuth mAuth;
    DatabaseReference PageReff;
    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);


        mAuth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        PageReff = FirebaseDatabase.getInstance().getReference().child("All Pages");

        mToolBar = findViewById(R.id.page_app_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Page");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pageNameText = findViewById(R.id.page_name_text);

        CreateNewPageBtn = findViewById(R.id.create_page_btn);
        PageInvitationBtn = findViewById(R.id.page_invitation_btn);
        PageNewBtn = findViewById(R.id.page_new_btn);
        PageStarBtn = findViewById(R.id.page_star_btn);
        //refreshLayout = findViewById(R.id.page_swift_refresh_layout);

        MyPageList = findViewById(R.id.all_page_list);
        MyPageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        MyPageList.setLayoutManager(linearLayoutManager);

        DisplayMyPages();


        CreateNewPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(PageActivity.this,CreatePageActivity.class);
                startActivity(intent);
            }
        });

        PageInvitationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PageActivity.this,FourInOnePageActivity.class);
                intent.putExtra("pageType","Invitation");
                startActivity(intent);
            }
        });

        PageNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PageActivity.this,FourInOnePageActivity.class);
                intent.putExtra("pageType","NewPage");
                startActivity(intent);
            }
        });


        PageStarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PageActivity.this,FourInOnePageActivity.class);
                intent.putExtra("pageType","StarPage");
                startActivity(intent);
            }
        });





    }

    private void DisplayMyPages()
    {
        Query query = PageReff.orderByChild("AdminUid")
                .startAt(currentUserId).endAt(currentUserId + " \uf8ff");// haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Page> options = new FirebaseRecyclerOptions.Builder<Page>().setQuery(query, Page.class).build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Page, PageActivity.PageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PageActivity.PageViewHolder pageViewHolder, final int position, @NonNull Page page) {

                //refreshLayout.isRefreshing();
                //refreshLayout.setRefreshing(false);
                pageViewHolder.setPageName(page.getPageName());
                pageViewHolder.setDate(page.getDate());
                pageViewHolder.setPageProfileImage(page.getPageProfileImage());
                final String usersIDs = getRef(position).getKey();

                pageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PageActivity.this,PageHomeActivity.class);
                        intent.putExtra("visit_user_id",usersIDs);
                        startActivity(intent);
                    }
                });

                pageNameText.setVisibility(View.VISIBLE);





            }

            public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_friends_display, parent ,false);
                return new PageViewHolder(view);
            }
        };
        MyPageList.setAdapter(adapter);
        adapter.startListening();

    }
    public class PageViewHolder extends RecyclerView.ViewHolder {

        View mView;

        PageViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setPageProfileImage(String pageProfileImage){
            CircleImageView image = mView.findViewById(R.id.all_users_profile_image);
            Picasso.get().load(pageProfileImage).placeholder(R.drawable.profile_icon).into(image);
        }


        public void setPageName(String pageName){
            TextView myName = mView.findViewById(R.id.all_friend_profile_full_name);
            myName.setText(pageName);
        }

        @SuppressLint("SetTextI18n")
        public void setDate(String date){
            TextView friendsDate = mView.findViewById(R.id.all_friend_university_name);
            friendsDate.setText("Page science: " + date);
        }
    }


}

package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.approxsoft.approxu.Model.Students;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class FindStudentsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView searchBtn;
    private EditText searchInputsText;

    private RecyclerView searchResultForStudent;
    private DatabaseReference userReff,StudentReff;
    private String currentUserId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_students);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        userReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId);


        mToolbar = findViewById(R.id.find_student_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Students");

        searchResultForStudent = findViewById(R.id.search_student_list);
        searchResultForStudent.setHasFixedSize(true);
        searchResultForStudent.setLayoutManager(new LinearLayoutManager(this));


        searchBtn = findViewById(R.id.find_student_btn);
        searchInputsText = findViewById(R.id.find_student_search);

        userReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String university = dataSnapshot.child("CreateUniversityName").getValue().toString();
                    StudentReff = FirebaseDatabase.getInstance().getReference().child("All University").child(university).child("All Students");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        searchInputsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(searchInputsText.getText().toString()))
                {
                    searchBtn.setEnabled(false);

                }
                else {
                    searchBtn.setEnabled(true);
                    searchBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {

                            String searchBoxInputs = searchInputsText.getText().toString();
                            DisplayAllFriends(searchBoxInputs);

                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void DisplayAllFriends( String searchBoxInputs) {

        Query searchStudentQuery = StudentReff.orderByChild("stdId")
                .startAt(searchBoxInputs).endAt(searchBoxInputs + "\uf8ff");

        FirebaseRecyclerOptions<Students> options = new FirebaseRecyclerOptions.Builder<Students>().setQuery(searchStudentQuery, Students.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Students, FindStudentsActivity.StudentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FindStudentsActivity.StudentsViewHolder studentsViewHolder, final int position, @NonNull Students students) {

                studentsViewHolder.setStdId(students.getStdId());
                final String usersIDs = getRef(position).getKey();

                StudentReff.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String userName = dataSnapshot.child("fullName").getValue().toString();
                            final String profileimage = dataSnapshot.child("profileImage").getValue().toString();

                            studentsViewHolder.setFullName(userName);
                            studentsViewHolder.setProfileImage(getApplicationContext(), profileimage);

                            studentsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    userName + "'s SD",
                                                    "Update SD"
                                            };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(FindStudentsActivity.this);
                                    builder.setTitle("Select Option");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            if (which == 0)
                                            {
                                                Intent profileIntent = new Intent(FindStudentsActivity.this,CheckSDActivity.class);
                                                profileIntent.putExtra("visit_user_id",usersIDs);
                                                startActivity(profileIntent);
                                            }
                                            if (which == 1)
                                            {
                                                Intent chatIntent = new Intent(FindStudentsActivity.this,UpdateStudentSdActivity.class);
                                                chatIntent.putExtra("visit_user_id",usersIDs);
                                                chatIntent.putExtra("userName",userName);
                                                startActivity(chatIntent);
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            public StudentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_student_layout, parent ,false);
                return new StudentsViewHolder(view);
            }
        };
        adapter.startListening();
        searchResultForStudent.setAdapter(adapter);
    }
    public static class StudentsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public StudentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProfileImage(Context applicationContext, String profileimage) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.all_student_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile_holder).into(image);
        }

        public void setFullName(String fullName){
            TextView myName = (TextView) mView.findViewById(R.id.all_student_profile_full_name);
            myName.setText(fullName);
        }

        public void setStdId(String sidid){
            TextView stdId = (TextView) mView.findViewById(R.id.all_student_id_no);
            stdId.setText(sidid);
        }
    }
}

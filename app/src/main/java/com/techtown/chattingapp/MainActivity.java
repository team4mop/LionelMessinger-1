package com.techtown.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Chat> chatList;

    private EditText chatText;
    private Button sendButton;

    private DatabaseReference myRef;
    private FirebaseAuth mFirebaseAuth;

    EditText edtnickname;
    LinearLayout baselayout;
    View dialogView;

    ChatAdapter adp;

    String names[] = {"탈모걸린 지단", "치루걸린 지루", "앞니돌출 호나우지뉴", "삼각김밥 호나우두", "2메다 메시",
            "날강두", "미남 이천수", "신의손 마라도나", "축구왕 펠레", "네이마루", "시꺼먼 드록바", "필 뻐킹 포든", "두개의 심장 박지성", "악동 루니", "따봉하는 박주영",
            "물회오리슛 이동국", "면도하는 베컴", "담배피는 즐라탄", "황소 황희찬", "사진찍는 손흥민"};
    int idx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Lionel Messinger");

        getSupportActionBar().setIcon(R.drawable.lm_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mFirebaseAuth = FirebaseAuth.getInstance();

        chatText = findViewById(R.id.chatText);
        sendButton = findViewById(R.id.sendButton);
        //edtnickname = findViewById(R.id.edtnickname);
        //Button btnlogout = findViewById(R.id.btnlogout);
        //Button btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        Random rand = new Random();
        idx = rand.nextInt(20);

        baselayout = findViewById(R.id.menulistLayout);

/*
    회원탈퇴 버튼

    mFirebaseAuth.getCurrentUser().delete();

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth.getCurrentUser().delete();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

 */
/*로그아웃 버튼
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

 */

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //입력창에 메시지를 입력 후 버튼클릭했을 때
                String msg = chatText.getText().toString();

                if(msg != null){
                    Chat chat = new Chat();
                    chat.setName(names[idx]);
                    chat.setMsg(msg);

                    //메시지를 파이어베이스에 보냄.
                    myRef.push().setValue(chat);

                    chatText.setText("");
                }

            }
        });
        //리사이클러뷰에 어댑터 적용
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        chatList = new ArrayList<>();
        adp = new ChatAdapter(chatList, names[idx]);
        adapter = adp;
        //adapter = new ChatAdapter(chatList, names[idx]);

        recyclerView.setAdapter(adapter);

        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");

        //데이터들을 추가, 변경, 제거, 이동, 취소
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                //어댑터에 DTO추가
                Chat chat = snapshot.getValue(Chat.class);
                ((ChatAdapter)adapter).addChat(chat);
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    //메뉴창
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.menu1, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menlogout:
            {
                mFirebaseAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            case R.id.Nickchange:
            {
                dialogView = (View) View.inflate(MainActivity.this,R.layout.changenick,null);

                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("닉네임 변경");
                dlg.setMessage("변경할 닉네임을 입력하세요");
                dlg.setView(dialogView);

                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        edtnickname = (EditText) dialogView.findViewById(R.id.edtnickname);

                        names[idx] = edtnickname.getText().toString();
                        adp.setName(names[idx]);
                    }
                });

                dlg.setNegativeButton("취소", null);

                dlg.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
package com.example.kimhyunwoo.runwithme.MainActivity;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.kimhyunwoo.runwithme.R;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {
    private GetUserListThread UserListThread;
    private ListView listView;
    private ListViewAdapter adapter;
    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        UserListThread = new GetUserListThread();
        UserListThread.start();

        setRetainInstance(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends, container, false);

        adapter = new ListViewAdapter();

        listView = (ListView)v.findViewById(R.id.UserListView);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListViewItem item = (ListViewItem)parent.getItemAtPosition(position);
                String SelectedUserEmail = item.getEmail();
                String SelectedUserNickname = item.getNickname();
                //Todo 메세지 입력창띄우기.
                AlertDialog dialog;
                final EditText InputEditText = new EditText(getContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                dialog = builder.setMessage("To . "+SelectedUserNickname)
                        .setTitle("Message Send")
                        .setView(InputEditText)
                        .setNegativeButton("NO", null)
                        .setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Todo 서버에 메시지 전송
                                String InputMessage = InputEditText.getText().toString();

                            }
                        })
                        .create();
                dialog.show();
            }
        });
        Log.v("User's Log","I'm in Friend Fragment before Thread");

        Log.v("User's Log","I'm in Friend Fragment after Thread");
        return v;
    }

    public class GetUserListThread extends Thread
    {
        public boolean ThreadFlag;
        public GetUserListThread()
        {
            ThreadFlag = false;
        }
        @Override
        public void run()
        {
            while(true)
            {
                Log.v("User's Log","I'm in Thread");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(ThreadFlag)
                {
                    break;
                }

                UserListGetRequest();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void UserListGetRequest()
    {
        adapter.clear();
        Response.Listener<String> reponseListener = new Response.Listener<String>() {
            // Volley 를 통해서 정상적으로 웹서버와 통신이 되면 실행되는 함수
            @Override
            public void onResponse(String response)
            {
                try
                {
                    Log.v("listgetUserlistlog",response);
                    // JSON 형식으로 값을 response 에 받아서 넘어온다.
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONObject Tempmessage = jsonResponse.getJSONObject("json");
                    String message = Tempmessage.getString("message");
                    if(message.equals("ok"))
                    {
                        JSONArray array = Tempmessage.getJSONArray("data");
                        Log.v("Userlistlog",array.toString());
                        for(int i=0;i<array.length();i++)
                        {
                            JSONObject object = array.getJSONObject(i);
                            String Emaildata = object.getString("email");
                            String Nicknamedata = object.getString("name");
                            adapter.addItem(ContextCompat.getDrawable(getContext(),R.drawable.ic_account_box_black_24dp),Nicknamedata,Emaildata);
                        }
                        listView.setAdapter(adapter);
                        Log.v("Adaptercheck", adapter.getItem(0).toString());
                    }
                    else
                    {
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        UserListRequest UserlistRequest = new UserListRequest(reponseListener,getContext());           // 위에서 작성한 리스너를 기반으로 요청하는 클래스를 선언.(LoginRequest참고)
        RequestQueue queue = Volley.newRequestQueue(getContext());            // Volley의 사용법으로 request queue로 queue를 하나 선언하고
        queue.add(UserlistRequest);
    }

    @Override
    public void onDestroy()
    {
        UserListThread.ThreadFlag = true;
        super.onDestroy();
    }
    @Override
    public void onDestroyView()
    {
        UserListThread.ThreadFlag = true;
        super.onDestroyView();
    }
}

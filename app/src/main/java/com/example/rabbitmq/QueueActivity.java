package com.example.rabbitmq;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeoutException;

public class QueueActivity extends AppCompatActivity {
    ConnectionFactory factory;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    ArrayList<String> messages;
    String QUEUE_NAME="tiago";
    String MY_QUEUE="thomas";
    EditText text;
    TextView queueName;
    Button publish;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        text = findViewById(R.id.text);
        queueName = findViewById(R.id.queueName);
        queueName.setText("Enviado para a queue: " + QUEUE_NAME);
        publish = findViewById(R.id.publish);

        factory = new ConnectionFactory();
        build();
        messages = new ArrayList<>();

        mRecyclerView = findViewById(R.id.chat);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ChatAdapter(messages);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        execute();

        publish.setOnClickListener(v -> {
            DateFormat df = new SimpleDateFormat(" HH:mm:ss");
            Thread threadQueueSender = new Thread(() -> {
                try {
                    Connection connection = factory.newConnection();
                    Channel channel = connection.createChannel();
                    channel.queueDeclare(QUEUE_NAME, false, false, false, null);

                    String message = "[Thomas]: "+ text.getText();
                    channel.basicPublish("", QUEUE_NAME, false, null, message.getBytes("UTF-8"));
                    messages.add(df.format(Calendar.getInstance().getTime()) +" "+ message);
                    updateText();
                    updateViews();
                    Log.d("Queue Send", "message sent: " + message);

                    updateViews();
                    channel.close();
                    connection.close();
                } catch (TimeoutException | IOException e) {
                    e.printStackTrace();
                }
            });
            threadQueueSender.start();

        });
    }
    public void updateText(){
        runOnUiThread(() -> text.setText(""));
    }
    private void updateViews(){
        runOnUiThread(() -> mAdapter.notifyDataSetChanged());

    }
    void build(){
        factory.setHost("jackal.rmq.cloudamqp.com");
        factory.setUsername("srfyinmc");
        factory.setPassword("gcckle90rrIydG82JHV972AZ9T9AUrAl");
        factory.setVirtualHost("srfyinmc");
    }
    void execute() {
        Thread threadQueueConsumer = new Thread(() -> {
            try {
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();
                channel.queueDeclare(MY_QUEUE, false, false, false, null);

                channel.basicConsume(MY_QUEUE, true, (consumerTag, message) -> {
                    String response = new String(message.getBody(), "UTF-8");
                    messages.add(response);
                    updateViews();
                    Log.d("Received", "" + response);
                }, consumerTag -> {
                    // nunca vou ser excluido pelo server
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        });
             threadQueueConsumer.start();
        }
}

package com.example.rabbitmq;

import android.os.AsyncTask;
import android.util.Log;

import androidx.loader.content.AsyncTaskLoader;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

public class ReceiverExchanges extends AsyncTask<Void, Void, Void> {
    private String nomeGrupo;

    public ReceiverExchanges(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        try{
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("jackal.rmq.cloudamqp.com");
            factory.setUsername("srfyinmc");
            factory.setPassword("gcckle90rrIydG82JHV972AZ9T9AUrAl");
            factory.setVirtualHost("srfyinmc");
            Connection conexao = factory.newConnection();
            Channel canal = conexao.createChannel();

            canal.exchangeDeclare(nomeGrupo, BuiltinExchangeType.FANOUT);
            String nomeFila = canal.queueDeclare().getQueue();
            canal.queueBind("recebedor", nomeGrupo,"");
            Consumer consumidor;

//            while(true){
            consumidor = new DefaultConsumer(canal){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                    try {
                        String message = new String(body,"UTF-8");
                        Log.d("aguardando", ""+message);
                    }catch (Exception e){
                        Log.d("aguardando",e.getCause()+"");
                    }
                }
            };
            canal.basicConsume(nomeFila,true,consumidor);

            Thread.sleep(5000);
//            }

        }catch (Exception e){
            Log.d("aguardando",e+"a");
        }

        return null;
    }
}
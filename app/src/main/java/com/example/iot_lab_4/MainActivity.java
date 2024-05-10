package com.example.iot_lab_4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    MQTTHelper mqttHelper;
    TextView txtTemp, txtHumi, txtLight;
    LabeledSwitch btnLight, btnFan, btnDry;

    int LightUpperLimit = 600;
    int LightLowerLimit = 200;

    int TemperatureUpperLimit = 30;
    int TemperatureLowerLimit = 20;

    int HumidityUpperLimit = 30;
    int HumidityLowerLimit = 25;

    Date currentTime = Calendar.getInstance().getTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtTemp = findViewById(R.id.txtTemperature);
        txtHumi = findViewById(R.id.txtHumidity);
        txtLight = findViewById(R.id.txtLight);

        btnLight = findViewById(R.id.btnLight);
        btnFan = findViewById(R.id.btnFan);
        btnDry = findViewById(R.id.btnDry);

        btnLight.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn == true){
                    sendDataMQTT("Anh_Ni/feeds/nutnhan1", "ON");
                }else{
                    sendDataMQTT("Anh_Ni/feeds/nutnhan1", "OFF");
                }

            }
        });

        btnFan.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn == true){
                    sendDataMQTT("Anh_Ni/feeds/nutnhan2", "ON");
                }else{
                    sendDataMQTT("Anh_Ni/feeds/nutnhan2", "OFF");
                }

            }
        });

        btnDry.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn == true){
                    sendDataMQTT("Anh_Ni/feeds/nutnhan3", "ON");
                }else{
                    sendDataMQTT("Anh_Ni/feeds/nutnhan3", "OFF");
                }

            }
        });
        startMQTT();


    }

    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);


        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (MqttException e){
        }
    }

    public void compareAndSendDataMQTT(String message, String feeds, int UpperLimit, int LowerLimit){
        if (Integer.parseInt(message) > UpperLimit){
            sendDataMQTT(feeds, "ON");
        } else if(Integer.parseInt(message) < LowerLimit){
            sendDataMQTT(feeds, "OFF");
        }
    }

    public void startMQTT(){
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST", topic + "***" + message.toString());
                if(topic.contains("cambien1")){
                    txtTemp.setText(message.toString() + "°C");
//                    if (Integer.parseInt(message.toString()) > TemperatureUpperLimit){
//                        sendDataMQTT("Anh_Ni/feeds/nutnhan2", "ON");
//                    } else if(Integer.parseInt(message.toString()) < TemperatureLowerLimit){
//                        sendDataMQTT("Anh_Ni/feeds/nutnhan2", "OFF");
//                    }
                    compareAndSendDataMQTT(message.toString(), "Anh_Ni/feeds/nutnhan2", TemperatureUpperLimit, TemperatureLowerLimit);
                }else if(topic.contains("cambien2")){
                    txtHumi.setText(message.toString() + "%");
//                    if (Integer.parseInt(message.toString()) > HumidityUpperLimit){
//                        sendDataMQTT("Anh_Ni/feeds/nutnhan3", "ON");
//                    } else if (Integer.parseInt(message.toString() )
                    compareAndSendDataMQTT(message.toString(), "Anh_Ni/feeds/nutnhan3", HumidityUpperLimit, HumidityLowerLimit);
                }else if(topic.contains("cambien3")){
                    txtLight.setText(message.toString() + " Lux");
                    if (Integer.parseInt(message.toString()) > LightUpperLimit){
                        sendDataMQTT("Anh_Ni/feeds/nutnhan1", "OFF");
                    } else if(Integer.parseInt(message.toString()) < LightLowerLimit){
                        sendDataMQTT("Anh_Ni/feeds/nutnhan1", "ON");
                    }
//                    compareAndSendDataMQTT(message.toString(), "Anh_Ni/feeds/nutnhan1", LightLowerLimit, LightUpperLimit);
                }else if(topic.contains("nutnhan1")){
                    if(message.toString().equals("ON")){
                        btnLight.setOn(true);
                    }else{
                        btnLight.setOn(false);
                    }
                }else if(topic.contains("nutnhan2")){
                    if(message.toString().equals("ON")){
                        btnFan.setOn(true);
                    }else{
                        btnFan.setOn(false);
                    }
                }else if(topic.contains("nutnhan3")){
                    if(message.toString().equals("ON")){
                        btnDry.setOn(true);
                    }else{
                        btnDry.setOn(false);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

}
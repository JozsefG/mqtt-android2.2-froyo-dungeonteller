package info.javadev.dungeonteller.push;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTService extends Service {

    public static final String BROKER_URL = "tcp://broker.mqttdashboard.com:1883";
    //public static final String BROKER_URL = "/tcp://test.mosquitto.org:1883";

    /* In a real application, you should get an Unique Client ID of the device and use this, see
    http://android-developers.blogspot.de/2011/03/identifying-app-installations.html */
    public static final String clientId = "dungeontelleryee";

    public static final String TOPIC_PREFIX = "info/javadev/dungeonteller/char";
    private MqttClient mqttClient;


    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {

        try {
            mqttClient = new MqttClient(BROKER_URL, clientId, new MemoryPersistence());

            mqttClient.setCallback(new PushCallback(this));
            mqttClient.connect();

            String realm = (String) intent.getExtras().get("realm");
            String playerName = (String) intent.getExtras().get("playername");

            String topic = String.format("%s/%s/%s", TOPIC_PREFIX, realm, playerName);
            //Subscribe to realm / player

            mqttClient.subscribe(topic.toLowerCase().replace(" ", "_"));


        } catch (MqttException e) {
            Toast.makeText(getApplicationContext(), "Something went wrong!" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        try {
            mqttClient.disconnect(0);
        } catch (MqttException e) {
            Toast.makeText(getApplicationContext(), "Something went wrong!" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}

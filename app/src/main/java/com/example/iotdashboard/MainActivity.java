package com.example.iotdashboard;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
//import android.os.Parcelable;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Base64;
import android.view.View;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;



public class MainActivity extends AppCompatActivity {
    //IMAGE
    private ImageView imageView;
    private ImageView imageView2;

    //SPEECH
    private static final int SPEECH_REQUEST_CODE = 0;

    //TEMPERATURE
    private LinearLayout linearLayout;
    private GraphView graph;
    private LineGraphSeries<DataPoint> graphSeries;
    private double graphLastXValue = 0d;

    //HUMIDITY
    private LinearLayout linearLayout1;
    private GraphView graph1;
    private LineGraphSeries<DataPoint> graphSeries1;
    private double graphLastXValue1 = 0d;

    CardView tempCard, humiCard;
    TextView txtTemp, txtHumi, txtAI, txtIA;
    LabeledSwitch button1, button2;
    MQTTHelper mqttHelper;

    int id = 0;
    int state = 0;
    boolean ackReceived = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TEMPERATURE
        graph = findViewById(R.id.graph);
        linearLayout = findViewById(R.id.graphLayout);
        linearLayout.setVisibility(View.GONE);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graphSeries = new LineGraphSeries<>();
        graphSeries.setColor(Color.RED); // set color of line to red
        graph.addSeries(graphSeries);

        // HUMIDITY
        graph1 = findViewById(R.id.graph1);
        linearLayout1 = findViewById(R.id.graphLayout1);
        linearLayout1.setVisibility(View.GONE);
        graph1.getViewport().setScalable(true);
        graph1.getViewport().setScalableY(true);
        graphSeries1 = new LineGraphSeries<>();
        graphSeries1.setColor(Color.BLUE); // set color of line to blue
        graph1.addSeries(graphSeries1);

        // SHOW/HIDE GRAPH
        tempCard = findViewById(R.id.tempCard);
        tempCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linearLayout.getVisibility() == View.VISIBLE) {
                    linearLayout.setVisibility(View.GONE);
                } else {
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        // SHOW/HIDE GRAPH
        humiCard = findViewById(R.id.humiCard);
        humiCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linearLayout1.getVisibility() == View.VISIBLE) {
                    linearLayout1.setVisibility(View.GONE);
                } else {
                    linearLayout1.setVisibility(View.VISIBLE);
                }
            }
        });

        txtTemp = findViewById(R.id.txtTemp);
        txtHumi = findViewById(R.id.txtHumi);

        txtAI = findViewById(R.id.AI);
        txtIA = findViewById(R.id.IA);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);

        button1.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true) {
//                    sendDataMQTT("haole1110/feeds/nutnhan1", "1");
                    button1.setEnabled(false);
                    ackReceived = false;
                    state = 1;
                    id = id + 1;
                    sendDataMQTT("haole1110/feeds/ack", Integer.toString(id));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            button1.setEnabled(true);
                            if (!ackReceived) {
                                // Show an alert box with a message
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Thông báo")
                                        .setMessage("Đèn không thể bật được")
                                        .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // OK button clicked
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                                button1.setOn(false);
                            }
                            else {
                                button1.setOn(true);
                            }
                        }
                    }, 5000);
                } else {
//                    sendDataMQTT("haole1110/feeds/nutnhan1", "0");
                    button1.setEnabled(false);
                    ackReceived = false;
                    state = 2;
                    id = id + 1;
                    sendDataMQTT("haole1110/feeds/ack", Integer.toString(id));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            button1.setEnabled(true);
                            if (!ackReceived) {
                                // Show an alert box with a message
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Thông báo")
                                        .setMessage("Đèn không thể tắt được")
                                        .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // OK button clicked
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                                button1.setOn(true);
                            }
                            else {
                                button1.setOn(false);
                            }
                        }
                    }, 5000);
                }
            }
        });

        button2.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true) {
//                    sendDataMQTT("haole1110/feeds/nutnhan2", "1");
                    button2.setEnabled(false);
                    ackReceived = false;
                    state = 3;
                    id = id + 1;
                    sendDataMQTT("haole1110/feeds/ack", Integer.toString(id));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            button2.setEnabled(true);
                            if (!ackReceived) {
                                // Show an alert box with a message
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Thông báo")
                                        .setMessage("Bơm không thể bật được")
                                        .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // OK button clicked
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                                button2.setOn(false);
                            }
                            else {
                                button2.setOn(true);
                            }
                        }
                    }, 5000);
                } else {
//                    sendDataMQTT("haole1110/feeds/nutnhan2", "0");
                    button2.setEnabled(false);
                    ackReceived = false;
                    state = 4;
                    id = id + 1;
                    sendDataMQTT("haole1110/feeds/ack", Integer.toString(id));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            button2.setEnabled(true);
                            if (!ackReceived) {
                                // Show an alert box with a message
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Thông báo")
                                        .setMessage("Bơm không thể tắt được")
                                        .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // OK button clicked
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                                button2.setOn(true);
                            }
                            else {
                                button2.setOn(false);
                            }
                        }
                    }, 5000);
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
                if(topic.contains("cambien1")) {
                    txtTemp.setText(message.toString() + "°C");
                    float value = Float.parseFloat(new String(message.getPayload(), StandardCharsets.UTF_8));
                    graphLastXValue += 1d;
                    graphSeries.appendData(new DataPoint(graphLastXValue, value), true, 100);
                    updateGraph(value);

                } else if(topic.contains("cambien3")) {
                    txtHumi.setText(message.toString() + "%");
                    float value = Float.parseFloat(new String(message.getPayload(), StandardCharsets.UTF_8));
                    graphLastXValue1 += 1d;
                    graphSeries1.appendData(new DataPoint(graphLastXValue1, value), true, 100);
                    updateGraph1(value);
                } else if(topic.contains("nutnhan1")) {
                    if(message.toString().equals("1")) {
                        button1.setOn(true);
                    } else {
                        button1.setOn(false);
                    }
                } else if(topic.contains("nutnhan2")) {
                    if(message.toString().equals("1")) {
                        button2.setOn(true);
                    } else {
                        button2.setOn(false);
                    }
                } else if(topic.contains("image")) {
                    byte[] imageBytes = Base64.decode(message.toString(), Base64.DEFAULT);
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    // Set image to ImageView
                    imageView = findViewById(R.id.imageView);
                    imageView.setImageBitmap(imageBitmap);
                } else if(topic.contains("img")) {
                    byte[] imageBytes1 = Base64.decode(message.toString(), Base64.DEFAULT);
                    Bitmap imageBitmap1 = BitmapFactory.decodeByteArray(imageBytes1, 0, imageBytes1.length);
                    // Set image to ImageView2
                    imageView2 = findViewById(R.id.imageView2);
                    imageView2.setImageBitmap(imageBitmap1);
                } else if(topic.contains("ai")) {
                    txtAI.setText(message.toString());
                } else if(topic.contains("ia")) {
                    txtIA.setText(message.toString());
                } else if (topic.contains("ack")) {
                    if (message.toString().equals(Integer.toString(id))) {
                        switch (state) {
                            case 1:
                                sendDataMQTT("haole1110/feeds/nutnhan1", "1");
                                break;
                            case 2:
                                sendDataMQTT("haole1110/feeds/nutnhan1", "0");
                                break;
                            case 3:
                                sendDataMQTT("haole1110/feeds/nutnhan2", "1");
                                break;
                            case 4:
                                sendDataMQTT("haole1110/feeds/nutnhan2", "0");
                                break;
                        }
                    } else if (message.toString().equals("OK")) {
                        ackReceived = true;
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }

    // TEMPERATURE GRAPH
    private void updateGraph(float value) {
        graphLastXValue += 1d;
        graphSeries.appendData(new DataPoint(graphLastXValue, value), true, 100);
        double maxX = graphLastXValue > 100 ? graphLastXValue : 100;
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(graphLastXValue);
        graph.getViewport().scrollToEnd();
    }

    // HUMIDITY GRAPH
    private void updateGraph1(float value) {
        graphLastXValue1 += 1d;
        graphSeries1.appendData(new DataPoint(graphLastXValue1, value), true, 100);
        double maxX = graphLastXValue1 > 100 ? graphLastXValue1 : 100;
        graph1.getViewport().setMinX(1);
        graph1.getViewport().setMaxX(graphLastXValue1);
        graph1.getViewport().scrollToEnd();
    }

    // SPEECH ACTIVITY
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // This starts the activity and populates the intent with the speech text.
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText.
            if (spokenText.toLowerCase().contains("bật đèn")) {
                button1.setEnabled(false);
                ackReceived = false;
                state = 1;
                id = id + 1;
                sendDataMQTT("haole1110/feeds/ack", Integer.toString(id));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        button1.setEnabled(true);
                        if (!ackReceived) {
                            // Show an alert box with a message
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Thông báo")
                                    .setMessage("Đèn không thể bật được")
                                    .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // OK button clicked
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                            button1.setOn(false);
                        }
                        else {
                            button1.setOn(true);
                        }
                    }
                }, 5000);
            }
            if (spokenText.toLowerCase().contains("tắt đèn")) {
                button1.setEnabled(false);
                ackReceived = false;
                state = 2;
                id = id + 1;
                sendDataMQTT("haole1110/feeds/ack", Integer.toString(id));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        button1.setEnabled(true);
                        if (!ackReceived) {
                            // Show an alert box with a message
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Thông báo")
                                    .setMessage("Đèn không thể tắt được")
                                    .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // OK button clicked
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                            button1.setOn(true);
                        }
                        else {
                            button1.setOn(false);
                        }
                    }
                }, 5000);
            }
            if (spokenText.toLowerCase().contains("bật máy bơm")) {
                button2.setEnabled(false);
                ackReceived = false;
                state = 3;
                id = id + 1;
                sendDataMQTT("haole1110/feeds/ack", Integer.toString(id));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        button2.setEnabled(true);
                        if (!ackReceived) {
                            // Show an alert box with a message
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Thông báo")
                                    .setMessage("Bơm không thể bật được")
                                    .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // OK button clicked
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                            button2.setOn(false);
                        }
                        else {
                            button2.setOn(true);
                        }
                    }
                }, 5000);
            }
            if (spokenText.toLowerCase().contains("tắt máy bơm")) {
                button2.setEnabled(false);
                ackReceived = false;
                state = 4;
                id = id + 1;
                sendDataMQTT("haole1110/feeds/ack", Integer.toString(id));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        button2.setEnabled(true);
                        if (!ackReceived) {
                            // Show an alert box with a message
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Thông báo")
                                    .setMessage("Bơm không thể tắt được")
                                    .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // OK button clicked
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                            button2.setOn(true);
                        }
                        else {
                            button2.setOn(false);
                        }
                    }
                }, 5000);
            }
            if (spokenText.toLowerCase().contains("hiện đồ thị nhiệt độ")) {
                linearLayout.setVisibility(View.VISIBLE);
            }
            if (spokenText.toLowerCase().contains("ẩn đồ thị nhiệt độ")) {
                linearLayout.setVisibility(View.GONE);
            }
            if (spokenText.toLowerCase().contains("hiện đồ thị độ ẩm")) {
                linearLayout1.setVisibility(View.VISIBLE);
            }
            if (spokenText.toLowerCase().contains("ẩn đồ thị độ ẩm")) {
                linearLayout1.setVisibility(View.GONE);
            }
            if (requestCode == SPEECH_REQUEST_CODE && resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
                // Handle speech recognition errors
                Toast.makeText(this, "Audio error occurred", Toast.LENGTH_SHORT).show();
            }
            if (requestCode == SPEECH_REQUEST_CODE && resultCode == RecognizerIntent.RESULT_NO_MATCH) {
                // Handle no speech detected
                Toast.makeText(this, "No speech detected", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void onSpeechButtonClicked(View view) {
        displaySpeechRecognizer();
    }
}
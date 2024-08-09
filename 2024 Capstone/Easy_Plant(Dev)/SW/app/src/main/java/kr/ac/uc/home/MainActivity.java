package kr.ac.uc.home;
import android.Manifest; // 안드로이드 권한 매니페스트
import android.app.Activity;
import android.bluetooth.BluetoothAdapter; // 블루투스 어댑터 클래스 임포트
import android.bluetooth.BluetoothDevice; // 블루투스 장치 클래스 임포트
import android.bluetooth.BluetoothSocket; // 블루투스 소켓 클래스 임포트
import android.content.BroadcastReceiver; // 브로드캐스트 리시버 클래스 임포트
import android.content.Context; // 안드로이드 컨텍스트 클래스 임포트
import android.content.Intent; // 인텐트 클래스 임포트
import android.content.IntentFilter; // 인텐트 필터 클래스 임포트
import android.content.pm.PackageManager; // 패키지 매니저 클래스 임포트
import android.os.Bundle; // 번들 클래스 임포트
import android.os.Handler; // 핸들러 클래스 임포트
import android.os.Looper; // 루퍼 클래스 임포트
import android.os.Message; // 메시지 클래스 임포트
/*
import android.support.v4.app.ActivityCompat; // 액티비티 컴팩트 클래스 임포트
import android.support.v4.content.ContextCompat; // 컨텍스트 컴팩트 클래스 임포트
import android.support.v7.app.AppCompatActivity; // 앱 컴팩트 액티비티 클래스 임포트*/

import android.util.Log; // 로그 클래스 임포트
import android.view.View; // 뷰 클래스 임포트
import android.widget.AdapterView; // 어댑터 뷰 클래스 임포트
import android.widget.ArrayAdapter; // 어레이 어댑터 클래스 임포트
import android.widget.Button; // 버튼 클래스 임포트
import android.widget.CheckBox; // 체크박스 클래스 임포트
import android.widget.ListView; // 리스트뷰 클래스 임포트
import android.widget.TextView; // 텍스트뷰 클래스 임포트
import android.widget.Toast; // 토스트 클래스 임포트

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity; // 추가 AppCompat 상호작용
import androidx.core.app.ActivityCompat;

import java.io.IOException; // IO 예외 클래스 임포트
import java.lang.reflect.Method; // 리플렉션 메소드 클래스 임포트
import java.nio.charset.StandardCharsets;
import java.util.Set; // 세트 클래스 임포트
import java.util.UUID; // UUID 클래스 임포트

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName(); // 로그 태그

    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "임의"의 고유 식별자

    // 호출 함수 간에 공유되는 유형 식별을 위한 #defines
    private final static int REQUEST_ENABLE_BT = 1; // 블루투스 이름 추가를 식별하는 데 사용
    public final static int MESSAGE_READ = 2; // 블루투스 핸들러에서 메시지 업데이트 식별에 사용
    private final static int CONNECTING_STATUS = 3; // 블루투스 핸들러에서 메시지 상태 식별에 사용
    private static final int REQUEST_BLUETOOTH_CONNECT = 2;
    private static final int REQUEST_FINE_LOCATION = 3;

    // GUI 구성 요소
    private TextView mBluetoothStatus; // 블루투스 상태 텍스트뷰
    private TextView tvWaterLevel;
    private TextView tvTemp;
    private TextView tvHumi;
    private Button mScanBtn; // 스캔 버튼
    private Button mOffBtn; // 블루투스 끄기 버튼
    private Button mListPairedDevicesBtn; // 페어링된 장치 목록 버튼
    private Button mDiscoverBtn; // 장치 검색 버튼
    private ListView mDevicesListView; // 장치 리스트뷰
    private CheckBox mLED1; // LED1 체크박스

    private BluetoothAdapter mBTAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> mPairedDevices; // 페어링된 장치 세트
    private ArrayAdapter<String> mBTArrayAdapter; // 블루투스 어레이 어댑터

    private Handler mHandler; // 콜백 알림을 받을 주요 핸들러
    private ConnectedThread mConnectedThread; // 데이터 송수신을 위한 블루투스 백그라운드 작업 스레드
    private BluetoothSocket mBTSocket = null; // 양방향 클라이언트-클라이언트 데이터 경로

    private ActivityResultLauncher<Intent> enableBluetoothLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ActivityResultLauncher 초기화
        enableBluetoothLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        mBluetoothStatus.setText(getString(R.string.sEnabled));
                    } else {
                        mBluetoothStatus.setText(getString(R.string.sDisabled));
                    }
                }
        );

        checkPermissions();

        mBluetoothStatus = (TextView)findViewById(R.id.bluetooth_status); // 블루투스 상태 텍스트뷰 초기화
        tvWaterLevel = (TextView) findViewById(R.id.tvWaterLevel);
        tvTemp = (TextView) findViewById(R.id.tvTemp);
        tvHumi = (TextView) findViewById(R.id.tvHumi);
        mScanBtn = (Button)findViewById(R.id.scan); // 스캔 버튼 초기화
        mOffBtn = (Button)findViewById(R.id.off); // 블루투스 끄기 버튼 초기화
        mDiscoverBtn = (Button)findViewById(R.id.discover); // 장치 검색 버튼 초기화
        mListPairedDevicesBtn = (Button)findViewById(R.id.paired_btn); // 페어링된 장치 목록 버튼 초기화
        mLED1 = (CheckBox)findViewById(R.id.checkbox_led_1); // LED1 체크박스 초기화

        mBTArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1); // 블루투스 어레이 어댑터 초기화
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터 초기화

        mDevicesListView = (ListView)findViewById(R.id.devices_list_view); // 장치 리스트뷰 초기화
        mDevicesListView.setAdapter(mBTArrayAdapter); // 어댑터를 리스트뷰에 설정
        mDevicesListView.setOnItemClickListener(mDeviceClickListener); // 리스트뷰 항목 클릭 리스너 설정

        // 위치 권한이 허용되지 않았으면 요청
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        // 핸들러 초기화
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){ // --데이터 송신--
                if(msg.what == MESSAGE_READ){ // 메시지가 읽기 메시지일 경우
                    byte[] readBuffer = (byte[]) msg.obj;
                    String readMessage = new String(readBuffer, StandardCharsets.UTF_8);
                    // 데이터를 구분하여 TextView에 표시
                    String[] data = readMessage.split("\n");
                    for (String datum : data) {
                        if (datum.startsWith("WaterLevel:")) {
                            tvWaterLevel.setText(datum.substring("WaterLevel:".length()).trim());
                        } else if (datum.startsWith("Temperature:")) {
                            tvTemp.setText(datum.substring("Temperature:".length()).trim());
                        } else if (datum.startsWith("Humidity:")) {
                            tvHumi.setText(datum.substring("Humidity:".length()).trim());
                        }
                    }
                }else{
                    Log.e(TAG, "수신된 메시지가 바이트 배열이 아닙니다.");
                }

                if(msg.what == CONNECTING_STATUS){ // 메시지가 연결 상태일 경우
                    if(msg.arg1 == 1) // 연결 성공 시
                        mBluetoothStatus.setText(getString(R.string.BTConnected) + msg.obj);
                    else // 연결 실패 시
                        mBluetoothStatus.setText(getString(R.string.BTconnFail));
                }
            }
        };

        // 블루투스 어댑터가 없으면 블루투스를 지원하지 않는 장치
        if (mBTArrayAdapter == null) {
            mBluetoothStatus.setText(getString(R.string.sBTstaNF)); // 블루투스 상태 설정
            Toast.makeText(getApplicationContext(),getString(R.string.sBTdevNF),Toast.LENGTH_SHORT).show(); // 토스트 메시지 출력
        }
        else {
            // LED1 체크박스 클릭 리스너 설정
            mLED1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(mConnectedThread != null) { // 스레드가 생성된 경우
                        if (mLED1.isChecked()){
                            mConnectedThread.write("1"); // "1" 메시지 전송
                        }else{
                            mConnectedThread.write("0"); // "0" 메시지 전송
                        }
                    }
                }
            });

            // 스캔 버튼 클릭 리스너 설정
            mScanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothOn(); // 블루투스 켜기 메서드 호출
                }
            });

            // 블루투스 끄기 버튼 클릭 리스너 설정
            mOffBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    bluetoothOff(); // 블루투스 끄기 메서드 호출
                }
            });

            // 페어링된 장치 목록 버튼 클릭 리스너 설정
            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    listPairedDevices(); // 페어링된 장치 목록 표시 메서드 호출
                }
            });

            // 장치 검색 버튼 클릭 리스너 설정
            mDiscoverBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    discover(); // 장치 검색 메서드 호출
                }
            });
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_FINE_LOCATION:
            case REQUEST_BLUETOOTH_CONNECT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with Bluetooth operations
                    if (requestCode == REQUEST_BLUETOOTH_CONNECT) {
                        listPairedDevices();
                    }
                } else {
                    // Permission denied
                    Toast.makeText(this, "Permission denied. Bluetooth functionality may be limited.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    // 권한 확인 및 요청 메서드 추가
    private void checkPermissions() {
        // Check for Bluetooth permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request missing permissions
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_FINE_LOCATION);
        }
    }

    // 블루투스 켜기 메서드
    private void bluetoothOn() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
            if (!mBTAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBluetoothLauncher.launch(enableBtIntent);
                mBluetoothStatus.setText(getString(R.string.BTEnable));
                Toast.makeText(getApplicationContext(), getString(R.string.sBTturON), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.BTisON), Toast.LENGTH_SHORT).show();
            }
        } else {
            checkPermissions(); // 필요한 권한 요청
        }
    }

    // 사용자가 라디오 활성화에 대해 "예" 또는 "아니오"를 선택한 후 여기에 진입
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {
        // 응답할 요청 확인
        super.onActivityResult(requestCode, resultCode, Data);
        if (requestCode == REQUEST_ENABLE_BT) {
            // 요청이 성공했는지 확인
            if (resultCode == RESULT_OK) {
                mBluetoothStatus.setText(getString(R.string.sEnabled)); // 활성화 성공 시 블루투스 상태 업데이트
            } else
                mBluetoothStatus.setText(getString(R.string.sDisabled)); // 활성화 실패 시 블루투스 상태 업데이트
        }
    }

    // 블루투스 끄기 메서드
    private void bluetoothOff() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED) {
            mBTAdapter.disable();
            mBluetoothStatus.setText(getString(R.string.sBTdisabl));
            Toast.makeText(getApplicationContext(), "Bluetooth turned Off", Toast.LENGTH_SHORT).show();
        } else {
            checkPermissions(); // 필요한 권한 요청
        }
    }

    // 장치 검색 메서드
    private void discover() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mBTAdapter.isDiscovering()) {
                mBTAdapter.cancelDiscovery();
                Toast.makeText(getApplicationContext(), getString(R.string.DisStop), Toast.LENGTH_SHORT).show();
            } else {
                if (mBTAdapter.isEnabled()) {
                    mBTArrayAdapter.clear();
                    mBTAdapter.startDiscovery();
                    Toast.makeText(getApplicationContext(), getString(R.string.DisStart), Toast.LENGTH_SHORT).show();
                    registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.BTnotOn), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            checkPermissions(); // 필요한 권한 요청
        }
    }

    // 브로드캐스트 리시버 객체
    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction(); // 인텐트 액션 확인
            if(BluetoothDevice.ACTION_FOUND.equals(action)){ // 장치가 발견되었을 때
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); // 블루투스 장치 객체 얻기
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
                    return;
                }
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress()); // 장치 이름과 주소를 어레이 어댑터에 추가
                mBTArrayAdapter.notifyDataSetChanged(); // 어댑터 데이터 변경 알림
            }
        }
    };

    // 페어링된 장치 목록 표시 메서드
    private void listPairedDevices() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            mBTArrayAdapter.clear();
            mPairedDevices = mBTAdapter.getBondedDevices();
            if (mBTAdapter.isEnabled()) {
                for (BluetoothDevice device : mPairedDevices) {
                    mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                Toast.makeText(getApplicationContext(), getString(R.string.show_paired_devices), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.BTnotOn), Toast.LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
            //checkPermissions();
        }
    }

    // 장치 리스트뷰 항목 클릭 리스너
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(!mBTAdapter.isEnabled()) { // 블루투스가 꺼져 있으면
                Toast.makeText(getBaseContext(), getString(R.string.BTnotOn), Toast.LENGTH_SHORT).show(); // 토스트 메시지 출력
                return;
            }

            mBluetoothStatus.setText(getString(R.string.cConnet)); // 블루투스 상태 업데이트
            // 장치 MAC 주소를 얻기, 이는 뷰의 마지막 17 자임
            String info = ((TextView) view).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            // GUI 스레드 블로킹을 피하기 위해 새 스레드 생성
            new Thread() {
                @Override
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address); // 원격 장치 얻기

                    try {
                        mBTSocket = createBluetoothSocket(device); // 블루투스 소켓 생성
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), getString(R.string.ErrSockCrea), Toast.LENGTH_SHORT).show(); // 소켓 생성 실패 시 토스트 메시지 출력
                    }
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // 권한이 없을 경우 요청
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
                        return;
                    }
                    // 블루투스 소켓 연결 설정
                    try {
                        mBTSocket.connect(); // 소켓 연결
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close(); // 연결 실패 시 소켓 닫기
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1) // 연결 실패 메시지 보내기
                                    .sendToTarget();
                        } catch (IOException e2) {
                            // 이 상황 처리 코드 삽입
                            Toast.makeText(getBaseContext(), getString(R.string.ErrSockCrea), Toast.LENGTH_SHORT).show(); // 소켓 생성 실패 시 토스트 메시지 출력
                        }
                    }
                    if(!fail) { // 실패하지 않았을 경우
                        mConnectedThread = new ConnectedThread(mBTSocket, mHandler); // 연결된 스레드 생성
                        mConnectedThread.start(); // 스레드 시작

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name) // 연결 성공 메시지 보내기
                                .sendToTarget();
                    }
                }
            }.start();
        }
    };

    // 블루투스 소켓 생성 메서드
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
            return null;
        }

        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
        return socket;
    }

}

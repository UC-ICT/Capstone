package kr.ac.uc.home;

import static android.content.ContentValues.TAG;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ConnectedThread extends Thread{
    private final BluetoothSocket mmSocket; // 블루투스 소켓 객체
    private final InputStream mmInStream; // 입력 스트림 객체
    private final OutputStream mmOutStream; // 출력 스트림 객체
    private final Handler mHandler; // UI 업데이트를 위한 핸들러 객체

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        mmSocket = socket; // 생성자 매개변수로 전달된 소켓을 멤버 변수에 저장
        mHandler = handler; // 생성자 매개변수로 전달된 핸들러를 멤버 변수에 저장
        InputStream tmpIn = null; // 임시 입력 스트림 변수 초기화
        OutputStream tmpOut = null; // 임시 출력 스트림 변수 초기화

        // 임시 객체를 사용하여 입력 및 출력 스트림을 가져옵니다.
        // 멤버 스트림은 최종 스트림입니다.
        try {
            tmpIn = socket.getInputStream(); // 소켓으로부터 입력 스트림을 얻어옴
            tmpOut = socket.getOutputStream(); // 소켓으로부터 출력 스트림을 얻어옴
        } catch (IOException e) { // 예외 발생 시
            e.printStackTrace(); // 예외 스택 트레이스를 출력
        }

        mmInStream = tmpIn; // 입력 스트림을 멤버 변수에 저장
        mmOutStream = tmpOut; // 출력 스트림을 멤버 변수에 저장
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024]; // 스트림 버퍼 초기화
        int bytes; // 읽은 바이트 수 변수 선언

        // 예외가 발생할 때까지 입력 스트림을 계속 듣습니다.
        while (true) {
            try {
                // 입력 스트림에서 읽습니다.
                bytes = mmInStream.available(); // 입력 스트림에서 읽을 수 있는 바이트 수 확인
                if (bytes != 0) { // 읽을 바이트가 있으면
                    buffer = new byte[1024]; // 버퍼 초기화
                    SystemClock.sleep(100); // 나머지 데이터를 기다리기 위해 잠시 대기
                    bytes = mmInStream.available(); // 다시 읽을 수 있는 바이트 수 확인
                    bytes = mmInStream.read(buffer, 0, bytes); // 실제로 읽은 바이트 수를 버퍼에 저장

                    mHandler.obtainMessage(MainActivity.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget(); // 읽은 바이트를 핸들러에 메시지로 보내서 UI 업데이트
                }
            } catch (IOException e) { // 예외 발생 시
                e.printStackTrace(); // 예외 스택 트레이스를 출력
                break; // while 루프를 빠져나옴
            }
        }
    }

    /* 원격 장치로 데이터를 보내기 위해 메인 액티비티에서 호출 */
    public void write(String input) {
        byte[] bytes = input.getBytes(); // 입력된 문자열을 바이트 배열로 변환
        try {
            mmOutStream.write(bytes); // 출력 스트림에 바이트 배열을 씀
            Log.d(TAG, "Message Sent: " + input);
        } catch (IOException e) { // 예외 발생 시
            e.printStackTrace(); // 예외 스택 트레이스를 출력
        }
    }

    /* 연결을 종료하기 위해 메인 액티비티에서 호출 */
    public void cancel() {
        try {
            mmSocket.close(); // 블루투스 소켓을 닫음
        } catch (IOException e) { // 예외 발생 시
            e.printStackTrace(); // 예외 스택 트레이스를 출력
        }
    }
}

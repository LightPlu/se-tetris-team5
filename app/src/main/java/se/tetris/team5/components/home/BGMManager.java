package se.tetris.team5.components.home;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import se.tetris.team5.utils.setting.GameSettings;

/**
 * BGM을 관리하는 클래스
 * 로딩 화면과 홈 화면에서 각각 다른 BGM을 재생합니다.
 */
public class BGMManager {
    
    private static BGMManager instance;
    private Clip currentClip;
    private String currentBGM = "";
    private GameSettings gameSettings;
    
    // BGM 파일 경로 (여러 형식 시도)
    private static final String[] LOADING_BGM_FILES = {"loadingbgm.wav", "loadingbgm.mp3"};
    private static final String[] MAIN_BGM_FILES = {"mainbgm.wav", "mainbgm.mp3"};
    private static final String[] SCORE_BGM_FILES = {"backgroundmusic.mp3", "backgroundmusic.wav"};
    
    private BGMManager() {
        gameSettings = GameSettings.getInstance();
    }
    
    /**
     * 싱글톤 인스턴스 반환
     */
    public static BGMManager getInstance() {
        if (instance == null) {
            instance = new BGMManager();
        }
        return instance;
    }
    
    /**
     * 로딩 BGM을 재생합니다
     */
    public void playLoadingBGM() {
        if (!gameSettings.isSoundEnabled()) {
            return;
        }
        playBGMFromFiles(LOADING_BGM_FILES, "loading");
    }
    
    /**
     * 메인 BGM을 재생합니다
     */
    public void playMainBGM() {
        if (!gameSettings.isSoundEnabled()) {
            return;
        }
        playBGMFromFiles(MAIN_BGM_FILES, "main");
    }
    
    /**
     * 스코어 화면용 BGM 재생
     */
    public void playScoreBGM() {
        if (!gameSettings.isSoundEnabled()) {
            return;
        }
        playBGMFromFiles(SCORE_BGM_FILES, "score");
    }
    
    /**
     * 모든 BGM을 정지합니다
     */
    public void stopBGM() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
        }
        currentBGM = "";
        System.out.println("BGM stopped");
    }
    
    /**
     * 여러 파일 형식을 시도하여 BGM을 재생합니다
     * @param bgmFiles 시도할 BGM 파일명들
     * @param bgmType BGM 타입 (식별용)
     */
    private void playBGMFromFiles(String[] bgmFiles, String bgmType) {
        for (String bgmFile : bgmFiles) {
            if (playBGM(bgmFile, bgmType)) {
                return; // 성공적으로 재생되면 종료
            }
        }
        System.out.println("Failed to play BGM for type: " + bgmType);
    }
    
    /**
     * BGM을 재생합니다
     * @param bgmFile BGM 파일명
     * @param bgmType BGM 타입 (식별용)
     * @return 재생 성공 여부
     */
    private boolean playBGM(String bgmFile, String bgmType) {
        // 같은 BGM이 이미 재생 중이면 무시
        if (currentBGM.equals(bgmType) && currentClip != null && currentClip.isRunning()) {
            return true; // 이미 재생 중이므로 성공으로 처리
        }
        
        // 기존 BGM 정지
        stopBGM();
        
        AudioInputStream baseStream = null;
        AudioInputStream audioStream = null;
        try {
            // 리소스에서 BGM 파일 로드
            URL bgmUrl = getClass().getClassLoader().getResource(bgmFile);
            if (bgmUrl == null) {
                String[] paths = {
                    "app/src/main/resources/" + bgmFile,
                    "src/main/resources/" + bgmFile,
                    bgmFile
                };
                for (String path : paths) {
                    File file = new File(path);
                    if (file.exists()) {
                        bgmUrl = file.toURI().toURL();
                        break;
                    }
                }
            }
            if (bgmUrl != null) {
                baseStream = AudioSystem.getAudioInputStream(bgmUrl);
                audioStream = ensurePCM(baseStream);
                currentClip = AudioSystem.getClip();
                currentClip.open(audioStream);
                
                // 볼륨 조절 (게임 설정에 따라)
                adjustVolume();
                
                // BGM 루프 재생
                currentClip.loop(Clip.LOOP_CONTINUOUSLY);
                currentClip.start();
                
                currentBGM = bgmType;
                System.out.println("BGM started: " + bgmFile + " (" + bgmType + ")");
                return true; // 성공
                
            } else {
                System.out.println("BGM file not found: " + bgmFile);
                return false; // 파일 없음
            }
            
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Unsupported audio format: " + bgmFile + " - " + e.getMessage());
            return false; // 지원되지 않는 형식
        } catch (IOException e) {
            System.out.println("Error loading BGM file: " + bgmFile + " - " + e.getMessage());
            return false; // IO 오류
        } catch (LineUnavailableException e) {
            System.out.println("Audio line unavailable: " + bgmFile + " - " + e.getMessage());
            return false; // 오디오 라인 오류
        } catch (Exception e) {
            System.out.println("Unexpected error playing BGM: " + bgmFile + " - " + e.getMessage());
            return false; // 기타 오류
        } finally {
            try {
                if (audioStream != null) {
                    audioStream.close();
                }
            } catch (Exception ignored) {}
            try {
                if (baseStream != null && baseStream != audioStream) {
                    baseStream.close();
                }
            } catch (Exception ignored) {}
        }
    }
    
    private AudioInputStream ensurePCM(AudioInputStream source) throws Exception {
        AudioFormat baseFormat = source.getFormat();
        if (AudioFormat.Encoding.PCM_SIGNED.equals(baseFormat.getEncoding()) ||
            AudioFormat.Encoding.PCM_UNSIGNED.equals(baseFormat.getEncoding())) {
            return source;
        }
        
        AudioFormat decodedFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            baseFormat.getSampleRate(),
            16,
            baseFormat.getChannels(),
            baseFormat.getChannels() * 2,
            baseFormat.getSampleRate(),
            false
        );
        return AudioSystem.getAudioInputStream(decodedFormat, source);
    }
    
    /**
     * 볼륨을 조절합니다
     */
    private void adjustVolume() {
        if (currentClip != null && currentClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            try {
                FloatControl gainControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
                
                // 볼륨을 50%로 설정 (-6dB)
                float volume = -6.0f;
                
                // 게임 설정의 사운드 볼륨이 있다면 적용
                // (GameSettings에 볼륨 설정이 있다면 여기서 사용)
                
                gainControl.setValue(volume);
                System.out.println("Volume adjusted to: " + volume + "dB");
                
            } catch (Exception e) {
                System.out.println("Failed to adjust volume: " + e.getMessage());
            }
        }
    }
    
    /**
     * 현재 재생 중인 BGM 타입을 반환합니다
     */
    public String getCurrentBGM() {
        return currentBGM;
    }
    
    /**
     * BGM이 재생 중인지 확인합니다
     */
    public boolean isPlaying() {
        return currentClip != null && currentClip.isRunning();
    }
    
    /**
     * BGM을 일시정지합니다
     */
    public void pauseBGM() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            System.out.println("BGM paused");
        }
    }
    
    /**
     * 일시정지된 BGM을 재개합니다
     */
    public void resumeBGM() {
        if (currentClip != null && !currentClip.isRunning() && !currentBGM.isEmpty()) {
            if (gameSettings.isSoundEnabled()) {
                currentClip.start();
                System.out.println("BGM resumed");
            }
        }
    }
    
    /**
     * 사운드 설정 변경 시 호출되는 메서드
     */
    public void onSoundSettingChanged() {
        if (!gameSettings.isSoundEnabled() && isPlaying()) {
            // 사운드가 비활성화되면 현재 재생 중인 BGM 정지
            stopBGM();
            System.out.println("Sound disabled - BGM stopped");
        }
        // 사운드가 활성화되면 각 화면에서 적절한 BGM을 다시 재생하도록 함
    }
    
    /**
     * 리소스 정리
     */
    public void cleanup() {
        stopBGM();
        instance = null;
    }
}

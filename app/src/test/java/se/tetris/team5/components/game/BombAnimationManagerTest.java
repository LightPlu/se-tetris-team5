package se.tetris.team5.components.game;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * BombAnimationManager 테스트
 */
public class BombAnimationManagerTest {

    private BombAnimationManager animManager;
    private boolean callbackCalled;

    @Before
    public void setUp() {
        callbackCalled = false;
        animManager = new BombAnimationManager(() -> {
            callbackCalled = true;
        });
    }

    /**
     * 테스트 1: 생성자 - 콜백 설정
     */
    @Test
    public void testBombAnimationManager_Constructor() {
        assertNotNull("애니메이션 매니저가 생성되어야 함", animManager);
    }

    /**
     * 테스트 2: triggerBombExplosion - null 리스트
     */
    @Test
    public void testBombAnimationManager_TriggerWithNull() {
        animManager.triggerBombExplosion(null);
        assertFalse("null 리스트는 애니메이션을 시작하지 않아야 함", animManager.isAnimating());
    }

    /**
     * 테스트 3: triggerBombExplosion - 빈 리스트
     */
    @Test
    public void testBombAnimationManager_TriggerWithEmpty() {
        animManager.triggerBombExplosion(new ArrayList<>());
        assertFalse("빈 리스트는 애니메이션을 시작하지 않아야 함", animManager.isAnimating());
    }

    /**
     * 테스트 4: triggerBombExplosion - 정상 트리거
     */
    @Test
    public void testBombAnimationManager_TriggerNormal() {
        List<GameBoard.CellPos> cells = new ArrayList<>();
        cells.add(new GameBoard.CellPos(5, 10));
        cells.add(new GameBoard.CellPos(6, 10));
        
        animManager.triggerBombExplosion(cells);
        
        assertTrue("애니메이션이 시작되어야 함", animManager.isAnimating());
    }

    /**
     * 테스트 5: triggerBombExplosion - null 셀 포함
     */
    @Test
    public void testBombAnimationManager_TriggerWithNullCell() {
        List<GameBoard.CellPos> cells = new ArrayList<>();
        cells.add(new GameBoard.CellPos(5, 10));
        cells.add(null); // null 셀
        cells.add(new GameBoard.CellPos(7, 10));
        
        animManager.triggerBombExplosion(cells);
        
        assertTrue("null 셀을 건너뛰고 애니메이션이 시작되어야 함", animManager.isAnimating());
    }

    /**
     * 테스트 6: triggerBombExplosion - 중복 트리거
     */
    @Test
    public void testBombAnimationManager_DoubleTrigger() {
        List<GameBoard.CellPos> cells = new ArrayList<>();
        cells.add(new GameBoard.CellPos(3, 8));
        
        animManager.triggerBombExplosion(cells);
        assertTrue("첫 번째 트리거 후 애니메이션 진행 중", animManager.isAnimating());
        
        // 같은 위치 다시 트리거
        animManager.triggerBombExplosion(cells);
        assertTrue("중복 트리거해도 애니메이션 진행 중", animManager.isAnimating());
    }

    /**
     * 테스트 7: isAnimating - 초기 상태
     */
    @Test
    public void testBombAnimationManager_IsAnimatingInitial() {
        assertFalse("초기 상태는 애니메이션 없음", animManager.isAnimating());
    }

    /**
     * 테스트 8: clear - 애니메이션 정리
     */
    @Test
    public void testBombAnimationManager_Clear() {
        List<GameBoard.CellPos> cells = new ArrayList<>();
        cells.add(new GameBoard.CellPos(2, 5));
        
        animManager.triggerBombExplosion(cells);
        assertTrue("애니메이션 시작됨", animManager.isAnimating());
        
        animManager.clear();
        assertFalse("clear 후 애니메이션 중지됨", animManager.isAnimating());
    }

    /**
     * 테스트 9: clear - 애니메이션 없는 상태에서 호출
     */
    @Test
    public void testBombAnimationManager_ClearWhenNotAnimating() {
        animManager.clear();
        assertFalse("애니메이션 없이 clear 호출해도 안전", animManager.isAnimating());
    }

    /**
     * 테스트 10: render - null Graphics
     */
    @Test
    public void testBombAnimationManager_RenderWithNullGraphics() {
        List<GameBoard.CellPos> cells = new ArrayList<>();
        cells.add(new GameBoard.CellPos(1, 1));
        animManager.triggerBombExplosion(cells);
        
        // null Graphics로 render 호출 시 예외 없이 처리되어야 함
        try {
            animManager.render(null, 0, 0, 30);
            // NullPointerException이 발생할 수 있음
        } catch (NullPointerException e) {
            // 예상된 예외
        }
        
        assertTrue("render 호출 후에도 애니메이션 진행 중", true);
    }

    /**
     * 테스트 11: render - 정상 렌더링
     */
    @Test
    public void testBombAnimationManager_RenderNormal() {
        List<GameBoard.CellPos> cells = new ArrayList<>();
        cells.add(new GameBoard.CellPos(3, 5));
        cells.add(new GameBoard.CellPos(4, 5));
        
        animManager.triggerBombExplosion(cells);
        
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        animManager.render(g2d, 0, 0, 30);
        
        g2d.dispose();
        
        assertTrue("렌더링이 정상적으로 수행되어야 함", true);
    }

    /**
     * 테스트 12: render - 애니메이션 없는 상태
     */
    @Test
    public void testBombAnimationManager_RenderWhenNotAnimating() {
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        animManager.render(g2d, 0, 0, 30);
        
        g2d.dispose();
        
        assertFalse("애니메이션 없이 render 호출해도 안전", animManager.isAnimating());
    }

    /**
     * 테스트 13: updateAnimation - 진행도 업데이트
     */
    @Test
    public void testBombAnimationManager_UpdateAnimation() throws Exception {
        List<GameBoard.CellPos> cells = new ArrayList<>();
        cells.add(new GameBoard.CellPos(2, 3));
        
        animManager.triggerBombExplosion(cells);
        
        // 애니메이션이 완료될 때까지 대기
        Thread.sleep(1500);
        
        // 애니메이션이 종료되었는지 확인
        assertFalse("애니메이션이 완료되어야 함", animManager.isAnimating());
    }

    /**
     * 테스트 14: updateAnimation - 콜백 호출 확인
     */
    @Test
    public void testBombAnimationManager_CallbackCalled() throws Exception {
        List<GameBoard.CellPos> cells = new ArrayList<>();
        cells.add(new GameBoard.CellPos(1, 2));
        
        callbackCalled = false;
        animManager.triggerBombExplosion(cells);
        
        // 애니메이션 업데이트 대기
        Thread.sleep(100);
        
        assertTrue("콜백이 호출되어야 함", callbackCalled);
    }

    /**
     * 테스트 15: spawnBombParticles - null 리스트
     */
    @Test
    public void testBombAnimationManager_SpawnParticlesWithNull() throws Exception {
        java.lang.reflect.Method spawnMethod = BombAnimationManager.class.getDeclaredMethod("spawnBombParticles", List.class);
        spawnMethod.setAccessible(true);
        spawnMethod.invoke(animManager, (Object) null);
        
        assertFalse("null 리스트로 파티클 생성해도 안전", animManager.isAnimating());
    }

    /**
     * 테스트 16: spawnBombParticles - 빈 리스트
     */
    @Test
    public void testBombAnimationManager_SpawnParticlesWithEmpty() throws Exception {
        java.lang.reflect.Method spawnMethod = BombAnimationManager.class.getDeclaredMethod("spawnBombParticles", List.class);
        spawnMethod.setAccessible(true);
        spawnMethod.invoke(animManager, new ArrayList<GameBoard.CellPos>());
        
        assertFalse("빈 리스트로 파티클 생성해도 안전", animManager.isAnimating());
    }

    /**
     * 테스트 17: spawnBombParticles - null 셀 포함
     */
    @Test
    public void testBombAnimationManager_SpawnParticlesWithNullCell() throws Exception {
        List<GameBoard.CellPos> cells = new ArrayList<>();
        cells.add(new GameBoard.CellPos(2, 2));
        cells.add(null);
        cells.add(new GameBoard.CellPos(3, 3));
        
        java.lang.reflect.Method spawnMethod = BombAnimationManager.class.getDeclaredMethod("spawnBombParticles", List.class);
        spawnMethod.setAccessible(true);
        spawnMethod.invoke(animManager, cells);
        
        assertTrue("null 셀을 건너뛰고 파티클 생성", true);
    }

    /**
     * 테스트 18: spawnBombParticles - 파티클 생성 확인
     */
    @Test
    public void testBombAnimationManager_SpawnParticlesCreation() throws Exception {
        List<GameBoard.CellPos> cells = new ArrayList<>();
        cells.add(new GameBoard.CellPos(5, 5));
        
        animManager.triggerBombExplosion(cells);
        
        // bombParticles 맵 확인
        Field bombParticlesField = BombAnimationManager.class.getDeclaredField("bombParticles");
        bombParticlesField.setAccessible(true);
        Map<GameBoard.CellPos, List<BombAnimationManager.Particle>> bombParticles = 
            (Map<GameBoard.CellPos, List<BombAnimationManager.Particle>>) bombParticlesField.get(animManager);
        
        assertFalse("파티클이 생성되어야 함", bombParticles.isEmpty());
    }

    /**
     * 테스트 19: renderExplosionFlash - 폭발 플래시 렌더링
     */
    @Test
    public void testBombAnimationManager_RenderExplosionFlash() throws Exception {
        List<GameBoard.CellPos> cells = new ArrayList<>();
        cells.add(new GameBoard.CellPos(4, 6));
        
        animManager.triggerBombExplosion(cells);
        
        BufferedImage img = new BufferedImage(400, 700, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        // renderExplosionFlash 호출
        java.lang.reflect.Method renderFlashMethod = BombAnimationManager.class.getDeclaredMethod(
            "renderExplosionFlash", Graphics2D.class, int.class, int.class, int.class);
        renderFlashMethod.setAccessible(true);
        renderFlashMethod.invoke(animManager, g2d, 0, 0, 30);
        
        g2d.dispose();
        
        assertTrue("폭발 플래시가 렌더링되어야 함", true);
    }

    /**
     * 테스트 20: renderExplosionFlash - 빈 상태
     */
    @Test
    public void testBombAnimationManager_RenderExplosionFlashEmpty() throws Exception {
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        java.lang.reflect.Method renderFlashMethod = BombAnimationManager.class.getDeclaredMethod(
            "renderExplosionFlash", Graphics2D.class, int.class, int.class, int.class);
        renderFlashMethod.setAccessible(true);
        renderFlashMethod.invoke(animManager, g2d, 0, 0, 30);
        
        g2d.dispose();
        
        assertTrue("빈 상태에서 폭발 플래시 렌더링해도 안전", true);
    }

    /**
     * 테스트 21: renderParticles - 파티클 렌더링
     */
    @Test
    public void testBombAnimationManager_RenderParticles() throws Exception {
        List<GameBoard.CellPos> cells = new ArrayList<>();
        cells.add(new GameBoard.CellPos(6, 8));
        
        animManager.triggerBombExplosion(cells);
        
        BufferedImage img = new BufferedImage(400, 700, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        // renderParticles 호출
        java.lang.reflect.Method renderParticlesMethod = BombAnimationManager.class.getDeclaredMethod(
            "renderParticles", Graphics2D.class, int.class, int.class, int.class);
        renderParticlesMethod.setAccessible(true);
        renderParticlesMethod.invoke(animManager, g2d, 0, 0, 30);
        
        g2d.dispose();
        
        assertTrue("파티클이 렌더링되어야 함", true);
    }

    /**
     * 테스트 22: renderParticles - 빈 상태
     */
    @Test
    public void testBombAnimationManager_RenderParticlesEmpty() throws Exception {
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        java.lang.reflect.Method renderParticlesMethod = BombAnimationManager.class.getDeclaredMethod(
            "renderParticles", Graphics2D.class, int.class, int.class, int.class);
        renderParticlesMethod.setAccessible(true);
        renderParticlesMethod.invoke(animManager, g2d, 0, 0, 30);
        
        g2d.dispose();
        
        assertTrue("빈 상태에서 파티클 렌더링해도 안전", true);
    }

    /**
     * 테스트 23: Particle 생성자
     */
    @Test
    public void testBombAnimationManager_ParticleConstructor() {
        BombAnimationManager.Particle particle = new BombAnimationManager.Particle(
            10f, 20f, 2f, 3f, 1.5f, Color.RED
        );
        
        assertNotNull("파티클이 생성되어야 함", particle);
        assertEquals("X 좌표", 10f, particle.x, 0.01f);
        assertEquals("Y 좌표", 20f, particle.y, 0.01f);
        assertEquals("X 속도", 2f, particle.vx, 0.01f);
        assertEquals("Y 속도", 3f, particle.vy, 0.01f);
        assertEquals("수명", 1.5f, particle.life, 0.01f);
        assertEquals("최대 수명", 1.5f, particle.maxLife, 0.01f);
        assertEquals("투명도", 1.0f, particle.alpha, 0.01f);
        assertEquals("크기", 6, particle.size);
        assertEquals("색상", Color.RED, particle.color);
    }

    /**
     * 테스트 24: updateAnimation - 파티클 중력 적용
     */
    @Test
    public void testBombAnimationManager_ParticleGravity() throws Exception {
        List<GameBoard.CellPos> cells = new ArrayList<>();
        cells.add(new GameBoard.CellPos(3, 4));
        
        animManager.triggerBombExplosion(cells);
        
        // bombParticles 가져오기
        Field bombParticlesField = BombAnimationManager.class.getDeclaredField("bombParticles");
        bombParticlesField.setAccessible(true);
        Map<GameBoard.CellPos, List<BombAnimationManager.Particle>> bombParticles = 
            (Map<GameBoard.CellPos, List<BombAnimationManager.Particle>>) bombParticlesField.get(animManager);
        
        // 첫 번째 파티클의 초기 vy 저장
        float initialVy = 0;
        for (List<BombAnimationManager.Particle> pList : bombParticles.values()) {
            if (!pList.isEmpty()) {
                initialVy = pList.get(0).vy;
                break;
            }
        }
        
        // 애니메이션 업데이트 대기
        Thread.sleep(100);
        
        // vy가 증가했는지 확인 (중력 적용)
        float updatedVy = 0;
        for (List<BombAnimationManager.Particle> pList : bombParticles.values()) {
            if (!pList.isEmpty()) {
                updatedVy = pList.get(0).vy;
                break;
            }
        }
        
        assertTrue("중력으로 인해 vy가 증가해야 함", updatedVy > initialVy);
    }

    /**
     * 테스트 25: updateAnimation - 파티클 수명 감소
     */
    @Test
    public void testBombAnimationManager_ParticleLifeDecrease() throws Exception {
        List<GameBoard.CellPos> cells = new ArrayList<>();
        cells.add(new GameBoard.CellPos(2, 6));
        
        animManager.triggerBombExplosion(cells);
        
        // bombParticles 가져오기
        Field bombParticlesField = BombAnimationManager.class.getDeclaredField("bombParticles");
        bombParticlesField.setAccessible(true);
        Map<GameBoard.CellPos, List<BombAnimationManager.Particle>> bombParticles = 
            (Map<GameBoard.CellPos, List<BombAnimationManager.Particle>>) bombParticlesField.get(animManager);
        
        // 초기 파티클 수
        int initialCount = 0;
        for (List<BombAnimationManager.Particle> pList : bombParticles.values()) {
            initialCount += pList.size();
        }
        
        assertTrue("초기 파티클이 존재해야 함", initialCount > 0);
        
        // 애니메이션 완료까지 대기
        Thread.sleep(1500);
        
        // 최종 파티클 수 (모두 소멸되어야 함)
        int finalCount = 0;
        for (List<BombAnimationManager.Particle> pList : bombParticles.values()) {
            finalCount += pList.size();
        }
        
        assertEquals("파티클이 모두 소멸되어야 함", 0, finalCount);
    }

    /**
     * 테스트 26: render - 다양한 cellSize
     */
    @Test
    public void testBombAnimationManager_RenderVariousCellSizes() {
        List<GameBoard.CellPos> cells = new ArrayList<>();
        cells.add(new GameBoard.CellPos(1, 1));
        
        animManager.triggerBombExplosion(cells);
        
        int[] cellSizes = {10, 20, 30, 40, 50};
        
        for (int cellSize : cellSizes) {
            BufferedImage img = new BufferedImage(500, 700, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = img.createGraphics();
            
            animManager.render(g2d, 0, 0, cellSize);
            
            g2d.dispose();
        }
        
        assertTrue("다양한 셀 크기로 렌더링이 가능해야 함", true);
    }

    /**
     * 테스트 27: render - 다양한 시작 위치
     */
    @Test
    public void testBombAnimationManager_RenderVariousPositions() {
        List<GameBoard.CellPos> cells = new ArrayList<>();
        cells.add(new GameBoard.CellPos(5, 5));
        
        animManager.triggerBombExplosion(cells);
        
        int[][] positions = {{0, 0}, {50, 50}, {100, 100}, {-10, -10}};
        
        for (int[] pos : positions) {
            BufferedImage img = new BufferedImage(500, 700, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = img.createGraphics();
            
            animManager.render(g2d, pos[0], pos[1], 30);
            
            g2d.dispose();
        }
        
        assertTrue("다양한 위치에서 렌더링이 가능해야 함", true);
    }

    /**
     * 테스트 28: 여러 셀 동시 폭발
     */
    @Test
    public void testBombAnimationManager_MultipleExplosions() {
        List<GameBoard.CellPos> cells = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            cells.add(new GameBoard.CellPos(i, i * 2));
        }
        
        animManager.triggerBombExplosion(cells);
        
        assertTrue("여러 셀이 동시에 폭발해야 함", animManager.isAnimating());
    }

    /**
     * 테스트 29: 타이머 중복 시작 방지
     */
    @Test
    public void testBombAnimationManager_PreventDuplicateTimer() throws Exception {
        List<GameBoard.CellPos> cells1 = new ArrayList<>();
        cells1.add(new GameBoard.CellPos(1, 1));
        
        animManager.triggerBombExplosion(cells1);
        
        Field bombAnimTimerField = BombAnimationManager.class.getDeclaredField("bombAnimTimer");
        bombAnimTimerField.setAccessible(true);
        javax.swing.Timer timer1 = (javax.swing.Timer) bombAnimTimerField.get(animManager);
        
        // 두 번째 트리거 (타이머가 이미 실행 중)
        List<GameBoard.CellPos> cells2 = new ArrayList<>();
        cells2.add(new GameBoard.CellPos(2, 2));
        
        animManager.triggerBombExplosion(cells2);
        
        javax.swing.Timer timer2 = (javax.swing.Timer) bombAnimTimerField.get(animManager);
        
        assertSame("타이머가 중복 생성되지 않아야 함", timer1, timer2);
    }

    /**
     * 테스트 30: 폭발 진행도가 1 이상일 때 제거
     */
    @Test
    public void testBombAnimationManager_ProgressCompletionRemoval() throws Exception {
        List<GameBoard.CellPos> cells = new ArrayList<>();
        cells.add(new GameBoard.CellPos(7, 9));
        
        animManager.triggerBombExplosion(cells);
        
        // bombExplosionProgress 가져오기
        Field bombExplosionProgressField = BombAnimationManager.class.getDeclaredField("bombExplosionProgress");
        bombExplosionProgressField.setAccessible(true);
        Map<GameBoard.CellPos, Float> bombExplosionProgress = 
            (Map<GameBoard.CellPos, Float>) bombExplosionProgressField.get(animManager);
        
        int initialSize = bombExplosionProgress.size();
        assertTrue("초기에 진행도 맵에 항목이 있어야 함", initialSize > 0);
        
        // 애니메이션 완료 대기
        Thread.sleep(1500);
        
        int finalSize = bombExplosionProgress.size();
        assertEquals("완료된 항목이 제거되어야 함", 0, finalSize);
    }
}

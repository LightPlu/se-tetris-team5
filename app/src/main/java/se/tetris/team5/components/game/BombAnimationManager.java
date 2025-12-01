package se.tetris.team5.components.game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import java.util.*;
import java.util.List;

/**
 * 폭탄 폭발 애니메이션을 관리하는 클래스
 * GameBoard에서 분리하여 폭탄 애니메이션 관련 로직을 독립적으로 관리합니다.
 */
public class BombAnimationManager {
    
    // 폭발 애니메이션 진행도 맵 (셀 위치 -> 진행도 0..1)
    private Map<GameBoard.CellPos, Float> bombExplosionProgress = new LinkedHashMap<>();
    
    // 폭발 파티클 맵 (셀 위치 -> 파티클 리스트)
    private Map<GameBoard.CellPos, List<Particle>> bombParticles = new HashMap<>();
    
    // 애니메이션 타이머
    private Timer bombAnimTimer = null;
    
    // 애니메이션이 변경될 때 호출할 콜백 (repaint 등)
    private Runnable onAnimationUpdate;
    
    /**
     * 폭탄 애니메이션 매니저 생성자
     * @param onAnimationUpdate 애니메이션 업데이트 시 호출할 콜백 (예: repaint)
     */
    public BombAnimationManager(Runnable onAnimationUpdate) {
        this.onAnimationUpdate = onAnimationUpdate;
    }
    
    /**
     * 폭탄 폭발 애니메이션 트리거
     * @param cells 폭발한 셀들의 위치 리스트
     */
    public void triggerBombExplosion(List<GameBoard.CellPos> cells) {
        if (cells == null || cells.isEmpty()) return;
        
        // 셀별 애니메이션 등록
        for (GameBoard.CellPos pos : cells) {
            if (pos == null) continue;
            if (!bombExplosionProgress.containsKey(pos)) {
                bombExplosionProgress.put(pos, 0f);
            }
        }
        
        // 폭탄 파티클 생성
        spawnBombParticles(cells);
        
        if (bombAnimTimer != null && bombAnimTimer.isRunning()) {
            return; // 타이머 이미 실행 중
        }
        
        // 폭탄 애니메이션 타이머 시작 (30ms 간격)
        bombAnimTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAnimation();
            }
        });
        bombAnimTimer.setInitialDelay(0);
        bombAnimTimer.start();
    }
    
    /**
     * 애니메이션 업데이트 (타이머에서 호출)
     */
    private void updateAnimation() {
        List<GameBoard.CellPos> finished = new ArrayList<>();
        
        // 셀별 폭발 진행도 업데이트
        for (Map.Entry<GameBoard.CellPos, Float> en : new ArrayList<>(bombExplosionProgress.entrySet())) {
            float p = en.getValue() + 0.12f; // 더 빠른 폭발
            if (p >= 1f) {
                finished.add(en.getKey());
            } else {
                bombExplosionProgress.put(en.getKey(), p);
            }
        }
        for (GameBoard.CellPos k : finished) bombExplosionProgress.remove(k);
        
        // 파티클 업데이트
        List<GameBoard.CellPos> emptyCells = new ArrayList<>();
        for (Map.Entry<GameBoard.CellPos, List<Particle>> en : new ArrayList<>(bombParticles.entrySet())) {
            List<Particle> pls = en.getValue();
            for (Particle p : new ArrayList<>(pls)) {
                p.vy += 0.5f; // 중력
                p.x += p.vx;
                p.y += p.vy;
                p.life -= 0.08f;
                p.alpha = Math.max(0f, p.life / p.maxLife);
                if (p.life <= 0f) pls.remove(p);
            }
            if (pls.isEmpty()) emptyCells.add(en.getKey());
        }
        for (GameBoard.CellPos k : emptyCells) bombParticles.remove(k);
        
        // 애니메이션 종료 체크
        if (bombExplosionProgress.isEmpty() && bombParticles.isEmpty()) {
            bombAnimTimer.stop();
        }
        
        // UI 업데이트 콜백 호출
        if (onAnimationUpdate != null) {
            onAnimationUpdate.run();
        }
    }
    
    /**
     * 폭탄 파티클 생성
     */
    private void spawnBombParticles(List<GameBoard.CellPos> cells) {
        if (cells == null || cells.isEmpty()) return;
        
        Random rand = new Random();
        
        for (GameBoard.CellPos pos : cells) {
            if (pos == null) continue;
            List<Particle> pls = new ArrayList<>();
            
            // 셀 하나당 12개 파티클 생성 (폭발 효과)
            int fragments = 12;
            for (int f = 0; f < fragments; f++) {
                // 사방으로 퍼지는 속도
                float angle = rand.nextFloat() * (float)Math.PI * 2;
                float speed = 8f + rand.nextFloat() * 8f;
                float vx = (float)Math.cos(angle) * speed;
                float vy = (float)Math.sin(angle) * speed;
                
                float life = 1.2f + rand.nextFloat() * 0.8f;
                
                // 폭발 색상: 주황색~빨간색
                int r = 200 + rand.nextInt(56);
                int g = 50 + rand.nextInt(100);
                int b = 0;
                Color col = new Color(r, g, b);
                
                Particle p = new Particle(0, 0, vx, vy, life, col);
                pls.add(p);
            }
            bombParticles.put(pos, pls);
        }
    }
    
    /**
     * 폭탄 애니메이션 렌더링
     * @param g Graphics2D 객체
     * @param startX 보드 시작 X 좌표
     * @param startY 보드 시작 Y 좌표
     * @param cellSize 셀 크기
     */
    public void render(Graphics2D g, int startX, int startY, int cellSize) {
        // 폭발 플래시 렌더링
        renderExplosionFlash(g, startX, startY, cellSize);
        
        // 파티클 렌더링
        renderParticles(g, startX, startY, cellSize);
    }
    
    /**
     * 폭발 플래시 효과 렌더링
     */
    private void renderExplosionFlash(Graphics2D g, int startX, int startY, int cellSize) {
        if (bombExplosionProgress == null || bombExplosionProgress.isEmpty()) return;
        
        Graphics2D g5 = (Graphics2D) g.create();
        g5.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (Map.Entry<GameBoard.CellPos, Float> en : bombExplosionProgress.entrySet()) {
            GameBoard.CellPos pos = en.getKey();
            float prog = en.getValue();
            
            int x = startX + pos.col * cellSize;
            int y = startY + pos.row * cellSize;
            
            // 폭발 플래시 (주황색 -> 빨간색)
            float alpha = Math.max(0f, 1f - prog);
            float flash = (float) Math.sin((1f - prog) * Math.PI);
            float finalAlpha = Math.max(0f, Math.min(1f, flash * 0.9f + alpha * 0.1f));
            int opa = (int) (finalAlpha * 240);
            
            // 외부 플래시 (주황색)
            Color outer = new Color(255, 120, 20, Math.max(20, opa));
            g5.setColor(outer);
            g5.fillRoundRect(x, y, cellSize, cellSize, 6, 6);
            
            // 내부 플래시 (밝은 노란색)
            int innerOpa = (int) (finalAlpha * 200);
            Color inner = new Color(255, 220, 100, Math.max(10, innerOpa));
            int inset = Math.max(3, cellSize / 6);
            g5.setColor(inner);
            g5.fillRoundRect(x + inset, y + inset, cellSize - inset*2, cellSize - inset*2, 4, 4);
            
            // 폭발 확장 효과 (원형 파동)
            if (prog < 0.5f) {
                float expandProg = prog * 2f;
                int radius = (int)(cellSize * expandProg * 0.8f);
                int waveOpa = (int)((1f - expandProg) * 150);
                Color wave = new Color(255, 80, 0, Math.max(0, waveOpa));
                g5.setColor(wave);
                g5.setStroke(new BasicStroke(3f));
                g5.drawOval(x + cellSize/2 - radius, y + cellSize/2 - radius, radius*2, radius*2);
            }
        }
        g5.dispose();
    }
    
    /**
     * 폭탄 파티클 렌더링
     */
    private void renderParticles(Graphics2D g, int startX, int startY, int cellSize) {
        if (bombParticles == null || bombParticles.isEmpty()) return;
        
        Graphics2D g6 = (Graphics2D) g.create();
        g6.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (Map.Entry<GameBoard.CellPos, List<Particle>> en : bombParticles.entrySet()) {
            GameBoard.CellPos pos = en.getKey();
            List<Particle> pls = en.getValue();
            int baseX = startX + pos.col * cellSize + cellSize/2;
            int baseY = startY + pos.row * cellSize + cellSize/2;
            
            for (Particle p : pls) {
                int px = Math.round(baseX + p.x);
                int py = Math.round(baseY + p.y);
                int sz = Math.max(2, p.size);
                int ia = Math.max(0, Math.min(255, (int) (p.alpha * 255)));
                Color pc = new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), ia);
                g6.setColor(pc);
                g6.fillOval(px - sz/2, py - sz/2, sz, sz);
                
                // 파티클 중심에 밝은 점 추가
                if (p.alpha > 0.5f) {
                    int coreSize = Math.max(1, sz/3);
                    Color core = new Color(255, 255, 200, ia);
                    g6.setColor(core);
                    g6.fillOval(px - coreSize/2, py - coreSize/2, coreSize, coreSize);
                }
            }
        }
        g6.dispose();
    }
    
    /**
     * 애니메이션이 진행 중인지 확인
     */
    public boolean isAnimating() {
        return !bombExplosionProgress.isEmpty() || !bombParticles.isEmpty();
    }
    
    /**
     * 애니메이션 정리 (게임 리셋 등에 사용)
     */
    public void clear() {
        if (bombAnimTimer != null && bombAnimTimer.isRunning()) {
            bombAnimTimer.stop();
        }
        bombExplosionProgress.clear();
        bombParticles.clear();
    }
    
    /**
     * 파티클 내부 클래스
     */
    public static class Particle {
        float x, y;        // 위치
        float vx, vy;      // 속도
        float life;        // 남은 수명
        float maxLife;     // 최대 수명
        float alpha;       // 투명도
        int size;          // 크기
        Color color;       // 색상
        
        public Particle(float x, float y, float vx, float vy, float life, Color color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.life = life;
            this.maxLife = life;
            this.alpha = 1.0f;
            this.size = 6;
            this.color = color;
        }
    }
}

package se.tetris.team5.screens;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

import se.tetris.team5.components.home.Title;
import se.tetris.team5.utils.setting.GameSettings;
import se.tetris.team5.ScreenController;

public class home extends JPanel implements KeyListener {

  private ScreenController screenController;
  private int selectedMenu = 0; // 0: 게임시작, 1: 스코어보기, 2: 설정, 3: 종료

  // 메뉴 상태 관리
  private boolean inDifficultySelection = false; // 난이도 선택 화면인지 여부

  // 도움말 표시 관리
  private boolean showHelpMessage = false; // 도움말 메시지 표시 여부
  private Timer helpMessageTimer; // 도움말 메시지 자동 숨김 타이머
  private JWindow helpWindow; // 도움말 창

  // 창 크기 정보
  private int windowWidth;
  private int windowHeight;
  private Title.WindowSize currentWindowSize;

  // GUI 컴포넌트들
  private JLabel titleLabel;
  private JButton[] menuButtons;
  private JLabel descriptionLabel;
  private JLabel gameInfoLabel;

  // 메인 메뉴
  private String[] mainMenuOptions = {
      "일반 모드",
      "아이템 모드",
      "대전 모드",
      "스코어 보기",
      "설정",
      "종료"
  };

  private String[] mainMenuIcons = {
      "🎮", "💎", "⚔️", "🏆", "⚙️", "❌"
  };

  private String[] mainMenuDescriptions = {
      "난이도를 선택하여 일반 테트리스를 플레이합니다",
      "아이템이 포함된 테트리스를 플레이합니다",
      "다른 플레이어와 1대1 대결을 펼칩니다",
      "역대 최고 기록들을 확인합니다",
      "게임 설정을 변경합니다",
      "게임을 종료합니다"
  };

  // 난이도 선택 메뉴
  private String[] difficultyMenuOptions = {
      "이지",
      "노말",
      "하드",
      "뒤로 가기"
  };

  private String[] difficultyMenuIcons = {
      "🟢", "🟡", "🔴", "↩️"
  };

  private String[] difficultyMenuDescriptions = {
      "🟢 이지: 쉬운 블록들로 구성된 난이도",
      "🟡 노말: 일반적인 블록 구성의 기본 난이도",
      "🔴 하드: 어려운 블록들로 구성된 고난이도",
      "메인 메뉴로 돌아갑니다"
  };

  // 대전 모드 선택 메뉴
  private String[] battleModeOptions = {
      "일반 대전",
      "아이템 대전",
      "시간제한 대전",
      "AI 대전",
      "AI vs AI",
      "뒤로 가기"
  };

  private String[] battleModeIcons = {
      "⚔️", "💎⚔️", "⏱️", "🤖⚔️", "🤖🤖", "↩️"
  };

  private String[] battleModeDescriptions = {
      "기본 대전 모드 - 먼저 게임오버되는 사람이 패배",
      "아이템이 포함된 대전 모드",
      "5분 시간제한 - 시간 종료 시 점수가 높은 사람이 승리",
      "AI 대전 모드 - 플레이어 vs AI",
      "AI vs AI 모드 - 두 AI가 자동으로 대결",
      "메인 메뉴로 돌아갑니다"
  };

  private boolean inBattleModeSelection = false;
  private boolean inAIDifficultySelection = false; // AI 난이도 선택 화면인지 여부

  // AI 난이도 선택 메뉴
  private String[] aiDifficultyOptions = {
      "보통",
      "어려움",
      "뒤로 가기"
  };

  private String[] aiDifficultyIcons = {
      "🟡", "🔴", "↩️"
  };

  private String[] aiDifficultyDescriptions = {
      "🟡 보통: AI가 기본 성능으로 플레이합니다",
      "🔴 어려움: AI가 최적화된 성능으로 플레이합니다",
      "대전 모드 선택으로 돌아갑니다"
  };

  // 배경 관련
  private BufferedImage backgroundImage;
  private ImageIcon backgroundGif;
  private List<Particle> particles;
  private Timer animationTimer;
  private Random random;

  public home(ScreenController screenController) {
    this.screenController = screenController;

    // GameSettings에서 창 크기 가져오기 및 윈도우 크기 설정
    updateWindowSize();

    initializeComponents();
    setupLayout();
    setupKeyListener();
    initializeBackground();
    updateMenuSelection();
  }

  /**
   * 현재 상태에 맞는 메뉴 옵션들을 반환합니다
   */
  private String[] getCurrentMenuOptions() {
    if (inAIDifficultySelection) {
      return aiDifficultyOptions;
    } else if (inBattleModeSelection) {
      return battleModeOptions;
    } else if (inDifficultySelection) {
      return difficultyMenuOptions;
    } else {
      return mainMenuOptions;
    }
  }

  /**
   * 현재 상태에 맞는 메뉴 아이콘들을 반환합니다
   */
  private String[] getCurrentMenuIcons() {
    if (inAIDifficultySelection) {
      return aiDifficultyIcons;
    } else if (inBattleModeSelection) {
      return battleModeIcons;
    } else if (inDifficultySelection) {
      return difficultyMenuIcons;
    } else {
      return mainMenuIcons;
    }
  }

  /**
   * 현재 상태에 맞는 메뉴 설명들을 반환합니다
   */
  private String[] getCurrentMenuDescriptions() {
    if (inAIDifficultySelection) {
      return aiDifficultyDescriptions;
    } else if (inBattleModeSelection) {
      return battleModeDescriptions;
    } else if (inDifficultySelection) {
      return difficultyMenuDescriptions;
    } else {
      return mainMenuDescriptions;
    }
  }

  /**
   * 창 크기 정보를 업데이트합니다
   */
  public void updateWindowSize() {
    GameSettings settings = GameSettings.getInstance();
    windowWidth = settings.getWindowWidth();
    windowHeight = settings.getWindowHeight();

    // 창 크기에 따른 레이아웃 모드 결정
    Title.WindowSize oldSize = currentWindowSize;
    currentWindowSize = Title.determineWindowSize(windowWidth, windowHeight);

    System.out.println("Window size updated: " + windowWidth + "x" + windowHeight +
        " -> " + oldSize + " to " + currentWindowSize);

    if (titleLabel != null) {
      updateComponentSizes();
    }
  }

  /**
   * GUI 컴포넌트들을 초기화합니다
   */
  private void initializeComponents() {
    // 배경을 투명하게 설정하여 배경 이미지가 보이도록 함
    setOpaque(false);

    // 제목 라벨 - Title 컴포넌트 사용
    Title titleComponent = new Title(currentWindowSize);
    titleLabel = new JLabel(titleComponent.getGUITitle());
    titleLabel.setFont(new Font("Arial", Font.BOLD, titleComponent.getTitleFontSize()));
    titleLabel.setForeground(Color.WHITE); // 흰색 텍스트
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

    // 메뉴 버튼들
    String[] currentOptions = getCurrentMenuOptions();
    menuButtons = new JButton[currentOptions.length];
    for (int i = 0; i < currentOptions.length; i++) {
      menuButtons[i] = createMenuButton(i);
    }

    // 설명 라벨
    descriptionLabel = new JLabel();
    descriptionLabel.setFont(getFontForSize(getFontSize() - 2));
    descriptionLabel.setForeground(Color.WHITE); // 흰색 텍스트
    descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);

    // 게임 정보 라벨
    String highScore = getHighestScore();
    gameInfoLabel = new JLabel(
        "<html><center>📋 게임 정보:<br/>CHAINSAW TETRIS v1.0<br/>🏆 최고 기록: " + highScore + "점</center></html>");
    gameInfoLabel.setFont(getFontForSize(getFontSize() - 4));
    gameInfoLabel.setForeground(Color.WHITE); // 흰색 텍스트
    gameInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
  }

  /**
   * 메뉴 버튼을 생성합니다
   */
  private JButton createMenuButton(int index) {
    String[] currentIcons = getCurrentMenuIcons();
    String[] currentOptions = getCurrentMenuOptions();
    String buttonText = currentIcons[index] + " " + currentOptions[index];
    JButton button = new JButton(buttonText);

    // 버튼 스타일 설정
    button.setFont(getFontForSize(getFontSize()));
    button.setFocusable(false); // 키보드 포커스 비활성화 (우리가 직접 처리)
    button.setBackground(new Color(60, 60, 60)); // 더 밝은 회색
    button.setForeground(Color.WHITE);
    button.setBorder(BorderFactory.createRaisedBevelBorder());

    // 버튼 크기를 모든 방향으로 고정
    Dimension buttonSize = new Dimension(getButtonWidth(), getButtonHeight());
    button.setPreferredSize(buttonSize);
    button.setMinimumSize(buttonSize);
    button.setMaximumSize(buttonSize);
    button.setSize(buttonSize);

    // 텍스트 정렬을 중앙으로 고정
    button.setHorizontalAlignment(SwingConstants.CENTER);
    button.setVerticalAlignment(SwingConstants.CENTER);

    // 버튼 클릭 이벤트 리스너 추가
    final int buttonIndex = index;
    button.addActionListener(e -> {
      selectedMenu = buttonIndex;
      updateMenuSelection();
      selectCurrentMenu();
    });

    // 마우스 호버 효과 추가
    button.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseEntered(java.awt.event.MouseEvent e) {
        if (selectedMenu != buttonIndex) {
          selectedMenu = buttonIndex;
          updateMenuSelection();
        }
      }
    });

    return button;
  }

  /**
   * 레이아웃을 설정합니다
   */
  private void setupLayout() {
    setLayout(new BorderLayout());

    // 메인 패널 - 중앙 정렬을 위해 FlowLayout 사용
    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.setOpaque(false);

    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setOpaque(false);

    // 모든 컴포넌트를 중앙 정렬로 설정
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    gameInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    // 제목 추가
    contentPanel.add(Box.createVerticalStrut(20));
    contentPanel.add(titleLabel);
    contentPanel.add(Box.createVerticalStrut(30));

    // 메뉴 패널 - 버튼들을 완전히 중앙에 배치
    JPanel menuPanel = new JPanel();
    menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
    menuPanel.setOpaque(false);
    menuPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

    for (JButton button : menuButtons) {
      // 버튼을 패널로 감싸서 크기 고정 및 중앙 정렬
      JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
      buttonWrapper.setOpaque(false);
      buttonWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

      // 버튼 크기 고정
      button.setPreferredSize(new Dimension(getButtonWidth(), getButtonHeight()));
      button.setMinimumSize(new Dimension(getButtonWidth(), getButtonHeight()));
      button.setMaximumSize(new Dimension(getButtonWidth(), getButtonHeight()));

      buttonWrapper.add(button);
      menuPanel.add(buttonWrapper);
      menuPanel.add(Box.createVerticalStrut(10));
    }

    contentPanel.add(menuPanel);
    contentPanel.add(Box.createVerticalStrut(20));

    // 설명 라벨 추가
    contentPanel.add(descriptionLabel);
    contentPanel.add(Box.createVerticalStrut(30));

    // 게임 정보 추가
    contentPanel.add(gameInfoLabel);
    contentPanel.add(Box.createVerticalGlue());

    centerPanel.add(contentPanel, BorderLayout.CENTER);
    add(centerPanel, BorderLayout.CENTER);
  }

  /**
   * 키보드 리스너를 설정합니다
   */
  private void setupKeyListener() {
    setFocusable(true);
    addKeyListener(this);
    requestFocusInWindow();
  }

  /**
   * 메뉴 선택 상태를 업데이트합니다
   */
  private void updateMenuSelection() {
    String[] currentDescriptions = getCurrentMenuDescriptions();

    for (int i = 0; i < menuButtons.length; i++) {
      if (i == selectedMenu) {
        // 선택된 버튼 스타일 - 색맹 모드 고려 색상
        menuButtons[i].setBackground(GameSettings.getInstance().getUIColor("success"));
        menuButtons[i].setForeground(Color.BLACK);
        menuButtons[i].setBorder(BorderFactory.createLoweredBevelBorder());

        // 설명 업데이트
        descriptionLabel.setText("💬 " + currentDescriptions[i]);
      } else {
        // 기본 버튼 스타일
        menuButtons[i].setBackground(new Color(60, 60, 60)); // 더 밝은 회색
        menuButtons[i].setForeground(Color.WHITE);
        menuButtons[i].setBorder(BorderFactory.createRaisedBevelBorder());
      }
    }

    repaint();
  }

  /**
   * 컴포넌트 크기를 업데이트합니다
   */
  private void updateComponentSizes() {
    // 제목 업데이트 - Title 컴포넌트 사용
    Title titleComponent = new Title(currentWindowSize);
    String newTitle = titleComponent.getGUITitle();
    int newFontSize = titleComponent.getTitleFontSize();

    System.out.println("Updating title for size: " + currentWindowSize);
    System.out.println("New title: " + newTitle);
    System.out.println("New font size: " + newFontSize);

    titleLabel.setText(newTitle);
    titleLabel.setFont(new Font("Arial", Font.BOLD, newFontSize));

    // 버튼들 크기 업데이트
    for (JButton button : menuButtons) {
      button.setFont(getFontForSize(getFontSize()));

      // 버튼 크기를 모든 방향으로 다시 고정
      Dimension buttonSize = new Dimension(getButtonWidth(), getButtonHeight());
      button.setPreferredSize(buttonSize);
      button.setMinimumSize(buttonSize);
      button.setMaximumSize(buttonSize);
      button.setSize(buttonSize);
    }

    // 라벨들 폰트 업데이트
    descriptionLabel.setFont(getFontForSize(getFontSize() - 2));
    gameInfoLabel.setFont(getFontForSize(getFontSize() - 4));

    revalidate();
    repaint();
  }

  /**
   * 창 크기에 따른 기본 폰트 크기를 반환합니다 (3가지 크기로 변경)
   */
  private int getFontSize() {
    switch (currentWindowSize) {
      case SMALL:
        return 14; // 450x600 (기존 중형 크기의 폰트)
      case MEDIUM:
        return 16; // 550x700 (기존 대형 크기의 폰트)
      case LARGE:
        return 18; // 650x800 (기존 특대형 크기의 폰트)
      default:
        return 16;
    }
  }

  /**
   * 창 크기에 따른 버튼 너비를 반환합니다 (3가지 크기로 변경)
   */
  private int getButtonWidth() {
    switch (currentWindowSize) {
      case SMALL:
        return 250; // 450x600 (기존 중형 크기의 버튼)
      case MEDIUM:
        return 300; // 550x700 (기존 대형 크기의 버튼)
      case LARGE:
        return 350; // 650x800 (기존 특대형 크기의 버튼)
      default:
        return 300;
    }
  }

  /**
   * 창 크기에 따른 버튼 높이를 반환합니다 (3가지 크기로 변경)
   */
  private int getButtonHeight() {
    switch (currentWindowSize) {
      case SMALL:
        return 40; // 450x600 (기존 중형 크기의 버튼)
      case MEDIUM:
        return 45; // 550x700 (기존 대형 크기의 버튼)
      case LARGE:
        return 50; // 650x800 (기존 특대형 크기의 버튼)
      default:
        return 45;
    }
  }

  /**
   * 폰트를 생성합니다
   */
  private Font getFontForSize(int size) {
    return new Font("Dialog", Font.BOLD, size);
  }

  /**
   * 최고 점수를 가져옵니다
   */
  private String getHighestScore() {
    try {
      se.tetris.team5.utils.score.ScoreManager scoreManager = se.tetris.team5.utils.score.ScoreManager.getInstance();
      var topScores = scoreManager.getTopScores(1);
      if (!topScores.isEmpty()) {
        return String.format("%,d", topScores.get(0).getScore());
      }
    } catch (Exception e) {
      // ScoreManager 초기화 중 오류 발생시 기본값 반환
    }
    return "없음";
  }

  /**
   * 현재 선택된 메뉴를 실행합니다
   */
  private void selectCurrentMenu() {
    if (inAIDifficultySelection) {
      // AI 난이도 선택 화면
      switch (selectedMenu) {
        case 0: // 보통
          startBattleMode("AI", "NORMAL");
          break;
        case 1: // 어려움
          startBattleMode("AI", "HARD");
          break;
        case 2: // 뒤로 가기
          backToAIModeSelection();
          break;
      }
    } else if (inBattleModeSelection) {
      // 대전 모드 선택 화면
      switch (selectedMenu) {
        case 0: // 일반 대전
          startBattleMode("NORMAL", null);
          break;
        case 1: // 아이템 대전
          startBattleMode("ITEM", null);
          break;
        case 2: // 시간제한 대전
          startBattleMode("TIMELIMIT", null);
          break;
        case 3: // AI 대전
          showAIDifficultySelection();
          break;
        case 4: // AI vs AI
          startBattleMode("AI_VS_AI", null);
          break;
        case 5: // 뒤로 가기
          backToMainMenu();
          break;
      }
    } else if (inDifficultySelection) {
      // 난이도 선택 화면
      switch (selectedMenu) {
        case 0: // 이지
          startNormalMode("EASY");
          break;
        case 1: // 노말
          startNormalMode("NORMAL");
          break;
        case 2: // 하드
          startNormalMode("HARD");
          break;
        case 3: // 뒤로 가기
          backToMainMenu();
          break;
      }
    } else {
      // 메인 메뉴
      switch (selectedMenu) {
        case 0: // 일반 모드 (난이도 선택으로 이동)
          showDifficultySelection();
          break;
        case 1: // 아이템 모드 (바로 시작)
          startItemMode();
          break;
        case 2: // 대전 모드 (모드 선택으로 이동)
          showBattleModeSelection();
          break;
        case 3: // 스코어 보기
          screenController.showScreen("score");
          break;
        case 4: // 설정
          screenController.showScreen("setting");
          break;
        case 5: // 종료
          showExitConfirmation();
          break;
      }
    }
  }

  /**
   * 메인 메뉴로 돌아갑니다
   */
  private void backToMainMenu() {
    inDifficultySelection = false;
    inBattleModeSelection = false;
    inAIDifficultySelection = false;
    selectedMenu = 0;
    rebuildMenu();
  }

  /**
   * AI 대전 모드 선택으로 돌아갑니다
   */
  private void backToAIModeSelection() {
    inAIDifficultySelection = false;
    inBattleModeSelection = true;
    selectedMenu = 3; // AI 대전 선택
    rebuildMenu();
  }

  /**
   * 난이도 선택 화면으로 전환합니다
   */
  private void showDifficultySelection() {
    inDifficultySelection = true;
    selectedMenu = 1; // 기본값: 노말 선택
    rebuildMenu();
  }

  /**
   * 대전 모드 선택 화면으로 전환합니다
   */
  private void showBattleModeSelection() {
    inBattleModeSelection = true;
    inAIDifficultySelection = false;
    selectedMenu = 0; // 기본값: 일반 대전 선택
    rebuildMenu();
  }

  /**
   * AI 난이도 선택 화면으로 전환합니다
   */
  private void showAIDifficultySelection() {
    inAIDifficultySelection = true;
    inBattleModeSelection = false;
    selectedMenu = 0; // 기본값: 보통 선택
    rebuildMenu();
  }

  /**
   * 메뉴를 다시 구성합니다
   */
  private void rebuildMenu() {
    // 기존 버튼들 제거
    Component[] components = getComponents();
    for (Component comp : components) {
      if (comp instanceof JPanel) {
        ((JPanel) comp).removeAll();
      }
    }
    removeAll();

    // 새로운 메뉴로 다시 초기화
    initializeComponents();
    setupLayout();

    // 화면 갱신
    revalidate();
    repaint();

    // 포커스 재설정
    requestFocusInWindow();

    // 메뉴 선택 상태 업데이트
    updateMenuSelection();
  }

  /**
   * 일반 모드로 게임을 시작합니다
   */
  private void startNormalMode(String difficulty) {
    System.out.println("[게임 시작] 일반 모드 - 난이도: " + difficulty);

    // 전역 변수로 게임 모드와 난이도 저장 (game 화면에서 참조)
    System.setProperty("tetris.game.mode", "NORMAL");
    System.setProperty("tetris.game.difficulty", difficulty);

    screenController.showScreen("game");
  }

  /**
   * 아이템 모드로 게임을 시작합니다
   */
  private void startItemMode() {
    System.out.println("[게임 시작] 아이템 모드");

    // 전역 변수로 게임 모드 저장 (game 화면에서 참조)
    System.setProperty("tetris.game.mode", "ITEM");
    System.setProperty("tetris.game.difficulty", "NORMAL");

    screenController.showScreen("game");
  }

  /**
   * 대전 모드로 게임을 시작합니다
   * 
   * @param battleMode "NORMAL", "ITEM", "TIMELIMIT", "AI", "AI_VS_AI"
   * @param aiDifficulty AI 난이도 ("NORMAL", "HARD") - AI 모드일 때만 사용
   */
  private void startBattleMode(String battleMode, String aiDifficulty) {
    System.out.println("[게임 시작] 대전 모드 - " + battleMode + (aiDifficulty != null ? " (AI 난이도: " + aiDifficulty + ")" : ""));

    // 전역 변수로 게임 모드 저장 (battle 화면에서 참조)
    System.setProperty("tetris.game.mode", "BATTLE");
    System.setProperty("tetris.battle.mode", battleMode);
    System.setProperty("tetris.game.difficulty", "NORMAL");
    
    // AI 난이도 설정 (AI 모드일 때만)
    if (aiDifficulty != null) {
      System.setProperty("tetris.ai.difficulty", aiDifficulty);
    } else {
      System.setProperty("tetris.ai.difficulty", "NORMAL"); // 기본값
    }

    // 대전 모드는 창 크기를 가로로 2배 확장
    GameSettings settings = GameSettings.getInstance();
    // 화면 크기 변경 전에 원래 크기를 시스템 속성에 저장
    String originalSize = settings.getWindowSize();
    System.setProperty("tetris.battle.originalSize", originalSize);

    int originalWidth = settings.getWindowWidth();
    int originalHeight = settings.getWindowHeight();

    // 가로를 2배로 확장 (보드 2개 표시)
    settings.setCustomWindowSize(originalWidth * 2, originalHeight);
    screenController.updateWindowSize();

    screenController.showScreen("game");
  }

  /**
   * 종료 확인 화면을 표시합니다 (키보드 선택 가능)
   */
  private void showExitConfirmation() {
    int option = JOptionPane.showConfirmDialog(
        this,
        "정말로 게임을 종료하시겠습니까?",
        "게임 종료 확인",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);

    if (option == JOptionPane.YES_OPTION) {
      System.exit(0);
    }
  }

  /**
   * 도움말 메시지를 표시합니다 (JWindow 사용)
   */
  private void showHelpMessage() {
    if (helpWindow != null) {
      helpWindow.dispose(); // 기존 창 제거
    }

    showHelpMessage = true;
    createHelpWindow();

    // 기존 타이머가 있으면 정지
    if (helpMessageTimer != null && helpMessageTimer.isRunning()) {
      helpMessageTimer.stop();
    }

    // 3초 후 자동으로 도움말 숨김
    helpMessageTimer = new Timer(3000, e -> {
      hideHelpMessage();
    });
    helpMessageTimer.setRepeats(false);
    helpMessageTimer.start();
  }

  /**
   * 도움말 메시지를 숨깁니다
   */
  private void hideHelpMessage() {
    if (showHelpMessage) {
      showHelpMessage = false;
      if (helpWindow != null) {
        helpWindow.dispose();
        helpWindow = null;
      }
    }

    if (helpMessageTimer != null && helpMessageTimer.isRunning()) {
      helpMessageTimer.stop();
    }
  }

  /**
   * 도움말 창을 생성합니다
   */
  private void createHelpWindow() {
    // 부모 프레임 찾기
    Window parentWindow = SwingUtilities.getWindowAncestor(this);
    if (parentWindow == null) {
      return; // 부모 창이 없으면 표시하지 않음
    }

    helpWindow = new JWindow(parentWindow);
    helpWindow.setAlwaysOnTop(true); // 항상 맨 위에 표시

    // 도움말 패널 생성
    JPanel helpPanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 배경 (진한 검은색)
        g2d.setColor(new Color(0, 0, 0, 230));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // 테두리 (밝은 청록색)
        g2d.setColor(new Color(0, 230, 160));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(2, 2, getWidth() - 4, getHeight() - 4);

        g2d.dispose();
      }
    };

    helpPanel.setOpaque(false);
    helpPanel.setLayout(new BorderLayout());

    // 내용 추가
    JLabel titleLabel = new JLabel("🎮 조작법", SwingConstants.CENTER);
    titleLabel.setFont(getFontForSize(getFontSize()));
    titleLabel.setForeground(new Color(0, 230, 160));
    titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

    String helpText = "<html><center>" +
        "⬆️⬇️  위/아래 화살표: 메뉴 선택<br/>" +
        "⏎  엔터키: 선택 확인<br/>" +
        "⎋  ESC: 뒤로가기/종료<br/><br/>" +
        "<span style='color: #FFD700'>💡 3초 후 자동으로 사라집니다</span>" +
        "</center></html>";

    JLabel contentLabel = new JLabel(helpText, SwingConstants.CENTER);
    contentLabel.setFont(getFontForSize(getFontSize() - 2));
    contentLabel.setForeground(Color.WHITE);
    contentLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

    helpPanel.add(titleLabel, BorderLayout.NORTH);
    helpPanel.add(contentLabel, BorderLayout.CENTER);

    helpWindow.add(helpPanel);

    // 크기와 위치 설정
    int panelWidth = getButtonWidth() + 100;
    int panelHeight = 200;
    helpWindow.setSize(panelWidth, panelHeight);

    // 부모 창 중심에서 하단에 위치
    Point parentLocation = parentWindow.getLocationOnScreen();
    Dimension parentSize = parentWindow.getSize();
    int x = parentLocation.x + (parentSize.width - panelWidth) / 2;
    int y = parentLocation.y + parentSize.height - panelHeight - 100;

    helpWindow.setLocation(x, y);
    helpWindow.setVisible(true);
  }

  // JTextPane 호환성을 위한 display 메서드
  public void display(JTextPane textPane) {
    // 홈 화면 표시 시 항상 메인 메뉴로 초기화 (모든 선택 화면 상태 해제)
    if (inDifficultySelection || inBattleModeSelection || inAIDifficultySelection) {
      inDifficultySelection = false;
      inBattleModeSelection = false;
      inAIDifficultySelection = false;
      selectedMenu = 0;
      // 메뉴를 다시 구성
      rebuildMenu();
    }

    // GUI 버전에서는 JTextPane 대신 JPanel을 사용
    // textPane의 부모 컨테이너에 이 GUI 패널을 추가
    if (textPane != null && textPane.getParent() != null) {
      Container parent = textPane.getParent();
      parent.removeAll();
      parent.setLayout(new BorderLayout());

      parent.add(this, BorderLayout.CENTER);
      parent.revalidate();
      parent.repaint();

      // 포커스를 이 패널로 강제 이동
      SwingUtilities.invokeLater(() -> {
        setFocusable(true);
        requestFocus();
        requestFocusInWindow();
        repaint();
        System.out.println("Focus requested for home panel");
      });

      // 강제로 화면 업데이트 (설정 변경 후 돌아올 때 적용)
      refreshDisplay();
      updateMenuSelection();
    } else {
      // textPane이 null인 경우를 대비한 fallback
      System.out.println("Warning: textPane is null or has no parent. GUI may not display correctly.");
    }
  }

  @Override
  public void keyPressed(KeyEvent e) {
    String[] currentOptions = getCurrentMenuOptions();

    switch (e.getKeyCode()) {
      case KeyEvent.VK_UP:
        selectedMenu = (selectedMenu - 1 + currentOptions.length) % currentOptions.length;
        updateMenuSelection();
        hideHelpMessage(); // 유효한 키를 누르면 도움말 숨김
        break;
      case KeyEvent.VK_DOWN:
        selectedMenu = (selectedMenu + 1) % currentOptions.length;
        updateMenuSelection();
        hideHelpMessage(); // 유효한 키를 누르면 도움말 숨김
        break;
      case KeyEvent.VK_ENTER:
        selectCurrentMenu();
        hideHelpMessage(); // 유효한 키를 누르면 도움말 숨김
        break;
      case KeyEvent.VK_ESCAPE:
        if (inAIDifficultySelection) {
          // AI 난이도 선택 화면에서 ESC: 대전 모드 선택으로 돌아가기
          backToAIModeSelection();
        } else if (inDifficultySelection) {
          // 난이도 선택 화면에서 ESC: 메인 메뉴로 돌아가기
          backToMainMenu();
        } else if (inBattleModeSelection) {
          // 대전 모드 선택 화면에서 ESC: 메인 메뉴로 돌아가기
          backToMainMenu();
        } else {
          // 메인 메뉴에서 ESC: 종료 확인
          showExitConfirmation();
        }
        hideHelpMessage(); // 유효한 키를 누르면 도움말 숨김
        break;
      default:
        // 다른 키를 누르면 도움말 표시
        showHelpMessage();
        break;
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }

  /**
   * 컴포넌트를 그릴 때 종료 확인 오버레이도 함께 그립니다
   */

  /**
   * 배경과 파티클을 초기화합니다
   */
  private void initializeBackground() {
    random = new Random();
    particles = new ArrayList<>();

    // 50개의 파티클 생성
    for (int i = 0; i < 50; i++) {
      particles.add(new Particle(
          random.nextFloat() * windowWidth,
          random.nextFloat() * windowHeight));
    }

    // 배경 이미지 로드 시도 (선택사항)
    loadBackgroundImage();

    // 애니메이션 타이머 시작 (60 FPS)
    animationTimer = new Timer(16, e -> {
      updateParticles();
      repaint();
    });
    animationTimer.start();
  }

  /**
   * 배경 이미지를 로드합니다 (선택사항)
   */
  private void loadBackgroundImage() {
    try {
      // 먼저 resources 폴더에서 찾기 (classpath 사용)
      String[] resourceNames = {
          "/mainbackground.gif",
          "/mainbackground.jpg",
          "/mainbackground.png"
      };

      for (String resourceName : resourceNames) {
        java.net.URL resourceUrl = getClass().getResource(resourceName);
        if (resourceUrl != null) {
          if (resourceName.toLowerCase().endsWith(".gif")) {
            // GIF 애니메이션 로드
            backgroundGif = new ImageIcon(resourceUrl);
            System.out.println("Animated GIF background loaded from resources: " + resourceName);
            return;
          } else {
            // 정적 이미지 로드
            backgroundImage = ImageIO.read(resourceUrl);
            System.out.println("Static background image loaded from resources: " + resourceName);
            return;
          }
        }
      }

      // resources에서 찾지 못하면 파일 시스템에서 찾기
      String[] filePaths = {
          "app/src/main/resources/mainbackground.gif",
          "app/src/main/resources/mainbackground.jpg",
          "app/src/main/resources/mainbackground.png",
          "src/main/resources/mainbackground.gif",
          "src/main/resources/mainbackground.jpg",
          "src/main/resources/mainbackground.png",
          "mainbackground.gif",
          "mainbackground.jpg",
          "mainbackground.png"
      };

      for (String path : filePaths) {
        File file = new File(path);
        if (file.exists()) {
          if (path.toLowerCase().endsWith(".gif")) {
            // GIF 애니메이션 로드
            backgroundGif = new ImageIcon(path);
            System.out.println("Animated GIF background loaded from file: " + path);
          } else {
            // 정적 이미지 로드
            backgroundImage = ImageIO.read(file);
            System.out.println("Static background image loaded from file: " + path);
          }
          return;
        }
      }

      System.out.println("No background image found, using default gradient background");
    } catch (Exception e) {
      System.out.println("Error loading background image: " + e.getMessage());
      System.out.println("Using default gradient background");
    }
  }

  /**
   * 파티클들을 업데이트합니다
   */
  private void updateParticles() {
    for (Particle particle : particles) {
      particle.update();
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // 배경 그리기
    drawBackground(g2d);

    // 파티클 그리기
    drawParticles(g2d);

    g2d.dispose();
  }

  /**
   * 배경을 그립니다
   */
  private void drawBackground(Graphics2D g2d) {
    if (backgroundGif != null) {
      // 애니메이션 GIF 배경 (this를 ImageObserver로 전달)
      g2d.drawImage(backgroundGif.getImage(), 0, 0, getWidth(), getHeight(), this);

      // 반투명 오버레이 (가독성을 위해)
      g2d.setColor(new Color(0, 0, 0, 120));
      g2d.fillRect(0, 0, getWidth(), getHeight());
    } else if (backgroundImage != null) {
      // 정적 이미지 배경
      g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

      // 반투명 오버레이 (가독성을 위해)
      g2d.setColor(new Color(0, 0, 0, 100));
      g2d.fillRect(0, 0, getWidth(), getHeight());
    } else {
      // 기본 그라데이션 배경
      GradientPaint gradient = new GradientPaint(
          0, 0, new Color(20, 20, 40),
          0, getHeight(), new Color(40, 20, 60));
      g2d.setPaint(gradient);
      g2d.fillRect(0, 0, getWidth(), getHeight());
    }
  }

  /**
   * 파티클들을 그립니다
   */
  private void drawParticles(Graphics2D g2d) {
    for (Particle particle : particles) {
      particle.draw(g2d);
    }
  }

  /**
   * 파티클 클래스
   */
  private static class Particle {
    private float x, y;
    private float vx, vy;
    private float size;
    private Color color;
    private float alpha;
    private float life;
    private float maxLife;

    public Particle(float x, float y) {
      this.x = x;
      this.y = y;
      Random rand = new Random();
      this.vx = (rand.nextFloat() - 0.5f) * 2.0f;
      this.vy = -rand.nextFloat() * 3.0f - 1.0f;
      this.size = rand.nextFloat() * 4 + 2;

      // 테트리스 색상 팔레트
      Color[] colors = {
          new Color(0, 240, 240), // I-piece (청록)
          new Color(0, 0, 240), // J-piece (파랑)
          new Color(240, 160, 0), // L-piece (주황)
          new Color(240, 240, 0), // O-piece (노랑)
          new Color(0, 240, 0), // S-piece (초록)
          new Color(160, 0, 240), // T-piece (보라)
          new Color(240, 0, 0) // Z-piece (빨강)
      };
      this.color = colors[rand.nextInt(colors.length)];
      this.maxLife = rand.nextFloat() * 3 + 2;
      this.life = maxLife;
      this.alpha = 1.0f;
    }

    public void update() {
      x += vx;
      y += vy;
      vy += 0.1f; // 중력

      life -= 0.016f; // 약 60fps 기준
      alpha = Math.max(0, life / maxLife);

      // 화면 밖으로 나가면 재생성
      if (y > 600 || x < -10 || x > 460 || life <= 0) {
        resetParticle();
      }
    }

    private void resetParticle() {
      Random rand = new Random();
      x = rand.nextFloat() * 450;
      y = -10;
      vx = (rand.nextFloat() - 0.5f) * 2.0f;
      vy = -rand.nextFloat() * 3.0f - 1.0f;
      life = maxLife = rand.nextFloat() * 3 + 2;
      alpha = 1.0f;
    }

    public void draw(Graphics2D g2d) {
      if (alpha > 0) {
        Color drawColor = new Color(
            color.getRed(),
            color.getGreen(),
            color.getBlue(),
            (int) (alpha * 100));
        g2d.setColor(drawColor);
        g2d.fillOval((int) x, (int) y, (int) size, (int) size);

        // 글로우 효과
        drawColor = new Color(
            color.getRed(),
            color.getGreen(),
            color.getBlue(),
            (int) (alpha * 30));
        g2d.setColor(drawColor);
        g2d.fillOval((int) x - 1, (int) y - 1, (int) size + 2, (int) size + 2);
      }
    }
  }

  /**
   * 강제로 화면을 새로고침합니다 (설정 변경 후 호출)
   */
  public void refreshDisplay() {
    updateWindowSize();
    if (titleLabel != null) {
      updateComponentSizes();
    }
    System.out.println("Display refreshed manually");
  }
}
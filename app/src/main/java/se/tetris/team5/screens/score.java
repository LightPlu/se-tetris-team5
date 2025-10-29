package se.tetris.team5.screens;

import se.tetris.team5.ScreenController;
import se.tetris.team5.utils.setting.GameSettings;
import se.tetris.team5.utils.score.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class score {
    private final ScreenController screenController;
    private JTextPane currentTextPane;
    private final GameSettings gameSettings;
    private final ScoreManager scoreManager;
    // Keep a reference to the list scroll so we can re-render when viewport size changes
    private JScrollPane scoreListScroll;
    // By default show real persisted scores. Do not auto-show in-memory dummy entries.
    private boolean showOnlyDummy = false;

    // Pagination
    private static final int PAGE_SIZE = 10;
    // fixed row height to keep alignment consistent
    // Reduced to make the overall panel more compact while keeping readability
    private static final int ROW_HEIGHT = 40;
    private int currentPage = 0;

    public score(ScreenController screenController) {
        this.screenController = screenController;
        this.gameSettings = GameSettings.getInstance();
        this.scoreManager = ScoreManager.getInstance();
    }

    // NOTE: persistent dummy injection removed — UI uses only in-memory sample entries

    private java.util.List<ScoreManager.ScoreEntry> buildSampleEntries() {
        java.util.List<ScoreManager.ScoreEntry> sample = new java.util.ArrayList<>();
        String[] names = {"Player A", "Player B", "Player C", "Player D", "Player E", "Player F", "Player G", "Player H", "Player I", "Player J"};
        int[] scores = {256, 233, 211, 210, 195, 190, 180, 170, 160, 150};
        int[] levels = {5,5,4,4,3,3,3,2,2,1};
        int[] lines = {0,0,0,0,0,0,0,0,0,0};
        long[] plays = {2000,4000,3000,3000,2000,120000,180000,90000,60000,45000};
        for (int i = 0; i < names.length; i++) {
            sample.add(new ScoreManager.ScoreEntry(names[i], scores[i], levels[i], lines[i], plays[i]));
        }
        return sample;
    }

    public void display(JTextPane textPane) {
        this.currentTextPane = textPane;

        // Prepare textPane as a container
        textPane.removeAll();
        textPane.setLayout(new BorderLayout());
        // make textPane transparent so background image shows
        textPane.setOpaque(false);
        textPane.setForeground(gameSettings.getUIColor("text"));
        textPane.setFont(new Font("Arial", Font.BOLD, 14));

        // Remove previous key listeners and add our own
        for (KeyListener kl : textPane.getKeyListeners()) {
            textPane.removeKeyListener(kl);
        }
        textPane.addKeyListener(new ScoreKeyListener());

        // Build UI
        buildUI();
    }

    private void buildUI() {
        // Top title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        // Left controls: Back + Score reset
        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftControls.setOpaque(false);
        JButton topBack = new JButton("Back");
        topBack.setFocusable(false);
        topBack.setBackground(new Color(40, 40, 48));
        topBack.setForeground(Color.WHITE);
        topBack.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
        topBack.addActionListener(ae -> {
            int res = JOptionPane.showConfirmDialog(screenController,
                "뒤로 가시겠습니까? (현재 화면을 벗어납니다)",
                "확인",
                JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) screenController.showScreen("home");
        });

        JButton resetScores = new JButton("스코어 초기화");
        resetScores.setFocusable(false);
        resetScores.setBackground(new Color(60, 60, 68));
        resetScores.setForeground(Color.WHITE);
        resetScores.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
        resetScores.addActionListener(ae -> {
            int res = JOptionPane.showConfirmDialog(screenController,
                "모든 스코어를 초기화 하시겠습니까? (복구 불가)",
                "스코어 초기화",
                JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                try {
                    scoreManager.clearAllScores();
                } catch (Exception ex) {
                    // ignore; ensure UI still updates
                }
                // After clearing, stop using the in-memory sample list so the screen
                // shows the (now empty) persisted list and placeholder instead of sample entries.
                showOnlyDummy = false;
                currentPage = 0;
                SwingUtilities.invokeLater(this::renderScores);
            }
        });

        leftControls.add(topBack);
        leftControls.add(resetScores);
        titlePanel.add(leftControls, BorderLayout.WEST);

    JLabel title = new JLabel("SCORE BOARD", SwingConstants.CENTER);
    title.setForeground(new Color(0, 230, 64)); // neon green
    // slightly smaller title to save vertical space
    title.setFont(new Font("Segoe UI", Font.BOLD, 22));
    // Place the title on the far right as requested
    titlePanel.add(title, BorderLayout.EAST);
        // We'll compose the whole UI into a background panel so an image can show behind it
        JPanel bgPanel = createBackgroundPanel();
        bgPanel.setLayout(new BorderLayout());
        bgPanel.add(titlePanel, BorderLayout.NORTH);

        // Center: podium + list
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);

        JPanel podium = buildPodiumPanel();
    // Center the podium in its row (helper image removed per request)
        JPanel podiumRow = new JPanel(new BorderLayout());
        podiumRow.setOpaque(false);
        podiumRow.add(podium, BorderLayout.CENTER);
        // top panel contains podium and header
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
    // ensure the podium row and podium are centered
    podiumRow.setAlignmentX(Component.CENTER_ALIGNMENT);
    podium.setAlignmentX(Component.CENTER_ALIGNMENT);
    topPanel.add(podiumRow);

        // Header (columns) using GridBag to control column widths
        JPanel header = new JPanel(new GridBagLayout());
        header.setOpaque(true);
        header.setBackground(new Color(24, 24, 36));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, gameSettings.getUIColor("border")));
    header.setName("score-header");
    String[] headers = {"순위", "이름", "점수", "줄", "플레이타임", "날짜"};
    // Adjusted weights: give a bit more room to '순위' and '날짜' to prevent truncation
    // New distribution: rank, name, score, lines, playtime, date
    double[] weights = {0.08, 0.30, 0.15, 0.10, 0.13, 0.24};
    GridBagConstraints hgbc = new GridBagConstraints();
    // Use normal insets so header and rows align predictably after layout
    hgbc.gridy = 0; hgbc.fill = GridBagConstraints.BOTH; hgbc.insets = new Insets(6,0,6,8);
        for (int i = 0; i < headers.length; i++) {
            hgbc.gridx = i; hgbc.weightx = weights[i];
            JLabel hl = new JLabel(headers[i], (i==1)?SwingConstants.LEFT:SwingConstants.CENTER);
            hl.setForeground(new Color(255, 204, 0)); // warm yellow
            // slightly smaller header font for compact layout
            hl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            // Adjust per-column internal padding to improve visual alignment with row values
            // i==0 (순위): add extra right padding so 이름 doesn't overlap
            // i==1 (이름): keep small left padding
            // i==2,3,4 (점수, 줄, 플레이타임): reduce left padding so header text shifts left
            int leftPad = 6;
            int rightPad = 8;
            if (i == 0) { leftPad = 6; rightPad = 16; }
            else if (i == 1) { leftPad = 4; rightPad = 8; }
            else if (i == 2 || i == 3 || i == 4) { leftPad = 2; rightPad = 8; }
            else if (i == 5) { leftPad = 4; rightPad = 6; }
            hl.setBorder(BorderFactory.createEmptyBorder(6, leftPad, 6, rightPad));
            header.add(hl, hgbc);
        }
        // Make header height match row height (plus small padding) so columns align vertically
            int headerH = ROW_HEIGHT + 6;
        header.setPreferredSize(new Dimension(0, headerH));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, headerH));
        topPanel.add(header);

        center.add(topPanel, BorderLayout.NORTH);

    JScrollPane listScroll = new JScrollPane();
        // prevent horizontal scrolling — keep table columns responsive to width
        listScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        listScroll.setBorder(BorderFactory.createEmptyBorder());
        listScroll.setOpaque(false);
        listScroll.getViewport().setOpaque(false);

    JPanel listContainer = new JPanel();
    listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
    listContainer.setOpaque(false);
    listContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
    // Reserve a consistent height so rows align predictably regardless of content
    listScroll.setViewportView(listContainer);
    listScroll.setPreferredSize(new Dimension(0, ROW_HEIGHT * PAGE_SIZE));
    // keep reference for resize events so we can re-calc column widths reliably
    this.scoreListScroll = listScroll;
    // re-render after viewport resizes (e.g., when window is resized)
    listScroll.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
        @Override
        public void componentResized(java.awt.event.ComponentEvent e) {
            SwingUtilities.invokeLater(() -> renderScores());
        }
    });

        center.add(listScroll, BorderLayout.CENTER);
        bgPanel.add(center, BorderLayout.CENTER);

        // Bottom: footer image (small) + controls
        JPanel controls = buildControlsPanel();
        JPanel footerPanel = buildFooterImagePanel();

        JPanel southWrapper = new JPanel(new BorderLayout());
        southWrapper.setOpaque(false);
        southWrapper.add(footerPanel, BorderLayout.CENTER);
        southWrapper.add(controls, BorderLayout.SOUTH);
        bgPanel.add(southWrapper, BorderLayout.SOUTH);

        // Place bgPanel into the textPane
        currentTextPane.add(bgPanel, BorderLayout.CENTER);

    // Initial render: schedule after layout so component widths (header/viewport)
    // are available and column pixel calculations are correct.
    SwingUtilities.invokeLater(this::renderScores);
    }

    private JPanel buildPodiumPanel() {
        JPanel podium = new JPanel(new GridBagLayout());
        podium.setOpaque(false);

        JPanel first = createPodiumBlock(1);
        JPanel second = createPodiumBlock(2);
        JPanel third = createPodiumBlock(3);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);

        // second, first, third (visual center first)
        gbc.gridx = 0; gbc.gridy = 0; podium.add(second, gbc);
        gbc.gridx = 1; gbc.gridy = 0; podium.add(first, gbc);
        gbc.gridx = 2; gbc.gridy = 0; podium.add(third, gbc);

        return podium;
    }

    private JPanel createPodiumBlock(int rank) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel name = new JLabel("-", SwingConstants.CENTER);
        name.setForeground(gameSettings.getUIColor("text"));
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // medal symbol
        String medal = "#" + rank;
        if (rank == 1) medal = "🥇";
        if (rank == 2) medal = "🥈";
        if (rank == 3) medal = "🥉";

        JLabel rankLbl = new JLabel(medal, SwingConstants.CENTER);
        rankLbl.setForeground(Color.BLACK);
        rankLbl.setFont(new Font("Segoe UI", Font.BOLD, rank == 1 ? 20 : 16));

        // Medal panel draws a rounded card behind the medal icon
        JPanel medalPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg;
                switch (rank) {
                    case 1: bg = new Color(212, 175, 55); break;
                    case 2: bg = new Color(192, 192, 192); break;
                    default: bg = new Color(205, 127, 50); break;
                }
                int w = getWidth();
                int h = getHeight();
                int arc = 14;
                g2.setColor(bg);
                g2.fillRoundRect(4, 4, Math.max(0, w-8), Math.max(0, h-8), arc, arc);
                // subtle inner shadow
                g2.setColor(new Color(0,0,0,40));
                g2.fillRoundRect(4, h-12, Math.max(0, w-8), 8, arc, arc);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        medalPanel.setOpaque(false);
    // scale down medal panel so podium uses less vertical space
    medalPanel.setPreferredSize(new Dimension(120, rank == 1 ? 52 : 40));
        medalPanel.add(rankLbl);

        // pedestal bar to make it look like a podium step
        JPanel pedestal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 40));
                int w = getWidth();
                int h = getHeight();
                g2.fillRoundRect(0, 0, w, h, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pedestal.setOpaque(false);
    pedestal.setPreferredSize(new Dimension(120, 10));

    name.setFont(new Font("Segoe UI", Font.BOLD, rank == 1 ? 14 : 12));
        name.setForeground(gameSettings.getUIColor("text"));

        // mark components so renderScores updates the correct label (name vs medal)
        name.setName("podium-name-" + rank);
        rankLbl.setName("podium-medal-" + rank);

        panel.add(name, BorderLayout.NORTH);
        panel.add(medalPanel, BorderLayout.CENTER);
        panel.add(pedestal, BorderLayout.SOUTH);
        panel.setName("podium-" + rank); // for lookup when rendering
        return panel;
    }

    private JPanel buildControlsPanel() {
        JPanel controls = new JPanel(new BorderLayout());
        controls.setOpaque(false);

        JPanel left = new JPanel(); left.setOpaque(false);
        JPanel right = new JPanel(); right.setOpaque(false);

        JButton prev = new JButton("◀ Prev");
        JButton next = new JButton("Next ▶");
        prev.setFocusable(false);
        next.setFocusable(false);

        prev.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                renderScores();
            }
        });

        next.addActionListener(e -> {
            int totalPages;
            if (showOnlyDummy) {
                int total = buildSampleEntries().size();
                totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
            } else {
                totalPages = Math.max(1, scoreManager.getTotalPages(PAGE_SIZE));
            }
            if (currentPage < totalPages - 1) {
                currentPage++;
                renderScores();
            }
        });

        left.add(prev);
        right.add(next);

        // Note: always in sample-only mode; no toggle provided.

        JLabel pageInfo = new JLabel("", SwingConstants.CENTER);
        pageInfo.setName("page-info");
        pageInfo.setForeground(gameSettings.getUIColor("text"));
        pageInfo.setHorizontalAlignment(SwingConstants.CENTER);

        // small navigation hint below page info
        pageInfo.setText("Page 1 / 1    —    Use ← / → or buttons to navigate. ESC to return.");

        controls.add(left, BorderLayout.WEST);
        controls.add(pageInfo, BorderLayout.CENTER);
        controls.add(right, BorderLayout.EAST);

        return controls;
    }

    private JPanel createBackgroundPanel() {
        return new BackgroundPanel();
    }

    private JPanel buildFooterImagePanel() {
        JPanel footer = new JPanel(new GridBagLayout());
        footer.setOpaque(false);
    // Reserve a bit more vertical space for footer so animated GIFs are visible
    footer.setPreferredSize(new Dimension(0, 160));

        // try classpath first
    // Prefer animated GIF if present (classpath or filesystem).
    // NOTE: previously we added the GIF unscaled which ignored maxH/maxW.
    // To ensure maxH/maxW take effect, scale the GIF here (may affect animation for some GIFs).
    java.net.URL gifUrl = getClass().getResource("/scoreBackground.gif");
        if (gifUrl == null) {
            String[] gifPaths = {"app/src/main/resources/scoreBackground.gif", "src/main/resources/scoreBackground.gif", "scoreBackground.gif"};
            for (String p : gifPaths) {
                java.io.File f = new java.io.File(p);
                if (f.exists()) {
                    try {
                        gifUrl = f.toURI().toURL();
                    } catch (java.net.MalformedURLException ignored) {
                    }
                    break;
                }
            }
        }

        if (gifUrl != null) {
            try {
                // Add GIF as-is to preserve animation and avoid accidental disappearance.
                ImageIcon gifIcon = new ImageIcon(gifUrl);
                JLabel gifLabel = new JLabel(gifIcon);
                gifLabel.setOpaque(false);
                footer.add(gifLabel);
                return footer;
            } catch (Exception ignored) {
                // fall through to static image loading below
            }
        }

        // No GIF — try static images and scale them down to fit the footer.
        String[] exts = {"png", "jpg", "jpeg"};
        Image img = null;
        for (String ext : exts) {
            java.net.URL url = getClass().getResource("/scoreBackground." + ext);
            if (url != null) {
                img = new ImageIcon(url).getImage();
                break;
            }
        }
        if (img == null) {
            String[] filePaths = {
                "app/src/main/resources/scoreBackground.png",
                "app/src/main/resources/scoreBackground.jpg",
                "app/src/main/resources/scoreBackground.jpeg",
                "src/main/resources/scoreBackground.png",
                "src/main/resources/scoreBackground.jpg",
                "src/main/resources/scoreBackground.jpeg",
                "scoreBackground.png",
                "scoreBackground.jpg",
                "scoreBackground.jpeg"
            };
            for (String p : filePaths) {
                java.io.File f = new java.io.File(p);
                if (f.exists()) {
                    img = new ImageIcon(f.getAbsolutePath()).getImage();
                    break;
                }
            }
        }

        if (img != null) {
            // scale to fit height up to 140px and width up to 600px
            int maxH = 200, maxW = 600;
            int iw = img.getWidth(null);
            int ih = img.getHeight(null);
            if (iw > 0 && ih > 0) {
                double scale = Math.min((double) maxW / iw, (double) maxH / ih);
                if (scale > 1.0) scale = 1.0;
                int nw = (int) (iw * scale);
                int nh = (int) (ih * scale);
                Image s = img.getScaledInstance(nw, nh, Image.SCALE_SMOOTH);
                JLabel imgLabel = new JLabel(new ImageIcon(s));
                imgLabel.setOpaque(false);
                footer.add(imgLabel);
            }
        }

        return footer;
    }

    /**
     * Background panel: always draws gradient background and optionally shows an animated
     * GIF on the right side (so it doesn't replace the full background). Animated GIF is
     * loaded via ImageIcon (classpath or filesystem fallback) so animation works.
     */
    private class BackgroundPanel extends JPanel {
        BackgroundPanel() {
            setOpaque(true);
            setLayout(new BorderLayout());
            // Gradient-only background. Footer will host optional decorative images/GIFs.
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(20, 20, 40),
                    0, getHeight(), new Color(40, 20, 60)
            );
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    private void renderScores() {
        if (currentTextPane == null) return;

        // Update podium info (top 3). In sample-only mode use sample entries.
        java.util.List<ScoreManager.ScoreEntry> top3;
        java.util.List<ScoreManager.ScoreEntry> sampleForPodium = null;
        if (showOnlyDummy) {
            sampleForPodium = buildSampleEntries();
            top3 = sampleForPodium.subList(0, Math.min(3, sampleForPodium.size()));
        } else {
            top3 = scoreManager.getTopScores(3);
        }
        for (Component c : findAllComponents(currentTextPane)) {
            if (c instanceof JPanel && c.getName() != null && c.getName().startsWith("podium-")) {
                String[] parts = c.getName().split("-");
                int rank = Integer.parseInt(parts[1]);
                JPanel p = (JPanel) c;
                // find the named name-label specifically
                Component nameComp = null;
                for (Component cc : p.getComponents()) {
                    if (cc instanceof JLabel && ("podium-name-" + rank).equals(cc.getName())) {
                        nameComp = cc;
                        break;
                    }
                }
                if (nameComp instanceof JLabel) {
                    JLabel nameLabel = (JLabel) nameComp;
                    if (top3.size() >= rank) {
                        nameLabel.setText(top3.get(rank - 1).getPlayerName());
                        // color the podium names according to rank
                        if (rank == 1) nameLabel.setForeground(new Color(212, 175, 55));
                        else if (rank == 2) nameLabel.setForeground(new Color(192, 192, 192));
                        else if (rank == 3) nameLabel.setForeground(new Color(205, 127, 50));
                        else nameLabel.setForeground(gameSettings.getUIColor("text"));
                    } else {
                        nameLabel.setText("-");
                        nameLabel.setForeground(gameSettings.getUIColor("text"));
                    }
                }
            }
        }

        // Find the list container inside the JScrollPane
        JPanel listContainer = findListContainer(currentTextPane);
        if (listContainer == null) return;

        listContainer.removeAll();

        List<ScoreManager.ScoreEntry> pageScores;
        // If UI-only sample view is enabled, build an in-memory sample list and page from it.
        if (showOnlyDummy) {
            java.util.List<ScoreManager.ScoreEntry> sample = buildSampleEntries();
            int start = currentPage * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, sample.size());
            if (start >= sample.size()) pageScores = new java.util.ArrayList<>();
            else pageScores = new java.util.ArrayList<>(sample.subList(start, end));
        } else {
            pageScores = scoreManager.getScoresPage(currentPage, PAGE_SIZE);
        }

    // Debug output to help diagnose why list area may be empty at runtime
    int dbgTotal = showOnlyDummy ? (sampleForPodium == null ? buildSampleEntries().size() : sampleForPodium.size()) : scoreManager.getTotalScores();
    System.out.println("[score] totalScores=" + dbgTotal + ", pageScores=" + pageScores.size() + ", currentPage=" + currentPage);

        // NOTE: persistent dummy injection removed. The UI uses only in-memory sample entries

        int startRank = currentPage * PAGE_SIZE + 1;
        int rows = pageScores.size();

        // Compute available width for columns so we can set fixed cell widths per column
    // Match col weights to header: give more room to rank and date to avoid ellipses
    double[] colWeights = {0.08, 0.30, 0.15, 0.10, 0.13, 0.24};
        int availWidth = 0;
        // Prefer the actual viewport width from the scroll pane (most accurate)
        if (this.scoreListScroll != null) {
            try {
                availWidth = this.scoreListScroll.getViewport().getWidth();
            } catch (Exception ignored) {}
        }
        // Prefer header width as a secondary option and keep a reference to header panel
        JPanel headerPanel = findHeaderPanel(currentTextPane);
        if (availWidth <= 0 && headerPanel != null) {
            availWidth = headerPanel.getWidth();
        }
        // Fallback to any ancestor viewport width
        if (availWidth <= 0) {
            Container vp = SwingUtilities.getAncestorOfClass(JViewport.class, listContainer);
            if (vp instanceof JViewport) {
                availWidth = ((JViewport) vp).getWidth();
            }
        }
        if (availWidth <= 0) {
            availWidth = currentTextPane.getWidth();
        }
        if (availWidth <= 0) availWidth = 800; // last-resort fallback
        // account for some padding and scrollbar area; we'll prefer the header width when available
        int padding = 40;
        int usable = Math.max(200, availWidth - padding);
        // If header width is known, use it as the target full-row width so rows align exactly
        int targetWidth = usable;
        if (headerPanel != null && headerPanel.getWidth() > 0) {
            targetWidth = headerPanel.getWidth();
        }
        int[] colPixels = new int[colWeights.length];
        for (int ci = 0; ci < colWeights.length; ci++) {
            colPixels[ci] = (int) Math.round(colWeights[ci] * usable);
        }

        // Update header label sizes to match column pixels (if header exists)
        if (headerPanel != null) {
            Component[] comps = headerPanel.getComponents();
                for (int i = 0; i < comps.length && i < colPixels.length; i++) {
                    if (comps[i] instanceof JLabel) {
                        JLabel hl = (JLabel) comps[i];
                        Dimension d = new Dimension(colPixels[i], ROW_HEIGHT);
                        hl.setPreferredSize(d);
                        hl.setMinimumSize(d);
                        hl.setMaximumSize(d);
                        // Align header text similar to column: name left, date right
                        if (i == 1) hl.setHorizontalAlignment(SwingConstants.LEFT);
                        else if (i == 5) hl.setHorizontalAlignment(SwingConstants.RIGHT);
                        else hl.setHorizontalAlignment(SwingConstants.CENTER);
                    }
                }
            headerPanel.revalidate();
            headerPanel.repaint();
        }

        // If there are no scores on this page, show a friendly placeholder so the
        // panel doesn't look broken/empty (user-visible feedback).
        if (rows == 0) {
            JPanel placeholder = new JPanel(new BorderLayout());
            placeholder.setOpaque(false);
            placeholder.setBorder(BorderFactory.createEmptyBorder(24, 8, 24, 8));
            JLabel noLabel = new JLabel("등록된 기록이 없습니다.", SwingConstants.CENTER);
            noLabel.setForeground(new Color(180, 180, 200));
            noLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            noLabel.setHorizontalAlignment(SwingConstants.CENTER);
            placeholder.add(noLabel, BorderLayout.CENTER);
            // keep consistent vertical space so footer/GIF placement doesn't jump
            int ph = ROW_HEIGHT * Math.min(PAGE_SIZE, 3);
            placeholder.setPreferredSize(new Dimension(0, ph));
            placeholder.setMaximumSize(new Dimension(Integer.MAX_VALUE/2, ph));
            placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
            listContainer.add(placeholder);
            // ensure viewport layout updates
            listContainer.revalidate();
            listContainer.repaint();
        }
        for (int i = 0; i < rows; i++) {
            int rank = startRank + i;
            JPanel row = new JPanel(new GridBagLayout());
            // make row visually distinct from background to avoid terminal look
            Color rowBg = (i % 2 == 0) ? new Color(28, 28, 38) : new Color(36, 36, 46);
            row.setBackground(rowBg);
            row.setOpaque(true);
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, gameSettings.getUIColor("border")),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));

            if (i < pageScores.size()) {
                ScoreManager.ScoreEntry entry = pageScores.get(i);
                JLabel colRank = new JLabel(String.valueOf(rank));
                colRank.setForeground(gameSettings.getUIColor("text"));
                colRank.setFont(new Font("Segoe UI", Font.BOLD, 14));
                Dimension d0 = new Dimension(colPixels[0], ROW_HEIGHT);
                colRank.setPreferredSize(d0);
                colRank.setMinimumSize(d0);
                colRank.setMaximumSize(d0);
                colRank.setHorizontalAlignment(SwingConstants.CENTER);

                JLabel colName = new JLabel(entry.getPlayerName(), SwingConstants.LEFT);
                colName.setForeground(gameSettings.getUIColor("text"));
                colName.setFont(new Font("Segoe UI", Font.BOLD, 14));
                Dimension d1 = new Dimension(colPixels[1], ROW_HEIGHT);
                colName.setPreferredSize(d1);
                colName.setMinimumSize(d1);
                colName.setMaximumSize(d1);
                colName.setHorizontalAlignment(SwingConstants.LEFT);
                // show full name on hover if it's longer than the visible text
                colName.setToolTipText(entry.getPlayerName());

                JLabel colScore = new JLabel(String.format("%,d", entry.getScore()));
                colScore.setForeground(new Color(220, 220, 220));
                colScore.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                Dimension d2 = new Dimension(colPixels[2], ROW_HEIGHT);
                colScore.setPreferredSize(d2);
                colScore.setMinimumSize(d2);
                colScore.setMaximumSize(d2);
                colScore.setHorizontalAlignment(SwingConstants.CENTER);

                JLabel colLines = new JLabel(String.valueOf(entry.getLines()));
                colLines.setForeground(new Color(200, 200, 200));
                colLines.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                Dimension d3 = new Dimension(colPixels[3], ROW_HEIGHT);
                colLines.setPreferredSize(d3);
                colLines.setMinimumSize(d3);
                colLines.setMaximumSize(d3);
                colLines.setHorizontalAlignment(SwingConstants.CENTER);

                JLabel colPlay = new JLabel(entry.getFormattedPlayTime());
                colPlay.setForeground(new Color(200, 200, 200));
                colPlay.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                Dimension d4 = new Dimension(colPixels[4], ROW_HEIGHT);
                colPlay.setPreferredSize(d4);
                colPlay.setMinimumSize(d4);
                colPlay.setMaximumSize(d4);
                colPlay.setHorizontalAlignment(SwingConstants.CENTER);

                JLabel colDate = new JLabel(entry.getFormattedDate());
                colDate.setForeground(new Color(170, 170, 170));
                colDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                Dimension d5 = new Dimension(colPixels[5], ROW_HEIGHT);
                colDate.setPreferredSize(d5);
                colDate.setMinimumSize(d5);
                colDate.setMaximumSize(d5);
                colDate.setHorizontalAlignment(SwingConstants.RIGHT);

                // Emphasize top 3 names with medal colors
                if (rank == 1) colName.setForeground(new Color(212, 175, 55));
                if (rank == 2) colName.setForeground(new Color(192,192,192));
                if (rank == 3) colName.setForeground(new Color(205,127,50));

                GridBagConstraints c = new GridBagConstraints();
                c.gridy = 0; c.fill = GridBagConstraints.BOTH; c.insets = new Insets(2,6,2,6);
                // Row column weights should match header/colWeights for consistent layout
                double[] weights = {0.08, 0.30, 0.15, 0.10, 0.13, 0.24};
                // rank
                c.gridx = 0; c.weightx = weights[0]; c.anchor = GridBagConstraints.CENTER; row.add(colRank, c);
                // name (left aligned under header)
                c.gridx = 1; c.weightx = weights[1]; c.anchor = GridBagConstraints.WEST; c.insets = new Insets(2,0,2,6); row.add(colName, c);
                // restore insets for other columns
                c.insets = new Insets(2,6,2,6);
                // score (center)
                c.gridx = 2; c.weightx = weights[2]; c.anchor = GridBagConstraints.CENTER; row.add(colScore, c);
                // lines
                c.gridx = 3; c.weightx = weights[3]; c.anchor = GridBagConstraints.CENTER; row.add(colLines, c);
                // playtime
                c.gridx = 4; c.weightx = weights[4]; c.anchor = GridBagConstraints.CENTER; row.add(colPlay, c);
                // date (right)
                c.gridx = 5; c.weightx = weights[5]; c.anchor = GridBagConstraints.EAST; row.add(colDate, c);
                // force the row to exactly match header width (targetWidth) and fixed height
                row.setPreferredSize(new Dimension(targetWidth, ROW_HEIGHT));
                row.setMinimumSize(new Dimension(targetWidth, ROW_HEIGHT));
                row.setMaximumSize(new Dimension(targetWidth, ROW_HEIGHT));
                row.setAlignmentX(Component.LEFT_ALIGNMENT);
            }
            // Add the populated row to the list container
            listContainer.add(row);
        }

        // fill remaining space with an invisible filler so we don't draw empty bordered rows
        int remaining = PAGE_SIZE - rows;
        if (remaining > 0) {
            JPanel filler = new JPanel();
            filler.setOpaque(false);
            filler.setPreferredSize(new Dimension(0, remaining * ROW_HEIGHT));
            listContainer.add(filler);
        }

        // Ensure the list container updates its layout after adding rows
        listContainer.revalidate();
        listContainer.repaint();

        // Page indicator
        int totalPages;
        if (showOnlyDummy) {
            int total = buildSampleEntries().size();
            totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        } else {
            totalPages = Math.max(1, scoreManager.getTotalPages(PAGE_SIZE));
        }
        JLabel pageInfo = findPageInfoLabel(currentTextPane);
        if (pageInfo == null) {
            pageInfo = new JLabel();
            pageInfo.setForeground(gameSettings.getUIColor("text"));
            pageInfo.setHorizontalAlignment(SwingConstants.CENTER);
            currentTextPane.add(pageInfo, BorderLayout.SOUTH);
        }
        pageInfo.setText(String.format("Page %d / %d    —    Use ← / → or buttons to navigate.", currentPage + 1, totalPages));

        currentTextPane.revalidate();
        currentTextPane.repaint();
    }

    // Helpers to find components
    private JPanel findListContainer(Container root) {
        for (Component c : root.getComponents()) {
            if (c instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) c;
                Component view = sp.getViewport().getView();
                if (view instanceof JPanel) return (JPanel) view;
            }
            if (c instanceof Container) {
                JPanel res = findListContainer((Container) c);
                if (res != null) return res;
            }
        }
        return null;
    }

    private JLabel findPageInfoLabel(Container root) {
        for (Component c : root.getComponents()) {
            if (c instanceof JLabel && "page-info".equals(c.getName())) return (JLabel) c;
            if (c instanceof Container) {
                JLabel res = findPageInfoLabel((Container) c);
                if (res != null) return res;
            }
        }
        return null;
    }

    private java.util.List<Component> findAllComponents(Container root) {
        java.util.List<Component> list = new java.util.ArrayList<>();
        for (Component c : root.getComponents()) {
            list.add(c);
            if (c instanceof Container) list.addAll(findAllComponents((Container) c));
        }
        return list;
    }

    private JPanel findHeaderPanel(Container root) {
        for (Component c : root.getComponents()) {
            if (c instanceof JPanel && "score-header".equals(c.getName())) return (JPanel) c;
            if (c instanceof Container) {
                JPanel res = findHeaderPanel((Container) c);
                if (res != null) return res;
            }
        }
        return null;
    }

    private class ScoreKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            // consume to prevent default
            e.consume();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ESCAPE:
                    // confirm before leaving
                    int res = JOptionPane.showConfirmDialog(screenController,
                            "뒤로 가시겠습니까? (현재 화면을 벗어납니다)",
                            "확인",
                            JOptionPane.YES_NO_OPTION);
                    if (res == JOptionPane.YES_OPTION) {
                        screenController.showScreen("home");
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if (currentPage > 0) {
                        currentPage--;
                        renderScores();
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    int totalPages;
                    if (showOnlyDummy) {
                        int total = buildSampleEntries().size();
                        totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
                    } else {
                        totalPages = Math.max(1, scoreManager.getTotalPages(PAGE_SIZE));
                    }
                    if (currentPage < totalPages - 1) {
                        currentPage++;
                        renderScores();
                    }
                    break;
            }
            e.consume();
        }

        @Override
        public void keyReleased(KeyEvent e) {
            e.consume();
        }
    }
}
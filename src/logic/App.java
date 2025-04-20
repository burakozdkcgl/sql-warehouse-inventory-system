package logic;

import gui.LoginPanel;
import gui.NavigationPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class App extends JFrame {
    private final JPanel root = new JPanel(new BorderLayout());
    private static App instance;


    public static App getInstance() {
        return instance;
    }




    public App() {
        instance = this;
        setTitle("Warehouse Inventory System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(root); // root handles screen changes
        setBackground(Color.BLACK);



        setLocationRelativeTo(null);

        // Show the window first (temporarily undecorated if needed)
        setVisible(true);

        // THEN change fullscreen state after shown
        if (Session.getInstance().isFullscreen()) {
            dispose();
            setUndecorated(true);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setVisible(true);
        } else {
            disableFullscreen(); // This will actually just re-set everything properly
        }

        setScreen(new SplashScreen()); // show first screen

        // Show splash screen with a delay of 4000ms (4 seconds)
        SplashScreen splashScreen = (SplashScreen) ((AspectRatioPanel) root.getComponent(0)).content;  // Access SplashScreen directly
        splashScreen.showSplashScreen(4000);  // Call the splash screen show method

        // ESC keybind
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "exitFullscreen");

        getRootPane().getActionMap().put("exitFullscreen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Session.getInstance().isFullscreen()) return;
                disableFullscreen();
            }
        });
    }






    public void setScreen(JPanel screen) {
        root.removeAll();

        boolean isSplash = screen instanceof SplashScreen;
        boolean isLogin = screen instanceof LoginPanel;

        if (!isSplash && !isLogin && !(screen instanceof NavigationPanel)) {
            screen = new NavigationPanel(screen);
        }

        JPanel wrapped = new AspectRatioPanel(3.0 / 2.0,
                isSplash ? screen : wrapWithBackground(screen, "/background.png"));

        root.add(wrapped, BorderLayout.CENTER);
        root.revalidate();
        root.repaint();
    }

    private JPanel wrapWithBackground(JPanel content, String imagePath) {
        return new JPanel() {
            private final Image background = new ImageIcon(getClass().getResource(imagePath)).getImage();

            {
                setLayout(new BorderLayout());
                add(content, BorderLayout.CENTER);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };
    }


    public void enableFullscreen() {
        if (Session.getInstance().isFullscreen()) return;

        Session.getInstance().setFullscreen(true);
        dispose();
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    public void disableFullscreen() {

        Session.getInstance().setFullscreen(false);
        dispose();
        setUndecorated(false);
        setResizable(false); // <-- move this up!
        setExtendedState(JFrame.NORMAL);

        // Set content area (JPanel) to match 3:2 aspect ratio
        getContentPane().setPreferredSize(new Dimension(1050, 700));
        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void toggleFullscreen() {
        if (Session.getInstance().isFullscreen()) {
            disableFullscreen();
        } else {
            enableFullscreen();
        }
    }





    static class AspectRatioPanel extends JPanel {
        private final double aspectRatio;
        private final JPanel content;

        public AspectRatioPanel(double aspectRatio, JPanel content) {
            this.aspectRatio = aspectRatio;
            this.content = content;
            setLayout(null); // manual layout for centering
            add(content);
            setOpaque(false);
        }

        @Override
        public void doLayout() {
            Dimension size = getSize();
            int w = size.width;
            int h = (int) (w / aspectRatio);

            if (h > size.height) {
                h = size.height;
                w = (int) (h * aspectRatio);
            }

            int x = (size.width - w) / 2;
            int y = (size.height - h) / 2;
            content.setBounds(x, y, w, h);
            content.revalidate();
            content.repaint();
        }
    }





}
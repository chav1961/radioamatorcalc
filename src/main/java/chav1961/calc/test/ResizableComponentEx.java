package chav1961.calc.test;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ResizableComponentEx extends JFrame {
	private static final long serialVersionUID = 1L;
	private Resizable res;

    public ResizableComponentEx() {

        initUI();
    }

    private void initUI() {

    	JPanel pnl = new JPanel(null);
        add(pnl);

        JPanel area = new JPanel();
        area.setBackground(Color.white);

        res = new Resizable(area);
        res.setBounds(50, 50, 200, 150);
        pnl.add(res);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent me) {

                requestFocus();
                res.repaint();
            }
        });

        setSize(550, 400);
        setTitle("Resizable component");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {

        	ResizableComponentEx ex = new ResizableComponentEx();
            ex.setVisible(true);
        });
    }
}
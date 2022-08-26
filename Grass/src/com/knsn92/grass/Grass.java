package com.knsn92.grass;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Taskbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class Grass extends JFrame implements ActionListener {

	private double[][][] generatedRandomGrassArray;
	private static Color grassColor = Color.GREEN;
	private static Color skyColor = Color.CYAN;
	private static Color[] gamingColors = new Color[] {Color.RED,Color.ORANGE,Color.PINK,Color.YELLOW,Color.GREEN,Color.CYAN,Color.BLUE,Color.MAGENTA};
	private static int grassColorIndex = 0;
	private static int skyColorIndex = 0;

	private static JSlider updateSpeed = new JSlider(0, 1000, 1000);
	private static int updateSpeedValue = 1000;
	private static JLabel speed = new JLabel("");
	private static JLabel Message = new JLabel("...");
	private static JLabel controllerTitle = new JLabel("Grass Controller");
	private static JCheckBox checkBox = new JCheckBox("Repeat Mode");
	private static JCheckBox gamingMode = new JCheckBox("Gaming Mode! ※This mode is bad for the eyes.");
	private static JCheckBox Japanese = new JCheckBox("Japanese");
	private static Button button = new Button("reGenerate");

	public Grass() {
		super("Grass");

		//loading an image from a file
		final Image image = new ImageIcon(Grass.class.getResource("grass.png")).getImage();

		//this is new since JDK 9
		final Taskbar taskbar = Taskbar.getTaskbar();

		try {
			//set icon for mac os (and other systems which do support this method)
			taskbar.setIconImage(image);
		} catch (final UnsupportedOperationException e) {
			System.out.println("The os does not support: 'taskbar.setIconImage'");
		} catch (final SecurityException e) {
			System.out.println("There was a security exception for: 'taskbar.setIconImage'");
		}

		//set icon for windows os (and other systems which do support this method)
		this.setIconImage(image);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setSize(new Dimension(900, 600));
		this.setMinimumSize(new Dimension(900, 600));
		generatedRandomGrassArray = generateRandomGrassArray();
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(new Drawing(), BorderLayout.CENTER);

		JPanel settings = new JPanel();
		settings.setLayout(new FlowLayout());

		button = new Button("reGenerate");
		button.addActionListener(this);

		/*Hashtable<Integer, JLabel> labels = new Hashtable<>();
		
		labels.put(1000, new JLabel("1000"));
		labels.put(750, new JLabel("750"));
		labels.put(500, new JLabel("500"));
		labels.put(0, new JLabel("0"));
		
		updateSpeed.setLabelTable(labels);
		updateSpeed.setPaintLabels(true);
		updateSpeed.setMajorTickSpacing(100);
		updateSpeed.setMinorTickSpacing(50);
		updateSpeed.setPaintTicks(true);
		
		updateSpeed.setPaintTrack(false);*/

		JPanel sliderP = new JPanel();
		sliderP.setLayout(new BoxLayout(sliderP, BoxLayout.PAGE_AXIS));

		//sliderP.add(new JLabel("Update Speed Slider 1/1000s"));
		sliderP.add(speed);
		sliderP.add(Message);
		sliderP.add(updateSpeed);

		settings.add(controllerTitle);
		settings.add(button);
		settings.add(sliderP);
		settings.add(checkBox);
		settings.add(gamingMode);
		settings.add(Japanese);

		JPanel settingsAndOther = new JPanel();
		settingsAndOther.setLayout(new BoxLayout(settingsAndOther, BoxLayout.Y_AXIS));
		settingsAndOther.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));

		settingsAndOther.add(controllerTitle);
		settingsAndOther.add(settings);

		panel.add(settingsAndOther, BorderLayout.SOUTH);
		this.add(panel);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		Grass grass = new Grass();

		Thread labelSetter = new Thread(() -> {
			while (true) {
				updateSpeedValue = updateSpeed.getValue()
						/ (updateSpeed.getValue() <= 750 ? updateSpeed.getValue() <= 500 ? 4 : 2 : 1);

				if (Japanese.isSelected()) {
					speed.setText("更新速度 : " + Double.toString(updateSpeedValue / 1000.0) + "秒");
					checkBox.setText("繰り返しモード");
					Japanese.setText("日本語");
					button.setLabel("再生成");
					controllerTitle.setText("草コントローラー");
					gamingMode.setText("ゲーミングモード！ ※このモードは目に悪いです。");

					if (updateSpeedValue == 1000 & checkBox.isSelected()) {
						Message.setText("これは遅い！ :(");
					} else if (updateSpeedValue == 0 & checkBox.isSelected()) {
						Message.setText("これは速い！ :)");
					} else if (checkBox.isSelected()) {
						Message.setText("アップデート草。");
					} else {
						Message.setText("...");
					}

				} else {
					speed.setText("Update Speed : " + Double.toString(updateSpeedValue / 1000.0) + "s");
					checkBox.setText("Repeat Mode");
					Japanese.setText("Japanese");
					button.setLabel("reGenerate");
					controllerTitle.setText("Grass Controller");
					gamingMode.setText("Gaming Mode! ※This mode is bad for the eyes.");

					if (updateSpeedValue == 1000 & checkBox.isSelected()) {
						Message.setText("This is Slow! :(");
					} else if (updateSpeedValue == 0 & checkBox.isSelected()) {
						Message.setText("This is Fast! :)");
					} else if (checkBox.isSelected()) {
						Message.setText("update grass.");
					} else {
						Message.setText("...");
					}

				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		labelSetter.start();
		while (true) {
			while (checkBox.isSelected()) {
				grass.repaint();
				grass.generatedRandomGrassArray = grass.generateRandomGrassArray();
				try {
					Thread.sleep(updateSpeedValue);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(gamingMode.isSelected()) {
					do {
						skyColorIndex  = new Random().nextInt(gamingColors.length);
						grassColorIndex = new Random().nextInt(gamingColors.length);
					}while(skyColorIndex == grassColorIndex);
					skyColor = gamingColors[skyColorIndex];
					grassColor = gamingColors[grassColorIndex];
				}else {
					grassColor = Color.GREEN;
					skyColor = Color.CYAN;
				}
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.generatedRandomGrassArray = this.generateRandomGrassArray();
		if(gamingMode.isSelected()) {
			do {
				skyColorIndex  = new Random().nextInt(gamingColors.length);
				grassColorIndex = new Random().nextInt(gamingColors.length);
			}while(skyColorIndex == grassColorIndex);
			skyColor = gamingColors[skyColorIndex];
			grassColor = gamingColors[grassColorIndex];
		}else {
			grassColor = Color.GREEN;
			skyColor = Color.CYAN;
		}
		this.repaint();
	}

	private class Drawing extends JPanel {
		public Drawing() {
			super();
			this.setBackground(skyColor);
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			this.setBackground(skyColor);

			for (int[][] args : decodeRandomGrassArray(generatedRandomGrassArray, this.getSize())) {
				g.setColor(grassColor);
				g.fillPolygon(args[0], args[1], 3);
				int height = (int) this.getSize().getHeight();
				g.fillPolygon(new int[] { args[0][0], args[0][2], args[0][2], args[0][0] },
						new int[] { args[1][0], args[1][2], height, height }, 4);
				g.setColor(Color.BLACK);
				g.drawLine(args[0][0], args[1][0], args[0][1], args[1][1]);
				g.drawLine(args[0][1], args[1][1], args[0][2], args[1][2]);
			}
			g.setFont(new Font("Arial", Font.BOLD, (int) (this.getSize().getWidth() * 0.101)));
			//g.drawString("草", (int) (this.getSize().getWidth() / 2 - this.getSize().getWidth() / 10),
			//		(int) this.getSize().getHeight() / 5);
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.PLAIN, (int) (this.getSize().getWidth() / 10)));
			int w = (int) (this.getSize().getWidth() / 2 - this.getSize().getWidth() / 20);
			int h = (int) this.getSize().getHeight() / 5;
			g.drawString("草", w, h + 1);//upper
			g.drawString("草", w + 1, h + 1);//upper right
			g.drawString("草", w + 1, h);//midium right
			g.drawString("草", w + 1, h - 1);//lower right
			g.drawString("草", w, h - 1);//lower
			g.drawString("草", w - 1, h - 1);//lower left
			g.drawString("草", w - 1, h);//midium left
			g.drawString("草", w - 1, h + 1);//upper left
			g.setColor(grassColor);
			g.drawString("草", w, h);//main
		}
	}

	private double[][][] generateRandomGrassArray() {
		double length = 0;
		List<double[][]> returnList = new ArrayList<>();
		Random rand = new Random();
		double upperHeight = rand.nextDouble() / 4 + 0.5;
		double lowerHeight = 0;
		double lowerHeight2 = rand.nextDouble() / 4 + 0.125;
		double upperXPos = 0;
		double lowerXPos = 0;
		double lowerXPos2 = 0;
		do {
			upperHeight = rand.nextDouble() / 4 + 0.5;
			lowerHeight = lowerHeight2;
			lowerHeight2 = rand.nextDouble() / 4 + 0.125;

			lowerXPos = lowerXPos2;
			upperXPos = lowerXPos + rand.nextDouble() / 100 + 0.01;
			lowerXPos2 = upperXPos + rand.nextDouble() / 100 + 0.01;
			returnList.add(new double[][] { { lowerXPos, upperXPos, lowerXPos2 },
					{ lowerHeight, upperHeight, lowerHeight2 } });
			length += lowerXPos2 - lowerXPos;
		} while (length < 1);

		for (int i = 0; i < returnList.size(); i++) {
			double[][] getArr = returnList.get(i);
			getArr[1][0] = 1 - getArr[1][0];
			getArr[1][1] = 1 - getArr[1][1];
			getArr[1][2] = 1 - getArr[1][2];
		}
		return returnList.toArray(new double[0][0][0]);
	}

	private int[][][] decodeRandomGrassArray(double[][][] decordArray, Dimension size) {
		int[][][] grassArray = new int[decordArray.length][2][3];
		for (int i1 = 0; i1 < decordArray.length; i1++) {
			for (int i2 = 0; i2 < 2; i2++) {
				for (int i3 = 0; i3 < 3; i3++) {
					if (i2 == 0) {
						grassArray[i1][i2][i3] = (int) (size.getWidth() * decordArray[i1][i2][i3]);
					} else {
						grassArray[i1][i2][i3] = (int) (size.getHeight() * decordArray[i1][i2][i3]);
					}
				}
			}
		}
		return grassArray;
	}

	/*private int[][][] generateGrassArray(Dimension size) {
		int length = 0;
		List<int[][]> returnList = new ArrayList<>();
		Random rand = new Random();
		int upperHeight = (int) (rand.nextInt((int) (size.getHeight() / 4)) + size.getHeight() / 2);
		int lowerHeight = 0;
		int lowerHeight2 = (int) (rand.nextInt((int) (size.getHeight() / 4)) + size.getHeight() / 8);
		int upperXPos = 0;
		int lowerXPos = 0;
		int lowerXPos2 = 0;
		do {
			upperHeight = (int) (rand.nextInt((int) (size.getHeight() / 4)) + size.getHeight() / 2);
			lowerHeight = lowerHeight2;
			lowerHeight2 = (int) (rand.nextInt((int) (size.getHeight() / 4)) + size.getHeight() / 8);

			lowerXPos = lowerXPos2;
			upperXPos = lowerXPos + rand.nextInt((int) size.getWidth() / 100) + 10;
			lowerXPos2 = upperXPos + rand.nextInt((int) size.getWidth() / 100) + 10;
			returnList.add(
					new int[][] { { lowerXPos, upperXPos, lowerXPos2 }, { lowerHeight, upperHeight, lowerHeight2 } });
			length += lowerXPos2 - lowerXPos;
		} while (length < size.getWidth());

		for (int i = 0; i < returnList.size(); i++) {
			int[][] getArr = returnList.get(i);
			getArr[1][0] = (int) size.getHeight() - getArr[1][0];
			getArr[1][1] = (int) size.getHeight() - getArr[1][1];
			getArr[1][2] = (int) size.getHeight() - getArr[1][2];
		}
		return returnList.toArray(new int[0][0][0]);
	}*/
}

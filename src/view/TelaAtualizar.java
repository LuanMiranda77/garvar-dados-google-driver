package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.MatteBorder;

import com.google.api.services.drive.Drive;

public class TelaAtualizar extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JProgressBar barra = new JProgressBar();
	private final JPanel panel = new JPanel();
	private JLabel lblTempoCorrido;
	private final JLabel lbSeg = new JLabel("");
	private int seg = 0;
	private int min = 0;

	public TelaAtualizar() {
		setBackground(Color.DARK_GRAY);
		setTitle("Processando seu Pedido....");
		setResizable(false);// seuJFrame
		setType(java.awt.Window.Type.UTILITY);// nao minimizar
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(586, 144);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new MatteBorder(10, 5, 5, 5, (Color) new Color(0, 102, 51)));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setModal(true);

		Timer timer = new Timer(1000, new hora());
		timer.start();

		barra.setMaximum(100);
		barra.setStringPainted(true);
		barra.setBounds(22, 40, 527, 34);
		new Temporizador().start();
		contentPane.add(barra);

		JLabel lblNewLabel = new JLabel("Atualizando...");
		lblNewLabel.setFont(new Font("SansSerif", Font.BOLD, 25));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(26, 2, 523, 40);
		contentPane.add(lblNewLabel);
		panel.setBackground(Color.WHITE);
		panel.setForeground(Color.RED);
		panel.setBounds(22, 75, 527, 15);
		contentPane.add(panel);
		panel.setLayout(null);

		lblTempoCorrido = new JLabel("Tempo Corrido: ");
		lblTempoCorrido.setForeground(Color.RED);
		lblTempoCorrido.setBounds(166, 0, 83, 14);
		lblTempoCorrido.setFont(new Font("Dialog", Font.BOLD, 10));
		panel.add(lblTempoCorrido);

		lbSeg.setFont(new Font("Tahoma", Font.BOLD, 14));
		lbSeg.setForeground(Color.BLACK);
		lbSeg.setHorizontalAlignment(SwingConstants.CENTER);
		lbSeg.setBounds(261, 0, 61, 14);

		panel.add(lbSeg);

		setVisible(true);

	}

	class hora implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			seg++;
			if (seg == 60) {
				min++;
				seg = 0;
			}
			lbSeg.setText(String.format("%02d:%02d", min, seg));

		}
	}

	public class Temporizador extends Thread {
		public void run() {
			try {
				Drive service = DriveQuickstart.getDriveServico();
				String fileId =""; 
	             if(System.getProperties().contains("32")) {
	            	 fileId = "1kA4TDnutb6GQqbe9hO2xLi4YaMtw5icQ";
	            	 System.out.println("32");
	             }else {
	            	 fileId = "1uOzv8l0oBXJDLrfoN_Efp0RW0pRK54I9";
	            	 System.out.println("64");
	             }
	
				OutputStream outputStream = new ByteArrayOutputStream();
				service.files().get(fileId).executeMediaAndDownloadTo(outputStream);

				ByteArrayOutputStream buffer = (ByteArrayOutputStream) outputStream;
				InputStream inputStream = new ByteArrayInputStream(buffer.toByteArray());
				FileOutputStream fileOut = new FileOutputStream("C:\\System Clinica\\GabClinic.exe");// destino uso
																											// padrao
				BufferedInputStream in = new BufferedInputStream(inputStream);
				BufferedOutputStream out = new BufferedOutputStream(fileOut);

				byte[] buffer1 = new byte[10240];
				int len = 0;

				while ((len = in.read(buffer1)) > 0) {
					sleep(20);
					barra.setValue(barra.getValue() + 1);
					out.write(buffer1, 0, len);
				}
				in.close();
				out.close();
				if (barra.getValue() >= 100) {
					dispose();
					JOptionPane.showMessageDialog(null,"Atualizadoo com sucesso!!");
					System.exit(0);
				}
				

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException io) {
				io.printStackTrace();
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void dowloadAtualizacao() {
		try {
			Drive service = DriveQuickstart.getDriveServico();
			String fileId = "1kA4TDnutb6GQqbe9hO2xLi4YaMtw5icQ";
			OutputStream outputStream = new ByteArrayOutputStream();
			service.files().get(fileId).executeMediaAndDownloadTo(outputStream);

			ByteArrayOutputStream buffer = (ByteArrayOutputStream) outputStream;
			InputStream inputStream = new ByteArrayInputStream(buffer.toByteArray());
			FileOutputStream fileOut = new FileOutputStream("C:\\System Clinica\\GabClinic-2.0.exe");// destino uso
																										// padrao
			BufferedInputStream in = new BufferedInputStream(inputStream);
			BufferedOutputStream out = new BufferedOutputStream(fileOut);

			byte[] buffer1 = new byte[10240];
			int len = 0;

			while ((len = in.read(buffer1)) > 0) {
				out.write(buffer1, 0, len);
				System.out.println(buffer1.toString());
			}

			in.close();
			out.close();
			System.out.println("Atualizadoo com sucesso!!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException io) {
			io.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TelaAtualizar();
	}
}

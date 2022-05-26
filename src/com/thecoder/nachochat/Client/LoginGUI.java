package com.thecoder.nachochat.Client;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AbstractDocument;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginGUI  extends JFrame {
	private JTextField txtUsername;
	private JTextField txtIP;
	private JTextField txtPort;

	private InputVerifier inputVerifier;

    public LoginGUI() {

		inputVerifier = new MyInputVerifier();

    	setTitle("Login");
    	setResizable(false);
		setSize(370, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
    	getContentPane().setLayout(null);
		

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

    	JLabel lbLogin = new JLabel("Login");
    	lbLogin.setBounds(0, 11, 354, 31);
    	lbLogin.setFont(new Font("Tahoma", Font.PLAIN, 20));
    	lbLogin.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lbLogin);
    	
    	JLabel lbUsername = new JLabel("Usuario:");
    	lbUsername.setFont(new Font("Tahoma", Font.PLAIN, 17));
    	lbUsername.setBounds(23, 53, 92, 25);
    	getContentPane().add(lbUsername);
    	
    	txtUsername = new JTextField();
		((AbstractDocument)txtUsername.getDocument()).setDocumentFilter(new LimitDocumentFilter(15));
		txtUsername.setInputVerifier(inputVerifier);
    	txtUsername.setFont(new Font("Tahoma", Font.PLAIN, 17));
    	txtUsername.setBounds(23, 81, 191, 25);
    	getContentPane().add(txtUsername);
    	txtUsername.setColumns(10);
    	
    	JLabel lbIP = new JLabel("Direccion IP:");
    	lbIP.setFont(new Font("Tahoma", Font.PLAIN, 17));
    	lbIP.setBounds(23, 139, 106, 25);
    	getContentPane().add(lbIP);
    	
    	txtIP = new JTextField();
		((AbstractDocument)txtIP.getDocument()).setDocumentFilter(new LimitDocumentFilter(15));
		txtIP.setInputVerifier(inputVerifier);
    	txtIP.setFont(new Font("Tahoma", Font.PLAIN, 17));
    	txtIP.setColumns(10);
    	txtIP.setBounds(23, 168, 191, 25);
    	getContentPane().add(txtIP);
    	
    	JLabel lbPort = new JLabel("Puerto:");
    	lbPort.setFont(new Font("Tahoma", Font.PLAIN, 17));
    	lbPort.setBounds(23, 226, 106, 21);
    	getContentPane().add(lbPort);
    	
    	txtPort = new JTextField();
		((AbstractDocument)txtPort.getDocument()).setDocumentFilter(new LimitDocumentFilter(4));
		txtPort.setInputVerifier(inputVerifier);
    	txtPort.setFont(new Font("Tahoma", Font.PLAIN, 17));
    	txtPort.setColumns(10);
    	txtPort.setBounds(23, 252, 92, 25);
    	getContentPane().add(txtPort);
    	
    	JButton btLogin = new JButton("Entrar");
    	btLogin.setFont(new Font("Tahoma", Font.PLAIN, 18));
    	btLogin.setBounds(124, 300, 106, 50);
		btLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = txtUsername.getText().trim();
				String ip = txtIP.getText();
				String stport = txtPort.getText();
				
				if (username.isBlank() || ip.isBlank() || stport.isBlank()) {
					JOptionPane.showMessageDialog(null, "No puedes dejar ningun campo vacio", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				ClientMain.login(username, ip, Integer.parseInt(stport));
			}
		});
    	getContentPane().add(btLogin);

        
        
    }

	public class MyInputVerifier extends InputVerifier {
		@Override
		public boolean verify(JComponent input) {
			String text = ((JTextField)input).getText();
			String pattern = "";
			if (input == txtPort){  pattern = "^[0-9][0-9][0-9][0-9]$"; }
			else if (input == txtIP) { pattern = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$"; }
			else if (input == txtUsername) { pattern = "^[a-zA-Z0-9]([_-](?![_-])|[a-zA-Z0-9]){2,15}[a-zA-Z0-9]$"; }
			Pattern r = Pattern.compile(pattern);
			Matcher m = r.matcher(text);
			if (m.matches()) {
				return true;
			}
			else {
				String s = "Error en el formato. \n\r \n\rFormatos validos:\n\r" +
				"   Usuario: solo permitidos caracteres alfanumericos, guiones bajos y guiones. Minimo 4 caracteres. \n\r" +
				"   IP: solo permitido formato de IP (123.123.123.123) \n\r" +
				"   Puerto: solo permitidos numeros del 0000 al 9999";
				JOptionPane.showMessageDialog(null, s, "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		
	}


	public class LimitDocumentFilter extends DocumentFilter {
	
		private int limit;
	
		public LimitDocumentFilter(int limit) {
			if (limit <= 0) {
				throw new IllegalArgumentException("Limit can not be <= 0");
			}
			this.limit = limit;
		}
	
		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			int currentLength = fb.getDocument().getLength();
			int overLimit = (currentLength + text.length()) - limit - length;
			if (overLimit > 0) {
				text = text.substring(0, text.length() - overLimit);
			}
			if (text.length() > 0 || length > 0) {
				super.replace(fb, offset, length, text, attrs); 
			}
		}
	
	}

}


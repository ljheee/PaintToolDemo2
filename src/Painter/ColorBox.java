package Painter;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

public class ColorBox extends JPanel implements MouseListener,ActionListener{//调试板
	private	JPanel jPanel_color0[]=new JPanel[5];
	private	JPanel jPanel_color1[]=new JPanel[32];
	private	JPanel jPanel_color2[]=new JPanel[32];
	private	ImageIcon special_color[]= new ImageIcon[4];
	private BufferedImage bufImg = new BufferedImage(12 ,12,BufferedImage.TYPE_3BYTE_BGR) ,bufImg2 = new BufferedImage(12 ,12,BufferedImage.TYPE_3BYTE_BGR);
	private JLabel jlbImg=new JLabel() ,jlbImg2=new JLabel();
	private	ImageIcon icon;
	private JDialog jDialog;
	private JButton ok, cancel,left,right;
	private Gradient center = new Gradient();
	//private Paint color_border,color_inside;
	private	int rgb[][]={
		{0,255,128,192,128,255,128,255,  0,  0,  0,  0,  0,  0,128,255,128,255,  0,  0,  0,128,  0,128,128,255,128,255,255,255,255,255},
		{0,255,128,192,  0,  0,128,255,128,255,128,255,  0,  0,  0,  0,128,255, 64,255,128,255, 64,128,  0,  0, 64,128,255,255,255,255},
		{0,255,128,192,  0,  0,  0,  0,  0,  0,128,255,128,255,128,255, 64,128, 64,128,255,255,128,255,255,128,  0, 64,255,255,255,255}
	};
	private int i;
	 
	public Painter painter;
	public ColorBox(){//生成版面//
		addMouseListener( this );
		jlbImg.setIcon(new ImageIcon(bufImg));
		jlbImg2.setIcon(new ImageIcon(bufImg2));

		special_color[0] = new ImageIcon( "img/icon1.gif" );
		special_color[1] = new ImageIcon( "img/icon2.gif" );
		special_color[2] = new ImageIcon( "img/icon3.gif" );
		special_color[3] = new ImageIcon( "img/icon4.gif" );
		
		this.setLayout(null);
		
		
		for(i=0;i<jPanel_color0.length;i++){
			jPanel_color0[i]=new JPanel();
			if(i<=2){
				jPanel_color0[i].setBorder(BorderFactory.createEtchedBorder(BevelBorder.RAISED));
				jPanel_color0[i].setLayout(null);
			}
			else{
				jPanel_color0[i].setBackground(new Color(rgb[0][i-3],rgb[1][i-3],rgb[2][i-3]));
				jPanel_color0[i].setLayout(new GridLayout(1,1));
				jPanel_color0[i-2].add(jPanel_color0[i]);
			}
		}
		for(i=0;i<jPanel_color2.length;i++){
			jPanel_color2[i]=new JPanel();
			jPanel_color2[i].setLayout(new GridLayout(1,1));
			jPanel_color2[i].setBounds(new Rectangle(2, 2, 12, 12));
			jPanel_color2[i].setBackground(new Color(rgb[0][i],rgb[1][i],rgb[2][i]));
			if(i>=28)
				jPanel_color2[i].add(new JLabel(special_color[i-28]));
		}
		
		for(i=0;i<jPanel_color1.length;i++){
			jPanel_color1[i]=new JPanel();
			jPanel_color1[i].setLayout(null);
			jPanel_color1[i].add(jPanel_color2[i]);
			this.add(jPanel_color1[i]);
			if(i%2==0){jPanel_color1[i].setBounds(new Rectangle(0, 32+i/2*16, 16, 16));}
			else{jPanel_color1[i].setBounds(new Rectangle( 16,32+i/2*16, 16, 16));}
			jPanel_color1[i].setBorder(BorderFactory.createEtchedBorder(BevelBorder.RAISED));
		}
		
		jPanel_color0[3].add(jlbImg);
		jPanel_color0[4].add(jlbImg2);
		
		Graphics2D g2d = bufImg2.createGraphics();
		g2d.setPaint( Color.white );
		g2d.fill( new Rectangle2D.Double( 0, 0, 12, 12 ) );
		g2d.setPaint( Color.red ); 
		g2d.draw( new Line2D.Double( 0, 0, 12, 12 ) );
		g2d.draw( new Line2D.Double( 11, 0, 0, 11 ) );
		repaint();
		
		this.add(jPanel_color0[1]);
		this.add(jPanel_color0[2]);
		this.add(jPanel_color0[0]);

		jPanel_color0[0].setBounds(new Rectangle(0, 0, 32, 32));
		jPanel_color0[1].setBounds(new Rectangle(4, 4, 16, 16));
		jPanel_color0[2].setBounds(new Rectangle(12,12,16, 16));
		jPanel_color0[3].setBounds(new Rectangle(2, 2, 12, 12));
		jPanel_color0[4].setBounds(new Rectangle(2, 2, 12, 12));
		
		jDialog = new JDialog(painter, "请选择两种颜色做渐层", true);
    	jDialog.getContentPane().setLayout(new FlowLayout());
    	jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE );
    	jDialog.setSize(250, 110);
    	JPanel temp = new JPanel(new GridLayout(2,1));
    	JPanel up = new JPanel(new FlowLayout());
    	JPanel down = new JPanel(new FlowLayout());
    	
		ok = new JButton("确定");
		cancel = new JButton("取消");
		left = new JButton(" ");
		right = new JButton(" ");
		center.setBorder(BorderFactory.createEtchedBorder(BevelBorder.RAISED));
		up.add(left);
		up.add(center);
		up.add(right);
		down.add(ok);
		down.add(cancel);
		temp.add(up);
		temp.add(down);
		jDialog.getContentPane().add(temp);
		
		ok.addActionListener(this);
		cancel.addActionListener(this);
		left.addActionListener(this);
		right.addActionListener(this);
	}
	public void actionPerformed( ActionEvent e ){
		if(e.getSource() == left){
			center.G_color_left = JColorChooser.showDialog( painter, "请选择线的颜色", center.G_color_left );
			center.repaint();
		}
		else if(e.getSource() == right){
			center.G_color_right = JColorChooser.showDialog( painter, "请选择线的颜色", center.G_color_right );
			center.repaint();
		}
		else{
			jDialog.dispose();
		}
	}
	
 	public Dimension getPreferredSize(){
 		return new Dimension( 32, 300);
 	}
	public void mouseClicked( MouseEvent e ){}
	public void mousePressed( MouseEvent e ){
		Graphics2D g2d;
		if(e.getY()>=5 && e.getY()<=20 && e.getX()>=5 && e.getX()<=20){
			g2d = bufImg.createGraphics();
			painter.color_border = JColorChooser.showDialog( painter, "请选择线的颜色", (Color)painter.color_border );
			g2d.setPaint(painter.color_border);
			g2d.fill( new Rectangle2D.Double( 0, 0, 12, 12 ) );
			repaint();
		}
		else if(e.getY()>=13 && e.getY()<=28 && e.getX()>=13 && e.getX()<=28){
			g2d = bufImg2.createGraphics();
			painter.color_inside = JColorChooser.showDialog( painter, "请选择填充颜色", (Color)painter.color_inside );
			g2d.setPaint(painter.color_inside);
			g2d.fill( new Rectangle2D.Double( 0, 0, 12, 12 ) );
			repaint();
		}
		
		if(!(e.getY()>=32 && e.getY()<=288)) return;
		int choose=(e.getY()-32)/16*2+e.getX()/16;
		
		if(e.getButton()==1)//判断是填充边框还是内部
			g2d = bufImg.createGraphics();
		else
			g2d = bufImg2.createGraphics();
		
		if(choose==28){//填充为无色
			g2d.setPaint( Color.white );
			g2d.fill( new Rectangle2D.Double( 0, 0, 12, 12 ) );
			g2d.setPaint( Color.red ); 
			g2d.draw( new Line2D.Double( 0, 0, 12, 12 ) );
			g2d.draw( new Line2D.Double( 11, 0, 0, 11 ) );
			repaint();
				
			if(e.getButton()==1)
				painter.color_border=null;
			else
				painter.color_inside=null;
		}
		else if(choose==29){//填充渐进颜色
			jDialog.show();
			
			g2d.setPaint( new GradientPaint( 0, 0, center.G_color_left, 12, 12, center.G_color_right, true ) );
			g2d.fill( new Rectangle2D.Double( 0, 0, 12, 12 ) );
			repaint();
			
			if(e.getButton()==1)
				painter.color_border=new GradientPaint( 0, 0, center.G_color_left, 12, 12, center.G_color_right, true );
			else
				painter.color_inside=new GradientPaint( 0, 0, center.G_color_left, 12, 12, center.G_color_right, true );
		}
		else if(choose==30){//填充图形
			FileDialog fileDialog = new FileDialog( new Frame() , "请选择一个文件", FileDialog.LOAD );//利用FileDialog选取文件
			fileDialog.show();
			if(fileDialog.getFile()==null) return;//按取消
			
			g2d.drawImage(special_color[2].getImage(), 0, 0,this);//替换调色板左边的图像	
			icon = new ImageIcon(fileDialog.getDirectory()+fileDialog.getFile());//利用FileDialog传图片
			BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(),icon.getIconHeight(),BufferedImage.TYPE_3BYTE_BGR);
			bufferedImage.createGraphics().drawImage(icon.getImage(),0,0,this);//把icon画到BufferedImage上
			repaint();//重绘
			
			if(e.getButton()==1)//判断线的颜色或填充色
				painter.color_border=new TexturePaint(bufferedImage, new Rectangle( icon.getIconWidth(), icon.getIconHeight() ) );
			else
				painter.color_inside=new TexturePaint(bufferedImage, new Rectangle( icon.getIconWidth(), icon.getIconHeight() ) );
		}
		else if(choose==31){//填充文字
			String text=JOptionPane.showInputDialog("请输入文字","文字"); 
			if(text==null) return;
			
			Color FontColor=new Color(0,0,0);
			FontColor = JColorChooser.showDialog( painter, "请选择一个颜色", FontColor );
			
			g2d.drawImage(special_color[3].getImage(), 0, 0,this);
			
			BufferedImage bufferedImage = new BufferedImage(painter.draw_panel_width,painter.draw_panel_height,BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D g2d_bufferedImage = bufferedImage.createGraphics();
			
			FontRenderContext frc = g2d_bufferedImage.getFontRenderContext();//读Graphics中的Font
			Font f = new Font("宋体",Font.BOLD,10);//新Font
			TextLayout tl = new TextLayout(text, f, frc); 
			int sw = (int) (tl.getBounds().getWidth()+tl.getCharacterCount());//计算TextLayout的长
			int sh = (int) (tl.getBounds().getHeight()+3);//计算TextLayout的高
			
			bufferedImage = new BufferedImage(sw,sh,BufferedImage.TYPE_3BYTE_BGR);
			g2d_bufferedImage = bufferedImage.createGraphics(); 
			g2d_bufferedImage.setPaint(Color.WHITE);
			g2d_bufferedImage.fill(new Rectangle(0,0,sw,sh));
			g2d_bufferedImage.setPaint(FontColor);
			g2d_bufferedImage.drawString(text,0,10);
			repaint();
			
			if(e.getButton()==1)
				painter.color_border=new TexturePaint(bufferedImage, new Rectangle(sw,sh) );
			else
				painter.color_inside=new TexturePaint(bufferedImage, new Rectangle(sw,sh) );
		}
		else{//填充一般色
			g2d.setPaint(new Color(rgb[0][choose],rgb[1][choose],rgb[2][choose]));
			g2d.fill( new Rectangle2D.Double( 0, 0, 12, 12 ) );
			repaint();
			
			if(e.getButton()==1)
				painter.color_border=new Color(rgb[0][choose],rgb[1][choose],rgb[2][choose]);
			else
				painter.color_inside=new Color(rgb[0][choose],rgb[1][choose],rgb[2][choose]);
		}
	}

	public void mouseReleased( MouseEvent e ){}
	public void mouseEntered( MouseEvent e ){}
	public void mouseExited( MouseEvent e ){}
	public class Gradient extends JPanel{
		public Color G_color_left = new Color(255,255,255);
		public Color G_color_right = new Color(0,0,0);
		
		public Gradient(){
			repaint();
		}
		
		public void paint(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			
			g2d.setPaint( new GradientPaint( 0, 0, G_color_left, 100, 0, G_color_right, true ) );
			g2d.fill( new Rectangle2D.Double( 0, 0, 100, 25 ) );
		}
		
		public Dimension getPreferredSize(){
			return new Dimension( 100, 25 );
		}
	}
	
}

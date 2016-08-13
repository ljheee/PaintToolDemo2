package Painter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;


import javax.imageio.ImageIO;
import javax.print.DocFlavor.URL;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
 

public class Painter extends JFrame implements ActionListener{

	private boolean hasDraw=false;
	private	Container c = getContentPane();
	private	String menuBar[]={"文件(F)","编辑(E)","视图(V)","说明(H)"};
	private	String menuItem[][]={
		{"新建(N)|78","打开(O)|79","保存(S)|83","另存为(A)","退出(X)|69"},
		{"撤消(U)|90","重复(R)|89","剪切(T)|88","复制(C)|67","粘贴(P)|86"},
		{"工具栏(T)|84","色块(C)|75","状态栏(S)"},
		{"关于(A)"}
	};
	private	JMenuItem jMenuItem[][]=new JMenuItem[4][5];
	private	JMenu jMenu[];
	private	JCheckBoxMenuItem jCheckBoxMenuItem[] = new JCheckBoxMenuItem[3];
	private	String ButtonName[]={"直线","矩形","椭圆","圆角矩形","贝氏曲线","扇型","多边形","铅笔","橡皮擦","文字","选取","喷绘"};
	private JToggleButton jToggleButton[];
    private ButtonGroup buttonGroup;
	private	JPanel jPanel[]=new JPanel[5]; 
	private	JLabel jLabel[]=new JLabel[1];//状态列
	private	String toolname[]={"img/tool1.gif","img/tool2.gif","img/tool3.gif","img/tool4.gif","img/tool5.gif","img/tool8.gif","img/tool9.gif","img/tool7.gif","img/tool6.gif","img/tool10.gif","img/tool11.gif","img/tool12.gif"};
	private	Icon tool[]=new ImageIcon[12];
	private	int i,j,show_x,show_y,drawMethod=7;
	public  int draw_panel_width=700;
	public  int draw_panel_height=500;
	public  Paint color_border;
	public  Paint color_inside;
	private Setter setPanel;
	public  PaintFiled drawPanel;
	private ResizeBlock resizeBlock;
	private ColorBox colorPanel;
	public  Stroke stroke;
	private Shape shape;
	private String isFilled;
	private BufferedImage backGround;
	
	public Painter(){
		//设定JMenuBar，并产生JMenuItem、设置快捷键
		
		JMenuBar bar = new JMenuBar();
		jMenu=new JMenu[menuBar.length];
		for(i=0;i<menuBar.length;i++){
			jMenu[i] = new JMenu(menuBar[i]);
			jMenu[i].setMnemonic(menuBar[i].split("\\(")[1].charAt(0));
			bar.add(jMenu[i]);
		}
	 
		for(i=0;i<menuItem.length;i++){
			for(j=0;j<menuItem[i].length;j++){
				if(i==0 && j==4 || i==1 && j==2) jMenu[i].addSeparator();
				if(i!=2){
					jMenuItem[i][j] = new JMenuItem(menuItem[i][j].split("\\|")[0]);
					if(menuItem[i][j].split("\\|").length!=1)
						jMenuItem[i][j].setAccelerator(KeyStroke.getKeyStroke(Integer.parseInt(menuItem[i][j].split("\\|")[1]), ActionEvent.CTRL_MASK) );
					jMenuItem[i][j].addActionListener(this);
					jMenuItem[i][j].setMnemonic(menuItem[i][j].split("\\(")[1].charAt(0));

					jMenu[i].add(jMenuItem[i][j]);
				}
				else{
					jCheckBoxMenuItem[j] = new JCheckBoxMenuItem(menuItem[i][j].split("\\|")[0]);
					if(menuItem[i][j].split("\\|").length!=1)
						jCheckBoxMenuItem[j].setAccelerator(KeyStroke.getKeyStroke(Integer.parseInt(menuItem[i][j].split("\\|")[1]), ActionEvent.CTRL_MASK) );
					jCheckBoxMenuItem[j].addActionListener(this);
					jCheckBoxMenuItem[j].setMnemonic(menuItem[i][j].split("\\(")[1].charAt(0));
					jCheckBoxMenuItem[j].setSelected( true );
					jMenu[i].add(jCheckBoxMenuItem[j]);
				}
			}
		}
		this.setJMenuBar(bar);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e)
			{
				confirm();
			}
		});
		try {
			backGround=ImageIO.read(this.getClass().getResource("img/board.jpg"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		c.setLayout( new BorderLayout() );
		jPanel[0]=new JPanel(){
			public void paint(Graphics g)
			{
				Graphics2D g2d=(Graphics2D)g;
				Paint p=new TexturePaint(backGround,new Rectangle2D.Double(0,0,100,100));
				g2d.setPaint(p);
				g2d.fill(this.getBounds());
//				 
				super.paint(g);
			}
		};
		 
		for(i=1;i<5;i++)
			jPanel[i]=new JPanel();
			
		jLabel[0]=new JLabel(" 状态栏");
		
		buttonGroup = new ButtonGroup();
		JToolBar jToolBar=new JToolBar("工具箱",JToolBar.VERTICAL);
		jToggleButton=new JToggleButton[ButtonName.length];
		for(i=0;i<ButtonName.length;i++){
			//tool[i] = new ImageIcon(toolname[i]);
			java.net.URL url=this.getClass().getResource(toolname[i]);
			if(url==null)System.out.print(i);
			tool[i]=new ImageIcon(url);
			
			jToggleButton[i] = new JToggleButton(tool[i]);
			if(i==6)
				jToggleButton[i].setToolTipText("按Ctrl并单击完成最后一个操作");
			jToggleButton[i].addActionListener( this );
			jToggleButton[i].setFocusable( false );
			buttonGroup.add(jToggleButton[i]);
		}
		jToolBar.add(jToggleButton[7]);
		jToolBar.add(jToggleButton[8]);
		jToolBar.add(jToggleButton[0]);
		jToolBar.add(jToggleButton[4]);
		jToolBar.add(jToggleButton[1]);
		jToolBar.add(jToggleButton[3]);
		jToolBar.add(jToggleButton[2]);
		jToolBar.add(jToggleButton[5]);
		jToolBar.add(jToggleButton[6]);
		jToolBar.add(jToggleButton[9]);
		jToolBar.add(jToggleButton[10]);
		jToolBar.add(jToggleButton[11]);
		jToggleButton[7].setSelected(true);
		jToolBar.setLayout( new GridLayout( 6,2, 2, 2 ) );
		jPanel[2].add(jToolBar);
		
		jToolBar.setFloatable(false);//不可以动
		
		colorPanel=new ColorBox();
		colorPanel.painter=this;
		color_border=new Color(0,0,0);
		color_inside=null;
		jPanel[3].setLayout(new FlowLayout(FlowLayout.LEFT));
		jPanel[3].add(colorPanel);
		
		drawPanel=new  PaintFiled();
		resizeBlock=new ResizeBlock();
		//resizeBlock.setLayout(null);
		resizeBlock.add(drawPanel);
		drawPanel.setBounds(new Rectangle(2, 2, draw_panel_width, draw_panel_height));
		final JTabbedPane tabbedPane = new JTabbedPane(); 
		tabbedPane.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				if(tabbedPane.getSelectedIndex()==1)
				{
					jPanel[2].setVisible(false);
				}
				else
					setPanel.setVisible(false);
			}});
		setPanel=new Setter();
		 
		setPanel.painter=this;
		
		tabbedPane.addTab("设置",null, setPanel);
		tabbedPane.addTab("工具箱",null, jPanel[2]);
		jPanel[4].add(tabbedPane);
		//jPanel[4].add(setPane);
		jPanel[2].setPreferredSize(new Dimension(100,100));
		jPanel[0].setLayout( new BorderLayout() );
		jPanel[0].add(resizeBlock,BorderLayout.CENTER);
		//jPanel[0].add(jPanel[2],BorderLayout.WEST);
		jPanel[0].add(jPanel[3],BorderLayout.WEST);
		 
		jPanel[0].add(jPanel[4],BorderLayout.EAST);
		
		jLabel[0].setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		resizeBlock.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		resizeBlock.setBackground(new Color(128,128,128));
		jPanel[3].setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(172,168,153)));
		
		c.add(jPanel[0],BorderLayout.CENTER);
		c.add(jLabel[0],BorderLayout.SOUTH);
		stroke=new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
		setSize(draw_panel_width,draw_panel_height);
		setTitle("画图板");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		show();
	}
	public void save(){
		FileDialog fileDialog = new FileDialog( new Frame() , "请指定一个文件名", FileDialog.SAVE );
		fileDialog.show();
		if(fileDialog.getFile()==null) return;
		drawPanel.filename = fileDialog.getDirectory()+fileDialog.getFile();
		//ImageIO.write(new RenderedImage(drawPanel.bufImg), "jpg", drawPanel.filename);
		
	}
	

	@Override
	public void actionPerformed( ActionEvent e ){
		for(i=0;i<ButtonName.length;i++){
			if(e.getSource()==jToggleButton[i]){
				drawMethod=i;
				if(drawMethod==5)
					setPanel.pie_add_ctrl();
				else
					setPanel.pie_remove_ctrl();
				if(drawMethod==7 || drawMethod==8)
					setPanel.pencil_add_ctrl();
				else
					setPanel.pencil_remove_ctrl();
				drawPanel.clear();
				drawPanel.repaint();
   				jMenuItem[1][2].setEnabled(false);
   				jMenuItem[1][3].setEnabled(false);
			}
		}
		
		if(e.getSource()==jMenuItem[1][0]){
			drawPanel.undo();
		}
		else if(e.getSource()==jMenuItem[1][1]){
			drawPanel.redo();
		}
		else if(e.getSource()==jMenuItem[1][2]){
			drawPanel.cut();
		}
		else if(e.getSource()==jMenuItem[1][3]){
			drawPanel.copy();
		}
		else if(e.getSource()==jMenuItem[1][4]){
			drawPanel.paste();
		}
		else if(e.getSource()==jMenuItem[0][0]){//打开文件
			resizeBlock.remove(drawPanel);
			drawPanel=null;
			drawPanel=new PaintFiled();
			resizeBlock.add(drawPanel);
			drawPanel.setBounds(new Rectangle(2, 2, draw_panel_width, draw_panel_height));
			resizeBlock.ctrl_area.setLocation(draw_panel_width+3,draw_panel_height+3);
			resizeBlock.ctrl_area2.setLocation(draw_panel_width+3,draw_panel_height/2+3);
			resizeBlock.ctrl_area3.setLocation(draw_panel_width/2+3,draw_panel_height+3);
			repaint();
		}
		else if(e.getSource()==jMenuItem[0][1]){//打开文档
			FileDialog fileDialog = new FileDialog( new Frame() , "选择一个文件", FileDialog.LOAD );
			fileDialog.show();
			if(fileDialog.getFile()==null) return;
			
			resizeBlock.removeAll();
			drawPanel=null;
			drawPanel=new PaintFiled();
			resizeBlock.add(drawPanel);
			drawPanel.setBounds(new Rectangle(2, 2, draw_panel_width, draw_panel_height));
			
			drawPanel.openfile(fileDialog.getDirectory()+fileDialog.getFile());
		}
		else if(e.getSource()==jMenuItem[0][2]){//保存文件
			if(drawPanel.filename==null){
				save();
			}
			else{
				try{
					int dotpos = drawPanel.filename.lastIndexOf('.');
					ImageIO.write(drawPanel.bufImg, drawPanel.filename.substring(dotpos + 1), new File(drawPanel.filename));
				}
				catch(IOException even) {
					JOptionPane.showMessageDialog(null, even.toString(),"无法保存", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		else if(e.getSource()==jMenuItem[0][3]){//另存
			save();
			try{
				if(drawPanel.filename!=null){
					int dotpos = drawPanel.filename.lastIndexOf('.');
					ImageIO.write(drawPanel.bufImg, drawPanel.filename.substring(dotpos + 1), new File(drawPanel.filename));
				}
				
			}
			catch(IOException even) {
				JOptionPane.showMessageDialog(null, even.toString(),"无法保存", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(e.getSource()==jMenuItem[0][4]){//离开
			confirm();
			System.exit(0);
		}
		else if(e.getSource()==jMenuItem[3][0]){//关于
			JOptionPane.showMessageDialog(null, "\t画图板(2012/4/21)\n姓名：郭天佑\n学号：24320102202466\n", "关于画图板", 1, new ImageIcon("img/paint.gif"));
		}
		 
		if(jCheckBoxMenuItem[0].isSelected())
		{
		//	jPanel[2].setVisible( true );
			jPanel[4].setVisible( true );
		}
       	else
       		jPanel[4].setVisible( false );
		if(jCheckBoxMenuItem[1].isSelected())
			jPanel[3].setVisible( true );
       	else
       		jPanel[3].setVisible( false );
	}
	void confirm()
	{
		 if(hasDraw)
			{
				if(JOptionPane.showConfirmDialog(this, "是否要保存画图板","画图板",JOptionPane.YES_NO_OPTION)==0)
				{
					save();
				}
			}
	}
	public class PaintFiled extends JPanel implements MouseListener, MouseMotionListener, ItemListener, ActionListener, ChangeListener{//中央畫布
		public BufferedImage bufImg;//用于记录最新操作生成的画面
		private BufferedImage bufImg_data[];//记录历史画面，索引值越大，画面越新
		private BufferedImage bufImg_cut;
		private ImageIcon img;
		private JLabel jlbImg;
		private int x1=-1,y1=-1,x2,y2,count,redo_lim,press,temp_x1,temp_y1,temp_x2,temp_y2,temp_x3,temp_y3,step,step_chk,step_arc,step_chk_arc,chk,first,click,cut;
		private Arc2D.Double arc2D = new Arc2D.Double();//扇形
		private Line2D.Double line2D = new Line2D.Double();//直线
		private Ellipse2D.Double ellipse2D = new Ellipse2D.Double();//椭圆
		private Rectangle2D.Double rectangle2D = new Rectangle2D.Double();//矩形
		private CubicCurve2D.Double cubicCurve2D = new CubicCurve2D.Double();//贝斯曲线
		private RoundRectangle2D.Double roundRectangle2D = new RoundRectangle2D.Double();//圆角矩形
		private Polygon polygon;//多边形
		private float data[]={5};
		private Rectangle2D.Double rectangle2D_select = new Rectangle2D.Double();//矩形
		private Ellipse2D.Double ellipse2D_pan = new Ellipse2D.Double();
		private BasicStroke basicStroke_pen = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
		private BasicStroke basicStroke_select = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER,10, data, 0);
		private double center_point_x;
		private double center_point_y;
		private double start;
		private double end;
		public String filename;
		private JTextField textField_font = new JTextField("Fixedsys",16), textField_word = new JTextField("JAVA",16);
		private int size=100;
		private JSpinner fontsize = new JSpinner();
		private JDialog jDialog;
		private JCheckBox bold, italic;
		private JButton ok, cancel;
		public int pie_shape=Arc2D.PIE;
		private int valBold = Font.BOLD;
		private int valItalic = Font.ITALIC;
		private int select_x,select_y,select_w,select_h;
		
		public void resize(){//改变大小
			bufImg = new BufferedImage(draw_panel_width, draw_panel_height,BufferedImage.TYPE_3BYTE_BGR);
			jlbImg = new JLabel(new ImageIcon(bufImg));//在JLabel上放置bufImg，用来绘图
			this.removeAll();
			this.add(jlbImg);
			jlbImg.setBounds(new Rectangle(0, 0, draw_panel_width, draw_panel_height));
			
			//画出原本的矩形//
			Graphics2D g2d_bufImg = (Graphics2D) bufImg.getGraphics();
			g2d_bufImg.setPaint(Color.white);
			g2d_bufImg.fill(new Rectangle2D.Double(0,0,draw_panel_width,draw_panel_height));
			g2d_bufImg.drawImage(bufImg_data[count],0,0,this);
	
			//记录可重做的最大次数，并让重做不可按//
			redo_lim=count++;
			jMenuItem[1][1].setEnabled(false);
			
			//新增一张BufferedImage画面到bufImg_data[count]，并将bufImg画到其上面
			bufImg_data[count] = new BufferedImage(draw_panel_width, draw_panel_height, BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D g2d_bufImg_data = (Graphics2D) bufImg_data[count].getGraphics();
			g2d_bufImg_data.drawImage(bufImg,0,0,this);
			
			//判断坐标的新起点
			press=0;
			
			//使撤销按钮可选
			if(count>0)
				jMenuItem[1][0].setEnabled(true);
		}
		
	public PaintFiled() {
		bufImg_data = new BufferedImage[1000];
		bufImg = new BufferedImage(draw_panel_width, draw_panel_height,BufferedImage.TYPE_3BYTE_BGR);
		jlbImg = new JLabel(new ImageIcon(bufImg));//在JLabel上放置bufImg用于绘制

		this.setLayout(null);
		this.add(jlbImg);
		jlbImg.setBounds(new Rectangle(0, 0, draw_panel_width, draw_panel_height));
		
	 	jMenuItem[1][0].setEnabled(false);
		jMenuItem[1][1].setEnabled(false);
		jMenuItem[1][2].setEnabled(false);
		jMenuItem[1][3].setEnabled(false);
		jMenuItem[1][4].setEnabled(false);
		
		//画出空白
		Graphics2D g2d_bufImg = (Graphics2D) bufImg.getGraphics();
		g2d_bufImg.setPaint(Color.WHITE);
		g2d_bufImg.fill(new Rectangle2D.Double(0,0,draw_panel_width,draw_panel_height));
		
		bufImg_data[count] = new BufferedImage(draw_panel_width, draw_panel_height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2d_bufImg_data = (Graphics2D) bufImg_data[count].getGraphics();
		g2d_bufImg_data.drawImage(bufImg,0,0,this);
		
		//Font//
		jDialog = new JDialog(Painter.this, "请选择文字、字型、大小与属性", true);
		fontsize.setValue(new Integer(100));
		bold = new JCheckBox( "粗体" ,true);
		italic = new JCheckBox( "斜体" ,true);
		ok = new JButton("确定");
		cancel = new JButton("取消");
		JPanel temp_0 = new JPanel(new GridLayout(5,1));
		JPanel temp_1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel temp_2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel temp_3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel temp_4 = new JPanel(new FlowLayout());
		JPanel temp_5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		Container jDialog_c = jDialog.getContentPane();
			
    	jDialog_c.setLayout(new FlowLayout());
    	jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE );
    	jDialog.setSize(250, 200);
    	temp_5.add(new JLabel("文字:"));
		temp_5.add(textField_word);
		temp_1.add(new JLabel("字体:"));
		temp_1.add(textField_font);
		temp_2.add(new JLabel("大小:"));
		temp_2.add(fontsize);
		temp_3.add(new JLabel("属性:"));
		temp_3.add(bold);
		temp_3.add(italic);
		temp_4.add(ok);
		temp_4.add(cancel);
		temp_0.add(temp_5);
		temp_0.add(temp_1);
    	temp_0.add(temp_2);
    	temp_0.add(temp_3);
    	temp_0.add(temp_4);
    	jDialog_c.add(temp_0);
    	
    	bold.addItemListener( this );
    	italic.addItemListener( this );
    	fontsize.addChangeListener( this );
    	ok.addActionListener(this);
    	cancel.addActionListener(this);
    	temp_0.setPreferredSize(new Dimension( 180 , 150 ));
    	
		repaint();
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	public void stateChanged(ChangeEvent e){
		size = Integer.parseInt(fontsize.getValue().toString());
		if(size <= 0) {
			fontsize.setValue(new Integer(1));
			size = 1;
		}
	}
	
	public void actionPerformed( ActionEvent e ){
		jDialog.dispose();
	}
	public void itemStateChanged( ItemEvent e ){
		if ( e.getSource() == bold )
			if ( e.getStateChange() == ItemEvent.SELECTED )
				valBold = Font.BOLD;
			else
				valBold = Font.PLAIN;
		if ( e.getSource() == italic )
			if ( e.getStateChange() == ItemEvent.SELECTED )
				valItalic = Font.ITALIC;
			else
				valItalic = Font.PLAIN;
	}
	
	public Dimension getPreferredSize(){
		return new Dimension( draw_panel_width, draw_panel_height );
	}
	
	public void openfile(String filename){//打开文件
		Graphics2D g2d_bufImg = (Graphics2D) bufImg.getGraphics();
		ImageIcon icon = new ImageIcon(filename);
		g2d_bufImg.drawImage(icon.getImage(),0,0,this);
		
		count++;
		bufImg_data[count] = new BufferedImage(draw_panel_width, draw_panel_height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2d_bufImg_data = (Graphics2D) bufImg_data[count].getGraphics();
		g2d_bufImg_data.drawImage(bufImg,0,0,this);
		
		repaint();
	}
	
	public void undo(){//撤销
		count--;
		
		draw_panel_width=bufImg_data[count].getWidth();
		draw_panel_height=bufImg_data[count].getHeight();
		drawPanel.setSize(draw_panel_width,draw_panel_height);

		bufImg = new BufferedImage(draw_panel_width, draw_panel_height,BufferedImage.TYPE_3BYTE_BGR);
		jlbImg = new JLabel(new ImageIcon(bufImg));//在JLabel上放置bufImg，用于绘图
		this.removeAll();
		this.add(jlbImg);
		jlbImg.setBounds(new Rectangle(0, 0, draw_panel_width, draw_panel_height));
		
		Graphics2D g2d_bufImg = (Graphics2D) bufImg.getGraphics();
		g2d_bufImg.setPaint(Color.white);
		g2d_bufImg.fill(new Rectangle2D.Double(0,0,draw_panel_width,draw_panel_height));
		g2d_bufImg.drawImage(bufImg_data[count],0,0,this);

		resizeBlock.ctrl_area.setLocation(draw_panel_width+3,draw_panel_height+3);
		resizeBlock.ctrl_area2.setLocation(draw_panel_width+3,draw_panel_height/2+3);
		resizeBlock.ctrl_area3.setLocation(draw_panel_width/2+3,draw_panel_height+3);
		
		resizeBlock.x=draw_panel_width;
		resizeBlock.y=draw_panel_height;
		
   		if(count<=0)
   			jMenuItem[1][0].setEnabled(false);
    	jMenuItem[1][1].setEnabled(true);
    	cut=3;
		repaint();
	}

	public void redo(){//重做
		count++;
		
		draw_panel_width=bufImg_data[count].getWidth();
		draw_panel_height=bufImg_data[count].getHeight();
		drawPanel.setSize(draw_panel_width,draw_panel_height);

		bufImg = new BufferedImage(draw_panel_width, draw_panel_height,BufferedImage.TYPE_3BYTE_BGR);
		jlbImg = new JLabel(new ImageIcon(bufImg));//在JLabel上放置bufImg，用于绘图
		this.removeAll();
		this.add(jlbImg);
		jlbImg.setBounds(new Rectangle(0, 0, draw_panel_width, draw_panel_height));
		
		Graphics2D g2d_bufImg = (Graphics2D) bufImg.getGraphics();
		g2d_bufImg.setPaint(Color.white);
		g2d_bufImg.fill(new Rectangle2D.Double(0,0,draw_panel_width,draw_panel_height));
		g2d_bufImg.drawImage(bufImg_data[count],0,0,this);

		resizeBlock.ctrl_area.setLocation(draw_panel_width+3,draw_panel_height+3);
		resizeBlock.ctrl_area2.setLocation(draw_panel_width+3,draw_panel_height/2+3);
		resizeBlock.ctrl_area3.setLocation(draw_panel_width/2+3,draw_panel_height+3);
		
		resizeBlock.x=draw_panel_width;
		resizeBlock.y=draw_panel_height;
		
		if(redo_lim<count)
			jMenuItem[1][1].setEnabled(false);
		jMenuItem[1][0].setEnabled(true);
		cut=3;
		repaint();
	}
	
	public void cut(){
		bufImg_cut = new BufferedImage((int)rectangle2D_select.getWidth(), (int)rectangle2D_select.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		BufferedImage copy = bufImg.getSubimage((int)rectangle2D_select.getX(),(int)rectangle2D_select.getY(),(int)rectangle2D_select.getWidth(),(int)rectangle2D_select.getHeight());
		Graphics2D g2d_bufImg_cut = (Graphics2D) bufImg_cut.createGraphics();
		g2d_bufImg_cut.drawImage(copy,0,0,this);
		
		Graphics2D g2d_bufImg = (Graphics2D) bufImg.getGraphics();
		g2d_bufImg.setPaint(Color.WHITE);
		g2d_bufImg.fill(new Rectangle2D.Double((int)rectangle2D_select.getX(),(int)rectangle2D_select.getY(),(int)rectangle2D_select.getWidth(),(int)rectangle2D_select.getHeight()));
		
		redo_lim=count++;
		jMenuItem[1][1].setEnabled(false);
		
		//新增一张BufferedImage画面到bufImg_data[count]，并将bufImg画到其上
		bufImg_data[count] = new BufferedImage(draw_panel_width, draw_panel_height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2d_bufImg_data = (Graphics2D) bufImg_data[count].getGraphics();
		g2d_bufImg_data.drawImage(bufImg,0,0,this);

		//设置坐标为新起点
		press=0;
		
		//让撤销按钮可用
		if(count>0)
			jMenuItem[1][0].setEnabled(true);
		jMenuItem[1][2].setEnabled(false);
		jMenuItem[1][3].setEnabled(false);
		jMenuItem[1][4].setEnabled(true);
		cut=3;
		repaint();
	}
	public void copy(){
		bufImg_cut = new BufferedImage((int)rectangle2D_select.getWidth(), (int)rectangle2D_select.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		BufferedImage copy = bufImg.getSubimage((int)rectangle2D_select.getX(),(int)rectangle2D_select.getY(),(int)rectangle2D_select.getWidth(),(int)rectangle2D_select.getHeight());
		Graphics2D g2d_bufImg_cut = (Graphics2D) bufImg_cut.createGraphics();
		g2d_bufImg_cut.drawImage(copy,0,0,this);
		jMenuItem[1][4].setEnabled(true);
		cut=1;
		repaint();
	}
	public void paste(){
		cut=2;
		repaint();
	}
	public void mousePressed(MouseEvent e) {
		x1=e.getX();
		y1=e.getY();
		if(first==0){
			polygon = new Polygon();
			polygon.addPoint(x1, y1);
			first=1;
		}
		//设置坐标为新起点
		press=1;
		chk=0;
		if(cut!=2) cut=0;
	}

	public void mouseReleased(MouseEvent e) {
		x2=e.getX();
		y2=e.getY();
		
		if(step_chk==0)//设为贝斯曲线
			step=1;
		else if(step_chk==1)
			step=2;
		
		if(step_chk_arc==0)//设为扇形
			chk=step_arc=1;
		else if(step_chk_arc==1)
			chk=step_arc=2;
		
		if(drawMethod==6 ){
			
			polygon.addPoint(x2, y2);
			repaint();
			if(e.isControlDown())
				toDraw();
		}
		if(drawMethod==10){
			if(cut!=2) cut=1;
			select_x=(int)rectangle2D_select.getX();
			select_y=(int)rectangle2D_select.getY();
			select_w=(int)rectangle2D_select.getWidth();
			select_h=(int)rectangle2D_select.getHeight();
			jMenuItem[1][2].setEnabled(true);
			jMenuItem[1][3].setEnabled(true);
		}
		else if(drawMethod==11)
		{
			
			Graphics2D g2d_bufImg = (Graphics2D) bufImg.getGraphics();
			if(color_border!=null)
			{
				g2d_bufImg.setPaint(color_border);
			}
			else
			{
				g2d_bufImg.setPaint(Color.black);
			}
            Random random = new Random();
            int stroke =setPanel.number;
            for (int i = 0; i != 150; i++)
            {
                int s;
                double r;
                s = random.nextInt(360); 
                r =random.nextDouble();
                int dx=(int) ( 50*Math.sqrt(r*r*r)*Math.sin((double)s)) ;
                int dy=(int) ( 50*Math.sqrt(r*r*r)*Math.cos((double)s)) ;
                int x=x1+dx;
                int y=y1+dy;           
                g2d_bufImg.fillOval(x, y, stroke/2, stroke/2);
              
            }
            redo_lim=count++;
    		jMenuItem[1][1].setEnabled(false);
    		
    		 
    		bufImg_data[count] = new BufferedImage(draw_panel_width, draw_panel_height, BufferedImage.TYPE_3BYTE_BGR);
    		Graphics2D g2d_bufImg_data = (Graphics2D) bufImg_data[count].getGraphics();
    		g2d_bufImg_data.drawImage(bufImg,0,0,this);
    		repaint();
		}
		if((step_chk==2 && step==2) || (step_chk_arc==2 && step_arc==2) || drawMethod==0 || drawMethod==1 || drawMethod==2 || drawMethod==3 || drawMethod==7 || drawMethod==8 || drawMethod==9 || cut==2){
			toDraw();
		}
	}
	public void clear(){
		cut=select_x=select_y=select_w=select_h=step_chk_arc=step_arc=first=step_chk=step=0;
		x1=x2=y1=y2=-1;
	}
	
	public void toDraw(){
		if(x1<0 || y1<0) return;
		chk=3;
		draw(x1,y1,x2,y2);
		Graphics2D g2d_bufImg = (Graphics2D) bufImg.getGraphics();
		if(cut!=2){
			if(color_inside!=null && drawMethod!=8){
				g2d_bufImg.setPaint(color_inside);
				g2d_bufImg.fill(shape);
			}
			if(color_border!=null && drawMethod!=8){
				g2d_bufImg.setPaint(color_border);
				g2d_bufImg.setStroke(stroke);
				g2d_bufImg.draw(shape);
			}
		}
		else{
			g2d_bufImg.drawImage(bufImg_cut,x2,y2,this);
		}
		repaint();
		clear();
		 
		redo_lim=count++;
		jMenuItem[1][1].setEnabled(false);
		
		 
		bufImg_data[count] = new BufferedImage(draw_panel_width, draw_panel_height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2d_bufImg_data = (Graphics2D) bufImg_data[count].getGraphics();
		g2d_bufImg_data.drawImage(bufImg,0,0,this);
		
		 
		press=0;
		
		 
		if(count>0)
			jMenuItem[1][0].setEnabled(true);
		hasDraw=true;
	}
	
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseClicked(MouseEvent e){
		if(click==1){
			System.out.print("dd");
			toDraw();
		}
		click=1;
	}
	
	public void mouseDragged(MouseEvent e){
		x2=e.getX();
		y2=e.getY();
		if(drawMethod==7 || drawMethod==8){
			draw(x1,y1,x2,y2);
			x1=e.getX();
			y1=e.getY();
		}
		if(drawMethod!=9)
			repaint();
	}

	public void mouseMoved(MouseEvent e) {
		show_x=x2=e.getX();
		show_y=y2=e.getY();
		
		jLabel[0].setText(show_x+","+show_y);
		click=0;
		if(drawMethod==7 || drawMethod==8 || cut==2)
			repaint();
	}
	
	public void draw(int input_x1,int input_y1,int input_x2,int input_y2){
		if(drawMethod==0){//直线
			shape=line2D;
			line2D.setLine(input_x1,input_y1,input_x2,input_y2);
		}
		else if(drawMethod==1){//矩形
			shape=rectangle2D;
			rectangle2D.setRect(Math.min(input_x1,input_x2),Math.min(input_y1,input_y2),Math.abs(input_x1-input_x2),Math.abs(input_y1-input_y2));
		}
		else if(drawMethod==2){//椭圆
			shape=ellipse2D;
			ellipse2D.setFrame(Math.min(input_x1,input_x2),Math.min(input_y1,input_y2),Math.abs(input_x1-input_x2),Math.abs(input_y1-input_y2));
		}
		else if(drawMethod==3){//圆角矩形
			shape=roundRectangle2D;
			roundRectangle2D.setRoundRect(Math.min(input_x1,input_x2),Math.min(input_y1,input_y2),Math.abs(input_x1-input_x2),Math.abs(input_y1-input_y2),10.0f,10.0f);
		}
		else if(drawMethod==4){//贝斯曲线
			shape=cubicCurve2D;
			if(step==0){
				cubicCurve2D.setCurve(input_x1,input_y1,input_x1,input_y1,input_x2,input_y2,input_x2,input_y2);
				temp_x1=input_x1;
				temp_y1=input_y1;
				temp_x2=input_x2;
				temp_y2=input_y2;
				step_chk=0;
			}
			else if(step==1){
				cubicCurve2D.setCurve(temp_x1,temp_y1,input_x2,input_y2,input_x2,input_y2,temp_x2,temp_y2);
				temp_x3=input_x2;
				temp_y3=input_y2;
				step_chk=1;
			}
			else if(step==2){
				cubicCurve2D.setCurve(temp_x1,temp_y1,temp_x3,temp_y3,input_x2,input_y2,temp_x2,temp_y2);
				step_chk=2;
			}
		}
		else if(drawMethod==5){//扇型，chk用来防止意外的repaint//
			if(step_arc==0 || chk==1){//步骤控制
				shape=ellipse2D;
				ellipse2D.setFrame(Math.min(input_x1,input_x2),Math.min(input_y1,input_y2),Math.abs(input_x1-input_x2),Math.abs(input_y1-input_y2));
				temp_x1=input_x1;
				temp_y1=input_y1;
				temp_x2=input_x2;
				temp_y2=input_y2;
				step_chk_arc=0;
			}
			else if(step_arc==1 || chk==2){//步骤控制
				shape=arc2D;

				center_point_x = Math.min(temp_x1,temp_x2)+Math.abs(temp_x1-temp_x2)/2;
				center_point_y = Math.min(temp_y1,temp_y2)+Math.abs(temp_y1-temp_y2)/2;
				
				double a = Math.pow(Math.pow(input_x2-center_point_x,2)+Math.pow(input_y2-center_point_y,2),0.5);
				double b = input_x2-center_point_x;
				if(input_y2>center_point_y)
					start=360+Math.acos(b/a)/Math.PI*-180;
				else
					start=Math.acos(b/a)/Math.PI*180;
				
				arc2D.setArc(Math.min(temp_x1,temp_x2),Math.min(temp_y1,temp_y2),Math.abs(temp_x1-temp_x2),Math.abs(temp_y1-temp_y2),start,0,pie_shape);
				step_chk_arc=1;
			}
			else if(step_arc==2 || chk==3){//步骤控制
				shape=arc2D;
				
				double a = Math.pow(Math.pow(input_x2-center_point_x,2)+Math.pow(input_y2-center_point_y,2),0.5);
				double b = input_x2-center_point_x;
				if(input_y2>center_point_y)
					end=360+Math.acos(b/a)/Math.PI*-180-start;
				else
					end=Math.acos(b/a)/Math.PI*180-start;
				if(end<0){end=360-Math.abs(end);}
				
				arc2D.setArc(Math.min(temp_x1,temp_x2),Math.min(temp_y1,temp_y2),Math.abs(temp_x1-temp_x2),Math.abs(temp_y1-temp_y2),start,end,pie_shape);
				step_chk_arc=2;
			}
		}
		else if(drawMethod==6){//多边形
			shape=polygon;
		}
		else if(drawMethod==7 || drawMethod==8){//笔或者橡皮擦
			Graphics2D g2d_bufImg = (Graphics2D) bufImg.getGraphics();
			
			shape=line2D;
			line2D.setLine(input_x1,input_y1,input_x2,input_y2);
			if(drawMethod==7)
				g2d_bufImg.setPaint(color_border);
			else
				g2d_bufImg.setPaint(Color.white);
			g2d_bufImg.setStroke(stroke);
			g2d_bufImg.draw(shape);
		}
		
		else if(drawMethod==9){//文字
			Graphics2D g2d_bufImg = (Graphics2D) bufImg.getGraphics();
    		FontRenderContext frc = g2d_bufImg.getFontRenderContext();
    		jDialog.show();
    		
    		Font f = new Font(textField_font.getText(),valBold + valItalic,size);
    		TextLayout tl = new TextLayout(textField_word.getText(), f, frc);
    		double sw = tl.getBounds().getWidth();
    		double sh = tl.getBounds().getHeight();

    		AffineTransform Tx = AffineTransform.getScaleInstance(1, 1);
    		Tx.translate(input_x2,input_y2+sh);
    		shape = tl.getOutline(Tx);
		}
		else if(drawMethod==10){//选择工具
			shape=rectangle2D;
			rectangle2D.setRect(Math.min(input_x1,input_x2),Math.min(input_y1,input_y2),Math.abs(input_x1-input_x2),Math.abs(input_y1-input_y2));
		}
		
		if(color_border instanceof GradientPaint){
			color_border = new GradientPaint( input_x1,input_y1, (Color)((GradientPaint)color_border).getColor1(), input_x2,input_y2, (Color)((GradientPaint)color_border).getColor2(), true );
		}
		if(color_inside instanceof GradientPaint){
			color_inside = new GradientPaint( input_x1,input_y1, (Color)((GradientPaint)color_inside).getColor1(), input_x2,input_y2, (Color)((GradientPaint)color_inside).getColor2(), true );
		}
		
		hasDraw=true;
	}
	
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		super.paint(g2d); 

		if(press==1 && drawMethod!=10&&drawMethod!=11 && !(x1<0 || y1<0)) {//在最上面的JLabel上作图，只在是起点使画
			draw(x1,y1,x2,y2);
			if(drawMethod==8) return;
			if(color_inside!=null){
				g2d.setPaint(color_inside);
				g2d.fill(shape);
			}
			if(color_border!=null){
				g2d.setPaint(color_border);
				g2d.setStroke(stroke);
				g2d.draw(shape);
			}
		}

		if(drawMethod==10 && cut==0){
			g2d.setPaint(Color.black);
			g2d.setStroke(basicStroke_select);
			rectangle2D_select.setRect(Math.min(x1,x2),Math.min(y1,y2),Math.abs(x1-x2),Math.abs(y1-y2));
			g2d.draw(rectangle2D_select);
		}
		 
		if(cut==1){
			g2d.setPaint(Color.black);
			g2d.setStroke(basicStroke_select);
			rectangle2D_select.setRect(select_x,select_y,select_w,select_h);
			g2d.draw(rectangle2D_select);
		}
		if(cut==2){
			g2d.drawImage(bufImg_cut,x2,y2,this);
		}

	 
		if(drawMethod==7 || drawMethod==8){
			g2d.setPaint(Color.black);
			g2d.setStroke(basicStroke_pen);
			ellipse2D_pan.setFrame(x2-setPanel.number/2,y2-setPanel.number/2,setPanel.number,setPanel.number);
			g2d.draw(ellipse2D_pan);
		}
	}
}
	public class ResizeBlock extends JPanel implements MouseListener, MouseMotionListener{
		public int x,y;
		float data[]={2};
		public JPanel ctrl_area=new JPanel(),ctrl_area2=new JPanel(),ctrl_area3=new JPanel();
		
		public ResizeBlock(){
			this.setLayout(null);
			this.add(ctrl_area);
			this.add(ctrl_area2);
			this.add(ctrl_area3);
			
			ctrl_area.setBounds(new Rectangle(draw_panel_width+3, draw_panel_height+3, 5, 5));
			ctrl_area.setBackground(new Color(0,0,0));
			ctrl_area2.setBounds(new Rectangle(draw_panel_width+3, draw_panel_height/2, 5, 5));
			ctrl_area2.setBackground(new Color(0,0,0));
			ctrl_area3.setBounds(new Rectangle(draw_panel_width/2, draw_panel_height+3, 5, 5));
			ctrl_area3.setBackground(new Color(0,0,0));
    		ctrl_area.addMouseListener(this);
    		ctrl_area.addMouseMotionListener(this);
    		ctrl_area2.addMouseListener(this);
    		ctrl_area2.addMouseMotionListener(this);
    		ctrl_area3.addMouseListener(this);
    		ctrl_area3.addMouseMotionListener(this);
		}
		
		public void mouseClicked(MouseEvent e){}
		public void mousePressed(MouseEvent e){}
		public void mouseReleased(MouseEvent e){
			draw_panel_width=x;
			draw_panel_height=y;
			
			ctrl_area.setLocation(draw_panel_width+3,draw_panel_height+3);
			ctrl_area2.setLocation(draw_panel_width+3,draw_panel_height/2+3);
			ctrl_area3.setLocation(draw_panel_width/2+3,draw_panel_height+3);
			drawPanel.setSize(x,y);
			drawPanel.resize();
			repaint();
		}
		
		public void mouseEntered(MouseEvent e){
			if((JPanel)e.getSource()==ctrl_area)
				setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
			else if((JPanel)e.getSource()==ctrl_area2)
				setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
			else 
				setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
		}
		public void mouseExited(MouseEvent e){
			setCursor(Cursor.getDefaultCursor());
		}
		
    	public void mouseDragged(MouseEvent e){
    		if(e.getSource()==ctrl_area2){
    			x = e.getX()+draw_panel_width;
    			y = draw_panel_height;
    		}
    		else if(e.getSource()==ctrl_area3){
    			x = draw_panel_width;
    			y = e.getY()+draw_panel_height;
    		}
    		else{
    			x = e.getX()+draw_panel_width;
    			y = e.getY()+draw_panel_height;
    		}
    		repaint();
    		jLabel[0].setText(x+","+y);
    	}
    	public void mouseMoved(MouseEvent e) {}
    	
		public void paint(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			super.paint(g2d);
			
			g2d.setPaint( new Color(128,128,128) );
			g2d.setStroke( new BasicStroke( 1,  BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10, data, 0 ) );
			g2d.draw( new Rectangle2D.Double( -1, -1, x+3, y+3 ) );		
		}
		
	}
	
	public static void main(String []args)
	{
		try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
		catch(Exception e){e.printStackTrace();}
		
		Painter app = new Painter();
		app.setVisible(true);
		app.setExtendedState(Frame.MAXIMIZED_BOTH);
	}
}

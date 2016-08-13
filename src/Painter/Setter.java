package Painter;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Setter extends JPanel implements ItemListener, ChangeListener, ActionListener{
	private	JPanel jPanel_set1=new JPanel();
	private	JPanel jPanel_set2=new JPanel();
	private	JPanel temp0=new JPanel(new GridLayout(4,1)), temp1=new JPanel(new FlowLayout(FlowLayout.LEFT)), temp2=new JPanel(new FlowLayout(FlowLayout.LEFT)), temp3=new JPanel(new FlowLayout(FlowLayout.LEFT)), temp4=new JPanel(new FlowLayout(FlowLayout.LEFT)), temp5=new JPanel(new FlowLayout(FlowLayout.LEFT)), temp6=new JPanel(new FlowLayout(FlowLayout.LEFT)), temp7=new JPanel(new FlowLayout(FlowLayout.LEFT)), temp8=new JPanel(new GridLayout(3,1));
	
	public JCheckBox jCheckBox = new JCheckBox();
	private BufferedImage bufImg = new BufferedImage(50 ,50,BufferedImage.TYPE_3BYTE_BGR);
	private JLabel jlbImg=new JLabel();
	float data[]={20};
	JLabel pie[]=new JLabel[3];
	public int number=5;
	JSpinner lineWidthSelect = new JSpinner();
	JRadioButton style[] = new JRadioButton[ 5 ];
	ButtonGroup styleGroup = new ButtonGroup() ,pieGroup = new ButtonGroup();
	private int i;
	//public BasicStroke stroke;
	Painter painter;
    
	public Setter(){//生成版面//
	//	this.setLayout(null);
		jlbImg.setIcon(new ImageIcon(bufImg));
		jPanel_set1.setLayout(new FlowLayout());
		jPanel_set1.setBounds(new Rectangle(0, 0, 100, 160));
		jPanel_set1.setBorder( new TitledBorder(null, "边框",TitledBorder.LEFT, TitledBorder.TOP) );
		lineWidthSelect.setValue(new Integer(5));

		for(i=0;i<=1;i++){
			style[i] = new JRadioButton();
			styleGroup.add(style[i]);
			style[i].addActionListener(this);
		}
		style[0].setSelected( true );
  
		temp1.add(new JLabel("大小:"));
		temp1.add(lineWidthSelect);
		
		temp2.add(new JLabel("虛线:"));
		temp2.add(jCheckBox);
		
		temp3.add(new JLabel("圆角:"));
		temp3.add(style[0]);
		
		temp4.add(new JLabel("尖角:"));
		temp4.add(style[1]);
		
		temp0.add(temp1);
		temp0.add(temp2);
		temp0.add(temp3);
		temp0.add(temp4);
		
		jPanel_set1.add(temp0);
		lineWidthSelect.addChangeListener( this );
		jCheckBox.addItemListener( this );
		
		jPanel_set2.setBounds(new Rectangle(0, 170, 100, 130));
		jPanel_set2.setBorder( new TitledBorder(null, "扇型设定",TitledBorder.LEFT, TitledBorder.TOP) );
		
		for(i=2;i<=4;i++){
			style[i] = new JRadioButton();
			pieGroup.add(style[i]);
			style[i].addActionListener(this);
		}
		style[4].setSelected( true );
		
		pie[0] = new JLabel("弦状:");
		temp5.add(pie[0]);
		temp5.add(style[2]);
		
		pie[1] = new JLabel("开放:");
		temp6.add(pie[1]);
		temp6.add(style[3]);
		
		pie[2] = new JLabel("派状:");
		temp7.add(pie[2]);
		temp7.add(style[4]);
		
		temp8.add(temp5);
		temp8.add(temp6);
		temp8.add(temp7);
		
		temp8.setPreferredSize(new Dimension( 71 , 95 ));

		jPanel_set2.add(temp8);
		this.add(jPanel_set1,BorderLayout.NORTH);
		this.add(jPanel_set2,BorderLayout.SOUTH);
		
	 	 
		
		pie_remove_ctrl();
		
	}
	
	public void pencil_add_ctrl(){
		style[0].setSelected(true);
		style[1].setEnabled(false);
		jCheckBox.setSelected(false);
		jCheckBox.setEnabled(false);
		BasicStroke stroke2 = (BasicStroke) painter.stroke;
		painter.stroke = new BasicStroke(stroke2.getLineWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	}
	
	public void pencil_remove_ctrl(){
		style[1].setEnabled(true);
		jCheckBox.setEnabled(true);
	}
	
	public void pie_add_ctrl(){
		pie[0].setEnabled(true);
		pie[1].setEnabled(true);
		pie[2].setEnabled(true);
		style[2].setEnabled(true);
		style[3].setEnabled(true);
		style[4].setEnabled(true);
	}
	
	public void pie_remove_ctrl(){
		pie[0].setEnabled(false);
		pie[1].setEnabled(false);
		pie[2].setEnabled(false);
		style[2].setEnabled(false);
		style[3].setEnabled(false);
		style[4].setEnabled(false);
	}
	
	public void actionPerformed( ActionEvent e ){
		BasicStroke stroke2 = (BasicStroke) painter.stroke;
		if ( e.getSource() == style[0] )
			painter.stroke = new BasicStroke( stroke2.getLineWidth(), BasicStroke.CAP_ROUND, stroke2.getLineJoin(), stroke2.getMiterLimit(), stroke2.getDashArray(), stroke2.getDashPhase() );
		else if ( e.getSource() == style[1] )
			painter.stroke = new BasicStroke( stroke2.getLineWidth(), BasicStroke.CAP_BUTT, stroke2.getLineJoin(), stroke2.getMiterLimit(), stroke2.getDashArray(), stroke2.getDashPhase() );
		else if ( e.getSource() == style[2] )
			painter.drawPanel.pie_shape=Arc2D.CHORD;
		else if ( e.getSource() == style[3] )
			painter.drawPanel.pie_shape=Arc2D.OPEN;
		else if ( e.getSource() == style[4] )
			painter.drawPanel.pie_shape=Arc2D.PIE;
	}
	
	public void stateChanged(ChangeEvent e){
		number = Integer.parseInt(lineWidthSelect.getValue().toString());
		if(number <= 0) {
			lineWidthSelect.setValue(new Integer(1));
			number = 1;
		}
    	BasicStroke stroke2 = (BasicStroke) painter.stroke;
		painter.stroke = new BasicStroke( number, stroke2.getEndCap(), stroke2.getLineJoin(), stroke2.getMiterLimit(), stroke2.getDashArray(), stroke2.getDashPhase() );
	}
	
	public void itemStateChanged( ItemEvent e ){
		BasicStroke stroke2 = (BasicStroke) painter.stroke;
		if ( e.getSource() == jCheckBox ){
			if ( e.getStateChange() == ItemEvent.SELECTED )
				painter.stroke = new BasicStroke( stroke2.getLineWidth(), stroke2.getEndCap(), stroke2.getLineJoin(), 10, data, 0 );
			else
				painter.stroke = new BasicStroke( stroke2.getLineWidth(), stroke2.getEndCap(), stroke2.getLineJoin());
		}
	}
	
	public Dimension getPreferredSize(){
		return new Dimension( 100, 400 );
	}
}

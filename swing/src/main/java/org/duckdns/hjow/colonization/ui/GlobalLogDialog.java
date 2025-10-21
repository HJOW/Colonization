package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;

import org.duckdns.hjow.commons.data.CompressedDocument;
import org.duckdns.hjow.commons.ui.JLogArea;
import org.duckdns.hjow.commons.util.GUIUtil;
import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.GlobalLogs;

/** 전역 로그 출력 대화상자 */
public class GlobalLogDialog implements GlobalLogUI {
    protected JLogArea taLog;
    protected JDialog dialog;
    protected int detailLevel = 0;
    protected boolean threadSwitch = true;
    protected boolean autoOutput = false;
    
    public GlobalLogDialog(ColonyManager superInstance) {
        init(superInstance);
    }

    protected void init(ColonyManager superInstance) {
        if(superInstance instanceof GUIColonyManager) dialog = new JDialog(((GUIColonyManager) superInstance).getDialog());
        else dialog = new JDialog();
        dialog.setSize(600, 400);
        dialog.setTitle(ColonyManager.t("로그"));
        GUIUtil.centerWindow(dialog);

        dialog.setLayout(new BorderLayout());
        
        JPanel pnMain, pnDown;
        pnMain = new JPanel();
        pnDown = new JPanel();
        pnMain.setLayout(new BorderLayout());
        pnDown.setLayout(new BorderLayout());
        dialog.add(pnMain, BorderLayout.CENTER);
        dialog.add(pnDown, BorderLayout.SOUTH);

        taLog = new JLogArea();
        taLog.setLineWrap(true);
        pnMain.add(new JScrollPane(taLog), BorderLayout.CENTER);
        
        JFileChooser chooserText = new JFileChooser();
        JFileChooser chooserCLog = new JFileChooser();
        
        chooserText.setMultiSelectionEnabled(false);
        chooserText.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        chooserCLog.setMultiSelectionEnabled(false);
        chooserCLog.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        chooserText.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return ColonyManager.t("텍스트 파일") + " (*.txt)";
			}
			@Override
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".txt");
			}
		});
        
        chooserCLog.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return ColonyManager.t(CompressedDocument.FILE_DESC) + " (*" + "." + CompressedDocument.FILE_EXT + ")";
			}
			@Override
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith("." + CompressedDocument.FILE_EXT);
			}
		});
        
        JToolBar toolbar = new JToolBar();
        pnDown.add(toolbar, BorderLayout.CENTER);
        
        JButton btnSave = new JButton(ColonyManager.t("저장"));
        toolbar.add(btnSave);
        
        JButton btnClear = new JButton(ColonyManager.t("비우기"));
        toolbar.add(btnClear);
        
        JButton btnToggleAutoWrite = new JButton(ColonyManager.t("자동기록"));
        toolbar.add(btnToggleAutoWrite);
        
        btnSave.addActionListener(new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent e) {
        		try {
        		    int sel = chooserCLog.showSaveDialog(getDialog());
			        if(sel != JFileChooser.APPROVE_OPTION) return;
			        
			        File file = chooserCLog.getSelectedFile();
			        String nameLower = file.getName().toLowerCase();
			        if(! nameLower.endsWith("." + CompressedDocument.FILE_EXT)) {
			        	file = new File(file.getAbsolutePath() + "." + CompressedDocument.FILE_EXT);
			        }
			        
			        CompressedDocument doc = new CompressedDocument();
			        doc.setContentType("text/plain");
			        doc.setContent(taLog.getText());
			        doc.write(file);
        		} catch(Exception ex) {
        			JOptionPane.showMessageDialog(getDialog(), ColonyManager.t("오류") + " : " + ex.getMessage());
        		}
        	}
        });
        
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	autoOutput = false;
            	taLog.closeWriter();
            	btnToggleAutoWrite.setText(ColonyManager.t("자동기록"));
            	
                GlobalLogs.getInstance().clear();
                taLog.clear();
            }
        });
        
        btnToggleAutoWrite.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				autoOutput = (! autoOutput);
				
				if(autoOutput) {
					try {
					    int sel = chooserText.showSaveDialog(getDialog());
					    if(sel != JFileChooser.APPROVE_OPTION) return;
					    
					    File file = chooserText.getSelectedFile();
					    String nameLower = file.getName().toLowerCase();
					    if(! nameLower.endsWith(".txt")) {
					    	file = new File(file.getAbsolutePath() + ".txt");
					    }
					    
					    taLog.setWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
					    btnToggleAutoWrite.setText(ColonyManager.t("자동기록종료"));
					} catch(Exception ex) {
						JOptionPane.showMessageDialog(getDialog(), ColonyManager.t("오류") + " : " + ex.getMessage());
						taLog.closeWriter();
						btnToggleAutoWrite.setText(ColonyManager.t("자동기록"));
					}
				} else {
					taLog.closeWriter();
					btnToggleAutoWrite.setText(ColonyManager.t("자동기록"));
				}
			}
		});
        
        threadSwitch = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(threadSwitch) {
                    oneCycle();
                    try { Thread.sleep(250L); } catch(InterruptedException ex) { threadSwitch = false; break; }
                }
            }
        }).start();
    }
    
    public JDialog getDialog() {
    	return dialog;
    }

    @Override
    public void log(String msg) {
        log(msg, 1);
    }
    
    @Override
    public void log(String msg, int level) {
        if(detailLevel <= level) taLog.log(msg);
    }

    @Override
    public void clear() {
        taLog.clear();
    }

    @Override
    public void open(ColonyManager superInstance) {
        if(dialog == null) {
            init(superInstance);
        }
        dialog.setVisible(true);
    }

    @Override
    public void close() {
        dialog.setVisible(false);
    }
    
    public void setSize(int w, int h) {
        dialog.setSize(w, h);
    }
    
    public Dimension getSize() {
        return dialog.getSize();
    }
    
    public void setLocationBottom(Window superDialog) {
        Point p = superDialog.getLocation();
        dialog.setSize(superDialog.getWidth(), dialog.getHeight());
        dialog.setLocation((int) p.getX(), (int) (p.getY() + superDialog.getHeight()));
    } 
    
    public void oneCycle() {
        GlobalLogs inst = GlobalLogs.getInstance();
        while(! inst.isEmpty()) {
            log(inst.poll());
        }
    }
    
    @Override
    public void dispose() {
        threadSwitch = false;
        close();
        if(taLog != null) taLog.dispose();
        dialog.removeAll();
        dialog = null;
    }

	public int getDetailLevel() {
		return detailLevel;
	}

	public void setDetailLevel(int detailLevel) {
		this.detailLevel = detailLevel;
	}
}

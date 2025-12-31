package org.duckdns.hjow.colonization.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.duckdns.hjow.colonization.ColonyManager;
import org.duckdns.hjow.colonization.elements.Colony;
import org.duckdns.hjow.colonization.elements.Facility;
import org.duckdns.hjow.colonization.elements.city.City;
import org.duckdns.hjow.colonization.elements.facilities.ResearchCenter;
import org.duckdns.hjow.colonization.elements.research.Research;
import org.duckdns.hjow.commons.core.Disposeable;

/** 연구 하나의 상태를 출력하는 컴포넌트 */
public class ResearchPanel extends JPanel implements Disposeable {
    private static final long serialVersionUID = -4914214161092118509L;
    
    protected long researchKey = 0L;
    protected JProgressBar prog;
    protected JLabel lbName, lbLevel;
    protected JTextField tfResearchCenter;
    protected JTextArea ta;
    
    public ResearchPanel(Research r) { 
        super();
        researchKey = r.getKey();
        
        setLayout(new BorderLayout());
        
        JPanel pnUp, pnCenter;
        pnUp     = new JPanel();
        pnCenter = new JPanel();
        pnUp.setLayout(new BorderLayout());
        pnCenter.setLayout(new BorderLayout());
        add(pnUp    , BorderLayout.NORTH);
        add(pnCenter, BorderLayout.CENTER);
        
        lbName  = new JLabel();
        lbLevel = new JLabel();
        
        JPanel pnLb = new JPanel();
        pnLb.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnLb.add(lbName);
        pnLb.add(lbLevel);
        pnUp.add(pnLb, BorderLayout.WEST);
        
        tfResearchCenter = new JTextField();
        tfResearchCenter.setEditable(false);
        pnUp.add(tfResearchCenter, BorderLayout.CENTER);
        
        JPanel pnRight = new JPanel();
        pnRight.setLayout(new BorderLayout());
        pnUp.add(pnRight, BorderLayout.EAST);
        
        prog = new JProgressBar();
        pnRight.add(prog, BorderLayout.CENTER);
        
        ta = new JTextArea();
        ta.setEditable(false);
        pnCenter.add(ta, BorderLayout.CENTER);
    }
    
    public Research getResearch(Colony col) {
        for(Research r : col.getResearches()) {
            if(r.getKey() == researchKey) return r;
        }
        return null;
    }
    
    /** 화면 새로고침 쓰레드에서 매 사이클마다 호출 */
    public void refresh(int cycle, City city, Colony colony) {
        Research r = getResearch(colony);
        if(r != null) { if(r.getLevel() <= 0 && r.getProgress() <= 0) r = null; }
        
        // 해당 연구가 없는 경우 (일시적인 경우)
        if(r == null) {
            lbName.setText("");
            lbLevel.setText("");
            prog.setValue(0);
            ta.setText("");
            tfResearchCenter.setText("");
            return;
        }
        
        // 연구 진행 시설목록 새로고침
        List<Facility> facs = colony.getFacilities();
        // 연구 시설 필터링
        StringBuilder centerResearching = new StringBuilder("");
        boolean firsts = true;
        for(Facility f : facs) {
            if(f instanceof ResearchCenter) {
                ResearchCenter rch = (ResearchCenter) f;
                if(rch.getResearchKey() == r.getKey()) {
                    if(! firsts) centerResearching = centerResearching.append(", ");
                    centerResearching = centerResearching.append(rch.getName());
                    firsts = false;
                }
            }
        }
        facs = null;
        
        String startMsg = "";
        
        if(firsts) startMsg = ColonyManager.t("이 연구를 진행 중인 시설이 없습니다.");
        else       startMsg = ColonyManager.t("시설") + " : ";
        
        tfResearchCenter.setText(startMsg + centerResearching.toString().trim());
        
        lbName.setText(r.getTitle());
        if(r.getLevel() >= 1)         lbLevel.setText("(Lv " + r.getLevel() + ")");
        else if(r.getProgress() <= 0) lbLevel.setText("(" + ColonyManager.t("연구 필요") + ")");
        else                          lbLevel.setText("(" + ColonyManager.t("연구 중") + ")");
        
        long max = r.getMaxProgress(colony.getSpace());
        if(max >= (Integer.MAX_VALUE / 10)) {
            prog.setMaximum((int) (max / 10000));
            prog.setValue((int) (r.getProgress() / 10000));
        } else {
            prog.setMaximum((int) max);
            prog.setValue((int) r.getProgress());
        }
        
        ta.setText(r.getDescription());
    }

    @Override
    public void dispose() {
        
    }
}
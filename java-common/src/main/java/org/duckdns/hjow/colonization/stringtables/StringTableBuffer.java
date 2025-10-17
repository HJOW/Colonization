package org.duckdns.hjow.colonization.stringtables;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 스트링 테이블 내 값들 중 최근 혹은 빈번하게 쓴 데이터를 메모리 상에 보관하기 위한 용도의 버퍼 */
public class StringTableBuffer { // TODO 공통 lib  로 이관
    protected Map<String, BufferCounts> bufferMap = new HashMap<String, BufferCounts>();
    protected int maximumLength = 100000;
    protected transient long timeGapStandard = 1000L * 60L * 10L;
	public StringTableBuffer() {  }
	public StringTableBuffer(int properSize) { this.maximumLength = properSize; }
	
	/** 버퍼 사용 (버퍼에 데이터가 있으면 버퍼에 있는 값 반환, 아니면 null을 반환. 즉 null이 반환되면 아래 register 메소드를 써서 버퍼에 등록을 해야 함) */
    public synchronized String work(String originalString) {
    	if(originalString == null) return null;
    	BufferCounts cnt = bufferMap.get(originalString);
    	if(cnt == null) return null;
    	cnt.increase(System.currentTimeMillis());
    	return cnt.getContent(); 
    }
    
    /** 컨텐츠 버퍼에 등록 및 유지관리 */
    public synchronized void register(String originalString, String translatedString) {
    	BufferCounts cnt = bufferMap.get(originalString);
    	if(cnt != null) {
    		cnt = new BufferCounts(translatedString, 2, System.currentTimeMillis());
    		bufferMap.put(originalString, cnt);
    		maintain();
    	}
    }
    
    /** 유지관리, 길이 체크해서 한도를 넘어섰을 경우, 오래되고 안쓴 순으로 삭제 */
    protected void maintain() {
    	long now = System.currentTimeMillis();
    	boolean overs = (getLength() >= maximumLength);
    	int idx, len;
    	
    	List<BufferCounts> list1 = new ArrayList<BufferCounts>();
    	List<String>       list2 = new ArrayList<String>();
    	
	    Set<String> strs = bufferMap.keySet();
        for(String s : strs) {
            BufferCounts cnt = bufferMap.get(s);
            if(cnt == null) continue;
            if(now - cnt.getLasts() >= timeGapStandard) cnt.decrease();
            if(overs) {
            	list1.add(cnt);
            	list2.add(s);
            }
        }
        
        Collections.sort(list1);
        len = list1.size();
        for(idx=0; idx<len; idx++) {
        	if(idx == 0 || (getLength() >= maximumLength)) bufferMap.remove(list2.get(idx));
        }
        
        list1.clear();
        list2.clear();
    }
    
	/** 전체 글자 수 반환 (변환 후 텍스트 기준) */
    public int getLength() {
    	int len = 0;
    	Set<String> strs = bufferMap.keySet();
    	for(String s : strs) {
    	    BufferCounts cnt = bufferMap.get(s);
    	    if(cnt == null) continue;
    	    len += cnt.getContent().length();
    	}
    	return len;
    }
    
    /** 버퍼 비우기 */
    public void clear() {
    	bufferMap.clear();
    }
	
	public Map<String, BufferCounts> getBufferMap() {
		return bufferMap;
	}

	public void setBufferMap(Map<String, BufferCounts> bufferMap) {
		this.bufferMap = bufferMap;
	}
	
	public int getMaximumLength() {
		return maximumLength;
	}

	public void setMaximumLength(int maximumLength) {
		this.maximumLength = maximumLength;
	}

	class BufferCounts implements Serializable, Comparable<BufferCounts> {
		private static final long serialVersionUID = 3709127743512096515L;
		String content = "";
		int    counts = 0;
		long   lasts  = 0L;
		
		public BufferCounts() { }
		public BufferCounts(String str, int count, long lasts) { this(); setContent(str); setCounts(count); setLasts(lasts);  }
		public void increase(long lasts) { this.counts++; setLasts(lasts); }
		public void decrease() { this.counts--; }
		public int getCounts() {
			return counts;
		}

		public void setCounts(int counts) {
			this.counts = counts;
		}

		public long getLasts() {
			return lasts;
		}

		public void setLasts(long lasts) {
			this.lasts = lasts;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		@Override
		public int compareTo(BufferCounts o) {
			return new Integer(getCounts()).compareTo(new Integer(o.getCounts()));
		}
	}
}


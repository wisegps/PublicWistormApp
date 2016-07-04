package eventbrocast;

/**
 * @author wu
 * 更新车辆列表广播
 */
public class UpdataCarListEvent {
	
	private String mMsg;  
	
    public UpdataCarListEvent(String msg) {  
        // TODO Auto-generated constructor stub  
        mMsg = msg;  
    }  
    
    public String getMsg(){  
        return mMsg;  
    }  

}

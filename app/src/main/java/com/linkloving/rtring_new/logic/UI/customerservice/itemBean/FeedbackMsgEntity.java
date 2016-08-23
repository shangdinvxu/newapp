package com.linkloving.rtring_new.logic.UI.customerservice.itemBean;


public class FeedbackMsgEntity {
	  private static final String TAG = FeedbackMsgEntity.class.getSimpleName();
	    //名字
	    private String name;
	    //日期
	    private String date;
	    //聊天内容
	    private String text;
	    //是否为自己提问
	    private boolean isQuestion = true;
	    
	    public FeedbackMsgEntity() {
	    }
        /**
         * 
         * @param name 姓名
         * @param date 日期
         * @param text 内容
         * @param isQuestion 是否是问
         */
	    public FeedbackMsgEntity(String name, String date, String text, boolean isQuestion) {
	        this.name = name;
	        this.date = date;
	        this.text = text;
	        this.isQuestion = isQuestion;
	        toString();
	    }

	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public String getDate() {
	        return date;
	    }

	    public void setDate(String date) {
	        this.date = date;
	    }

	    public String getText() {
	        return text;
	    }

	    public void setText(String text) {
	        this.text = text;
	    }

	    public boolean getMsgType() {
	        return isQuestion;
	    }

	    public void setMsgType(boolean isQuestion) {
	    	this.isQuestion = isQuestion;
	    }
		@Override
		public String toString() {
			
			// TODO Auto-generated method stub
			return "反馈内容:"+text;
		}
	    
	    

	    
}

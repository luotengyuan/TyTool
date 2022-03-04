package com.lois.unclassifiedj.network.email;

import java.util.List;
import java.util.Properties;

import javax.activation.DataSource;

/**
 * @Description 邮件信息描述类
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class Email {

	/**配置session属性*/
	private Properties props;
	/**发送者*/
	private String sender;
	/**发送者邮箱账号*/
	private String username;
	/**发送者邮箱密码*/
	private String password;
	/**接收者*/
	private String receiver;
	/**标题*/
	private String title;
	/**邮件内容*/
	private String content;
	/**附件*/
	private List<DataSource> attachments;
	
	
	public Email() {

	}

	public Properties getProps() {
		return props;
	}
	public void setProps(Properties props) {
		this.props = props;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<DataSource> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<DataSource> attachments) {
		this.attachments = attachments;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}

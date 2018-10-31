package com.qpf.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.qpf.Untils.MailUntil;

@RestController
public class MailRest {

	@RequestMapping("/sendMessage")
	public String sendEmail() throws JsonProcessingException {
	       boolean isSend = MailUntil.sendMail("这是一封测试邮件", new String[]{"3056302661@qq.com"}, null, "<h3><a href='http://www.baidu.com'>恭喜董先生喜中100万大奖</a></h3>", null);
	       return "发送邮件:" + isSend;
	   }
	
}

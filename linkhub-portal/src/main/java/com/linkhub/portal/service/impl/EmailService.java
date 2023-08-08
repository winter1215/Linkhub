package com.linkhub.portal.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.linkhub.common.config.exception.GlobalException;
import com.linkhub.common.config.redis.RedisCache;
import com.linkhub.common.enums.CommonConstants;
import com.linkhub.common.enums.RedisPrefix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * 邮件发送
 * @author winter
 * @create 2022-11-22 上午11:52
 */
@Component
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    RedisCache redisCache;
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送验证码
     * @param mail: 邮件地址
     * @return: void
     * @author: winter
     * @date: 2022/11/22 下午12:02
     * @description:
     */

    public void sendVerifyCode(String mail) {
        try {
            // 获取随即四位数验证码
            String code = RandomUtil.randomNumbers(4);
            // 存 redis
            redisCache.setCacheObject(String.format(RedisPrefix.PREFIX_VERIFY_CODE, mail), code, RedisPrefix.CODE_AVAILABLE_TIME, TimeUnit.MINUTES);
            // 发送邮件
            sendMail(mail, "Linkhub_验证码", String.format(CommonConstants.REGISTER_CODE_TEMPLATE, code));

        } catch (Exception e) {
            log.error("验证码发送异常 => mail: {}, 异常为: ", mail, e);
            throw new GlobalException("验证码发送异常");
        }
    }

    /**
     * 发送纯文本邮件.
     *
     * @param to      目标email 地址
     * @param subject 邮件主题
     * @param text    纯文本内容
     */
        public void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }
    /**
     * 发送邮件并携带附件.
     * 请注意 from 、 to 邮件服务器是否限制邮件大小
     *
     * @param to       目标email 地址
     * @param subject  邮件主题
     * @param text     纯文本内容
     * @param filePath 附件的路径 当然你可以改写传入文件
     */
    public void sendMailWithAttachment(String to, String subject, String text, String filePath) throws MessagingException {
        File attachment = new File(filePath);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(mimeMessage,true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        helper.addAttachment(attachment.getName(),attachment);
        javaMailSender.send(mimeMessage);
    }

    /**
     * 发送富文本邮件.
     *
     * @param to       目标email 地址
     * @param subject  邮件主题
     * @param text     纯文本内容
     * @param filePath 附件的路径 当然你可以改写传入文件
     */
    public void sendRichMail(String to, String subject, String text, String filePath) throws   MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(mimeMessage,true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);

        helper.setText(text,true);
        // 图片占位写法  如果图片链接写入模板 注释下面这一行
        helper.addInline("qr",new FileSystemResource(filePath));
        javaMailSender.send(mimeMessage);

    }
}

package com.heart.utils;

import com.heart.config.FtpProperties;
import com.heart.execption.FtpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.io.IOException;

/**
 * Ftp客户端工厂
 */
@Slf4j
public class FtpClientFactory extends BasePooledObjectFactory<FTPClient> {
    private FtpProperties ftpProperties;

    public FtpClientFactory(FtpProperties ftpProperties) {
        this.ftpProperties = ftpProperties;
    }

    @Override
    public FTPClient create() throws FtpException {
        final FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding(ftpProperties.getEncoding());
        ftpClient.setConnectTimeout(ftpProperties.getConnectTimeout());
        try {
            // 连接ftp服务器
            ftpClient.connect(ftpProperties.getHost(), ftpProperties.getPort());
            // 登录ftp服务器
            ftpClient.login(ftpProperties.getName(), ftpProperties.getPassword());
        } catch (IOException e) {
            throw new FtpException("创建ftp连接失败", e);
        }
        // 是否成功登录服务器
        final int replyCode = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                // ignore
            }
            throw new FtpException(String.format("Login failed for user [%s], reply code is: [%s]",
                    ftpProperties.getName(), replyCode));
        }
        ftpClient.setBufferSize(ftpProperties.getBufferSize());
        //设置模式
        if (ftpProperties.isPassiveMode()) {
            ftpClient.enterLocalPassiveMode();
        }
        return ftpClient;
    }

    @Override
    public PooledObject<FTPClient> wrap(FTPClient obj) {
        return new DefaultPooledObject<>(obj);
    }

    /**
     * 销毁FtpClient对象
     */
    @Override
    public void destroyObject(PooledObject<FTPClient> ftpPooled) {
        if (ftpPooled == null) {
            return;
        }
        FTPClient ftpClient = ftpPooled.getObject();

        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
            }
        } catch (IOException io) {
            log.error("ftp client logout failed...{}", io);
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException io) {
                log.error("close ftp client failed...{}", io);
            }
        }
    }

    /**
     * 验证FtpClient对象是否还可用
     */
    @Override
    public boolean validateObject(PooledObject<FTPClient> ftpPooled) {
        try {
            FTPClient ftpClient = ftpPooled.getObject();
            return ftpClient.sendNoOp();
        } catch (IOException e) {
            log.error("Failed to validate client: {}", e);
        }
        return false;
    }

}
package com.weavernorth.meitanzy.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;

public class MeiTanZyFtpUtil {

    private static final Log LOGGER = LogFactory.getLog(MeiTanZyFtpUtil.class);

    /**
     * 连接 FTP 服务器
     *
     * @param addr     FTP 服务器 IP 地址
     * @param port     FTP 服务器端口号
     * @param username 登录用户名
     * @param password 登录密码
     * @return
     */
    public static FTPClient getFtpClient(String addr, int port, String username, String password) {
        FTPClient ftpClient = new FTPClient();
        try {
            /*
             * 连接 FTP 服务器
             * 如果连接失败，则此时抛出异常，如ftp服务器服务关闭时，抛出异常：
             * java.net.ConnectException: Connection refused: connect*/
            ftpClient.connect(addr, port);
            ftpClient.login(username, password);

            ftpClient.setControlEncoding("GBK");
            FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
            conf.setServerLanguageCode("zh");
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            /*
             * 确认应答状态码是否正确完成响应
             * 凡是 2开头的 isPositiveCompletion 都会返回 true，因为它底层判断是：
             * return (reply >= 200 && reply < 300);
             */
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                /*
                 * 如果 FTP 服务器响应错误 中断传输、断开连接
                 * abort：中断文件正在进行的文件传输，成功时返回 true,否则返回 false
                 * disconnect：断开与服务器的连接，并恢复默认参数值
                 */
                LOGGER.error("ftp连接失败getFtpClient()： reply = " + reply);
                ftpClient.abort();
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            LOGGER.error("========FTP服务器连接登录失败，请检查连接参数是否正确，或者网络是否通畅=========: " + e);
        }
        return ftpClient;
    }

    /**
     * 单个文件上传
     *
     * @param path        目的地路径
     * @param fileName    文件名称
     * @param inputStream 文件输入流
     * @param ftpClient   ftp对象
     * @return 上传结果
     */
    public static boolean upload(String path, String fileName, InputStream inputStream, FTPClient ftpClient) {
        boolean flag = false;
        try {
            if (!ftpClient.changeWorkingDirectory(path)) {
                LOGGER.info("目录： " + path + " 不存在，创建目录");
                createPaths(path, ftpClient);
            }
            flag = ftpClient.storeFile(fileName, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * 创建目录
     *
     * @param savePath  目标路径
     * @param ftpClient ftp对象
     */
    private static void createPaths(String savePath, FTPClient ftpClient) {
        String[] paths = savePath.split("/");
        String tempPath = "";
        for (String dir : paths) {
            if (StringUtils.isBlank(dir)) {
                continue;
            }
            tempPath += "/" + dir;
            try {
                boolean b = ftpClient.changeWorkingDirectory(tempPath);
                if (!b) {
                    ftpClient.makeDirectory(tempPath);
                    ftpClient.changeWorkingDirectory(tempPath);
                }
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("创建目录异常createPaths()： " + e);
            }
        }
    }

    public static void disconnect(FTPClient ftpClient) {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                LOGGER.error("ftp断开连接异常");
            }
        }
    }

}

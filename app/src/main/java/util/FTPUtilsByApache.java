package util;


import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 *
 */
public class FTPUtilsByApache {

    Logger logger = Logger.getLogger(FTPUtilsByApache.class);
    private final String encoding = System.getProperty("file.encoding");
    private FTPClient ftpClient = null;
    private final String hostname;
    private final int port;
    private final String username;
    private final String password;

    public FTPUtilsByApache() {
        this.hostname = FTPConnectConfig.hostname;
        this.port = FTPConnectConfig.port;
        this.username = FTPConnectConfig.username;
        this.password = FTPConnectConfig.password;
    }

    /**
     * 创建对象
     */
    public FTPUtilsByApache(String hostname, int port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * 获取FTP客户端连接工具
     *
     * @return
     */
    public FTPClient getFTPClient() {
        if (this.ftpClient == null || !this.ftpClient.isConnected()) {
            this.ftpClient = new FTPClient();
            try {
                this.ftpClient.connect(hostname, port);
                int reply = ftpClient.getReplyCode();
                boolean login = this.ftpClient.login(username, password);
                if (login) {
                    logger.info("FTP登录成功！");
                } else {
                    logger.info("FTP登录失败！");
                }
                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftpClient.disconnect();
                    return null;
                }
//                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                //这个被动模式登录非常重要！
                ftpClient.enterLocalPassiveMode();
                return this.ftpClient;
            } catch (IOException e) {
                e.printStackTrace();
                logger.info("获取连接失败");
                return null;
            }
        } else {
            return this.ftpClient;
        }
    }

    /**
     * 查看ftp服务器是否连接成功
     */
    public void isConnect(FTPClient f) {
        if (f.isConnected()) {
            Log.i("ftp连接", "ftp连接成功");
        } else {
            Log.e("ftp连接", "ftp连接失败");
        }
    }


    /**
     * @param pathname 用户的登录名称（app登录名称）
     * @return true:创建成功或已存在  false：创建失败
     * @Description: 在当前文件夹下创建pathname文件夹
     * @Description: 创建ftp目录(相对路径)
     */
    public boolean makeDirectory(String pathname) {
        if (!ftpClient.isConnected()) {
            logger.info("连接ftp失败！");
            return false;//连接失败
        }
        try {
            ftpClient.makeDirectory(pathname);
            ftpClient.sendSiteCommand("chmod 777 " + pathname);//给文件夹赋予所有权限
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 此方法继承于 makeDirectory,是为了跨模态检索这个项目重写的
     * 功能：为用户创建目录，并在其目录下创建image文件夹和video文件夹
     * 备注：如果已经创建了的话，不会重复创建
     *
     * @param userId 用户id
     * @return true：创建成功  flase：创建失败或者已经存在此文件夹
     */
    public boolean VS_makeDirectory(String userId) {
        if (!ftpClient.isConnected()) {
            logger.info("连接ftp失败！");
            return false;//连接失败
        }
        try {
            makeDirectory(userId);
            changeWorkingDirectory(userId);
            makeDirectory("video");
            makeDirectory("image");
            changeWorkingDirectory("/");
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }


    /**
     * 改变工作目录
     *
     * @param dir ftp服务器上目录
     * @return boolean 改变成功返回true
     */
    public boolean changeWorkingDirectory(String dir) {
        if (!ftpClient.isConnected()) {
            return false;
        }
        try {
            return ftpClient.changeWorkingDirectory(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * @param remotePath 要下载的文件所在的ftp路径
     * @param fileName   指定下载的文件
     * @param localPath  本地路径
     * @return 下载成功给返回true 否则返回false
     * @Description: 指定ftp文件名下载到本地
     **/
    public boolean downFile(String remotePath, String fileName, String localPath) {
        boolean result = false;
        getFTPClient();

        if (!isCreateFtpClient(this.ftpClient)) {
            return false;
        }
        try {
            this.ftpClient.changeWorkingDirectory(remotePath);
            FTPFile[] remoteFiles = this.ftpClient.listFiles();
            for (FTPFile rf : remoteFiles) {
                if (rf.getName().equals(fileName)) {
                    logger.info("获取到" + fileName + "文件");
                    // 创建本地存储路径
                    File lf = new File(localPath + File.separator + rf.getName());
                    FileOutputStream lfos = new FileOutputStream(lf);
                    // 通过文件检索系统,将文件写入流
                    this.ftpClient.retrieveFile(rf.getName(), lfos);
                    System.out.println("下载完毕");
                    lfos.close();
                    result = true;
                }
            }
            this.ftpClient.logout();

        } catch (IOException e) {
            e.printStackTrace();
            return result;
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }
        return result;
    }

    public boolean VS_downFile(String remotePath, String fileName, String localPath) throws IOException {
        //首先返回根目录
        ftpClient.changeWorkingDirectory("/");
        boolean result = downFile(remotePath, fileName, localPath);
        return result;
    }


    /**
     * @return true:上传成功 false:上传失败
     * @Description: 上传文件
     * remotePath ftp服务器的路径(相对路径)
     * filename 上传文件的名字
     **/
    public boolean uploadFileByName(String remotePath, String filename, InputStream lin) {
        boolean result = false;
        if (!this.isCreateFtpClient(this.ftpClient)) {
            return false;
        }
        try {
            ftpClient.changeWorkingDirectory(remotePath);
            ftpClient.setControlEncoding("UTF-8"); // 中文支持
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            result = ftpClient.storeFile(filename, lin);
            logger.info("上传完毕，结果为" + result);
            lin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public boolean uploadFile(String remotePath, String localFilePath) {
        File lf = new File(localFilePath);
        try {
            InputStream lfis = new BufferedInputStream(new FileInputStream(lf));
            return uploadFileByName(remotePath, lf.getName(), lfis);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * @param user_login_name user_login_name
     * @param path_tag_list   本地路径和tag列表
     * @return true:上传成功 false:上传失败
     * @Description: FTP文件上传
     **/
    public boolean uploadFile(String user_login_name, List<myPair<String, Integer>> path_tag_list) throws IOException {
        //创建操作的ftp路径地址
        changeWorkingDirectory("/www/wwwroot/124.221.95.230/wp-content/uploads/users");
        VS_makeDirectory(user_login_name);
        String workPath;
        String filePath;
        int tag;
        boolean allFilesUploaded = true;
        for (myPair tmp_path_tag : path_tag_list) {
            //用户的操作目录
            ftpClient.changeWorkingDirectory("/");
            ftpClient.changeWorkingDirectory("/www/wwwroot/124.221.95.230/wp-content/uploads/users/"+user_login_name);
            filePath = (String) tmp_path_tag.getFirst();
            tag = (int) tmp_path_tag.getSecond();
            File file = new File(filePath);
            workPath = "";
            try {
                switch (tag) {
                    case 1:
                        workPath += "image";
                        break;
                    case 2:
                        workPath += "video";
                        break;
                    default:
                        break;
                }
                InputStream fileStream = new FileInputStream(file);
                boolean fileUploaded = uploadFileByName(workPath, file.getName(), fileStream);
                allFilesUploaded &= fileUploaded;
            } catch (Exception e) {
                e.printStackTrace();
                allFilesUploaded = false;
            }
        }
        this.close();
        return allFilesUploaded;
    }

    /**
     * @param filePath ftp上的活动目录
     * @param fileName 文件名（包括后缀名，比如 test.mp4）
     * @return true:删除成功  false:文件不存在或者删除失败
     * @Description: 删除远程文件信息
     **/
    public boolean deleteFile(String filePath, String fileName) {
        getFTPClient();
        boolean result = false;
        if (isCreateFtpClient(this.ftpClient)) {
            try {
                //更改到活动目录
                ftpClient.changeWorkingDirectory("video");
                System.out.println(ftpClient.printWorkingDirectory());
                result = ftpClient.deleteFile(fileName);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (ftpClient.isConnected()) {
                    try {
                        ftpClient.disconnect();
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
            }
        }
        return result;
    }


    /**
     * @return true:创建成功 false:创建失败
     * @Description: 是否成功创建ftp客户端
     **/
    public boolean isCreateFtpClient(FTPClient c) {
        if (c == null || !c.isConnected()) {
            return false;
        } else {
            return true;
        }

    }

    public void close() {
        if (ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

}


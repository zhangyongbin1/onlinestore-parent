package com.onlinestore.fastdfs;

import com.onlinestore.utils.FastDFSClient;
import org.csource.fastdfs.*;
import org.junit.Test;

/**
 * FastDFS使用方法的测试
 */
public class TestFastDFS {
    @Test
    public void uploadFile() throws Exception{
        //1.向工程中添加jar包
        //2.创建一个配置文件,配置tracker服务器地址
        //3.加载配置文件
        ClientGlobal.init("D:/IdeaProjects/onlinestore-parent/onlinestore-manager-web/src/main/resources/resource/FastDFS.conf");
        //4.创建一个trackerClient对象
        TrackerClient trackerClient = new TrackerClient();
        //5.使用trackerClient对象获得trackerserver对象
        TrackerServer trackerServer = trackerClient.getConnection();
        //6.创建一个storageserver的引用,值为null即可
        StorageServer storageServer = null;
        //7.创建一个storeclient对象，需要两个参数：trackerserver和storageserver
        StorageClient storageClient = new StorageClient(trackerServer,storageServer);
        //8.使用storageclient上传图片
        String[] strings = storageClient.upload_file("D:/temp/gameFlow.jpg","jpg", null);
        for(String string : strings){
            System.out.println(string);
        }
    }

    @Test
    public void testFastDFSClient() throws Exception{
        FastDFSClient fastDFSClient = new FastDFSClient("D:/IdeaProjects/onlinestore-parent/onlinestore-manager-web/src/main/resources/resource/FastDFS.conf");
        String string = fastDFSClient.uploadFile("D:/temp/milestone.jpg");
        System.out.println(string);
    }
}

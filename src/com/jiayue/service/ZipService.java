package com.jiayue.service;



import android.util.Log;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

public class ZipService {
    private static int unLock_Num = 4;// 解密秘钥
    private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte
//	/**
//	 * 解压zip格式压缩包 对应的是ant.jar
//	 */
//	public static void unzip(String sourceZip, String destDir) {
//		try {
//			Project p = new Project();
//			Expand e = new Expand();
//			e.setProject(p);
//			e.setSrc(new File(sourceZip));
//			e.setOverwrite(false);
//			e.setDest(new File(destDir));
//			/*
//			 * ant下的zip工具默认压缩编码为UTF-8编码， 而winRAR软件压缩是用的windows默认的GBK或者GB2312编码
//			 * 所以解压缩时要制定编码格式
//			 */
//			e.setEncoding("UTF-8");
//			e.execute();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}




    public onZipSuccess onZipSuccess;

    public interface onZipSuccess {
        void onZipSuccess();
    }

    public static void unzip(String archive, String decompressDir, onZipSuccess onZipSuccess) throws IOException, FileNotFoundException, ZipException
    {
        BufferedInputStream bi;
//        ZipFile zf = new ZipFile(archive, "GBK");//windows zip
        ZipFile zf = new ZipFile(archive, "UTF-8");//mac zip
        Enumeration e = zf.getEntries();
        while (e.hasMoreElements())
        {
            ZipEntry ze2 = (ZipEntry) e.nextElement();
            String entryName = ze2.getName();
            String path = decompressDir + "/" + entryName;
            if (ze2.isDirectory())
            {
                System.out.println("正在创建解压目录 - " + entryName);
                File decompressDirFile = new File(path);
                if (!decompressDirFile.exists())
                {
                    decompressDirFile.mkdirs();
                }
            } else
            {
                System.out.println("正在创建解压文件 - " + entryName);
                String fileDir = path.substring(0, path.lastIndexOf("/"));
                File fileDirFile = new File(fileDir);
                if (!fileDirFile.exists())
                {
                    fileDirFile.mkdirs();
                }
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(decompressDir + "/" + entryName));
                bi = new BufferedInputStream(zf.getInputStream(ze2));
                byte[] readContent = new byte[1024];
                int readCount = bi.read(readContent);
                while (readCount != -1)
                {
                    bos.write(readContent, 0, readCount);
                    readCount = bi.read(readContent);
                }
                bos.close();
            }
        }
        zf.close();
        //bIsUnzipFinsh = true;
        onZipSuccess.onZipSuccess();
    }

    public static void unzip(String archive, String decompressDir) throws IOException, FileNotFoundException, ZipException
    {
        BufferedInputStream bi;
        ZipFile zf = new ZipFile(archive, "GBK");
        Enumeration e = zf.getEntries();
        while (e.hasMoreElements())
        {
            ZipEntry ze2 = (ZipEntry) e.nextElement();
            String entryName = ze2.getName();
            String path = decompressDir + "/" + entryName;
            if (ze2.isDirectory())
            {
                System.out.println("正在创建解压目录 - " + entryName);
                File decompressDirFile = new File(path);
                if (!decompressDirFile.exists())
                {
                    decompressDirFile.mkdirs();
                }
            } else
            {
                System.out.println("正在创建解压文件 - " + entryName);
                String fileDir = path.substring(0, path.lastIndexOf("/"));
                File fileDirFile = new File(fileDir);
                if (!fileDirFile.exists())
                {
                    fileDirFile.mkdirs();
                }
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(decompressDir + "/" + entryName));
                bi = new BufferedInputStream(zf.getInputStream(ze2));
                byte[] readContent = new byte[1024];
                int readCount = bi.read(readContent);
                while (readCount != -1)
                {
                    bos.write(readContent, 0, readCount);
                    readCount = bi.read(readContent);
                }
                bos.close();
            }
        }
        zf.close();
        //bIsUnzipFinsh = true;
    }

//    public static void unzip(String file, String destDir, onZipSuccess onZipSuccess) throws IOException, ZipException {
//
//        // 压缩文件
//        File srcZipFile = new File(file);
//        // 基本目录
//        if (!destDir.endsWith("/")) {
//            destDir += "/";
//        }
//        String prefixion = destDir;
//
//        // 压缩输入流
//        ZipInputStream zipInput = new ZipInputStream(new FileInputStream(srcZipFile));
//        // 压缩文件入口
//        ZipEntry currentZipEntry = null;
//        // 循环获取压缩文件及目录
//        while ((currentZipEntry = zipInput.getNextEntry()) != null) {
//            // 获取文件名或文件夹名
//            String fileName = currentZipEntry.getName();
//            Log.d("ZipService", "=====================" + fileName);
//            // 构成File对象
//            File tempFile = new File(prefixion + fileName);
//            Log.d("ZipService", "tempFile=====" + prefixion + fileName);
//            // 父目录是否存在
//            if (!tempFile.getParentFile().exists()) {
//                // 不存在就建立此目录
//                Log.d("ZipService", "1111111111111111111");
//                tempFile.getParentFile().mkdir();
//            }
//            // 如果是目录，文件名的末尾应该有“/"
//            if (currentZipEntry.isDirectory()) {
//                Log.d("ZipService", "222222222222222");
//                // 如果此目录不在，就建立目录。
//                if (!tempFile.exists()) {
//                    Log.d("ZipService", "3333333333333333");
//                    tempFile.mkdir();
//                }
//                // 是目录，就不需要进行后续操作，返回到下一次循环即可。
//                continue;
//            }
//            // 如果是文件
//            if (!tempFile.exists()) {
//                // 不存在就重新建立此文件。当文件不存在的时候，不建立文件就无法解压缩。
//                Log.d("ZipService", "444444444444444444");
//                tempFile.createNewFile();
//            }
//            // 输出解压的文件
//            FileOutputStream tempOutputStream = new FileOutputStream(tempFile);
//
//            // 获取压缩文件的数据
//            byte[] buffer = new byte[1024];
//            int hasRead = 0;
//            // 循环读取文件数据
//            while ((hasRead = zipInput.read(buffer)) > 0) {
//                tempOutputStream.write(buffer, 0, hasRead);
//            }
//            tempOutputStream.flush();
//            tempOutputStream.close();
//            Log.d("ZipService", "5555555555555");
//        }
//        zipInput.close();
//
//        Log.d("ZipService", "66666666666666666666");
//        onZipSuccess.onZipSuccess();
//    }
//
//    public static void unzip(String file, String destDir) throws IOException, ZipException {
//        // 压缩文件
//        File srcZipFile = new File(file);
//        // 基本目录
//        if (!destDir.endsWith("/")) {
//            destDir += "/";
//        }
//        String prefixion = destDir;
//
//        // 压缩输入流
//        ZipInputStream zipInput = new ZipInputStream(new FileInputStream(srcZipFile));
//        // 压缩文件入口
//        ZipEntry currentZipEntry = null;
//        // 循环获取压缩文件及目录
//        while ((currentZipEntry = zipInput.getNextEntry()) != null) {
//            // 获取文件名或文件夹名
//            String fileName = currentZipEntry.getName();
//            Log.d("filename", "=====================" + fileName);
//            // 构成File对象
//            File tempFile = new File(prefixion + fileName);
//            Log.d("tempFile", "tempFile=====" + prefixion + fileName);
//            // 父目录是否存在
//            if (!tempFile.getParentFile().exists()) {
//                // 不存在就建立此目录
//                Log.d("11111111111111", "1111111111111111111");
//                tempFile.getParentFile().mkdir();
//            }
//            // 如果是目录，文件名的末尾应该有“/"
//            if (currentZipEntry.isDirectory()) {
//                Log.d("2222222222222", "222222222222222");
//                // 如果此目录不在，就建立目录。
//                if (!tempFile.exists()) {
//                    Log.d("3333333333333333", "3333333333333333");
//                    tempFile.mkdir();
//                }
//                // 是目录，就不需要进行后续操作，返回到下一次循环即可。
//                continue;
//            }
//            // 如果是文件
//            if (!tempFile.exists()) {
//                // 不存在就重新建立此文件。当文件不存在的时候，不建立文件就无法解压缩。
//                Log.d("4444444444444444444", "444444444444444444");
//                tempFile.createNewFile();
//            }
//            // 输出解压的文件
//            FileOutputStream tempOutputStream = new FileOutputStream(tempFile);
//
//            // 获取压缩文件的数据
//            byte[] buffer = new byte[1024];
//            int hasRead = 0;
//            // 循环读取文件数据
//            while ((hasRead = zipInput.read(buffer)) > 0) {
//                tempOutputStream.write(buffer, 0, hasRead);
//            }
//            tempOutputStream.flush();
//            tempOutputStream.close();
//        }
//        zipInput.close();
//    }

    // 解密
    public static void unLockFile(File soureFile, File saveFile) {
        try {
            FileInputStream is = new FileInputStream(soureFile);
            FileOutputStream os = new FileOutputStream(saveFile);

            InputStream fis = new BufferedInputStream(is);
            OutputStream fos = new BufferedOutputStream(os);
            long beginTime = System.currentTimeMillis();
            int b = 0;
            int a = 0;
            int length = 1024 * 1024 * 3;
            while ((b = fis.read()) != -1 && a < length) {
                fos.write(b + unLock_Num);
                a++;
            }
            if (a >= length) {
                fos.write(b);
                long endTime = System.currentTimeMillis();

                System.out.println(endTime - beginTime);

                byte buffer[] = new byte[1024];
                while ((b = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, b);
                }
            }
            fos.flush();
            fos.close();
            fis.close();
            long doneTime = System.currentTimeMillis();
            System.out.println("解密完成:耗时" + (doneTime - beginTime) / 1000);

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}

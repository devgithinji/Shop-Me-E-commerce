package com.densoft.shopmeAdmin;


import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

class AmazonS3UtilTest {

    @Test
    public void testListFolder() {
        String folderName = "product-images/18";
        List<String> keys = AmazonS3Util.listFolder(folderName);
        keys.forEach(System.out::println);
    }

    @Test
    public void testUploadFile() throws FileNotFoundException {
        String folderName = "test-upload";
        String fileName = "ShopmeAdminBig.png";
        String filePath = "/home/dennis/Downloads/" + fileName;

        InputStream inputStream = new FileInputStream(filePath);

        AmazonS3Util.uploadFile(folderName, fileName, inputStream);
    }

    @Test
    public void testDeleteFile() {
        String fileName = "test-upload/hero-img.png";
        AmazonS3Util.deleteFile(fileName);
    }

    @Test
    public void removeFolder() {
        String folderName = "test-upload";
        AmazonS3Util.removeFolder(folderName);
    }

}